package com.w11k.lsql;

import java.util.Map;

public interface ColumnsContainer {

    Map<String, Column> getColumns();

    String getSchemaName();

    String getSchemaAndTableName();

    String getTableName();
}
