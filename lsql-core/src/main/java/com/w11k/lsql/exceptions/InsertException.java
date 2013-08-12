package com.w11k.lsql.exceptions;

public class InsertException extends RuntimeException {

    public InsertException(Exception cause, String sql) {
        super("Error while inserting: " + sql, cause);
    }

}
