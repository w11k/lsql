package com.w11k.lsql;

import com.google.common.base.Joiner;

import java.util.List;

public class SqlStringUtils {

    public static String createInsertString(LSql lSql, String tableName, List<String> columns) {
        String sqlString = "insert into `" + lSql.identifierJavaToSql(tableName) + "`";
        sqlString += "(`" + Joiner.on("`,`").join(columns) + "`)";
        sqlString += "values(";
        for (int i = 0; i < columns.size() - 1; i++) {
            sqlString += "?,";
        }
        sqlString += "?);";
        return sqlString;
    }

}
