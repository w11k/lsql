package com.w11k.lsql;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.exceptions.QueryException;
import com.w11k.lsql.jdbc.ConnectionUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class Query implements Iterable<QueriedRow> {

    private final LSql lSql;

    private final PreparedStatement preparedStatement;

    private final SelectStatement selectStatement;

    public Query(LSql lSql, PreparedStatement preparedStatement, SelectStatement selectStatement) {
        this.lSql = lSql;
        this.preparedStatement = preparedStatement;
        this.selectStatement = selectStatement;
    }

    public Query(LSql lSql, String sql) {
        this(lSql, ConnectionUtils.prepareStatement(lSql, sql, false), new SelectStatement(lSql, "raw query", sql));
    }

    public LSql getlSql() {
        return lSql;
    }

    public List<QueriedRow> asList() {
        try {
            ResultSet resultSet = preparedStatement.executeQuery();
            Map<String, ResultSetColumn<?>> columns = createRawResultSetColums(resultSet.getMetaData());
            List<QueriedRow> rows = Lists.newLinkedList();
            while (resultSet.next()) {
                rows.add(extractRow(resultSet, columns));
            }
            return rows;
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    @Override
    public Iterator<QueriedRow> iterator() {
        return asList().iterator();
    }

    public <T> List<T> map(Function<QueriedRow, T> rowHandler) {
        return new QueriedRows(asList()).map(rowHandler);
    }

    public Optional<QueriedRow> getFirstRow() {
        return new QueriedRows(asList()).getFirstRow();
    }

    public List<QueriedRow> groupByIds(final String... ids) {
        return new QueriedRows(asList()).groupByIds(ids);
    }

    private QueriedRow extractRow(ResultSet resultSet,
                                  Map<String, ResultSetColumn<?>> resultSetColumns) throws SQLException {

        QueriedRow r = new QueriedRow(resultSetColumns);
        for (ResultSetColumn rsc : resultSetColumns.values()) {
            Column column = rsc.getColumn();
            if (!column.isIgnored()) {
                Object val = column.getConverter().getValueFromResultSet(lSql, resultSet, rsc.getPosition());
                r.put(rsc.getName(), val);
            }
        }
        return r;
    }

    private Column<?> getColumnForResultSetColumn(ResultSetMetaData metaData, int position) throws SQLException {
        String sqlColumnName = metaData.getColumnName(position);
        String javaColumnName = lSql.getDialect().identifierSqlToJava(sqlColumnName);
        String sqlColumnLabel = metaData.getColumnLabel(position);
        String javaColumnLabel = lSql.getDialect().identifierSqlToJava(sqlColumnLabel);
        Optional<? extends Table<?>> table = getTable(metaData, position);

        // JDBC return all required information
        if (table.isPresent()) {
            if (table.get().getColumns().containsKey(javaColumnName)) {
                return table.get().column(javaColumnName);
            }
        }

        // Table lookup failed. Check SQL string for aliases.
        Optional<? extends Column<?>> columnOptional = selectStatement.getColumnFromSqlStatement(javaColumnLabel);
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

    private Converter getConverter(ResultSetMetaData metaData, int position) throws SQLException {
        int columnSqlType = metaData.getColumnType(position);
        return lSql.getDialect().getConverterRegistry().getConverterForSqlType(columnSqlType);
    }

    private Map<String, ResultSetColumn<?>> createRawResultSetColums(ResultSetMetaData metaData) throws SQLException {
        Set<String> processedColumnLabels = Sets.newLinkedHashSet(); // used to find duplicates
        Map<String, ResultSetColumn<?>> columnList = Maps.newLinkedHashMap();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String columnLabel = lSql.getDialect().identifierSqlToJava(metaData.getColumnLabel(i));
            Column<?> column = getColumnForResultSetColumn(metaData, i);
            if (processedColumnLabels.contains(columnLabel)) {
                throw new IllegalStateException("Dublicate column '" + columnLabel + "' in query.");
            }
            processedColumnLabels.add(columnLabel);

            ResultSetColumn<?> rsc = ResultSetColumn.create(i, columnLabel, column);
            columnList.put(columnLabel, rsc);
        }
        return columnList;
    }

    private Optional<? extends Table<?>> getTable(ResultSetMetaData metaData, int position) throws SQLException {
        String sqlTableName = lSql.getDialect().getTableNameFromResultSetMetaData(metaData, position);
        Optional<? extends Table<?>> table;
        if (sqlTableName == null || "".equals(sqlTableName)) {
            table = absent();
        } else {
            String javaTable = lSql.getDialect().identifierSqlToJava(sqlTableName);
            table = of(lSql.table(javaTable));
        }
        return table;
    }

}

