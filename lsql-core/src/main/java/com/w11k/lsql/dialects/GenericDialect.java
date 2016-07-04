package com.w11k.lsql.dialects;

import com.google.common.base.CaseFormat;
import com.google.common.base.Optional;
import com.w11k.lsql.LSql;
import com.w11k.lsql.Table;
import com.w11k.lsql.typemapper.ConverterRegistry;
import com.w11k.lsql.typemapper.sqltypes.*;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class GenericDialect {

    private LSql lSql;

    private StatementCreator statementCreator = new StatementCreator();

    private ConverterRegistry converterRegistry = new ConverterRegistry();

    private CaseFormat javaCaseFormat = CaseFormat.LOWER_CAMEL;

    private CaseFormat sqlCaseFormat = CaseFormat.LOWER_UNDERSCORE;

    public GenericDialect() {
        // http://docs.oracle.com/javase/1.5.0/docs/guide/jdbc/getstart/mapping.html

        this.converterRegistry.addConverter(IntTypeMapper.INSTANCE);
        this.converterRegistry.addConverter(DoubleTypeMapper.INSTANCE);
        this.converterRegistry.addConverter(FloatTypeMapper.INSTANCE);
        this.converterRegistry.addConverter(BooleanTypeMapper.INSTANCE);
        this.converterRegistry.addConverter(StringTypeMapper.INSTANCE);
        this.converterRegistry.addConverter(BinaryTypeMapper.INSTANCE);
        this.converterRegistry.addConverter(ByteTypeMapper.INSTANCE);
        this.converterRegistry.addConverter(BlobTypeMapper.INSTANCE);
        this.converterRegistry.addConverter(ClobTypeMapper.INSTANCE);
        this.converterRegistry.addConverter(JodaDateTypeMapper.INSTANCE);


        // TODO Missing primitives

        // TODO add more types
        // static int 	LONGVARBINARY;
        // static int 	STRUCT;
        // static int 	TIME;
        // static int 	DATE;
    }

    public LSql getlSql() {
        return lSql;
    }

    public void setlSql(LSql lSql) {
        this.lSql = lSql;
    }

    public ConverterRegistry getConverterRegistry() {
        return this.converterRegistry;
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
                return of(table.column(pkName).getTypeMapper()
                        .getValueFromResultSet(getlSql(), resultSet, i));
            }
        }
        return absent();
    }

}
