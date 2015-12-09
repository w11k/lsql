package com.w11k.lsql.dialects;

import com.google.common.base.CaseFormat;
import com.google.common.base.Optional;
import com.w11k.lsql.Table;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class H2Dialect extends BaseDialect {

    @Override
    public CaseFormat getSqlCaseFormat() {
        return CaseFormat.UPPER_UNDERSCORE;
    }

//    @Override
//    public String getTableNameFromResultSetMetaData(ResultSetMetaData metaData, int columnIndex) throws SQLException {
//        JdbcResultSetMetaData h2meta = (JdbcResultSetMetaData) metaData;
//        return h2meta.getTableName(columnIndex);
//        return super.getTableNameFromResultSetMetaData(metaData, columnIndex);
//    }

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

}
