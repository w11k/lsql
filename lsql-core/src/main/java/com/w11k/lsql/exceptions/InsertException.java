package com.w11k.lsql.exceptions;

public class InsertException extends DatabaseAccessException {

    public InsertException(Exception cause) {
        super(cause);
    }

    public InsertException(String msg) {
        super(msg);
    }

    public InsertException(String msg, Exception cause) {
        super(msg, cause);
    }

}
