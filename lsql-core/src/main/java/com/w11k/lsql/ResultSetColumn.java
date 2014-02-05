package com.w11k.lsql;

public class ResultSetColumn {

    private final int position;

    private final String name;

    private final Column column;

    public ResultSetColumn(int position, String name, Column column) {
        this.position = position;
        this.name = name;
        this.column = column;
    }

    public int getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public Column getColumn() {
        return column;
    }

}
