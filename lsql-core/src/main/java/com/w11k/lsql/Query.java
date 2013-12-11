package com.w11k.lsql;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.exceptions.QueryException;
import com.w11k.lsql.jdbc.ConnectionUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class Query implements Iterable<QueriedRow> {

    public static class ResultSetColumn {
        int position;

        Column column;

        String nameInRow;
    }

    private static class FoundTablesSetWithResultSetColumn {
        List<ResultSetColumn> columnList;

        Set<Optional<Table>> foundTables;
    }

    private final LSql lSql;

    private final PreparedStatement preparedStatement;

    public Query(LSql lSql, PreparedStatement preparedStatement) {
        this.lSql = lSql;
        this.preparedStatement = preparedStatement;
    }

    public Query(LSql lSql, String sql) {
        this(lSql, ConnectionUtils.prepareStatement(lSql, sql, false));
    }

    public LSql getlSql() {
        return lSql;
    }

    @Override
    public Iterator<QueriedRow> iterator() {
        return runAndProcessQuery().iterator();
    }

    public <T> List<T> map(Function<QueriedRow, T> rowHandler) {
        List<T> list = Lists.newLinkedList();
        for (QueriedRow row : this) {
            list.add(rowHandler.apply(row));
        }
        return list;
    }

    public Optional<QueriedRow> getFirstRow() {
        List<QueriedRow> queriedRows = runAndProcessQuery();
        if (queriedRows.size() == 0) {
            return absent();
        } else {
            return of(queriedRows.get(0));
        }
    }

    public List<QueriedRow> asList() {
        return Lists.newLinkedList(runAndProcessQuery());
    }

    public Map<String, Map<Object, LinkedRow>> groupByTables() {
        Map<String, Map<Object, LinkedRow>> byTables = Maps.newHashMap();

        for (QueriedRow row : asList()) {
            Map<String, Map<Object, LinkedRow>> grouped = row.groupByTables();
            for (String table : grouped.keySet()) {
                if (!byTables.containsKey(table)) {
                    byTables.put(table, Maps.<Object, LinkedRow>newLinkedHashMap());
                }
                for (Object rowForTableId : grouped.get(table).keySet()) {
                    LinkedRow value = grouped.get(table).get(rowForTableId);
                    if (!byTables.get(table).containsKey(rowForTableId)) {
                        byTables.get(table).put(rowForTableId, value);
                    } else {
                        byTables.get(table).get(rowForTableId).putAll(value);
                    }
                }
            }
        }
        return byTables;
    }

    public List<Row> asRawList() {
        try {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<ResultSetColumn> columns = createRawResultSetColumnList(resultSet.getMetaData());
            List<Row> rows = Lists.newLinkedList();
            while (resultSet.next()) {
                rows.add(extractRow(resultSet, columns));
            }
            return rows;
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    private Row extractRow(ResultSet resultSet, List<ResultSetColumn> resultSetColumns) throws SQLException {
        Row r = new Row();
        for (ResultSetColumn rsc : resultSetColumns) {
            Object val = rsc.column.getConverter().getValueFromResultSet(lSql, resultSet, rsc.position);
            r.put(rsc.nameInRow, val);
        }
        return r;
    }

    private Column getColumnForResultSetColumn(ResultSetMetaData metaData, int position) throws SQLException {
        String sqlColumnName = metaData.getColumnLabel(position);
        String javaColumnName = lSql.getDialect().identifierSqlToJava(sqlColumnName);
        Converter converter = getConverter(metaData, position);
        Optional<Table> table = getTable(metaData, position);
        Column column;
        if (table.isPresent()) {
            column = table.get().column(javaColumnName);
        } else {
            column = new Column(
                    Optional.<Table>absent(),
                    javaColumnName,
                    metaData.getColumnType(position),
                    converter,
                    -1);
        }
        return column;
    }

    private Converter getConverter(ResultSetMetaData metaData, int position) throws SQLException {
        int columnSqlType = metaData.getColumnType(position);
        return lSql.getDialect().getConverterRegistry().getConverterForSqlType(columnSqlType);
    }

    private List<ResultSetColumn> createRawResultSetColumnList(ResultSetMetaData metaData) throws SQLException {
        Set<String> processedColumnLabels = Sets.newLinkedHashSet(); // used to find duplicates
        List<ResultSetColumn> columnList = Lists.newLinkedList();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            Column column = getColumnForResultSetColumn(metaData, i);
            if (processedColumnLabels.contains(column.getColumnName())) {
                throw new IllegalStateException("Dublicate column '" + column.getColumnName() + "' in query.");
            }
            processedColumnLabels.add(column.getColumnName());
            ResultSetColumn rsc = new ResultSetColumn();
            rsc.position = i;
            rsc.column = column;
            rsc.nameInRow = column.getColumnName();
            columnList.add(rsc);
        }
        return columnList;
    }

    private Optional<Table> getTable(ResultSetMetaData metaData, int position) throws SQLException {
        String sqlTableName = lSql.getDialect().getTableNameFromResultSetMetaData(metaData, position);
        Optional<Table> table;
        if (sqlTableName == null || "".equals(sqlTableName)) {
            table = absent();
        } else {
            String javaTable = lSql.getDialect().identifierSqlToJava(sqlTableName);
            table = of(lSql.table(javaTable));
        }
        return table;
    }

    private FoundTablesSetWithResultSetColumn createConvertedResultSetColumnList(ResultSetMetaData metaData) throws SQLException {
        List<ResultSetColumn> columnList = Lists.newLinkedList();
        ConcurrentMap<String, AtomicInteger> tablePkCounter = Maps.newConcurrentMap();
        Set<Optional<Table>> foundTables = Sets.newHashSet();

        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            Column column = getColumnForResultSetColumn(metaData, i);
            foundTables.add(column.getTable());

            ResultSetColumn rsc = new ResultSetColumn();
            rsc.position = i;
            rsc.column = column;
            rsc.nameInRow = column.getColumnName(); // may be changed later
            columnList.add(rsc);

            // Count table PK occurrences
            tablePkCounter.putIfAbsent(column.getTableName().or(""), new AtomicInteger());
            if (column.isPkColumn()) {
                tablePkCounter.get(column.getTableName().get()).incrementAndGet();
            }
        }

        // Change e.g. table.column to table.1.column if the result set contains the table 2 times or more
        if (foundTables.size() > 1) {
            ConcurrentMap<String, AtomicInteger> tableOccuranceCounter = Maps.newConcurrentMap();
            for (ResultSetColumn rsc : columnList) {
                Column column = rsc.column;
                // Skip function columns
                if (column.hasCorrespondingTable()) {
                    int counter = tablePkCounter.get(column.getTableName().get()).get();
                    if (counter <= 1) {
                        rsc.nameInRow = column.getTableName().get() + "." + column.getColumnName();
                    } else {
                        tableOccuranceCounter.putIfAbsent(column.getTableName().get(), new AtomicInteger(0));
                        if (column.isPkColumn()) {
                            tableOccuranceCounter.get(column.getTableName().get()).incrementAndGet();
                        }
                        int c = tableOccuranceCounter.get(column.getTableName().get()).get();
                        rsc.nameInRow = column.getTableName().get() + "." + c + "." + column.getColumnName();
                    }
                }
            }
        }
        FoundTablesSetWithResultSetColumn tablesWithColumns = new FoundTablesSetWithResultSetColumn();
        tablesWithColumns.foundTables = foundTables;
        tablesWithColumns.columnList = columnList;
        return tablesWithColumns;
    }

    private List<QueriedRow> runAndProcessQuery() {
        try {
            ResultSet resultSet = preparedStatement.executeQuery();
            FoundTablesSetWithResultSetColumn foundWithColumns = createConvertedResultSetColumnList(
                    resultSet.getMetaData());
            Set<Optional<Table>> foundTables = foundWithColumns.foundTables;
            List<ResultSetColumn> columnList = foundWithColumns.columnList;

            List<QueriedRow> rows = Lists.newLinkedList();
            while (resultSet.next()) {
                Row row = extractRow(resultSet, columnList);
                QueriedRow queriedRow = new QueriedRow(row, columnList);
                if (foundTables.size() == 1 && !foundTables.contains(Optional.<Table>absent())) {
                    queriedRow.setTable(foundTables.iterator().next().get());
                }
                rows.add(queriedRow);
            }
            return rows;
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

}

