package com.w11k.lsql;

import java.sql.ResultSet;
import java.util.List;

public class ResultSetWithColumns {


    private final ResultSet resultSet;
    private final List<ResultSetColumn> resultSetColumns;

    public ResultSetWithColumns(ResultSet resultSet, List<ResultSetColumn> resultSetColumns) {
        this.resultSet = resultSet;
        this.resultSetColumns = resultSetColumns;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public List<ResultSetColumn> getResultSetColumns() {
        return resultSetColumns;
    }
}
