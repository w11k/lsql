package com.w11k.lsql.exceptions;

public class QueryException extends RuntimeException {

    public QueryException(Exception cause) {
        super("Error while accessing the database", cause);
    }
}
