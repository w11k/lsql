package com.w11k.lsql.validation;

public class StringTooLongError extends AbstractValidationError {

    private transient final String tableName;

    private final String columnName;

    private final int maxSize;

    private final int gotSize;

    public StringTooLongError(String tableName, String columnName, int maxSize, int gotSize) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.maxSize = maxSize;
        this.gotSize = gotSize;
    }

    @Override
    public void throwError() {
        throw new IllegalArgumentException("Table '" + tableName + "', column '" + columnName + "': " +
                "Max String length is " + maxSize + ", got " + gotSize + ".");
    }

}
