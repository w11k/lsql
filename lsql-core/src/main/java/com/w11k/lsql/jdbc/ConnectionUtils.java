package com.w11k.lsql.jdbc;

import com.w11k.lsql.LSql;
import com.w11k.lsql.exceptions.DatabaseAccessException;

import java.sql.Connection;

public class ConnectionUtils {

    public static Connection getConnection(LSql lSql) {
        try {
            return lSql.getConnectionProvider().call();
        } catch (Exception e) {
            throw new DatabaseAccessException(e);
        }
    }

}
