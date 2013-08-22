package com.w11k.lsql.exceptions;

public class UpdateException extends DatabaseAccessException {

    public UpdateException(Exception cause) {
        super(cause);
    }

    public UpdateException(String msg) {
        super(msg);
    }

    public UpdateException(String msg, Exception cause) {
        super(msg, cause);
    }

}
