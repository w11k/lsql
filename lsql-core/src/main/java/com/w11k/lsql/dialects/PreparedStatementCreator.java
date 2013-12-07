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

    public PreparedStatement createRevisionQueryStatement(Table table, Object id) {
        String sqlTableName = table.getlSql().getDialect().identifierJavaToSql(table.getTableName());
        String revCol = getRevisionColumnSqlIdentifier(table);
        String sql = "SELECT " + revCol + " FROM " + sqlTableName + " WHERE ";
        sql += table.getPrimaryKeyColumn().get();
        sql += "=?";
        return ConnectionUtils.prepareStatement(table.getlSql(), sql, false);
    }

    public PreparedStatement createInsertStatement(final Table table, List<String> columns) {
        String sqlTableName = table.getlSql().getDialect().identifierJavaToSql(table.getTableName());
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(sqlTableName);
        sql.append("(");
        sql.append(Joiner.on(",").join(createSqlColumnNames(table, columns)));
        sql.append(")VALUES(");
        sql.append(Joiner.on(",").join(Collections.nCopies(columns.size(), "?")));
        sql.append(");");
        return ConnectionUtils.prepareStatement(table.getlSql(), sql.toString(), true);
    }

    public PreparedStatement createUpdateStatement(Table table, List<String> columns) {
        String sqlTableName = table.getlSql().getDialect().identifierJavaToSql(table.getTableName());
        String sql = "UPDATE " + sqlTableName;
        sql += " SET ";
        if (table.getRevisionColumn().isPresent()) {
            String col = getRevisionColumnSqlIdentifier(table);
            sql += col + "=" + col + "+1";
            if (columns.size() > 0) {
                sql += ",";
            }
        }
        sql += Joiner.on(",").join(Lists.transform(
                createSqlColumnNames(table, columns),
                new Function<String, Object>() {
                    @Nullable
                    @Override
                    public Object apply(@Nullable String input) {
                        return input + "=?";
                    }
                }));
        sql += " WHERE ";
        sql += table.getPrimaryKeyColumn().get();
        sql += "=?";
        if (table.getRevisionColumn().isPresent()) {
            sql += " AND ";
            String sqlRevisionColumn = getRevisionColumnSqlIdentifier(table);
            sql += sqlRevisionColumn + "=?";
        }
        sql += ";";
        return ConnectionUtils.prepareStatement(table.getlSql(), sql, false);
    }

    public PreparedStatement createSelectByIdStatement(Table table, Column idColumn) {
        String sqlTableName = table.getlSql().getDialect()
                .identifierJavaToSql(table.getTableName());
        String sqlColumnName = idColumn.getTable().get().getlSql().getDialect()
                .identifierJavaToSql(idColumn.getColumnName());
        String sql = "select * from " + sqlTableName + " where " + sqlColumnName + "=?";
        return ConnectionUtils.prepareStatement(table.getlSql(), sql, false);
    }

    public PreparedStatement createDeleteByIdStatement(Table table) {
        Column idColumn = table.column(table.getPrimaryKeyColumn().get());
        String sqlTableName = table.getlSql().getDialect().identifierJavaToSql(table.getTableName());
        String sqlIdName = idColumn.getTable().get().getlSql().getDialect().identifierJavaToSql(idColumn.getColumnName());

        String sql = "DELETE FROM ";
        sql += sqlTableName;
        sql += " WHERE ";
        sql += sqlIdName + "=?";
        if (table.getRevisionColumn().isPresent()) {
            String sqlRevisionName = getRevisionColumnSqlIdentifier(table);
            sql += " AND " + sqlRevisionName + "=?";
        }
        sql += ";";

        return ConnectionUtils.prepareStatement(table.getlSql(), sql, false);
    }

    public PreparedStatement createCountForIdStatement(Table table) throws SQLException {
        Column idColumn = table.column(table.getPrimaryKeyColumn().get());
        String sqlTableName = table.getlSql().getDialect()
                .identifierJavaToSql(table.getTableName());
        String sqlColumnName = idColumn.getTable().get().getlSql().getDialect()
                .identifierJavaToSql(idColumn.getColumnName());
        String sql = "select count(" + sqlColumnName + ") from " + sqlTableName + " where " +
                sqlColumnName + "=?";
        return ConnectionUtils.prepareStatement(table.getlSql(), sql, false);
    }

    private String getRevisionColumnSqlIdentifier(Table table) {
        return table.getlSql().getDialect().identifierJavaToSql(table.getRevisionColumn().get().getColumnName());
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
