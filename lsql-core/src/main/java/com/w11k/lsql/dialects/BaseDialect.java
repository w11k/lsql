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

public class BaseDialect {

    protected LSql lSql;

    protected StatementCreator statementCreator = new StatementCreator();

    protected ByTypeConverterRegistry converterRegistry = new ByTypeConverterRegistry();

    public BaseDialect() {
        converterRegistry.addConverter(new BinaryConverter());
        converterRegistry.addConverter(new BlobConverter());
        converterRegistry.addConverter(new BooleanConverter());
        converterRegistry.addConverter(new ClobConverter());
        converterRegistry.addConverter(new DoubleConverter());
        converterRegistry.addConverter(new FloatConverter());
        converterRegistry.addConverter(new IntConverter());
        converterRegistry.addConverter(new JodaDateConverter());
        converterRegistry.addConverter(new StringConverter());

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
