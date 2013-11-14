package com.w11k.lsql.validation;

public class KeyError extends AbstractValidationError {

    private final transient String tableName;

    private final String columnName;

    public KeyError(String tableName, String columnName) {
        this.tableName = tableName;
        this.columnName = columnName;
    }

    @Override
    public void throwError() {
        throw new IllegalArgumentException(
                "Column '" + columnName + "' does not exist in table '" + tableName + "'.");
    }

}
