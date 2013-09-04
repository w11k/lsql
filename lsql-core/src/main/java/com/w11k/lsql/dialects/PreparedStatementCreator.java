package com.w11k.lsql.dialects;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.w11k.lsql.Column;
import com.w11k.lsql.Table;
import com.w11k.lsql.jdbc.ConnectionUtils;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class PreparedStatementCreator {

    public PreparedStatement createInsertStatement(final Table table, List<String> columns) {
        String sqlTableName = table.getlSql().getDialect().identifierJavaToSql(table.getTableName());
        StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(sqlTableName);
        sql.append("(");
        sql.append(Joiner.on(",").join(createSqlColumnNames(table, columns)));
        sql.append(")values(");
        sql.append(Joiner.on(",").join(Collections.nCopies(columns.size(), "?")));
        sql.append(")");
        return ConnectionUtils.prepareStatement(table.getlSql(), sql.toString(), true);
    }

    public PreparedStatement createUpdateStatement(Table table, List<String> columns) {
        String sqlTableName = table.getlSql().getDialect().identifierJavaToSql(table.getTableName());
        StringBuilder sql = new StringBuilder();
        sql.append("update ").append(sqlTableName);
        sql.append(" set ");
        sql.append(Joiner.on(",").join(Lists.transform(
                createSqlColumnNames(table, columns),
                new Function<String, Object>() {
                    @Nullable
                    @Override
                    public Object apply(@Nullable String input) {
                        return input + "=?";
                    }
                })));
        sql.append(" WHERE ");
        sql.append(table.getPrimaryKeyColumn().get()).append("=?;");
        return ConnectionUtils.prepareStatement(table.getlSql(), sql.toString(), false);
    }

    public PreparedStatement createSelectByIdStatement(Table table, Column idColumn) {
        String sqlTableName = table.getlSql().getDialect().identifierJavaToSql(table.getTableName());
        String sqlColumnName = idColumn.getTable().getlSql().getDialect().identifierJavaToSql(idColumn.getColumnName());
        String sql = "select * from " + sqlTableName + " where " + sqlColumnName + "=?";
        return ConnectionUtils.prepareStatement(table.getlSql(), sql, false);
    }

    public PreparedStatement createDeleteByIdStatement(Table table) {
        Column idColumn = table.column(table.getPrimaryKeyColumn().get());
        String sqlTableName = table.getlSql().getDialect().identifierJavaToSql(table.getTableName());
        String sqlColumnName = idColumn.getTable().getlSql().getDialect().identifierJavaToSql(idColumn.getColumnName());
        StringBuilder sb = new StringBuilder();
        sb.append("delete from ");
        sb.append(sqlTableName);
        sb.append(" where ");
        sb.append(sqlColumnName);
        sb.append("=?;");
        return ConnectionUtils.prepareStatement(table.getlSql(), sb.toString(), false);
    }

    public PreparedStatement createCountForIdStatement(Table table) throws SQLException {
        Column idColumn = table.column(table.getPrimaryKeyColumn().get());
        String sqlTableName = table.getlSql().getDialect().identifierJavaToSql(table.getTableName());
        String sqlColumnName = idColumn.getTable().getlSql().getDialect().identifierJavaToSql(idColumn.getColumnName());
        String sql = "select count(" + sqlColumnName + ") from " + sqlTableName + " where " + sqlColumnName + "=?";
        return ConnectionUtils.prepareStatement(table.getlSql(), sql, false);
    }

    private List<String> createSqlColumnNames(final Table table, List<String> columns) {
        return Lists.transform(columns, new Function<String, String>() {
            @Override
            public String apply(String input) {
                return table.getlSql().getDialect().identifierJavaToSql(input);
            }
        });
    }

}
