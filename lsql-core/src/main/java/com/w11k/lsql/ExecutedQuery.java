package com.w11k.lsql;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.exceptions.QueryException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExecutedQuery {

    private final LSql lSql;

    private final ResultSet resultSet;

    private final List<ResultSetColumn> resultSetColumns;

    private final List<Row> rows;

    public ExecutedQuery(LSql lSql, ResultSet resultSet, Map<String, Converter> queryConverters) {
        this.lSql = lSql;
        this.resultSet = resultSet;
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();

            // used to find duplicates
            Set<String> processedColumnLabels = Sets.newLinkedHashSet();

            // queryConverters for columns
            this.resultSetColumns = Lists.newLinkedList();

            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnLabel = lSql.getDialect().identifierSqlToJava(metaData.getColumnLabel(i));

                // check dublicates
                if (processedColumnLabels.contains(columnLabel)) {
                    throw new IllegalStateException("Dublicate column '" + columnLabel + "' in query.");
                }
                processedColumnLabels.add(columnLabel);

                Converter converter = queryConverters.containsKey(columnLabel)
                        ? queryConverters.get(columnLabel)
                        : getConverter(metaData, i);
                resultSetColumns.add(new ResultSetColumn(i, columnLabel, converter));
            }

            this.rows = Lists.newLinkedList();
            while (resultSet.next()) {
                this.rows.add(extractRow(resultSet, resultSetColumns));
            }
        } catch (SQLException e) {
            throw new QueryException(e);
        }

    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public List<ResultSetColumn> getResultSetColumns() {
        return resultSetColumns;
    }

    public Rows rows() {
        return new Rows(this.rows);
    }

    private Row extractRow(ResultSet resultSet, List<ResultSetColumn> columnConverters) throws SQLException {
        Row row = new Row();
        for (ResultSetColumn column : columnConverters) {
            row.put(column.getName(),
                    column.getConverter().getValueFromResultSet(lSql, resultSet, column.getPosition()));
        }
        return row;
    }

    private Converter getConverter(ResultSetMetaData metaData, int position) throws SQLException {
        int columnSqlType = metaData.getColumnType(position);
        return lSql.getDialect().getConverterRegistry().getConverterForSqlType(columnSqlType);
    }

}
