package com.w11k.lsql.relational;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.exceptions.InsertException;
import com.w11k.lsql.exceptions.UpdateException;
import com.w11k.lsql.utils.ConnectionUtils;
import com.w11k.lsql.utils.PreparedStatementUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Map;

import static com.google.common.base.Optional.of;

public class Table {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final LSql lSql;
    private final String tableName;
    private final Optional<String> primaryKeyColumn;
    private final Map<String, Column> columns = Maps.newHashMap();
    private Optional<Converter> tableConverter = Optional.absent();

    public Table(LSql lSql, String tableName) {
        this.lSql = lSql;
        this.tableName = tableName;
        primaryKeyColumn = getPrimaryKeyColumn();
    }

    // ----- getter/setter -----

    public Converter getTableConverter() {
        return tableConverter.or(lSql.getGlobalConverter());
    }

    public void setTableConverter(Converter tableConverter) {
        this.tableConverter = Optional.fromNullable(tableConverter);
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
        if (getPrimaryKeyColumn().isPresent() && row.containsKey(getPrimaryKeyColumn().get())) {
            throw new InsertException("Can not insert row because the primary key is already present. " +
                    "Use update or insertOrUpdate instead.");
        }
        try {
            PreparedStatement ps = PreparedStatementUtils.createInsertStatement(this, row);
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
                return of(newId);
            }
            return Optional.absent();
        } catch (Exception e) {
            throw new InsertException(e);
        }
    }

    public int update(Row row) {
        if (getPrimaryKeyColumn().isPresent() && !row.containsKey(getPrimaryKeyColumn().get())) {
            throw new UpdateException("Can not update row because the primary key column " +
                    "'" + getPrimaryKeyColumn().get() + "' is not present");
        }
        try {
            PreparedStatement ps = PreparedStatementUtils.createUpdateStatement(this, row);
            int i = ps.executeUpdate();
            if (i == 0) {
                throw new UpdateException("No rows where updated");
            }
            return i;
        } catch (Exception e) {
            throw new InsertException(e);
        }
    }

    public Optional<Object> insertOrUpdate(Row row) {
        if (row.containsKey(getPrimaryKeyColumn().get())) {
            update(row);
            return Optional.of(row.get(getPrimaryKeyColumn().get()));
        } else {
            return insert(row);
        }
    }

    public QueriedRow get(Object id) {
        String pkColumn = getPrimaryKeyColumn().get();
        Column column = column(pkColumn);
        String insertString = PreparedStatementUtils.createSelectByIdString(this, column);
        PreparedStatement preparedStatement = ConnectionUtils.prepareStatement(lSql, insertString, false);
        try {
            column.getColumnConverter().setValueInStatement(preparedStatement, 1, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new Query(lSql, preparedStatement).getFirstRow();
    }

    public Optional<String> getPrimaryKeyColumn() {
        Connection con = ConnectionUtils.getConnection(lSql);
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
            return of(lSql.identifierSqlToJava(idColumn));
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.absent();
        }
    }

}
