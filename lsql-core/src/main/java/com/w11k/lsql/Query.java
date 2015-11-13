package com.w11k.lsql;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.jdbc.ConnectionUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Optional.of;

public class Query {

    private final LSql lSql;

    private final PreparedStatement preparedStatement;

    private Map<String, Converter> converters = Maps.newHashMap();

    public Query(LSql lSql, PreparedStatement preparedStatement) {
        this.lSql = lSql;
        this.preparedStatement = preparedStatement;
    }

    public Query(LSql lSql, String sql) {
        this(lSql, ConnectionUtils.prepareStatement(lSql, sql, false));
    }

    public LSql getlSql() {
        return lSql;
    }

    public Query addConverter(String columnName, Converter converter) {
        this.converters.put(columnName, converter);
        return this;
    }

    public Query setConverters(Map<String, Converter> converters) {
        this.converters = converters;
        return this;
    }

    public List<Row> toList() {
        final List<Row> rows = Lists.newLinkedList();

        execute(new Function<Row, Object>() {
            public Object apply(Row input) {
                rows.add(input);
                return null;
            }
        }, false);

        return rows;
    }

    public void forEach(final RowConsumer rowConsumer) {
        execute(new Function<Row, Object>() {
            public Object apply(Row input) {
                rowConsumer.each(input);
                return null;
            }
        }, false);
    }

    /**
     * Executes the query and calls rowHandler for every row in the result set. Returns a list with all the results of the rowHandler.
     */
    public <A> List<A> map(final Function<Row, A> rowHandler) {
        final List<A> results = Lists.newLinkedList();
        execute(new Function<Row, Object>() {
            public Object apply(Row input) {
                A result = rowHandler.apply(input);
                results.add(result);
                return null;
            }
        }, false);
        return results;
    }

    /**
     * Executes the query and calls rowHandler for every row in the result set. Returns a list with all the non-null results of the rowHandler. null values returned by the rowHandler are ignored and are not added to the list.
     */
    public <A> List<A> flatMap(final Function<Row, A> rowHandler) {
        final List<A> results = Lists.newLinkedList();
        execute(new Function<Row, Object>() {
            public Object apply(Row input) {
                A result = rowHandler.apply(input);
                if (result != null) {
                    results.add(result);
                }
                return null;
            }
        }, false);
        return results;
    }

    /**
     * Executes the query and calls predicate for every row in the result set. Returns a list with all rows where the predicate returned true.
     */
    public List<Row> filter(final Predicate<Row> predicate) {
        final List<Row> results = Lists.newLinkedList();
        execute(new Function<Row, Object>() {
            public Object apply(Row input) {
                if (predicate.apply(input)) {
                    results.add(input);
                }
                return null;
            }
        }, false);
        return results;
    }

    /**
     * Executes the query and returns the first row in the result set. Return absent() if the result set is empty.
     */
    public Optional<Row> firstRow() {
        final List<Row> results = Lists.newLinkedList();
        execute(new Function<Row, Object>() {
            public Object apply(Row input) {
                results.add(input);
                return null;
            }
        }, true);

        return results.size() > 0 ? of(results.get(0)) : Optional.<Row>absent();
    }

    private <A> void execute(Function<Row, A> rowHandler, boolean onlyFirst) {
        try {
            ResultSet resultSet = this.preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();

            // used to find duplicates
            Set<String> processedColumnLabels = Sets.newLinkedHashSet();

            // queryConverters for columns
            List<ResultSetColumn> resultSetColumns = Lists.newLinkedList();

            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnLabel = lSql.getDialect().identifierSqlToJava(metaData.getColumnLabel(i));

                // check dublicates
                if (processedColumnLabels.contains(columnLabel)) {
                    throw new IllegalStateException("Dublicate column '" + columnLabel + "' in query.");
                }
                processedColumnLabels.add(columnLabel);

                Converter converter = this.converters.containsKey(columnLabel)
                        ? this.converters.get(columnLabel)
                        : getConverter(metaData, i);
                resultSetColumns.add(new ResultSetColumn(i, columnLabel, converter));
            }

            while (resultSet.next()) {
                Row row = extractRow(resultSet, resultSetColumns);
                rowHandler.apply(row);
                if (onlyFirst) {
                    return;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

