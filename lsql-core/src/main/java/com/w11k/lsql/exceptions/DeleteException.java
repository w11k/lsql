package com.w11k.lsql.exceptions;

public class DeleteException extends DatabaseAccessException {

    public DeleteException(Exception cause) {
        super(cause);
    }

    public DeleteException(String msg) {
        super(msg);
    }

    public DeleteException(String msg, Exception cause) {
        super(msg, cause);
    }

}
