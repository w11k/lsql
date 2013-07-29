package com.w11k.lsql;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.w11k.lsql.exceptions.SelectException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Query implements Iterable<QueriedRow> {

    public class ResultSetColumn {

        public int index;
        public Column column;

        public ResultSetColumn(int index, Column column) {
            this.index = index;
            this.column = column;
        }
    }

    private final LSql lSql;
    private final String sql;
    private List<QueriedRow> rows;
    private Map<String, ResultSetColumn> meta = Maps.newHashMap();

    public Query(LSql lSql, String sql) {
        this.lSql = lSql;
        this.sql = sql;
        run();
    }

    public LSql getlSql() {
        return lSql;
    }

    public Query run() {
        rows = Lists.newLinkedList();
        try {
            final ResultSet resultSet = lSql.createStatement().executeQuery(sql);
            ResultSetMetaData metaData = resultSet.getMetaData();

            boolean useTablePrefix = false;
            String lastUsedSqlTableName = null;
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String sqlTable = metaData.getTableName(i);
                if (!useTablePrefix
                        && lastUsedSqlTableName != null
                        && !lastUsedSqlTableName.equals(sqlTable)) {
                    // More than 1 table used in query. Switch to table prefix mode.
                    useTablePrefix = true;
                    // Rename the already processed columns
                    Map<String, ResultSetColumn> newMeta = Maps.newHashMap();
                    for (String name : meta.keySet()) {
                        ResultSetColumn resultSetColumn = meta.get(name);
                        newMeta.put(resultSetColumn.column.getTable().getTableName() + "." + name, resultSetColumn);
                    }
                    meta = newMeta;
                }
                lastUsedSqlTableName = sqlTable;

                String sqlColumn = metaData.getColumnLabel(i);
                String javaTable = lSql.getGlobalConverter().identifierSqlToJava(sqlTable);
                Table table = lSql.table(javaTable);
                String javaColumn = table.getTableConverter().identifierSqlToJava(sqlColumn);
                Column column = table.column(javaColumn);

                String name = javaColumn;
                if (useTablePrefix) {
                    name = javaTable + "." + name;
                }

                meta.put(name, new ResultSetColumn(i, column));
            }
            while (resultSet.next()) {
                QueriedRow row = new QueriedRow(this.meta, resultSet);
                rows.add(row);
            }
        } catch (SQLException e) {
            throw new SelectException(e);
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

    public QueriedRow getFirstRow() {
        return rows.get(0);
    }

    public List<QueriedRow> asList() {
        return Lists.newLinkedList(rows);
    }

}
