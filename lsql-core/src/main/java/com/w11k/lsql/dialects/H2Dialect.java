package com.w11k.lsql.dialects;

import com.google.common.base.Optional;
import com.w11k.lsql.Table;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class H2Dialect extends GenericDialect {

    public H2Dialect() {
        setStatementCreator(new StatementCreator("`", "`"));
    }

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

        return Optional.of(table.column(table.getPrimaryKeyColumn().get())
                .getConverter().getValueFromResultSet(getlSql(), resultSet, 1));
    }

    public String convertExternalSqlToInternalSql(String externalSql) {
        return externalSql.toLowerCase();
    }

    public String convertInternalSqlToExternalSql(String internalSql) {
        return internalSql.toUpperCase();
    }

}
