package com.w11k.lsql.exceptions;

public class SelectException extends RuntimeException {

    public SelectException(Exception cause) {
        super("Error while accessing the database", cause);
    }
}
