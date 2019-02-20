package com.w11k.lsql.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class JdbcTestUtils {

    @FunctionalInterface
    public static interface ResultSetHandler {
        boolean apply(ResultSet resultSet) throws Exception;
    }

    public static void queryForEach(Connection connection, String query, ResultSetHandler handler) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            boolean called = false;
            while (resultSet.next()) {
                called = true;
                boolean cont = handler.apply(resultSet);
                if (!cont) {
                    return;
                }
            }
            if (!called) {
                throw new RuntimeException("empty ResultSet");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    //noinspection ThrowFromFinallyBlock
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
