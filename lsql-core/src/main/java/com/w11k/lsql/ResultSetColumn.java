package com.w11k.lsql;

import com.w11k.lsql.converter.Converter;

import java.util.regex.Pattern;

public class ResultSetColumn {

    private final int position;

    private final String name;

    private final Converter converter;

    private boolean markerColumn = false;

    private final Pattern pattern;

    public ResultSetColumn(int position, String name, Converter converter) {
        this.position = position;
        this.name = name;
        this.converter = converter;
        this.pattern = null;
    }

    public ResultSetColumn(int position, String name, Pattern pattern) {
        this.position = position;
        this.name = name;
        this.converter = null;
        this.markerColumn = true;
        this.pattern = pattern;
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

    public boolean isMarkerColumn() {
        return markerColumn;
    }

    public void setMarkerColumn(boolean markerColumn) {
        this.markerColumn = markerColumn;
    }

    public Pattern getPattern() {
        return pattern;
    }
}
