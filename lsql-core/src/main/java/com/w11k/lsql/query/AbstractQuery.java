package com.w11k.lsql.query;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.w11k.lsql.*;
import com.w11k.lsql.converter.Converter;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.subjects.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public abstract class AbstractQuery<T> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LSql lSql;

    private final PreparedStatement preparedStatement;

    private Map<String, Converter> converters = Maps.newHashMap();

    private boolean ignoreDuplicateColumns = false;

    public AbstractQuery(LSql lSql, PreparedStatement preparedStatement/*, Map<String, Converter> outConverters*/) {
        this.lSql = lSql;
        this.preparedStatement = preparedStatement;

//        if (outConverters != null) {
//            this.setConverters(outConverters);
//        }

        Integer defaultQueryTimeoutInSeconds = lSql.getConfig().getDefaultQueryTimeoutInSeconds();
        if (defaultQueryTimeoutInSeconds != null) {
            try {
                preparedStatement.setQueryTimeout(defaultQueryTimeoutInSeconds);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public LSql getlSql() {
        return lSql;
    }

    public AbstractQuery<T> ignoreDuplicateColumns() {
        ignoreDuplicateColumns = true;
        return this;
    }

    public PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }

    public Map<String, Converter> getConverters() {
        return converters;
    }

    public AbstractQuery<T> setConverters(Map<String, Converter> converters) {
        this.converters = converters;
        return this;
    }

    public AbstractQuery<T> addConverter(String columnName, Converter converter) {
        this.converters.put(columnName, converter);
        return this;
    }

    public AbstractQuery<T> setQueryTimeout(int seconds) {
        try {
            this.preparedStatement.setQueryTimeout(seconds);
            return this;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<T> toList() {
        return rx().toList().blockingGet();
    }

    public <R> List<R> toList(Function<T, R> mapper) {
        return rx().map(mapper).toList().blockingGet();
    }

    public abstract List<T> toTree();

    /**
     * Executes the query and returns the first row in the result set. Return absent() if the result set is empty.
     */
    public Optional<T> first() {
        List<T> list = rx().take(1).toList().blockingGet();
        if (list.isEmpty()) {
            return Optional.absent();
        } else {
            return of(list.get(0));
        }
    }

    public <R> Optional<R> first(final Function<T, R> mapper) {
        return this.first().transform(t -> {
            try {
                return mapper.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    /**
     * Turns this query into an Observable.  Each subscription will trigger the underlying database operation.
     *
     * @return the Observable
     */
    public Observable<T> rx() {
        return rxResultSet().map(this::extractEntity);
    }

    /**
     * Turns this query into an Observable. Each subscription will trigger the underlying database operation.
     * <p/>
     * This is a low-level API to directly work with the JDBC ResultSet.
     *
     * @return the Observable
     */
    public Observable<ResultSetWithColumns> rxResultSet() {

        return Subject.create(emitter -> {
            try {
                ResultSetWithColumns resultSetWithColumns = createResultSetWithColumns();
//                checkConformity(resultSetWithColumns.getConverters());

                while (resultSetWithColumns.getResultSet().next() && !emitter.isDisposed()) {
                    emitter.onNext(resultSetWithColumns);
                }
                resultSetWithColumns.getResultSet().close();
                emitter.onComplete();
            } catch (SQLException e) {
                emitter.onError(e);
            }
        });
    }

    public ResultSetWithColumns createResultSetWithColumns() {
        try {
            ResultSet resultSet = this.preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();

            // used to find duplicates
            // or unused converter
            Set<String> processedColumnLabels = Sets.newLinkedHashSet();

            List<ResultSetColumn> resultSetColumns = Lists.newLinkedList();
            LinkedHashMap<String, Converter> converters = Maps.newLinkedHashMap();

            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                Converter converter = null;
                String columnLabel;
                String resultSetColumnName = metaData.getColumnLabel(i);

                // f as "f: type"
                int idxOfColon = resultSetColumnName.indexOf(':');
                if (idxOfColon == -1) {
                    columnLabel = AbstractQuery.this.lSql.convertExternalSqlToInternalSql(resultSetColumnName);
                } else {
                    columnLabel = resultSetColumnName.substring(0, idxOfColon).trim();
                    String typeAnnotation = resultSetColumnName.substring(idxOfColon + 1).trim();
                    if (typeAnnotation.length() > 0) {
                        converter = this.lSql.getConverterForAlias(typeAnnotation);
                    }
                }

                // check duplicates
                if (!this.ignoreDuplicateColumns && processedColumnLabels.contains(columnLabel.toLowerCase())) {
                    throw new IllegalStateException("Duplicate column '" + columnLabel + "' (lower case) in query.");
                }
                processedColumnLabels.add(columnLabel.toLowerCase());

                if (converter == null) {
                    Optional<Converter> converterForResultSetColumn = getConverterForResultSetColumn(metaData, i, columnLabel, false);
                    if (converterForResultSetColumn.isPresent()) {
                        converter = converterForResultSetColumn.get();
                    }
                }

                if (converter != null) {
                    ResultSetColumn resultSetColumn = new ResultSetColumn(i, columnLabel, converter);
                    boolean nullable = metaData.isNullable(i) == ResultSetMetaData.columnNullable;
                    resultSetColumn.setNullable(nullable);
                    resultSetColumns.add(resultSetColumn);
                    converters.put(columnLabel, converter);
                }
            }

            // Check for unused converters
            for (String converterFor : AbstractQuery.this.converters.keySet()) {
                if (!processedColumnLabels.contains(converterFor)) {
                    throw new IllegalArgumentException(
                            "unused converter for column '" + converterFor + "'");
                }
            }

            return new ResultSetWithColumns(resultSet, metaData, resultSetColumns, converters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Converter> getConverterForResultSetColumn(ResultSetMetaData metaData,
                                                              int position,
                                                              String columnLabel,
                                                              boolean getConverterBySqlType)
            throws SQLException {

        // Check for user provided Converter
        if (converters.containsKey(columnLabel)) {
            Converter converter = converters.get(columnLabel);
            if (converter != null) {
                return of(converter);
            }
        }

        // Determine source table and column from ResultSet
        String javaTableName = lSql.convertExternalSqlToInternalSql(
                lSql.getSqlSchemaAndTableNameFromResultSetMetaData(metaData, position));

        String javaColumnName = lSql.convertExternalSqlToInternalSql(
                lSql.getSqlColumnNameFromResultSetMetaData(metaData, position));

        if (javaTableName != null
                && javaTableName.length() > 0
                && javaColumnName != null
                && javaColumnName.length() > 0) {
            Column column = lSql.table(javaTableName).column(javaColumnName);
            if (column != null) {
                if (!column.isIgnored()) {
                    return of(column.getConverter());
                } else {
                    return absent();
                }
            }
        }

        // Check if the user registered null as a Converter to use a type-based Converter
        // or if the default converter was allowed
        if (getConverterBySqlType
                || (converters.containsKey(columnLabel) && converters.get(columnLabel) == null)) {
            return of(getConverterByColumnType(metaData, position));
        }

        // Error/Warn
        if (lSql.isUseColumnTypeForConverterLookupInQueries()) {
            return of(getConverterByColumnType(metaData, position));
        } else {
            String msg = "Unable to determine a Converter instance for column '" + columnLabel + "'. ";
            msg += "Annotate the query with `col_name as \"col_name: type\"` or " +
                    "register a converter with Query#addConverter() / Query#setConverters().";

            throw new IllegalStateException(msg);
        }
    }

    private Converter getConverterByColumnType(ResultSetMetaData metaData, int position) throws SQLException {
        int columnSqlType = metaData.getColumnType(position);
        return lSql.getConverterForSqlType(columnSqlType);
    }

    private T extractEntity(ResultSetWithColumns resultSetWithColumns) {
        ResultSet resultSet = resultSetWithColumns.getResultSet();
        Collection<ResultSetColumn> columnList = resultSetWithColumns.getColumnsByLabel().values();
        RowDeserializer<T> rowDeserializer = this.getRowDeserializer();
        try {
            T entity = rowDeserializer.createRow();
            for (ResultSetColumn column : columnList) {
                rowDeserializer.deserializeField(
                        this.lSql, entity, column.getConverter(), column.getName(), resultSet, column.getPosition());
            }
            return entity;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract RowDeserializer<T> getRowDeserializer();

}
