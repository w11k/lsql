package com.w11k.lsql;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.exceptions.DatabaseAccessException;
import com.w11k.lsql.exceptions.DeleteException;
import com.w11k.lsql.exceptions.InsertException;
import com.w11k.lsql.exceptions.UpdateException;
import com.w11k.lsql.jdbc.ConnectionUtils;
import com.w11k.lsql.validation.AbstractValidationError;
import com.w11k.lsql.validation.KeyError;

import java.sql.*;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class Table {

    private final LSql lSql;

    private final String tableName;

    private final Map<String, Column> columns = Maps.newHashMap();

    final private Map<Table, Column> exportedForeignKeyTables = Maps.newHashMap();

    private Optional<String> primaryKeyColumn = Optional.absent();

    public Table(LSql lSql, String tableName) {
        this.lSql = lSql;
        this.tableName = tableName;
        fetchMeta();
    }

    // ----- getter/setter -----

    public LSql getlSql() {
        return lSql;
    }

    public String getTableName() {
        return tableName;
    }

    public Optional<String> getPrimaryKeyColumn() {
        return primaryKeyColumn;
    }

    public Map<Table, Column> getExportedForeignKeyTables() {
        return ImmutableMap.copyOf(exportedForeignKeyTables);
    }

    public Map<String, Column> getColumns() {
        return ImmutableMap.copyOf(columns);
    }

    // ----- public -----

    public Column column(String columnName) {
        if (!columns.containsKey(columnName)) {
            throw new RuntimeException(
                    "Column '" + columnName + "' does not exist in table '" + tableName + "'.");
        }
        return columns.get(columnName);
    }

    /**
     * @throws InsertException
     */
    public Optional<Object> insert(Row row) {
        try {
            List<String> columns = row.getKeyList();
            PreparedStatement ps = lSql.getDialect().getPreparedStatementCreator()
                    .createInsertStatement(this, columns);
            setValuesInPreparedStatement(ps, columns, row);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new InsertException(
                        rowsAffected + " rows were affected by insert operation. Expected: 1");
            }
            if (primaryKeyColumn.isPresent()) {
                if (!row.containsKey(primaryKeyColumn.get())) {
                    // check for generated keys
                    ResultSet resultSet = ps.getGeneratedKeys();
                    if (resultSet.next()) {
                        Optional<Object> generated = lSql.getDialect()
                                .extractGeneratedPk(this, resultSet);
                        if (generated.isPresent()) {
                            row.put(primaryKeyColumn.get(), generated.get());
                            return generated;
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new InsertException(e);
        }
        return absent();
    }

    /**
     * @throws UpdateException
     */
    public Optional<Object> update(Row row) {
        if (getPrimaryKeyColumn().isPresent() && !row.containsKey(getPrimaryKeyColumn().get())) {
            throw new UpdateException("Can not update row because the primary key column " +
                    "'" + getPrimaryKeyColumn().get() + "' is not present.");
        }
        try {
            List<String> columns = row.getKeyList();
            columns.remove(getPrimaryKeyColumn().get());

            PreparedStatement ps = lSql.getDialect().getPreparedStatementCreator()
                    .createUpdateStatement(this, columns);
            setValuesInPreparedStatement(ps, columns, row);

            // Set ID
            String pkColumn = getPrimaryKeyColumn().get();
            Object id = row.get(pkColumn);
            column(pkColumn).getConverter().setValueInStatement(lSql, ps, columns.size() + 1, id);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new UpdateException(
                        rowsAffected + " rows were affected by update operation. Expected: 1");
            }
            return row.getOptional(getPrimaryKeyColumn().get());
        } catch (Exception e) {
            throw new UpdateException(e);
        }
    }

    /**
     * @throws InsertException
     * @throws UpdateException
     */
    public Optional<?> save(Row row) {
        if (!primaryKeyColumn.isPresent()) {
            throw new DatabaseAccessException("save() requires a primary key column.");
        }
        if (!row.containsKey(getPrimaryKeyColumn().get())) {
            // Insert
            return insert(row);
        } else {
            // Check if insert or update
            Object id = row.get(primaryKeyColumn.get());
            try {
                PreparedStatement ps = lSql.getDialect().getPreparedStatementCreator()
                        .createCountForIdStatement(this);
                column(getPrimaryKeyColumn().get()).getConverter()
                        .setValueInStatement(lSql, ps, 1, id);
                ps.setObject(1, id);
                ResultSet rs = ps.executeQuery();
                rs.next();
                int count = rs.getInt(1);
                if (count == 0) {
                    insert(row);
                } else {
                    update(row);
                }
            } catch (Exception e) {
                throw new DatabaseAccessException(e);
            }
            return of(id);
        }
    }

    public void delete(Object id) {
        PreparedStatement ps = lSql.getDialect().getPreparedStatementCreator()
                .createDeleteByIdStatement(this);
        try {
            column(getPrimaryKeyColumn().get()).getConverter().setValueInStatement(lSql, ps, 1, id);
            ps.execute();
        } catch (Exception e) {
            throw new DeleteException(e);
        }
    }

    public Optional<LinkedRow> get(Object id) {
        String pkColumn = getPrimaryKeyColumn().get();
        Column column = column(pkColumn);
        PreparedStatement ps = lSql.getDialect().getPreparedStatementCreator()
                .createSelectByIdStatement(this, column);
        try {
            column.getConverter().setValueInStatement(lSql, ps, 1, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<QueriedRow> queriedRows = new Query(lSql, ps).asList();
        if (queriedRows.size() == 1) {
            LinkedRow row = queriedRows.get(0);
            row.setTable(this);
            return of(row);
        }
        return absent();
    }

    public LinkedRow newLinkedRow() {
        return new LinkedRow(this);
    }

    public LinkedRow newLinkedRow(Map<String, Object> data) {
        LinkedRow linkedRow = new LinkedRow(this);
        linkedRow.putAll(data);
        return linkedRow;
    }

    public LinkedRow newLinkedRow(Object... keyVals) {
        LinkedRow linkedRow = new LinkedRow(this);
        linkedRow.addKeyVals(keyVals);
        return linkedRow;
    }

    public Map<String, AbstractValidationError> validate(Row row) {
        Map<String, AbstractValidationError> validationErrors = Maps.newHashMap();
        for (String key : row.keySet()) {
            Object value = row.get(key);
            Optional<? extends AbstractValidationError> error = validate(key, value);
            if (error.isPresent()) {
                validationErrors.put(key, error.get());
            }
        }
        return validationErrors;
    }

    public Optional<? extends AbstractValidationError> validate(String javaColumnName, Object value) {
        if (!getColumns().containsKey(javaColumnName)) {
            return of(new KeyError(getTableName(), javaColumnName));
        }
        return column(javaColumnName).validateValue(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Table otherTable = (Table) o;
        return lSql == otherTable.lSql && tableName.equals(otherTable.tableName);
    }

    @Override
    public int hashCode() {
        int result = lSql.hashCode();
        result = 31 * result + tableName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Table{tableName='" + tableName + "'}";
    }

    private void fetchMeta() {
        Connection con = ConnectionUtils.getConnection(lSql);
        try {
            DatabaseMetaData md = con.getMetaData();

            // Fetch Primary Key
            ResultSet primaryKeys = md.getPrimaryKeys(null, null,
                    lSql.getDialect().identifierJavaToSql(tableName));
            if (!primaryKeys.next()) {
                primaryKeyColumn = Optional.absent();
            } else {
                String idColumn = primaryKeys.getString(4);
                primaryKeyColumn = of(lSql.getDialect().identifierSqlToJava(idColumn));
            }

            // Fetch Foreign keys
            ResultSet exportedKeys = md.getExportedKeys(null, null,
                    lSql.getDialect().identifierJavaToSql(tableName));
            while (exportedKeys.next()) {
                String sqlTableName = exportedKeys.getString(7);
                String javaTableName = lSql.getDialect().identifierSqlToJava(sqlTableName);
                String sqlColumnName = exportedKeys.getString(8);
                String javaColumnName = lSql.getDialect().identifierSqlToJava(sqlColumnName);

                // Ignore references to same table
                if (!javaTableName.equals(tableName)) {
                    Table foreignTable = lSql.table(javaTableName);
                    Column foreignColumn = foreignTable.column(javaColumnName);
                    exportedForeignKeyTables.put(foreignTable, foreignColumn);
                }
            }

            // Fetch all columns
            ResultSet columnsMetaData = md.getColumns(
                    null, null, lSql.getDialect().identifierJavaToSql(tableName), null);
            while (columnsMetaData.next()) {
                String sqlColumnName = columnsMetaData.getString(4);
                int columnSize = columnsMetaData.getInt(7);
                String javaColumnName = lSql.getDialect().identifierSqlToJava(sqlColumnName);
                int dataType = columnsMetaData.getInt(5);
                Converter converter = lSql.getDialect().getConverterRegistry()
                        .getConverterForSqlType(dataType);
                columns.put(javaColumnName, new Column(of(this), javaColumnName, converter, columnSize));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private PreparedStatement setValuesInPreparedStatement(PreparedStatement ps,
                                                           List<String> columns, Row row) {
        try {
            for (int i = 0; i < columns.size(); i++) {
                Converter converter = column(columns.get(i)).getConverter();
                converter.setValueInStatement(lSql, ps, i + 1, row.get(columns.get(i)));
            }
            return ps;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
