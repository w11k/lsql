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

public class Query implements Iterable<Row> {

    private final LSql lSql;
    private final String sql;
    private List<Row> rows;
    private final Map<String, ResultSetColumnMeta> meta = Maps.newHashMap();

    public Query(LSql lSql, String sql) {
        this.lSql = lSql;
        this.sql = sql;
        run();
    }

    public Query run() {
        rows = Lists.newLinkedList();
        try {
            final ResultSet resultSet = lSql.createStatement().executeQuery(sql);
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String sqlTable = metaData.getTableName(i);
                String sqlColumn = metaData.getColumnLabel(i);
                JavaSqlConverter converter = lSql.getConverterRegistry().getConverterBySqlName(sqlTable, sqlColumn);
                String javaTable = converter.identifierSqlToJava(sqlTable);
                String javaColumn = converter.identifierSqlToJava(sqlColumn);
                ResultSetColumnMeta cm = new ResultSetColumnMeta(sqlTable, javaTable, sqlColumn, javaColumn, converter);
                meta.put(javaColumn, cm);
            }
            while (resultSet.next()) {
                Row row = new Row(new ResultSetMap(lSql, resultSet));
                rows.add(row);
            }
        } catch (SQLException e) {
            throw new SelectException(e);
        }
        return this;
    }

    @Override public Iterator<Row> iterator() {
        return rows.iterator();
    }

    public <T> List<T> map(Function<Row, T> rowHandler) {
        List<T> list = Lists.newLinkedList();
        for (Row row : this) {
            list.add(rowHandler.apply(row));
        }
        return list;
    }

    public Row getFirstRow() {
        return rows.get(0);
    }

    public List<Row> asList() {
        return Lists.newLinkedList(rows);
    }

    /*
    public Map<String, List<Row>> groupByTables() {
        Map<String, List<Row>> byTables = Maps.newHashMap();
        for (Row row : rows) {
            row.getMeta().
        }



        return byTables;
    }
    */

}
