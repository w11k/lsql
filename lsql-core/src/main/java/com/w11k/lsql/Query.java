package com.w11k.lsql;

import com.google.common.collect.Maps;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.jdbc.ConnectionUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class Query {

    private final LSql lSql;

    private final PreparedStatement preparedStatement;

    private Map<String, Converter> converters = Maps.newHashMap();

//    private final SqlStatement sqlStatement;

    public Query(LSql lSql, PreparedStatement preparedStatement/*, SqlStatement sqlStatement*/) {
        this.lSql = lSql;
        this.preparedStatement = preparedStatement;
//        this.sqlStatement = sqlStatement;
    }

    public Query(LSql lSql, String sql) {
        this(lSql, ConnectionUtils.prepareStatement(lSql, sql, false)/*, new SqlStatement(lSql, "raw query", sql)*/);
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

    public ExecutedQuery executeQuery() {
        try {
            return new ExecutedQuery(lSql, preparedStatement.executeQuery(), this.converters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Rows rows() {
        return executeQuery().rows();
    }

//    @Override
//    protected List<Row> delegate() {
//        return rows();
//    }

    //    public <T> List<T> map(Function<QueriedRow, T> rowHandler) {
//        return new QueriedRows(asList()).map(rowHandler);
//    }


    /*
    private Column getColumnForResultSetColumn(ResultSetMetaData metaData, int position) throws SQLException {
        String sqlColumnName = metaData.getColumnName(position);
        String javaColumnName = lSql.getDialect().identifierSqlToJava(sqlColumnName);
        String sqlColumnLabel = metaData.getColumnLabel(position);
        String javaColumnLabel = lSql.getDialect().identifierSqlToJava(sqlColumnLabel);
        Optional<Table> table = getTable(metaData, position);

        // JDBC returned all required information
        if (table.isPresent()) {
            if (table.get().getColumns().containsKey(javaColumnName)) {
                return table.get().column(javaColumnName);
            }
        }

        // Table lookup failed. Check SQL string for aliases.
        Optional<? extends Column> columnOptional = sqlStatement.getColumnFromSqlStatement(javaColumnLabel);
        if (columnOptional.isPresent()) {
            return columnOptional.get();
        }

        // Alias search in SQL string failed. Create column based on type.
        Converter converter = getConverter(metaData, position);
        return Column.create(
                table.orNull(),
                javaColumnName,
                metaData.getColumnType(position),
                converter,
                -1);
    }
    */



    /*
    private Map<String, ResultSetColumn<?>> createResultSetColums(ResultSetMetaData metaData)
            throws SQLException {

        // used to find duplicates
        Set<String> processedColumnLabels = Sets.newLinkedHashSet();

        Map<String, ResultSetColumn<?>> columnList = Maps.newLinkedHashMap();

        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String columnLabel = lSql.getDialect().identifierSqlToJava(metaData.getColumnLabel(i));

            // check dublicates
            if (processedColumnLabels.contains(columnLabel)) {
                throw new IllegalStateException("Dublicate column '" + columnLabel + "' in query.");
            }
            processedColumnLabels.add(columnLabel);

            Converter converter = getConverter(metaData, i);

            columnList.put(columnLabel, rsc);
        }
        return columnList;
    }
    */

//    private Optional<Table> getTable(ResultSetMetaData metaData, int position) throws SQLException {
//        String sqlTableName = lSql.getDialect().getTableNameFromResultSetMetaData(metaData, position);
//        Optional<Table> table;
//        if (sqlTableName == null || "".equals(sqlTableName)) {
//            table = absent();
//        } else {
//            String javaTable = lSql.getDialect().identifierSqlToJava(sqlTableName);
//            table = of(lSql.table(javaTable));
//        }
//        return table;
//    }

}

