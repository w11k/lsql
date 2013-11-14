package com.w11k.lsql.validation;

public abstract class AbstractValidationError {

    // Useful on client side
    @SuppressWarnings("UnusedDeclaration")
    public String error = getClass().getSimpleName();

    public abstract void throwError();

}
