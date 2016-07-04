package com.w11k.lsql;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.w11k.lsql.typemapper.TypeMapper;

public class ResultSetColumn {

    private final int position;

    private final String name;

    private final TypeMapper typeMapper;

    // Tree Marker columns

    private final boolean markerColumn;

    private final int level;

    private final String fullPath;

    private final String field;

    public ResultSetColumn(int position, String name, TypeMapper typeMapper) {
        this.position = position;
        this.name = name;
        this.typeMapper = typeMapper;
        this.markerColumn = false;
        this.level = -1;
        this.fullPath = null;
        this.field = null;
    }

    public ResultSetColumn(int position, String name, String fullPath) {
        fullPath = fullPath.trim();

        this.position = position;
        this.name = name;
        this.typeMapper = null;
        this.markerColumn = true;

        this.level = Iterables.size(Splitter.on("/").omitEmptyStrings().split(fullPath));
        this.fullPath = fullPath;

        int lastSlash = fullPath.lastIndexOf('/') + 1;
        this.field = fullPath.substring(lastSlash).trim();
    }

    public int getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public TypeMapper getTypeMapper() {
        return typeMapper;
    }

    public boolean isMarkerColumn() {
        return markerColumn;
    }

    public int getLevel() {
        return level;
    }

    public String getField() {
        return field;
    }

    public String getFullPath() {
        return fullPath;
    }
}
