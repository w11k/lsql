package com.w11k.lsql.exceptions;

import java.sql.SQLException;

public class InsertException extends RuntimeException {

    public InsertException(SQLException cause, String sql) {
        super("Error while inserting: " + sql, cause);
    }

}
