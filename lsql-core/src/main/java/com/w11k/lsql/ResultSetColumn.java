package com.w11k.lsql;

public class ResultSetColumn<P extends Row> {

    private final int position;

    private final String name;

    private final Column<P> column;

    public static <A extends Row> ResultSetColumn<A> create(int position, String name, Column<A> column) {
        return new ResultSetColumn<A>(position, name, column);
    }

    public ResultSetColumn(int position, String name, Column<P> column) {
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

    public Column<P> getColumn() {
        return column;
    }

}
