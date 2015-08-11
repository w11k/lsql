package com.w11k.lsql;

import com.w11k.lsql.converter.Converter;

public class ResultSetColumn {

    private final int position;

    private final String name;

    private final Converter converter;

    public ResultSetColumn(int position, String name, Converter converter) {
        this.position = position;
        this.name = name;
        this.converter = converter;
    }

    public int getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public Converter getConverter() {
        return converter;
    }
}
