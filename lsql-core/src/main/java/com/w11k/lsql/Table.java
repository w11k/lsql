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

    private Optional<String> primaryKeyColumn = absent();

    private Optional<Column> revisionColumn = absent();

    public Table(LSql lSql, String tableName) {
        this.lSql = lSql;
        this.tableName = tableName;
        fetchMeta();
    }

    public LSql getlSql() {
        return lSql;
    }

    public String getTableName() {
        return tableName;
    }

    public Optional<String> getPrimaryKeyColumn() {
        return primaryKeyColumn;
    }

    public Map<String, Column> getColumns() {
        return ImmutableMap.copyOf(columns);
    }

    public Column column(String columnName) {
        if (!columns.containsKey(columnName)) {
            throw new RuntimeException(
                    "Column '" + columnName + "' does not exist in table '" + tableName + "'.");
        }
        return columns.get(columnName);
    }

    /**
     * Convenience method. Same as <code>enableRevisionSupport("revision").</code>
     */
    public void enableRevisionSupport() {
        enableRevisionSupport("revision");
    }

    public void enableRevisionSupport(String revisionColumnName) {
        Column col = column(revisionColumnName);
        revisionColumn = of(col);
    }

    public Optional<Column> getRevisionColumn() {
        return revisionColumn;
    }

    /**
     * @throws InsertException
     */
    public Optional<Object> insert(Row row) {
        try {
            List<String> columns = row.getKeyList();
            PreparedStatement ps = lSql.getDialect().getPreparedStatementCreator().createInsertStatement(this, columns);
            setValuesInPreparedStatement(ps, columns, row);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new InsertException(rowsAffected + " rows were affected by insert operation. Expected: 1");
            }
            if (primaryKeyColumn.isPresent()) {
                Object id = null;
                if (!row.containsKey(primaryKeyColumn.get())) {
                    // check for generated keys
                    ResultSet resultSet = ps.getGeneratedKeys();
                    if (resultSet.next()) {
                        Optional<Object> generated = lSql.getDialect().extractGeneratedPk(this, resultSet);
                        if (generated.isPresent()) {
                            id = generated.get();
                            row.put(primaryKeyColumn.get(), id);
                            return generated;
                        }
                    }
                } else {
                    id = row.get(primaryKeyColumn.get());
                }

                // Set new revision
                applyNewRevision(row, id);

                return of(id);
            }
        } catch (Exception e) {
            throw new InsertException(e);
        }
        return absent();
    }

    /**
     * @throws UpdateException
     */
    public void update(Row row) {
        if (getPrimaryKeyColumn().isPresent() && !row.containsKey(getPrimaryKeyColumn().get())) {
            throw new UpdateException("Can not update row because the primary key column " +
                    "'" + getPrimaryKeyColumn().get() + "' is not present.");
        }
        try {
            List<String> columns = row.getKeyList();
            columns.remove(getPrimaryKeyColumn().get());
            if (revisionColumn.isPresent()) {
                columns.remove(getRevisionColumn().get().getColumnName());
            }

            PreparedStatement ps = lSql.getDialect().getPreparedStatementCreator().createUpdateStatement(this, columns);
            setValuesInPreparedStatement(ps, columns, row);

            // Set ID
            String pkColumn = getPrimaryKeyColumn().get();
            Object id = row.get(pkColumn);
            Column column = column(pkColumn);
            column.getConverter().setValueInStatement(lSql, ps, columns.size() + 1, id, column.getSqlType());

            // Set Revision
            if (revisionColumn.isPresent()) {
                Column col = revisionColumn.get();
                Object revision = row.get(col.getColumnName());
                col.getConverter().setValueInStatement(lSql, ps, columns.size() + 2, revision, col.getSqlType());
            }

            executeUpdate(ps);

            // Set new revision
            applyNewRevision(row, id);

            //return row.getOptional(getPrimaryKeyColumn().get());
        } catch (Exception e) {
            throw new UpdateException(e);
        }
    }

    private void executeUpdate(PreparedStatement ps) throws SQLException {
        int rowsAffected = ps.executeUpdate();
        if (rowsAffected != 1) {
            throw new UpdateException(rowsAffected +
                    " rows were affected by update operation (expected 1). Either the ID or the revision (if enabled) is wrong.");
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
                PreparedStatement ps = lSql.getDialect().getPreparedStatementCreator().createCountForIdStatement(this);
                Column column = column(getPrimaryKeyColumn().get());
                column.getConverter().setValueInStatement(lSql, ps, 1, id, column.getSqlType());
                ps.setObject(1, id);
                ResultSet rs = ps.executeQuery();
                rs.next();
                int count = rs.getInt(1);
                if (count == 0) {
                    insert(row);
                } else {
                    update(row);
                }
            } catch (DatabaseAccessException dae) {
                throw dae;
            } catch (Exception e) {
                throw new DatabaseAccessException(e);
            }
            return of(id);
        }
    }

    /**
     * Deletes the row with the given ID.
     * <p/>
     * If revision support is enabled, the delete will fail.
     *
     * @param id The row's ID to delete.
     */
    public void delete(Object id) {
        Row row = new Row();
        row.put(primaryKeyColumn.get(), id);
        delete(row);
    }

    public void delete(Row row) {
        PreparedStatement ps = lSql.getDialect().getPreparedStatementCreator().createDeleteByIdStatement(this);
        try {
            Column column = column(getPrimaryKeyColumn().get());
            Object id = row.get(getPrimaryKeyColumn().get());
            column.getConverter().setValueInStatement(lSql, ps, 1, id, column.getSqlType());
            if (revisionColumn.isPresent()) {
                Column revCol = revisionColumn.get();
                Object revVal = row.get(revCol.getColumnName());
                if (revVal == null) {
                    throw new IllegalStateException("Row must contain a revision.");
                }
                revCol.getConverter().setValueInStatement(lSql, ps, 2, revVal, revCol.getSqlType());
            }
            executeUpdate(ps);
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
            column.getConverter().setValueInStatement(lSql, ps, 1, id, column.getSqlType());
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
        return new LinkedRow(this, data);
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

    private void applyNewRevision(Row row, Object id) throws SQLException {
        if (revisionColumn.isPresent()) {
            Object revision = queryRevision(id);
            row.put(revisionColumn.get().getColumnName(), revision);
        }
    }

    private Object queryRevision(Object id) throws SQLException {
        Column revCol = revisionColumn.get();
        PreparedStatement revQuery = lSql.getDialect().getPreparedStatementCreator().createRevisionQueryStatement(this, id);
        revCol.getConverter().setValueInStatement(lSql, revQuery, 1, id, revCol.getSqlType());
        ResultSet resultSet = revQuery.executeQuery();
        resultSet.next();
        return resultSet.getObject(1);
    }

    private void fetchMeta() {
        Connection con = ConnectionUtils.getConnection(lSql);
        try {
            DatabaseMetaData md = con.getMetaData();

            // Fetch Primary Key
            ResultSet primaryKeys = md.getPrimaryKeys(null, null, lSql.getDialect().identifierJavaToSql(tableName));
            if (!primaryKeys.next()) {
                primaryKeyColumn = Optional.absent();
            } else {
                String idColumn = primaryKeys.getString(4);
                primaryKeyColumn = of(lSql.getDialect().identifierSqlToJava(idColumn));
            }

            // Fetch all columns
            ResultSet columnsMetaData = md.getColumns(null, null, lSql.getDialect()
                    .identifierJavaToSql(tableName), null);
            while (columnsMetaData.next()) {
                String sqlColumnName = columnsMetaData.getString(4);
                int columnSize = columnsMetaData.getInt(7);
                String javaColumnName = lSql.getDialect().identifierSqlToJava(sqlColumnName);
                int dataType = columnsMetaData.getInt(5);
                Converter converter = lSql.getDialect().getConverterRegistry().getConverterForSqlType(dataType);
                columns.put(javaColumnName, new Column(of(this), javaColumnName, dataType, converter, columnSize));
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
                Column column = column(columns.get(i));
                converter.setValueInStatement(lSql, ps, i + 1, row.get(columns.get(i)), column.getSqlType());
            }
            return ps;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
