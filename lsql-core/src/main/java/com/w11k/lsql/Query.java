package com.w11k.lsql;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
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
import java.util.Set;
import java.util.regex.Pattern;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class Query implements Iterable<QueriedRow> {

    private static final Pattern AS_STATEMENT = Pattern.compile("\\w(\\w\\d)");

    private final LSql lSql;

    private final PreparedStatement preparedStatement;

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

    @Override
    public Iterator<QueriedRow> iterator() {
        return asRawList().iterator();
    }

    public <T> List<T> map(Function<QueriedRow, T> rowHandler) {
        List<T> list = Lists.newLinkedList();
        for (QueriedRow row : this) {
            list.add(rowHandler.apply(row));
        }
        return list;
    }

    public Optional<QueriedRow> getFirstRow() {
        List<QueriedRow> queriedRows = asRawList();
        if (queriedRows.size() == 0) {
            return absent();
        } else {
            return of(queriedRows.get(0));
        }
    }

    public List<QueriedRow> asRawList() {
        try {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<ResultSetColumn> columns = createRawResultSetColumnList(resultSet.getMetaData());
            List<QueriedRow> rows = Lists.newLinkedList();
            while (resultSet.next()) {
                rows.add(extractRow(resultSet, columns));
            }
            return rows;
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    private QueriedRow extractRow(ResultSet resultSet, List<ResultSetColumn> resultSetColumns) throws SQLException {
        QueriedRow r = new QueriedRow(resultSetColumns);
        for (ResultSetColumn rsc : resultSetColumns) {
            Object val = rsc.getColumn().getConverter().getValueFromResultSet(lSql, resultSet, rsc.getPosition());
            r.put(rsc.getName(), val);
        }
        return r;
    }

    private Column getColumnForResultSetColumn(ResultSetMetaData metaData, int position) throws SQLException {
        String sqlColumnName = metaData.getColumnName(position);
        String javaColumnName = lSql.getDialect().identifierSqlToJava(sqlColumnName);
        String sqlColumnLabel = metaData.getColumnLabel(position);
        String javaColumnabel = lSql.getDialect().identifierSqlToJava(sqlColumnLabel);
        Optional<Table> table = getTable(metaData, position);
        Column column;

        if (!table.isPresent()) {
            // Check aliases in query
        }

        if (table.isPresent() && table.get().column(javaColumnName) == null) {
            // Check aliases in query
        }

        if (table.isPresent() && table.get().column(javaColumnName) != null) {
            column = table.get().column(javaColumnName);
        }
        // TODO: check is required for alias support
        //else if (table.isPresent() && table.get().column(javaColumnName) == null) {
        //    throw new RuntimeException();
        //}
        else {
            Converter converter = getConverter(metaData, position);
            column = new Column(
                    table,
                    javaColumnName,
                    metaData.getColumnType(position),
                    converter,
                    -1);
        }

        return column;
    }

    private Converter getConverter(ResultSetMetaData metaData, int position) throws SQLException {
        int columnSqlType = metaData.getColumnType(position);
        return lSql.getDialect().getConverterRegistry().getConverterForSqlType(columnSqlType);
    }

    private List<ResultSetColumn> createRawResultSetColumnList(ResultSetMetaData metaData) throws SQLException {
        Set<String> processedColumnLabels = Sets.newLinkedHashSet(); // used to find duplicates
        List<ResultSetColumn> columnList = Lists.newLinkedList();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            Column column = getColumnForResultSetColumn(metaData, i);
            if (processedColumnLabels.contains(column.getColumnName())) {
                throw new IllegalStateException("Dublicate column '" + column.getColumnName() + "' in query.");
            }
            processedColumnLabels.add(column.getColumnName());

            String javaLabel = lSql.getDialect().identifierSqlToJava(metaData.getColumnLabel(i));
            ResultSetColumn rsc = new ResultSetColumn(i, javaLabel, column);
            columnList.add(rsc);
        }
        return columnList;
    }

    private Optional<Table> getTable(ResultSetMetaData metaData, int position) throws SQLException {
        String sqlTableName = lSql.getDialect().getTableNameFromResultSetMetaData(metaData, position);
        Optional<Table> table;
        if (sqlTableName == null || "".equals(sqlTableName)) {
            table = absent();
        } else {
            String javaTable = lSql.getDialect().identifierSqlToJava(sqlTableName);
            table = of(lSql.table(javaTable));
        }
        return table;
    }

}

