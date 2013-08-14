package com.w11k.lsql;

import com.google.common.base.Joiner;

import java.util.List;

public class SqlStringUtils {

    public static String createInsertString(Table table, List<String> sqlColumnNames) {
        String sqlTableName = table.getlSql().identifierJavaToSql(table.getTableName());
        String sqlString = "insert into `" + sqlTableName + "`";
        sqlString += "(`" + Joiner.on("`,`").join(sqlColumnNames) + "`)";
        sqlString += "values(";
        for (int i = 0; i < sqlColumnNames.size() - 1; i++) {
            sqlString += "?,";
        }
        sqlString += "?);";
        return sqlString;
    }

    public static String createSelectByIdString(Table table, Column idColumn) {
        String sqlTableName = table.getlSql().identifierJavaToSql(table.getTableName());
        String sqlColumnName = idColumn.getTable().getlSql().identifierJavaToSql(idColumn.getColumnName());
        return "select * from `" + sqlTableName + "` where `" + sqlColumnName + "`=?";
    }

}
