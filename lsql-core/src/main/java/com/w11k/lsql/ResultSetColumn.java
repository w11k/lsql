package com.w11k.lsql;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.w11k.lsql.converter.Converter;

public class ResultSetColumn {

    private final int position;

    private final String name;

    private final Converter converter;

    // Tree Marker columns

    private final boolean markerColumn;

    private final int level;

    private final String fullPath;

    private final String field;

    public ResultSetColumn(int position, String name, Converter converter) {
        this.position = position;
        this.name = name;
        this.converter = converter;
        this.markerColumn = false;
        this.level = -1;
        this.fullPath = null;
        this.field = null;
    }

    public ResultSetColumn(int position, String name, String fullPath) {
        fullPath = fullPath.trim();

        this.position = position;
        this.name = name;
        this.converter = null;
        this.markerColumn = true;

        this.level = Iterables.size(Splitter.on("/").omitEmptyStrings().split(fullPath));
        this.fullPath = fullPath;

        int lastSlash = fullPath.lastIndexOf('/') + 1;
        this.field = fullPath.substring(lastSlash).trim();
    }

    public int getPosition() {
        return this.position;
    }

    public String getName() {
        return this.name;
    }

    public Converter getConverter() {
        return this.converter;
    }

    public boolean isMarkerColumn() {
        return this.markerColumn;
    }

    public int getLevel() {
        return this.level;
    }

    public String getField() {
        return this.field;
    }

    public String getFullPath() {
        return this.fullPath;
    }
}
