package com.w11k.lsql.exceptions;

public class InsertException extends RuntimeException {

    public InsertException(Exception cause, String message) {
        super(message, cause);
    }

    public InsertException(String message) {
        super(message);
    }

}
