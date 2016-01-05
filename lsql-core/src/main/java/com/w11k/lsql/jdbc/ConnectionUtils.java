package com.w11k.lsql.jdbc;

import com.w11k.lsql.LSql;
import com.w11k.lsql.exceptions.DatabaseAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class ConnectionUtils {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionUtils.class);

    public static Connection getConnection(LSql lSql) {
        try {
            logger.trace("Obtaining connection");
            return lSql.getConnectionProvider().call();
        } catch (Exception e) {
            throw new DatabaseAccessException(e);
        }
    }

}
