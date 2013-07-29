package com.w11k.lsql.exceptions;

import java.sql.SQLException;

public class SelectException extends RuntimeException {

    public SelectException(SQLException cause) {
        super("Error while accessing the database", cause);
    }
}
