package com.w11k.lsql;

import java.sql.ResultSet;
import java.util.Map;

public class ResultSetWithColumns {

    private final ResultSet resultSet;
    private final Map<String, ResultSetColumn> resultSetColumns;

    public ResultSetWithColumns(ResultSet resultSet, Map<String, ResultSetColumn> resultSetColumns) {
        this.resultSet = resultSet;
        this.resultSetColumns = resultSetColumns;
    }

    public ResultSet getResultSet() {
        return this.resultSet;
    }

    public Map<String, ResultSetColumn> getResultSetColumns() {
        return this.resultSetColumns;
    }
}
