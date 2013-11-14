package com.w11k.lsql.validation;

public class TypeError extends AbstractValidationError {

    private transient final String tableName;

    private final String columnName;

    private final String expectedType;

    private final String suppliedType;

    public TypeError(String tableName, String columnName, String expectedType, String suppliedType) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.expectedType = expectedType;
        this.suppliedType = suppliedType;
    }

    @Override
    public void throwError() {
        throw new IllegalArgumentException("Column '" + columnName + "' in table '" +
                tableName + "' requires a value of type '" + expectedType +
                "', got '" + suppliedType + "' instead.");
    }

}
