package com.w11k.lsql.dialects;

import com.google.common.base.CaseFormat;
import com.google.common.base.Optional;
import com.w11k.lsql.LSql;
import com.w11k.lsql.PreparedStatementCreator;
import com.w11k.lsql.Table;
import com.w11k.lsql.converter.ByTypeConverterRegistry;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class BaseDialect {

    private LSql lSql;

    private PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator();

    private ByTypeConverterRegistry converterRegistry = new ByTypeConverterRegistry();

    public LSql getlSql() {
        return lSql;
    }

    public void setlSql(LSql lSql) {
        this.lSql = lSql;
    }

    public ByTypeConverterRegistry getConverterRegistry() {
        return converterRegistry;
    }

    public PreparedStatementCreator getPreparedStatementCreator() {
        return preparedStatementCreator;
    }

    public void setPreparedStatementCreator(PreparedStatementCreator preparedStatementCreator) {
        this.preparedStatementCreator = preparedStatementCreator;
    }

    public CaseFormat getJavaCaseFormat() {
        return CaseFormat.LOWER_UNDERSCORE;
    }

    public CaseFormat getSqlCaseFormat() {
        return CaseFormat.LOWER_UNDERSCORE;
    }

    public String identifierSqlToJava(String sqlName) {
        return getSqlCaseFormat().to(getJavaCaseFormat(), sqlName);
    }

    public String identifierJavaToSql(String javaName) {
        return getJavaCaseFormat().to(getSqlCaseFormat(), javaName);
    }

//    public String getTableNameFromResultSetMetaData(ResultSetMetaData metaData,
//                                                    int columnIndex) throws SQLException {
//        return metaData.getTableName(columnIndex);
//    }

    public Optional<Object> extractGeneratedPk(Table table,
                                               ResultSet resultSet) throws SQLException {
        String pkName = table.getPrimaryKeyColumn().get();
        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String label = metaData.getColumnLabel(i);
            if (identifierSqlToJava(label).equals(pkName)) {
                return of(table.column(pkName).getConverter()
                        .getValueFromResultSet(getlSql(), resultSet, i));
            }
        }
        return absent();
    }

}
