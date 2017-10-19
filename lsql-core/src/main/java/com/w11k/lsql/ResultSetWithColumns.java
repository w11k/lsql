package com.w11k.lsql;

import com.google.common.collect.Maps;
import com.w11k.lsql.converter.Converter;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResultSetWithColumns {

    private final ResultSet resultSet;
    private final ResultSetMetaData metaData;
    private final Map<String, ResultSetColumn> columnsByLabel;
    private final List<ResultSetColumn> columns;
    private final LinkedHashMap<String, Converter> converters;

    public ResultSetWithColumns(ResultSet resultSet,
                                ResultSetMetaData metaData,
                                List<ResultSetColumn> columns,
                                LinkedHashMap<String, Converter> converters) {
        this.resultSet = resultSet;
        this.metaData = metaData;
        this.columns = columns;
        this.converters = converters;

        this.columnsByLabel = Maps.newLinkedHashMap();
        for (ResultSetColumn c : columns) {
            this.columnsByLabel.put(c.getName(), c);
        }
    }

    public ResultSet getResultSet() {
        return this.resultSet;
    }

    public Map<String, ResultSetColumn> getColumnsByLabel() {
        return this.columnsByLabel;
    }

    public ResultSetMetaData getMetaData() {
        return metaData;
    }

    public List<ResultSetColumn> getColumns() {
        return columns;
    }

    public Integer getColumnCount() {
        try {
            return this.metaData.getColumnCount();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public LinkedHashMap<String, Converter> getConverters() {
        return converters;
    }
}
