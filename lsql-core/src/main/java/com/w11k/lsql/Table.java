package com.w11k.lsql;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.w11k.lsql.exceptions.InsertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;
import java.util.Map;

public class Table {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LSql lSql;

    private final String tableName;

    private final Optional<String> primaryKeyColumn;

    private Optional<JavaSqlConverter> tableConverter = Optional.absent();

    private final Map<String, Column> columns = Maps.newHashMap();

    public Table(LSql lSql, String tableName) {
        this.lSql = lSql;
        this.tableName = tableName;
        primaryKeyColumn = getPrimaryKeyColumn();
    }

    // ----- getter/setter -----

    public void setTableConverter(JavaSqlConverter tableConverter) {
        this.tableConverter = Optional.fromNullable(tableConverter);
    }

    public JavaSqlConverter getTableConverter() {
        return tableConverter.or(lSql.getGlobalConverter());
    }

    public LSql getlSql() {
        return lSql;
    }

    public String getTableName() {
        return tableName;
    }

    // ----- public -----

    public Column column(String columnName) {
        if (!columns.containsKey(columnName)) {
            columns.put(columnName, new Column(this, columnName));
        }
        return columns.get(columnName);
    }

    public Optional<Object> insert(Row row) {
        // extract column names, values and corresponding converters
        List<String> columns = Lists.newLinkedList();
        List<Object> values = Lists.newLinkedList();
        List<JavaSqlConverter> valueConverter = Lists.newLinkedList();
        for (Map.Entry<String, Object> keyValue : row.entrySet()) {
            String key = keyValue.getKey();
            Object value = keyValue.getValue();
            JavaSqlConverter converter = column(key).getColumnConverter();
            columns.add(lSql.identifierJavaToSql(key));
            values.add(value);
            valueConverter.add(converter);
        }

        // create PreparedStatement and execute
        String sqlString = SqlStringUtils.createInsertString(lSql, tableName, columns);
        PreparedStatement ps = lSql.prepareStatement(sqlString);
        try {
            for (int i = 0; i < valueConverter.size(); i++) {
                JavaSqlConverter javaSqlConverter = valueConverter.get(i);
                javaSqlConverter.setValueInStatement(ps, i + 1, values.get(i));
            }
            ps.executeUpdate();

            // check for generated keys
            ResultSet resultSet = ps.getGeneratedKeys();
            if (resultSet.next()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                if (columnCount == 0) {
                    return Optional.absent();
                } else if (columnCount > 1) {
                    throw new IllegalStateException("ResultSet for retrieval of the generated " +
                            "ID contains more than one column.");
                }
                Object newId = resultSet.getObject(1);
                if (primaryKeyColumn.isPresent()) {
                    newId = column(primaryKeyColumn.get()).getColumnConverter()
                            .getValueFromResultSet(resultSet, 1);
                    row.put(primaryKeyColumn.get(), newId);
                }
                return Optional.of(newId);
            }
            return Optional.absent();
        } catch (Exception e) {
            throw new InsertException(e, sqlString);
        }
    }

    // ----- private -----

    private Optional<String> getPrimaryKeyColumn() {
        Connection con = lSql.getConnection();
        try {
            DatabaseMetaData md = con.getMetaData();
            ResultSet primaryKeys = md.getPrimaryKeys(null, null, lSql.identifierJavaToSql(tableName));
            if (!primaryKeys.next()) {
                // no row returned
                return Optional.absent();
            }

            // extract ID column
            // 1 -> catalog, 2 -> schema, 3 -> table, 4 -> column
            String idColumn = primaryKeys.getString(4);

            // Check if only one ID column was returned
            if (primaryKeys.next()) {
                throw new RuntimeException("Database returned more that one primary key column.");
            }
            return Optional.of(lSql.identifierSqlToJava(idColumn));
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.absent();
        }
    }

}
