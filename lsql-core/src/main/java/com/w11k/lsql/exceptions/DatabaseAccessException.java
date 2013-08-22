package com.w11k.lsql.exceptions;

public class DatabaseAccessException extends RuntimeException {

    public DatabaseAccessException(Exception cause) {
        super(cause);
    }

    public DatabaseAccessException(String msg) {
        super(msg);
    }

    public DatabaseAccessException(String msg, Exception cause) {
        super(msg, cause);
    }

}
