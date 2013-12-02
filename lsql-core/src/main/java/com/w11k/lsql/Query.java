package com.w11k.lsql;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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

    private final LSql lSql;

    private final PreparedStatement preparedStatement;

    private List<QueriedRow> rows;

    public Query(LSql lSql, PreparedStatement preparedStatement) {
        this.lSql = lSql;
        this.preparedStatement = preparedStatement;
        run();
    }

    public Query(LSql lSql, String sql) {
        this(lSql, ConnectionUtils.prepareStatement(lSql, sql, false));
    }

    public LSql getlSql() {
        return lSql;
    }

    @Override
    public Iterator<QueriedRow> iterator() {
        return rows.iterator();
    }

    public <T> List<T> map(Function<QueriedRow, T> rowHandler) {
        List<T> list = Lists.newLinkedList();
        for (QueriedRow row : this) {
            list.add(rowHandler.apply(row));
        }
        return list;
    }

    public Optional<QueriedRow> getFirstRow() {
        if (rows.size() == 0) {
            return absent();
        } else {
            return of(rows.get(0));
        }
    }

    public List<QueriedRow> asList() {
        return Lists.newLinkedList(rows);
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

    private void run() {
        try {
            Map<Integer, Column> headers = Maps.newHashMap();
            ConcurrentMap<String, AtomicInteger> tablePkCounter = Maps.newConcurrentMap();

            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            Set<Table> foundTables = Sets.newHashSet();
            boolean hasFunctionColumns = false;

            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                // Identify table
                String sqlTableName = lSql.getDialect().getTableNameFromResultSetMetaData(metaData, i);
                Optional<Table> table;
                if (sqlTableName == null || "".equals(sqlTableName)) {
                    table = absent();
                    hasFunctionColumns = true;
                } else {
                    String javaTable = lSql.getDialect().identifierSqlToJava(sqlTableName);
                    table = of(lSql.table(javaTable));
                    foundTables.add(table.get());
                }

                // Get Column instance
                String sqlColumn = metaData.getColumnName(i);
                String javaColumn = lSql.getDialect().identifierSqlToJava(sqlColumn);
                String sqlColumnLabel = metaData.getColumnLabel(i);
                String javaColumnLabel = lSql.getDialect().identifierSqlToJava(sqlColumnLabel);
                Column column;
                if (table.isPresent()) {
                    column = table.get().column(javaColumn);
                } else {
                    column = new Column(
                            Optional.<Table>absent(),
                            javaColumnLabel,
                            lSql.getDialect().getConverterRegistry().getConverterForSqlType(metaData.getColumnType(i)),
                            -1);
                }
                headers.put(i, column);

                // Count table PK occurrences
                tablePkCounter.putIfAbsent(column.getTableName().or(""), new AtomicInteger());
                if (column.isPkColumn()) {
                    tablePkCounter.get(column.getTable().getTableName()).incrementAndGet();
                }
            }

            // Calculate column key name
            ConcurrentMap<String, AtomicInteger> tableCounter = Maps.newConcurrentMap();
            Map<Integer, String> columnNames = Maps.newHashMap();
            for (Integer i : headers.keySet()) {
                Column column = headers.get(i);
                String columnName = column.getColumnName();
                if (foundTables.size() >= 2) {
                    if (column.hasCorrespondingTable()) {
                        int counter = tablePkCounter.get(column.getTable().getTableName()).get();
                        if (counter <= 1) {
                            columnName = column.getTable().getTableName() + "." + columnName;
                        } else {
                            tableCounter.putIfAbsent(column.getTable().getTableName(), new AtomicInteger());
                            if (column.isPkColumn()) {
                                tableCounter.get(column.getTable().getTableName()).incrementAndGet();
                            }
                            int c = tableCounter.get(column.getTable().getTableName()).get();
                            columnName = column.getTable().getTableName() + "." + c + "." + columnName;
                        }
                    }
                }
                columnNames.put(i, columnName);
            }

            // Read all rows
            List<QueriedRow> newRows = Lists.newLinkedList();
            while (resultSet.next()) {
                Map<String, Object> rowData = Maps.newHashMap();
                Map<String, Column> columnsByName = Maps.newHashMap();

                for (Integer columnIndex : headers.keySet()) {
                    Column column = headers.get(columnIndex);
                    String columnName = columnNames.get(columnIndex);
                    Object value = column.getConverter().getValueFromResultSet(lSql, resultSet, columnIndex);
                    rowData.put(columnName, value);
                    columnsByName.put(columnName, column);
                }
                QueriedRow row = new QueriedRow(rowData, columnsByName);
                if (foundTables.size() == 1 && !hasFunctionColumns) {
                    row.setTable(foundTables.iterator().next());
                }
                newRows.add(row);
            }
            this.rows = newRows;
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

}
