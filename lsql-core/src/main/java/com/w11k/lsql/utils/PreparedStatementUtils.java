package com.w11k.lsql.utils;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.relational.Column;
import com.w11k.lsql.relational.Row;
import com.w11k.lsql.relational.Table;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PreparedStatementUtils {

    static class ColumnInStatement {
        String sqlColumnName;

        Object value;

        Converter converter;
    }

    public static PreparedStatement createInsertStatement(Table table, Row row) {
        List<ColumnInStatement> columns = createColumnList(table, row);

        String sqlTableName = table.getlSql().getDialect().identifierJavaToSql(table.getTableName());
        StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(sqlTableName);
        sql.append("(");

        sql.append(Joiner.on(",").join(Lists.transform(columns, new Function<ColumnInStatement, Object>() {
            @Nullable
            @Override
            public Object apply(ColumnInStatement input) {
                return input.sqlColumnName;
            }
        })));

        sql.append(")values(");
        sql.append(Joiner.on(",").join(Collections.nCopies(columns.size(), "?")));
        sql.append(")");

        PreparedStatement ps = ConnectionUtils.prepareStatement(table.getlSql(), sql.toString(), true);
        return setValuesInPreparedStatement(columns, ps);
    }

    public static PreparedStatement createUpdateStatement(Table table, Row row) {
        List<ColumnInStatement> columns = createColumnList(table, row);

        String sqlTableName = table.getlSql().getDialect().identifierJavaToSql(table.getTableName());
        StringBuilder sql = new StringBuilder();
        sql.append("update ").append(sqlTableName);
        sql.append(" set ");

        sql.append(Joiner.on(",").join(Lists.transform(columns, new Function<ColumnInStatement, Object>() {
            @Nullable
            @Override
            public Object apply(ColumnInStatement input) {
                return input.sqlColumnName + "=?";
            }
        })));


        sql.append(" WHERE ");
        sql.append(table.getPrimaryKeyColumn().get()).append("=?;");

        PreparedStatement ps = ConnectionUtils.prepareStatement(table.getlSql(), sql.toString(), false);
        try {
            table.column(table.getPrimaryKeyColumn().get()).getColumnConverter()
                    .setValueInStatement(ps, columns.size() + 1, row.get(table.getPrimaryKeyColumn().get()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return setValuesInPreparedStatement(columns, ps);
    }

    public static String createSelectByIdString(Table table, Column idColumn) {
        String sqlTableName = table.getlSql().getDialect().identifierJavaToSql(table.getTableName());
        String sqlColumnName = idColumn.getTable().getlSql().getDialect().identifierJavaToSql(idColumn.getColumnName());
        return "select * from " + sqlTableName + " where " + sqlColumnName + "=?";
    }

    public static PreparedStatement createDeleteByIdString(Table table) {
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

    public static PreparedStatement createCountForIdStatement(Table table, Object id) throws SQLException {
        Column idColumn = table.column(table.getPrimaryKeyColumn().get());
        String sqlTableName = table.getlSql().getDialect().identifierJavaToSql(table.getTableName());
        String sqlColumnName = idColumn.getTable().getlSql().getDialect().identifierJavaToSql(idColumn.getColumnName());
        String sql = "select count(" + sqlColumnName + ") from " + sqlTableName + " where " + sqlColumnName + "=?";
        PreparedStatement ps = ConnectionUtils.prepareStatement(table.getlSql(), sql, false);
        ps.setObject(1, id);
        return ps;
    }

    private static PreparedStatement setValuesInPreparedStatement(List<ColumnInStatement> columns, PreparedStatement ps) {
        try {
            for (int i = 0; i < columns.size(); i++) {
                Converter converter = columns.get(i).converter;
                converter.setValueInStatement(ps, i + 1, columns.get(i).value);
            }
            return ps;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<ColumnInStatement> createColumnList(Table table, Row row) {
        List<ColumnInStatement> columns = Lists.newLinkedList();
        for (Map.Entry<String, Object> keyValue : row.entrySet()) {
            String key = keyValue.getKey();
            Object value = keyValue.getValue();
            Converter converter = table.column(key).getColumnConverter();
            String sqlColumnName = table.getlSql().getDialect().identifierJavaToSql(key);

            ColumnInStatement cis = new ColumnInStatement();
            cis.sqlColumnName = sqlColumnName;
            cis.value = value;
            cis.converter = converter;

            columns.add(cis);
        }
        return columns;
    }
}
