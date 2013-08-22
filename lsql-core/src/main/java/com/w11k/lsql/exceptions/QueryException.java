package com.w11k.lsql.exceptions;

public class QueryException extends DatabaseAccessException {


    public QueryException(Exception cause) {
        super(cause);
    }

    public QueryException(String msg) {
        super(msg);
    }

    public QueryException(String msg, Exception cause) {
        super(msg, cause);
    }
}
