package com.w11k.lsql;

public class WrongTypeException extends IllegalArgumentException {
    public WrongTypeException(String s) {
        super(s);
    }
}
