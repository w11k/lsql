package com.w11k.lsql.exceptions;

public class DatabaseAccessException extends RuntimeException {

    public DatabaseAccessException(Throwable cause) {
        super("Error while accessing the database", cause);
    }
}
