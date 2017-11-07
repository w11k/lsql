package com.w11k.lsql;

import com.google.common.base.Optional;

import java.util.Map;

public interface TableLike {

    public final class NoPrimaryKeyColumn {
        private NoPrimaryKeyColumn() {
        }
    }

    Map<String, Column> getColumns();

    String getSchemaName();

    String getSchemaAndTableName();

    String getTableName();

    Optional<Class<?>> getPrimaryKeyType();
}
