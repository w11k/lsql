package com.weiglewilczek.lsql;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class SelectStatement {

    private final LSql lSql;

    private String columns = "*";
    private String from;
    private String where;

    public SelectStatement(LSql lSql) {
        this.lSql = lSql;
    }

    public SelectStatement(LSql lSql, String columns) {
        this.lSql = lSql;
        this.columns = columns;
    }

    public SelectStatement from(String from) {
        this.from = from;
        return this;
    }

    public SelectStatement where(String where) {
        this.where = where;
        return this;
    }

    private String createSelectStatement() {
        String s = "select " + columns + " from " + from;
        s = where != null ? s + " where " + where : s;
        s += ";";
        return s;
    }

    public <T> List<T> map(Function<Row, T> function) {
        List<T> list = Lists.newLinkedList();
        Statement st = lSql.createStatement();
        try {
            final ResultSet resultSet = st.executeQuery(createSelectStatement());
            boolean hasNext = resultSet.next();
            while (hasNext) {
                list.add(function.apply(new Row(resultSet)));
                hasNext = resultSet.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

}
