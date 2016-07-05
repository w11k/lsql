package com.w11k.lsql.dialects;

import com.google.common.base.Optional;
import com.w11k.lsql.Table;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SqlServerDialect extends GenericDialect {

    public Optional<Object> extractGeneratedPk(Table table,
                                               ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        if (columnCount == 0) {
            return Optional.absent();
        } else if (columnCount > 1) {
            throw new IllegalStateException("ResultSet for retrieval of the generated " +
                    "ID contains more than one column.");
        }

        Optional<Object> id = Optional.of(table.column(table.getPrimaryKeyColumn().get())
                .getConverter().getValueFromResultSet(getlSql(), resultSet, 1));

        // Weird behaviour in SQL Server. Generated INT PRIMARY KEYS are returned
        // as NUMERIC. Hence we convert double to int because we assume that nobody would
        // use decimal numbers as primary keys.
        if (id.isPresent()) {
            Object o = id.get();
            if (o instanceof Double) {
                Double d = (Double) o;
                return Optional.of((Object) d.intValue());
            }
        }
        return id;
    }

}
