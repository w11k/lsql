package com.w11k.lsql.dialects;

import com.google.common.base.CaseFormat;
import com.google.common.base.Optional;
import com.w11k.lsql.LSql;
import com.w11k.lsql.Table;
import com.w11k.lsql.converter.ByTypeConverterRegistry;
import com.w11k.lsql.converter.sqltypes.*;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class GenericDialect {

    private LSql lSql;

    private StatementCreator statementCreator = new StatementCreator();

    private ByTypeConverterRegistry converterRegistry = new ByTypeConverterRegistry();

    private CaseFormat javaCaseFormat = CaseFormat.LOWER_CAMEL;

    private CaseFormat sqlCaseFormat = CaseFormat.LOWER_UNDERSCORE;

    public GenericDialect() {
        converterRegistry.addConverter(BinaryConverter.INSTANCE);
        converterRegistry.addConverter(BlobConverter.INSTANCE);
        converterRegistry.addConverter(BooleanConverter.INSTANCE);
        converterRegistry.addConverter(ClobConverter.INSTANCE);
        converterRegistry.addConverter(DoubleConverter.INSTANCE);
        converterRegistry.addConverter(FloatConverter.INSTANCE);
        converterRegistry.addConverter(IntConverter.INSTANCE);
        converterRegistry.addConverter(JodaDateConverter.INSTANCE);
        converterRegistry.addConverter(StringConverter.INSTANCE);

        // TODO add more types
        // static int 	DATALINK;
        // static int 	DISTINCT;
        // static int 	JAVA_OBJECT;
        // static int 	LONGVARBINARY;
        // static int 	NULL;
        // static int 	NUMERIC;
        // static int 	OTHER;
        // static int 	REF;
        // static int 	STRUCT;
        // static int 	TIME;
        // static int 	DATE;
        // static int 	VARBINARY;
    }

    public LSql getlSql() {
        return lSql;
    }

    public void setlSql(LSql lSql) {
        this.lSql = lSql;
    }

    public ByTypeConverterRegistry getConverterRegistry() {
        return converterRegistry;
    }

    public StatementCreator getStatementCreator() {
        return statementCreator;
    }

    public CaseFormat getJavaCaseFormat() {
        return javaCaseFormat;
    }

    public void setJavaCaseFormat(CaseFormat javaCaseFormat) {
        this.javaCaseFormat = javaCaseFormat;
    }

    public CaseFormat getSqlCaseFormat() {
        return sqlCaseFormat;
    }

    public void setSqlCaseFormat(CaseFormat sqlCaseFormat) {
        this.sqlCaseFormat = sqlCaseFormat;
    }

    public String identifierSqlToJava(String sqlName) {
        CaseFormat sqlStyle = getSqlCaseFormat();
        if (sqlStyle.equals(CaseFormat.LOWER_UNDERSCORE)) {
            sqlName = sqlName.toLowerCase();
        }
        CaseFormat javaStyle = getJavaCaseFormat();
        return sqlStyle.to(javaStyle, sqlName);
    }

    public String identifierJavaToSql(String javaName) {
        CaseFormat sqlStyle = getSqlCaseFormat();
        CaseFormat javaStyle = getJavaCaseFormat();
        return javaStyle.to(sqlStyle, javaName);
    }

    public String getTableNameFromResultSetMetaData(ResultSetMetaData metaData,
                                                    int columnIndex) throws SQLException {
        return identifierSqlToJava(metaData.getTableName(columnIndex));
    }

    public String getColumnNameFromResultSetMetaData(ResultSetMetaData metaData,
                                                     int columnIndex) throws SQLException {
        return identifierSqlToJava(metaData.getColumnName(columnIndex));
    }

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
