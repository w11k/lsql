package com.w11k.lsql;

import javax.annotation.Nullable;

public class LinkedRow extends Row {

    private final Table table;

    public LinkedRow(Table table) {
        this.table = table;
    }

    @Override
    public Object get(@Nullable Object key) {
        return super.get(key);
    }

}
