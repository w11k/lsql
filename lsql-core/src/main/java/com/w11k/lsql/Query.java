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
import java.util.*;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class Query implements Iterable<QueriedRow> {

    /*
    public class ResultSetColumn {

        final public int index;

        final public Column column;

        public ResultSetColumn(int index, Column column) {
            this.index = index;
            this.column = column;
        }
    }
    */

    private final LSql lSql;

    private final PreparedStatement preparedStatement;

    private List<QueriedRow> rows;

    //private Map<String, ResultSetColumn> meta = Maps.newHashMap();

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

    public Map<String, List<Row>> groupByTables() {
        // We first store everything in a Map to remove duplicate rows
        Map<String, Map<Object, Row>> byTables = Maps.newHashMap();

        // For each row in query
        for (QueriedRow queriedRow : asList()) {
            Map<String, LinkedRow> rowByTables = queriedRow.groupByTables();

            // for each table in a row
            for (String key : rowByTables.keySet()) {
                Row row = rowByTables.get(key);
                if (!byTables.containsKey(key)) {
                    byTables.put(key, Maps.<Object, Row>newLinkedHashMap());
                }
                String pkColumn = lSql.table(key).getPrimaryKeyColumn().get();
                Object pkValue = row.get(pkColumn);
                byTables.get(key).put(pkValue, row);
            }
        }

        // Transform Map of Rows to List of Rows
        return Maps.transformEntries(
                byTables,
                new Maps.EntryTransformer<String, Map<Object, Row>, List<Row>>() {
                    public List<Row> transformEntry(String key, Map<Object, Row> value) {
                        return Lists.newLinkedList(value.values());
                    }
                });
    }

    public List<Map<String, LinkedRow>> groupEachRowByTables() {
        List<Map<String, LinkedRow>> grouped = Lists.newLinkedList();
        for (QueriedRow row : rows) {
            grouped.add(row.groupByTables());
        }
        return grouped;
    }

    public List<Row> joinOn(Table startTable) {
        Map<String, List<Row>> byTables = groupByTables();
        List<Row> startRows = byTables.get(startTable.getTableName());
        for (Row row : startRows) {
            joinRow(row, startTable, byTables);
        }
        return startRows;
    }

    private void run() {
        List<QueriedRow> newRows = Lists.newLinkedList();
        try {
            Map<Integer, Column> columns = Maps.newHashMap();
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
                String sqlColumn = metaData.getColumnLabel(i);
                String javaColumn = lSql.getDialect().identifierSqlToJava(sqlColumn);
                Column column;
                if (table.isPresent()) {
                    column = table.get().column(javaColumn);
                } else {
                    column = new Column(
                            Optional.<Table>absent(),
                            javaColumn,
                            lSql.getDialect().getConverterRegistry()
                                    .getConverterForSqlType(metaData.getColumnType(i)),
                            -1);
                }
                columns.put(i, column);
            }

            // Read all rows
            while (resultSet.next()) {
                Map<String, Object> rowData = Maps.newHashMap();
                Map<String, Column> columnByName = Maps.newHashMap();

                for (Integer columnIndex : columns.keySet()) {
                    Column column = columns.get(columnIndex);
                    String key;
                    if (foundTables.size() <= 1) {
                        key = column.getColumnName();
                    } else {
                        key = column.getColumnNameWithPrefix();
                    }
                    Object value = column.getConverter().getValueFromResultSet(lSql, resultSet, columnIndex);
                    rowData.put(key, value);
                    columnByName.put(key, column);
                }
                QueriedRow row = new QueriedRow(rowData, columnByName);
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

    private Row joinRow(Row row, Table tableOfRow, Map<String, List<Row>> fullResult) {
        Map<Table, Column> foreignTables = tableOfRow.getExportedForeignKeyTables();

        // for each foreign table
        for (Table foreignTable : foreignTables.keySet()) {
            // check if the result contains rows for the foreign table
            if (fullResult.containsKey(foreignTable.getTableName())) {
                // prepare an empty List in the current row
                LinkedList<Row> joinedForeignRows = Lists.newLinkedList();
                row.addJoinedRows(foreignTable.getTableName(), joinedForeignRows);

                // for each row in the foreign table
                List<Row> foreignRows = fullResult.get(foreignTable.getTableName());
                for (Row foreignRow : foreignRows) {
                    // check if they join PK==FK
                    if (row.get(tableOfRow.getPrimaryKeyColumn().get()).equals(
                            foreignRow.get(foreignTables.get(foreignTable).getColumnName()))) {

                        joinRow(foreignRow, foreignTable, fullResult);
                        joinedForeignRows.add(foreignRow);
                    }
                }
            }
        }
        return row;
    }


}
