package com.w11k.lsql;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.exceptions.DatabaseAccessException;
import com.w11k.lsql.exceptions.DeleteException;
import com.w11k.lsql.exceptions.InsertException;
import com.w11k.lsql.exceptions.UpdateException;
import com.w11k.lsql.jdbc.ConnectionUtils;
import com.w11k.lsql.query.PlainQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class Table {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final LSql lSql;
    private final Map<String, Column> columns = Maps.newHashMap();
    private String sqlSchemaAndTableName;
    private String schemaName;
    private String tableName;
    private Optional<String> primaryKeyColumn = absent();
    private Optional<Column> revisionColumn = absent();

    Table(LSql lSql, String sqlSchemaAndTableName) {
        this.lSql = lSql;
        this.sqlSchemaAndTableName = sqlSchemaAndTableName;
        fetchMeta();

        if (logger.isDebugEnabled()) {
            StringBuilder msg = new StringBuilder("Read schema for table '" + this.sqlSchemaAndTableName + "':\n");
            for (Column column : columns.values()) {
                msg.append("    ").append(column).append("\n");
            }

            logger.debug(msg.toString());
        }
    }

    Table(LSql lSql,
          String sqlSchemaName,
          String sqlTableName,
          String primaryKeyColumn) {

        this.lSql = lSql;
        setSchemaAndTableName(sqlSchemaName, sqlTableName);
        this.primaryKeyColumn = Optional.fromNullable(primaryKeyColumn);

        // TODO: remove fetchMeta call, pass column info as constructor parameter
        this.fetchMeta();

        if (logger.isDebugEnabled()) {
            // TOOD
//            StringBuilder msg = new StringBuilder("Read schema for table '" + this.sqlSchemaAndTableName + "':\n");
//            for (Column column : columns.values()) {
//                msg.append("    ").append(column).append("\n");
//            }
//
//            logger.debug(msg.toString());
        }
    }

    public LSql getlSql() {
        return lSql;
    }

    public String getSqlSchemaAndTableName() {
        return this.sqlSchemaAndTableName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public Optional<String> getPrimaryKeyColumn() {
        return primaryKeyColumn;
    }

    public Optional<Class<?>> getPrimaryKeyType() {
        Optional<String> pkNameOpt = this.getPrimaryKeyColumn();
        if (!pkNameOpt.isPresent()) {
            return absent();
        }

        Column column = this.column(pkNameOpt.get());
        return of(column.getConverter().getJavaType());
    }

    public Map<String, Column> getColumns() {
        return ImmutableMap.copyOf(columns);
    }

    /**
     * @param columnName the name of the column
     * @return the column instance
     */
    @Nullable
    public synchronized Column column(String columnName) {
        if (!columns.containsKey(columnName)) {
            return null;
        }
        return columns.get(columnName);
    }

    @Deprecated
    public <T> PojoTable<T> withPojo(Class<T> pojoClass) {
        return new PojoTable<>(this, pojoClass);
    }

    /**
     * Convenience method. Same as {@code enableRevisionSupport("revision").}
     */
    public void enableRevisionSupport() {
        enableRevisionSupport("revision");
    }

    /**
     * Enables revision support and optimistic locking with the given column. LSql increases the revision column
     * on
     * every update operation. Hence the column must support the SQL operation "SET column=column+1".
     * Additionally,
     * every {@link com.w11k.lsql.Table#update(Row)} operation uses a WHERE constraint with the expected revision.
     *
     * @param revisionColumnName the revision column
     */
    public void enableRevisionSupport(String revisionColumnName) {
        Column col = column(revisionColumnName);
        revisionColumn = of(col);
    }

    public Optional<Column> getRevisionColumn() {
        return revisionColumn;
    }

    /**
     * Inserts the given {@link Row}. If a primary key was generated during the INSERT operation, the key will be
     * put into the passed row and additionally be returned.
     * <p/>
     * If revision support is enabled (see {@link com.w11k.lsql.Table#enableRevisionSupport()}), the revision
     * value
     * will be queried after the insert operation and be put into the passed row.
     *
     * @param row the values to be inserted
     * @throws InsertException
     */
    public Optional<Object> insert(Row row) {
        return this.insert(row, RowSerializer.INSTANCE_SPECIAL_ROWKEY, RowDeserializer.INSTANCE_SPECIAL_ROWKEY);
    }

    public Optional<Object> insert(Row row, RowSerializer<Row> rowSerializer, RowDeserializer<Row> rowDeserializer) {
        try {
            // remove the primary key column value if the value is null
            if (this.primaryKeyColumn.isPresent()) {
                String pkColumn = this.primaryKeyColumn.get();
                String pkRowKey = rowDeserializer.getDeserializedFieldName(lSql, pkColumn);
                if (row.containsKey(pkRowKey) && row.get(pkRowKey) == null) {
                    row = new Row(row);
                    row.remove(pkRowKey);
                }
            }

            List<String> columns = createColumnList(row, false, rowSerializer);

            PreparedStatement ps =
                    lSql.getStatementCreator().createInsertStatement(this, columns);

            setValuesInPreparedStatement(rowSerializer, rowDeserializer, ps, columns, row, null, null);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new InsertException(rowsAffected + " rows were affected by insert operation. Expected: 1");
            }
            if (primaryKeyColumn.isPresent()) {
                Object id = null;
                if (!row.containsKey(rowDeserializer.getDeserializedFieldName(lSql, primaryKeyColumn.get()))) {
                    // check for generated keys
                    ResultSet resultSet = ps.getGeneratedKeys();
                    if (resultSet.next()) {
                        Optional<Object> generated = lSql.extractGeneratedPk(this, resultSet);
                        if (generated.isPresent()) {
                            id = generated.get();
                            row.put(rowDeserializer.getDeserializedFieldName(lSql, primaryKeyColumn.get()), id);
                        }
                    }
                } else {
                    id = row.get(rowDeserializer.getDeserializedFieldName(lSql, primaryKeyColumn.get()));
                }

                // Set new revision
                applyNewRevision(row, id);

                return Optional.fromNullable(id);
            }
        } catch (Exception e) {
            throw new InsertException(e);
        }
        return absent();
    }

    /**
     * Updates a database row with the values in the passed {@link Row}. If you want to set {@code null} values,
     * you need to explicitly add null entries for the columns.
     * <p/>
     * If revision support is enabled (see {@link com.w11k.lsql.Table#enableRevisionSupport()}), the revision
     * value
     * will be queried after the update operation and be put into the passed row.
     *
     * @param row The values used to update the database. The row instance must contain a primary key value and,
     *            if
     *            revision support is enabled, a revision value.
     * @throws UpdateException
     */
    public void update(Row row) {
        this.update(row, RowSerializer.INSTANCE_SPECIAL_ROWKEY, RowDeserializer.INSTANCE_SPECIAL_ROWKEY);
    }

    public void update(Row row, RowSerializer<Row> rowSerializer, RowDeserializer<Row> rowDeserializer) {
        Optional<String> primaryKeyColumn = getPrimaryKeyColumn();

        if (!primaryKeyColumn.isPresent()) {
            throw new UpdateException("Can not update row without a primary key column.");
        }

        String rowKeyPk = rowDeserializer.getDeserializedFieldName(lSql, primaryKeyColumn.get());

        if (!row.containsKey(rowKeyPk)) {
            throw new UpdateException("Can not update row because the primary key column " +
                    "'" + rowKeyPk + "' is not present.");
        }

        Row whereIdVal = Row.fromKeyVals(rowKeyPk, row.get(rowKeyPk));
        updateWhere(row, whereIdVal, rowSerializer, rowDeserializer);
    }

    public void updateWhere(Row values, Row where) {
        this.updateWhere(values, where, RowSerializer.INSTANCE_SPECIAL_ROWKEY, RowDeserializer.INSTANCE_SPECIAL_ROWKEY);
    }

    public void updateWhere(Row values, Row where, RowSerializer<Row> rowSerializer, RowDeserializer<Row> rowDeserializer) {
        if (where.size() == 0) {
            throw new UpdateException("Can not update row without where values.");
        }
        try {
            List<String> valueColumns = createColumnList(values, true, rowSerializer);
            List<String> whereColumns = createColumnList(where, false, rowSerializer);

            if (revisionColumn.isPresent()) {
                valueColumns.remove(rowSerializer.getSerializedFieldName(lSql, getRevisionColumn().get().getColumnName()));
                whereColumns.remove(rowSerializer.getSerializedFieldName(lSql, getRevisionColumn().get().getColumnName()));
            }

            final int placeholderCount = valueColumns.size() + whereColumns.size();

            if (valueColumns.isEmpty()) {
                return;
            }

            PreparedStatement ps = lSql.getStatementCreator()
                    .createUpdateStatement(this, valueColumns, whereColumns);

            setValuesInPreparedStatement(rowSerializer, rowDeserializer, ps, valueColumns, values, whereColumns, where);

            // Set Revision
            if (revisionColumn.isPresent()) {
                Column col = revisionColumn.get();
                Object revision = values.get(rowSerializer.getSerializedFieldName(lSql, col.getColumnName()));
                col.getConverter().setValueInStatement(lSql, ps, placeholderCount + 1, revision);
            }

            executeUpdate(ps);

            // Set new revision
            if (getPrimaryKeyColumn().isPresent() && values.containsKey(rowSerializer.getSerializedFieldName(lSql, getPrimaryKeyColumn().get()))) {
                String pkColumn = getPrimaryKeyColumn().get();
                Object id = values.get(rowSerializer.getSerializedFieldName(lSql, pkColumn));
                applyNewRevision(values, id);
            }
        } catch (Exception e) {
            throw new UpdateException(e);
        }
    }

    /**
     * Saves the {@link Row} instance.
     * <p/>
     * If the passed row does not contain a primary key value, {@link #insert(Row)} will be called. If the passed
     * row contains a primary key value, it will be checked if this key is already existent in the database. If it
     * is, {@link #update(Row)} will be called, {@link #insert(Row)} otherwise.
     */
    public Optional<?> save(Row row) {
        return this.save(row, RowSerializer.INSTANCE_SPECIAL_ROWKEY, RowDeserializer.INSTANCE_SPECIAL_ROWKEY);
    }

    public Optional<?> save(Row row, RowSerializer<Row> rowSerializer, RowDeserializer<Row> rowDeserializer) {
        if (!primaryKeyColumn.isPresent()) {
            throw new DatabaseAccessException("save() requires a primary key column.");
        }

        String rowKeyPrimaryKey = rowDeserializer.getDeserializedFieldName(lSql, primaryKeyColumn.get());

        if (!row.containsKey(rowKeyPrimaryKey)) {
            // Insert
            return insert(row);
        } else {
            // Check if insert or update
            Object id = row.get(rowKeyPrimaryKey);
            try {
                PreparedStatement ps = lSql.getStatementCreator().createCountForIdStatement(this);
                Column column = column(getPrimaryKeyColumn().get());
                column.getConverter().setValueInStatement(lSql, ps, 1, id);
                ps.setObject(1, id);
                ResultSet rs = ps.executeQuery();
                rs.next();
                int count = rs.getInt(1);
                if (count == 0) {
                    insert(row, rowSerializer, rowDeserializer);
                } else {
                    update(row, rowSerializer, rowDeserializer);
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
     * Deletes the row with the given primary key value.
     * <p/>
     * If revision support is enabled, the operation will fail. Use {@link #delete(Row)} instead.
     *
     * @param id delete the row with this primary key value
     */
    public void delete(Object id) {
        this.delete(id, RowDeserializer.INSTANCE_SPECIAL_ROWKEY);
    }

    public void delete(Object id, RowDeserializer<Row> rowDeserializer) {
        Row row = new Row();
        row.put(rowDeserializer.getDeserializedFieldName(lSql, primaryKeyColumn.get()), id);
        delete(row);
    }

    /**
     * Deletes the row that matches the primary key value and, if enabled, the revision value in the passed {@link
     * Row} instance.
     *
     * @throws com.w11k.lsql.exceptions.DeleteException
     */
    public void delete(Row row) {
        this.delete(row, RowDeserializer.INSTANCE_SPECIAL_ROWKEY);
    }

    public void delete(Row row, RowDeserializer<Row> rowDeserializer) {
        Optional<String> primaryKeyColumn = getPrimaryKeyColumn();
        if (!primaryKeyColumn.isPresent()) {
            throw new IllegalArgumentException("Can not delete row, table has no primary column");
        }

        PreparedStatement ps = lSql.getStatementCreator().createDeleteByIdStatement(this);
        try {
            Column column = column(primaryKeyColumn.get());
            Object id = row.get(rowDeserializer.getDeserializedFieldName(lSql, primaryKeyColumn.get()));
            column.getConverter().setValueInStatement(lSql, ps, 1, id);
            if (revisionColumn.isPresent()) {
                Column revCol = revisionColumn.get();
                Object revVal = row.get(rowDeserializer.getDeserializedFieldName(lSql, revCol.getColumnName()));
                if (revVal == null) {
                    throw new IllegalStateException("Row must contain a revision.");
                }
                revCol.getConverter().setValueInStatement(lSql, ps, 2, revVal);
            }
            executeUpdate(ps);
        } catch (Exception e) {
            throw new DeleteException(e);
        }
    }

    /**
     * Loads the row with the given primary key value.
     *
     * @param id the primary key
     * @return a {@link com.google.common.base.Present} with a {@link Row} instance if the passed primary key
     * values matches a row in the database. {@link com.google.common.base.Absent} otherwise.
     */
    public Optional<LinkedRow> load(Object id) {
        return this.load(id, RowDeserializer.INSTANCE_SPECIAL_ROWKEY);
    }

    public Optional<LinkedRow> load(Object id, RowDeserializer<Row> rowDeserializer) {
        if (!this.primaryKeyColumn.isPresent()) {
            throw new IllegalArgumentException("Can not load by ID, table has no primary column");
        }
        PreparedStatement ps = createLoadPreparedStatement();

        String pkColumn = getPrimaryKeyColumn().get();
        Column column = this.column(pkColumn);
        try {
            column.getConverter().setValueInStatement(lSql, ps, 1, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        PlainQuery query = new PlainQuery(lSql, ps, null) {
            @Override
            protected RowDeserializer<Row> getRowDeserializer() {
                return rowDeserializer;
            }
        };
        for (Map.Entry<String, Column> columnInTable : this.columns.entrySet()) {
            Column value = columnInTable.getValue();
            if (value.isIgnored()) {
                continue;
            }
            query.addConverter(columnInTable.getKey(), value.getConverter());
        }
        Optional<Row> first = query.first();
        if (first.isPresent()) {
            return of(newLinkedRow(first.get()));
        } else {
            return absent();
        }
    }

    /**
     * @see com.w11k.lsql.Table#newLinkedRow(java.util.Map)
     */
    @Deprecated
    public LinkedRow newLinkedRow() {
        Map<String, Object> empty = new HashMap<>();
        return newLinkedRow(empty);
    }

    /**
     * @see com.w11k.lsql.Table#newLinkedRow(java.util.Map)
     */
    public LinkedRow newLinkedRow(Object... keyVals) {
        return newLinkedRow(Row.fromKeyVals(keyVals));
    }

    /**
     * Creates and returns a new {@link LinkedRow} linked to this table and adds {@code data}.
     * <p/>
     * A {@link LinkedRow} will call {@link #validate(String, Object)} on every
     * {@link LinkedRow#put(String, Object)} operation.
     *
     * @param data content to be added
     */
    public LinkedRow newLinkedRow(Map<String, Object> data) {
        LinkedRow linkedRow = new LinkedRow();
        linkedRow.setTable(this);
        linkedRow.setData(data);
        return linkedRow;
    }

    /**
     * Same as {@link #validate(Row)} but limited to the passed column and value.
     */
//    public Optional<? extends AbstractValidationError> validate(String javaColumnName, Object value) {
//        if (!getColumns().containsKey(javaColumnName)) {
//            return of(new KeyError(getSqlSchemaAndTableName(), javaColumnName));
//        }
//        return column(javaColumnName).validateValue(value);
//    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Table otherTable = (Table) o;
        return lSql == otherTable.lSql && sqlSchemaAndTableName.equals(otherTable.sqlSchemaAndTableName);
    }

    /**
     * Validates the passed {@link Row} instance. The validation will check
     * <ul>
     * <li>if all entries in the row instance match a database column ({@link com.w11k.lsql.validation.KeyError}),</li>
     * <li>if all entries have the correct type ({@link com.w11k.lsql.validation.TypeError}) and </li>
     * <li>if the String values are too long ({@link com.w11k.lsql.validation.StringTooLongError}).</li>
     * </ul>
     *
     * @return A {@link java.util.Map} with potential validation errors. The keys match the column names
     * and the values are subclasses of {@link com.w11k.lsql.validation.AbstractValidationError}.
     */
//    public Map<String, AbstractValidationError> validate(Row row) {
//        Map<String, AbstractValidationError> validationErrors = Maps.newHashMap();
//        for (String key : row.keySet()) {
//            Object value = row.get(key);
//            Optional<? extends AbstractValidationError> error = validate(key, value);
//            if (error.isPresent()) {
//                validationErrors.put(key, error.get());
//            }
//        }
//        return validationErrors;
//    }
    @Override
    public int hashCode() {
        int result = lSql.hashCode();
        result = 31 * result + sqlSchemaAndTableName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Table{sqlSchemaAndTableName='" + sqlSchemaAndTableName + "'}";
    }

    private PreparedStatement createLoadPreparedStatement() {
        Optional<String> primaryKeyColumn = getPrimaryKeyColumn();
        if (!primaryKeyColumn.isPresent()) {
            throw new IllegalStateException("table has no primary key column");
        }
        String pkColumn = primaryKeyColumn.get();
        Column column = column(pkColumn);
        String psString = lSql.getStatementCreator().createSelectByIdStatement(this, column, this.columns.values());
        return lSql.getStatementCreator().createPreparedStatement(lSql, psString, false);
    }

    private List<String> createColumnList(final Row row, final boolean filterIgnoreOnUpdateColumns, RowSerializer<Row> rowSerializer) {
        List<String> columns = Lists.newLinkedList(row.keySet());
        columns = columns.stream()
                .map(c -> rowSerializer.getSerializedFieldName(lSql, c))
                .filter(input -> {
                    Column column = column(input);
                    if (column == null) {
                        String message = "Column '" + input + "' does not exist in table '" + sqlSchemaAndTableName + "'. ";
                        message += "Known columns: [";
                        message += Joiner.on(",").join(Table.this.columns.keySet());
                        message += "]";
                        throw new RuntimeException(message);
                    }

                    if (filterIgnoreOnUpdateColumns && column.isIgnoreOnUpdate()) {
                        return false;
                    } else {
                        return !column.isIgnored();
                    }

                }).collect(Collectors.toList());
        return columns;
    }

    private void executeUpdate(PreparedStatement ps) throws SQLException {
        int rowsAffected = ps.executeUpdate();
        if (rowsAffected != 1) {
            throw new UpdateException(rowsAffected +
                    " rows were affected by update operation (expected 1). " +
                    "Either the ID or the revision (if enabled) is wrong.");
        }
    }

    private void applyNewRevision(Row row, Object id) throws SQLException {
        if (revisionColumn.isPresent()) {
            Object revision = queryRevision(id);
            row.put(revisionColumn.get().getColumnName(), revision);
        }
    }

    private Object queryRevision(Object id) throws SQLException {
        Column revCol = revisionColumn.get();
        PreparedStatement revQuery = lSql.getStatementCreator().createRevisionQueryStatement(this);
        revCol.getConverter().setValueInStatement(lSql, revQuery, 1, id);
        ResultSet resultSet = revQuery.executeQuery();
        resultSet.next();
        return resultSet.getObject(1);
    }

    private void fetchMeta() {
        Connection con = ConnectionUtils.getConnection(lSql);
        try {
            DatabaseMetaData md = con.getMetaData();

            // Schema name
            String sqlSchema;
            String sqlTableName;
            if (this.sqlSchemaAndTableName.contains(".")) {
                int dot = this.sqlSchemaAndTableName.indexOf('.');
                sqlSchema = this.sqlSchemaAndTableName.substring(0, dot);
                sqlTableName = this.sqlSchemaAndTableName.substring(dot + 1);
            } else {
                sqlSchema = null;
                sqlTableName = this.sqlSchemaAndTableName;
            }

            // Check table name
            ResultSet tables = md.getTables(null, sqlSchema, sqlTableName, null);
            if (!tables.next()) {
                throw new IllegalArgumentException("Unknown table '" + this.sqlSchemaAndTableName + "'");
            }

            // Missing schema name?
            if (sqlSchema == null) {
                sqlSchema = tables.getString(2);
            }

            setSchemaAndTableName(sqlSchema, sqlTableName);


            // Fetch Primary Key
            ResultSet primaryKeys =
                    md.getPrimaryKeys(null, sqlSchema, sqlTableName);

            if (!primaryKeys.next()) {
                this.primaryKeyColumn = Optional.absent();
            } else {
                String idColumn = primaryKeys.getString(4);
                this.primaryKeyColumn = of(lSql.convertExternalSqlToInternalSql(idColumn));

                // no support for compound keys yet
                if (primaryKeys.next()) {
                    this.primaryKeyColumn = absent();
                }
            }

            // Fetch all columns
            ResultSet columnsMetaData =
                    md.getColumns(null, sqlSchema, sqlTableName, null);

            while (columnsMetaData.next()) {
                String sqlColumnName = columnsMetaData.getString(4);
                int columnSize = columnsMetaData.getInt(7);
                String javaColumnName = lSql.convertExternalSqlToInternalSql(sqlColumnName);
                int sqlType = columnsMetaData.getInt(5);
                boolean isNotNullable = columnsMetaData.getString(18).equalsIgnoreCase("NO");
                Converter converter = this.lSql.getConverterForTableColumn(
                        this.lSql.convertExternalSqlToInternalSql(this.sqlSchemaAndTableName), javaColumnName, sqlType);

                Column column = new Column(this, javaColumnName, sqlType, converter, columnSize);
                column.setNullable(!isNotNullable);

                lSql.getInitColumnCallback().onNewColumn(column);
                this.columns.put(javaColumnName, column);
            }

            if (tables.next()) {
                throw new IllegalArgumentException("meta data fetch returned more than one table for '"
                        + this.sqlSchemaAndTableName + "'");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setSchemaAndTableName(String sqlSchema, String sqlTableName) {
        if (sqlSchema != null && !sqlSchema.equals("")) {
            this.sqlSchemaAndTableName = sqlSchema + "." + sqlTableName;
            this.schemaName = sqlSchema;
        } else {
            this.sqlSchemaAndTableName = sqlTableName;
            this.schemaName = null;

        }

        this.tableName = sqlTableName;
    }

    private void setValuesInPreparedStatement(RowSerializer<Row> rowSerializer,
                                              RowDeserializer<Row> rowDeserializer,
                                              PreparedStatement ps,
                                              List<String> columns1,
                                              Row values1,
                                              List<String> columns2,
                                              Row values2) {
        try {
            for (int i = 0; i < columns1.size(); i++) {
                Converter converter = column(columns1.get(i)).getConverter();
                String fieldName = rowDeserializer.getDeserializedFieldName(lSql, columns1.get(i));
                rowSerializer.serializeField(lSql, values1, converter, fieldName, ps, i + 1);

            }
            if (columns2 != null && values2 != null) {
                for (int i = 0; i < columns2.size(); i++) {
                    Converter converter = column(columns2.get(i)).getConverter();
                    String fieldName = rowDeserializer.getDeserializedFieldName(lSql, columns2.get(i));
                    rowSerializer.serializeField(
                            lSql, values2, converter, fieldName, ps, columns1.size() + i + 1);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
