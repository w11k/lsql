package com.w11k.lsql.query;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.w11k.lsql.LSql;
import com.w11k.lsql.ResultSetColumn;
import com.w11k.lsql.ResultSetWithColumns;
import com.w11k.lsql.converter.Converter;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.subjects.Subject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public abstract class AbstractQuery<T> {

    private final LSql lSql;

    private final PreparedStatement preparedStatement;

    private Map<String, Converter> converters = Maps.newHashMap();

    private boolean ignoreDuplicateColumns = false;

    AbstractQuery(LSql lSql, PreparedStatement preparedStatement) {
        this.lSql = lSql;
        this.preparedStatement = preparedStatement;
    }

    public LSql getlSql() {
        return lSql;
    }

    public AbstractQuery ignoreDuplicateColumns() {
        ignoreDuplicateColumns = true;
        return this;
    }

    public PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }

    public Map<String, Converter> getConverters() {
        return converters;
    }

    public AbstractQuery setConverters(Map<String, Converter> converters) {
        this.converters = converters;
        return this;
    }

    public AbstractQuery addConverter(String columnName, Converter converter) {
        this.converters.put(columnName, converter);
        return this;
    }

    public List<T> toList() {
        return rx().toList().toBlocking().first();

    }

    /**
     * Executes the query and returns the first row in the result set. Return absent() if the result set is empty.
     */
    public Optional<T> first() {
        List<T> list = rx().take(1).toList().toBlocking().first();
        if (list.isEmpty()) {
            return Optional.absent();
        } else {
            return Optional.of(list.get(0));
        }
    }

    /**
     * Turns this query into an Observable.  Each subscription will trigger the underlying database operation.
     *
     * @return the Observable
     */
    public Observable<T> rx() {
        return rxResultSet().map(new Func1<ResultSetWithColumns, T>() {
            @Override
            public T call(ResultSetWithColumns resultSetWithColumns) {
                return extractEntity(resultSetWithColumns);
            }
        });
    }

    /**
     * Turns this query into an Observable. Each subscription will trigger the underlying database operation.
     * <p/>
     * This is a low-level API to directly work with the JDBC ResultSet.
     *
     * @return the Observable
     */
    public Observable<ResultSetWithColumns> rxResultSet() {

        return Subject.create(new Observable.OnSubscribe<ResultSetWithColumns>() {
            @Override
            public void call(Subscriber<? super ResultSetWithColumns> subscriber) {
                try {
                    ResultSet resultSet = AbstractQuery.this.preparedStatement.executeQuery();
                    ResultSetMetaData metaData = resultSet.getMetaData();

                    // used to find duplicates
                    // or unused converter
                    Set<String> processedColumnLabels = Sets.newLinkedHashSet();

                    List<ResultSetColumn> resultSetColumns = Lists.newLinkedList();
                    LinkedHashMap<String, Converter> converters = Maps.newLinkedHashMap();

                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        String columnLabel = AbstractQuery.this.lSql.identifierSqlToJava(metaData.getColumnLabel(i));

                        // check duplicates
                        if (!AbstractQuery.this.ignoreDuplicateColumns && processedColumnLabels.contains(columnLabel)) {
                            throw new IllegalStateException("Duplicate column '" + columnLabel + "' in query.");
                        }
                        processedColumnLabels.add(columnLabel);

                        Converter converter = getConverterForResultSetColumn(metaData, i, columnLabel, false);

                        resultSetColumns.add(new ResultSetColumn(i, columnLabel, converter));
                        converters.put(columnLabel, converter);
                    }

                    // Check for unused converters
                    for (String converterFor : AbstractQuery.this.converters.keySet()) {
                        if (!processedColumnLabels.contains(converterFor)) {
                            throw new IllegalArgumentException(
                                    "unused converter for column '" + converterFor + "'");
                        }
                    }

                    ResultSetWithColumns resultSetWithColumns =
                            new ResultSetWithColumns(resultSet, metaData, resultSetColumns);

                    checkConformity(converters);

                    while (resultSet.next() && !subscriber.isUnsubscribed()) {
                        subscriber.onNext(resultSetWithColumns);
                    }
                    resultSet.close();
                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Converter getConverterForResultSetColumn(ResultSetMetaData metaData,
                                                    int position,
                                                    String columnLabel,
                                                    boolean getConverterBySqlType)
            throws SQLException {

        // Check for user provided Converter
        if (converters.containsKey(columnLabel)) {
            Converter converter = converters.get(columnLabel);
            if (converter != null) {
                return converter;
            }
        }

        // Determine source table and column from ResultSet
        String tableName = lSql.getDialect().getTableNameFromResultSetMetaData(metaData, position);
        String columnName = lSql.getDialect().getColumnNameFromResultSetMetaData(metaData, position);
        if (tableName != null
                && tableName.length() > 0
                && columnName != null
                && columnName.length() > 0) {
            return lSql.table(tableName).column(columnName).getConverter();
        }

        // Check if the user registered null as a Converter to use a type-based Converter
        // or if the default converter was allowed
        if (getConverterBySqlType
                || (converters.containsKey(columnLabel) && converters.get(columnLabel) == null)) {

            int columnSqlType = metaData.getColumnType(position);
            return lSql.getDialect().getConverterRegistry().getConverterForSqlType(columnSqlType);
        }

        // Error
        String msg = "Unable to determine a Converter instance for column '" + columnLabel + "'. ";
        msg += "Register a converter with Query#addConverter() / Query#setConverters().";
        throw new IllegalStateException(msg);
    }

    protected abstract T createEntity();

    protected abstract void checkConformity(Map<String, Converter> converters);

    protected abstract void setValue(T entity, String name, Object value);

    private T extractEntity(ResultSetWithColumns resultSetWithColumns) {
        ResultSet resultSet = resultSetWithColumns.getResultSet();
        Collection<ResultSetColumn> columnList = resultSetWithColumns.getColumnsByLabel().values();
        T entity = createEntity();
        for (ResultSetColumn column : columnList) {
            try {
                setValue(
                        entity,
                        column.getName(),
                        column.getConverter().getValueFromResultSet(lSql, resultSet, column.getPosition()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return entity;
    }
}
