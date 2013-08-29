package com.w11k.lsql.relational;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.exceptions.DatabaseAccessException;
import com.w11k.lsql.exceptions.DeleteException;
import com.w11k.lsql.exceptions.InsertException;
import com.w11k.lsql.exceptions.UpdateException;
import com.w11k.lsql.utils.ConnectionUtils;
import com.w11k.lsql.utils.PreparedStatementUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class Table {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LSql lSql;

    private final String tableName;

    private final Map<String, Column> columns = Maps.newHashMap();

    private Optional<String> primaryKeyColumn = Optional.absent();

    private Map<Table, Column> exportedForeignKeyTables = Maps.newHashMap();

    private Optional<Converter> tableConverter = Optional.absent();

    public Table(LSql lSql, String tableName) {
        this.lSql = lSql;
        this.tableName = tableName;
        fetchKeys();
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

    public Optional<String> getPrimaryKeyColumn() {
        return primaryKeyColumn;
    }

    public Map<Table, Column> getExportedForeignKeyTables() {
        return ImmutableMap.copyOf(exportedForeignKeyTables);
    }

    // ----- public -----

    public Column column(String columnName) {
        if (!columns.containsKey(columnName)) {
            columns.put(columnName, new Column(this, columnName));
        }
        return columns.get(columnName);
    }

    public Optional<Object> insert(Row row) {
        try {
            PreparedStatement ps = PreparedStatementUtils.createInsertStatement(this, row);
            ps.executeUpdate();

            // PK column not defined
            if (!primaryKeyColumn.isPresent()) {
                return absent();
            }

            // check if row already contains the PK
            if (row.containsKey(primaryKeyColumn.get())) {
                return row.getOptional(primaryKeyColumn.get());
            }

            // check for generated keys
            ResultSet resultSet = ps.getGeneratedKeys();
            if (resultSet.next()) {
                Optional<Object> generated = lSql.getDialect().extractGeneratedPk(this, resultSet);
                if (generated.isPresent()) {
                    row.put(primaryKeyColumn.get(), generated.get());
                    return generated;
                }
            }
            return absent();
        } catch (Exception e) {
            throw new InsertException(e);
        }
    }

    public Optional<Object> update(Row row) {
        if (getPrimaryKeyColumn().isPresent() && !row.containsKey(getPrimaryKeyColumn().get())) {
            throw new UpdateException("Can not update row because the primary key column " +
                    "'" + getPrimaryKeyColumn().get() + "' is not present.");
        }
        try {
            PreparedStatement ps = PreparedStatementUtils.createUpdateStatement(this, row);
            int i = ps.executeUpdate();
            if (i == 0) {
                throw new UpdateException("No rows where updated");
            }
            return row.getOptional(primaryKeyColumn.get());
        } catch (Exception e) {
            throw new InsertException(e);
        }
    }

    public Optional<Object> save(Row row) {
        if (!primaryKeyColumn.isPresent()) {
            throw new RuntimeException("save() requires a primary key column.");
        }
        if (!row.containsKey(getPrimaryKeyColumn().get())) {
            return insert(row);
        }

        Object id = row.get(primaryKeyColumn.get());
        try {
            PreparedStatement ps = PreparedStatementUtils.createCountForIdStatement(this, id);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            if (count == 0) {
                return insert(row);
            } else {
                return update(row);
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException(e);
        }
    }

    public void delete(Object id) {
        PreparedStatement ps = PreparedStatementUtils.createDeleteByIdString(this);
        try {
            column(getPrimaryKeyColumn().get()).getColumnConverter().setValueInStatement(ps, 1, id);
            ps.execute();
        } catch (Exception e) {
            throw new DeleteException(e);
        }
    }

    public Optional<QueriedRow> get(Object id) {
        String pkColumn = getPrimaryKeyColumn().get();
        Column column = column(pkColumn);
        String insertString = PreparedStatementUtils.createSelectByIdString(this, column);
        PreparedStatement preparedStatement = ConnectionUtils.prepareStatement(lSql, insertString, false);
        try {
            column.getColumnConverter().setValueInStatement(preparedStatement, 1, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<QueriedRow> queriedRows = new Query(lSql, preparedStatement).asList();
        if (queriedRows.size() == 1) {
            return of(queriedRows.get(0));
        }
        return absent();
    }

    public void fetchKeys() {
        Connection con = ConnectionUtils.getConnection(lSql);
        try {
            DatabaseMetaData md = con.getMetaData();

            // Fetch Primary Key
            ResultSet primaryKeys = md.getPrimaryKeys(null, null, lSql.getDialect().identifierJavaToSql(tableName));
            if (!primaryKeys.next()) {
                primaryKeyColumn = Optional.absent();
                return;
            }
            String idColumn = primaryKeys.getString(4);
            primaryKeyColumn = of(lSql.getDialect().identifierSqlToJava(idColumn));

            // Fetch Foreign keys
            ResultSet exportedKeys = md.getExportedKeys(null, null, lSql.getDialect().identifierJavaToSql(tableName));
            while (exportedKeys.next()) {
                String sqlTableName = exportedKeys.getString(7);
                String javaTableName = lSql.getDialect().identifierSqlToJava(sqlTableName);
                String sqlColumnName = exportedKeys.getString(8);
                String javaColumnName = lSql.getDialect().identifierSqlToJava(sqlColumnName);

                Table foreignTable = lSql.table(javaTableName);
                Column foreignColumn = foreignTable.column(javaColumnName);
                exportedForeignKeyTables.put(foreignTable, foreignColumn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table otherTable = (Table) o;
        return lSql == otherTable.lSql && tableName.equals(otherTable.tableName);
    }

    @Override
    public int hashCode() {
        int result = lSql.hashCode();
        result = 31 * result + tableName.hashCode();
        return result;
    }
}
