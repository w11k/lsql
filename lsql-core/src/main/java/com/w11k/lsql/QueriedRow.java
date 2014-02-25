package com.w11k.lsql;

import java.util.List;
import java.util.Map;

public class QueriedRow extends Row {

    private final Map<String, ResultSetColumn<?>> resultSetColumns;

    public QueriedRow(Map<String, ResultSetColumn<?>> resultSetColumns) {
        this.resultSetColumns = resultSetColumns;
    }

    public Map<String, ResultSetColumn<?>> getResultSetColumns() {
        return resultSetColumns;
    }

    public List<QueriedRow> getJoined(String key) {
        return getListOf(QueriedRow.class, key);
    }

}
