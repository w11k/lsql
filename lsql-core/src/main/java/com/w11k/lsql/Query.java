package com.w11k.lsql;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.w11k.lsql.exceptions.QueryException;
import com.w11k.lsql.jdbc.ConnectionUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Query implements Iterable<QueriedRow> {

    public class ResultSetColumn {

        final public int index;

        final public Column column;

        public ResultSetColumn(int index, Column column) {
            this.index = index;
            this.column = column;
        }
    }

    private final LSql lSql;

    private final PreparedStatement preparedStatement;

    private List<QueriedRow> rows;

    private Map<String, ResultSetColumn> meta = Maps.newHashMap();

    public Query(LSql lSql, PreparedStatement preparedStatement) {
        this.lSql = lSql;
        this.preparedStatement = preparedStatement;
        run();
    }

    public Query(LSql lSql, String sql) {
        this.lSql = lSql;
        this.preparedStatement = ConnectionUtils.prepareStatement(lSql, sql, false);
        run();
    }

    public LSql getlSql() {
        return lSql;
    }

    public Query run() {
        rows = Lists.newLinkedList();
        try {
            final ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();

            boolean useTablePrefix = false;
            String lastUsedSqlTableName = null;
            for (int i = 1; i <= metaData.getColumnCount(); i++) {

                String sqlTable = lSql.getDialect().getTableNameFromResultSetMetaData(metaData, i);

                if (sqlTable == null || sqlTable.equals("")) {
                    String c = metaData.getColumnName(i);
                    throw new IllegalStateException(
                            "Driver/Dialect returned an empty table name for column '" + c + "'");
                }

                if (!useTablePrefix
                        && lastUsedSqlTableName != null
                        && !lastUsedSqlTableName.equals(sqlTable)) {
                    // More than 1 table used in query. Switch to table prefix mode.
                    useTablePrefix = true;
                    // Rename the already processed columns
                    Map<String, ResultSetColumn> newMeta = Maps.newHashMap();
                    for (String name : meta.keySet()) {
                        ResultSetColumn resultSetColumn = meta.get(name);
                        newMeta.put(resultSetColumn.column.getTable().getTableName() + "." +
                                name, resultSetColumn);
                    }
                    meta = newMeta;
                }
                lastUsedSqlTableName = sqlTable;

                String sqlColumn = metaData.getColumnLabel(i);
                String javaTable = lSql.getDialect().identifierSqlToJava(sqlTable);
                Table table = lSql.table(javaTable);
                String javaColumn = lSql.getDialect().identifierSqlToJava(sqlColumn);
                Column column = table.column(javaColumn);

                String name = javaColumn;
                if (useTablePrefix) {
                    name = javaTable + "." + name;
                }

                meta.put(name, new ResultSetColumn(i, column));
            }
            while (resultSet.next()) {
                QueriedRow row = new QueriedRow(lSql, this.meta, resultSet);
                rows.add(row);
            }
        } catch (SQLException e) {
            throw new QueryException(e);
        }
        return this;
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
            return Optional.absent();
        } else {
            return Optional.of(rows.get(0));
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
            Map<String, Row> rowByTables = queriedRow.groupByTables();

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

    public List<Row> joinOn(Table startTable) {
        Map<String, List<Row>> byTables = groupByTables();
        List<Row> startRows = byTables.get(startTable.getTableName());
        for (Row row : startRows) {
            joinRow(row, startTable, byTables);
        }
        return startRows;
    }

    private Row joinRow(Row row, Table tableOfRow, Map<String, List<Row>> fullResult) {
        Map<Table, Column> foreignTables = tableOfRow.getExportedForeignKeyTables();

        // for each foreign table
        for (Table foreignTable : foreignTables.keySet()) {
            // check if the result contains rows for the foreign table
            if (fullResult.containsKey(foreignTable.getTableName())) {
                // prepare an empty List in the current row
                LinkedList<Row> joinedForeignRows = Lists.newLinkedList();
                row.put("__" + foreignTable.getTableName(), joinedForeignRows);

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
