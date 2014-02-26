package com.w11k.lsql;

import java.util.Map;

public class QueriedRow extends Row {

    private final Map<String, ResultSetColumn<?>> resultSetColumns;

    public QueriedRow(Map<String, ResultSetColumn<?>> resultSetColumns) {
        this.resultSetColumns = resultSetColumns;
    }

    public Map<String, ResultSetColumn<?>> getResultSetColumns() {
        return resultSetColumns;
    }

}
