package com.w11k.lsql.dialects;

import com.google.common.base.Optional;
import com.w11k.lsql.LSql;
import com.w11k.lsql.Table;
import com.w11k.lsql.converter.ConverterRegistry;
import com.w11k.lsql.converter.types.*;
import org.joda.time.DateTime;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class GenericDialect {

    private LSql lSql;

    private StatementCreator statementCreator = new StatementCreator();

    private ConverterRegistry converterRegistry = new ConverterRegistry();

    public GenericDialect() {
        // http://docs.oracle.com/javase/1.5.0/docs/guide/jdbc/getstart/mapping.html

        // Number
        this.converterRegistry.addConverter(new NumberConverter(), false);

        // int
        for (int sqlType : IntConverter.SQL_TYPES) {
            this.converterRegistry.addSqlToJavaConverter(new IntConverter(sqlType), false);
        }
        this.converterRegistry.addJavaToSqlConverter(
                this.converterRegistry.getConverterForSqlType(Types.INTEGER), false);

        // long
        this.converterRegistry.addConverter(new LongConverter(), false);

        // double
        for (int sqlType : DoubleConverter.SQL_TYPES) {
            this.converterRegistry.addSqlToJavaConverter(new DoubleConverter(sqlType), false);
        }
        this.converterRegistry.addJavaToSqlConverter(
                this.converterRegistry.getConverterForSqlType(Types.DOUBLE), false);

        this.converterRegistry.addConverter(new FloatConverter(), false);

        // boolean
        for (int sqlType : BooleanConverter.SQL_TYPES) {
            this.converterRegistry.addSqlToJavaConverter(new BooleanConverter(sqlType), false);
        }
        this.converterRegistry.addJavaToSqlConverter(
                this.converterRegistry.getConverterForSqlType(Types.BOOLEAN), false);

        // string
        for (int sqlType : StringConverter.SQL_TYPES) {
            this.converterRegistry.addSqlToJavaConverter(new StringConverter(sqlType), false);
        }
        this.converterRegistry.addJavaToSqlConverter(
                this.converterRegistry.getConverterForSqlType(Types.VARCHAR), false);

        // binary
        for (int sqlType : BinaryConverter.SQL_TYPES) {
            this.converterRegistry.addSqlToJavaConverter(new BinaryConverter(sqlType), false);
        }
        this.converterRegistry.addJavaToSqlConverter(
                this.converterRegistry.getConverterForSqlType(Types.BINARY), false);

//        this.converterRegistry.addConverter(new ByteConverter());
//        this.converterRegistry.addConverter(new BlobConverter(), false);
        this.converterRegistry.addSqlToJavaConverter(new ClobConverter(), false);
        this.converterRegistry.addConverter(new JodaDateTimeConverter(), false);
        this.converterRegistry.addConverter(new JodaLocalDateConverter(), false);

        // Aliases
        this.converterRegistry.addTypeAlias("number", Number.class);
        this.converterRegistry.addTypeAlias("int", Integer.class);
        this.converterRegistry.addTypeAlias("integer", Integer.class);
        this.converterRegistry.addTypeAlias("long", Long.class);
        this.converterRegistry.addTypeAlias("float", Float.class);
        this.converterRegistry.addTypeAlias("double", Double.class);
        this.converterRegistry.addTypeAlias("string", String.class);
        this.converterRegistry.addTypeAlias("boolean", Boolean.class);
        this.converterRegistry.addTypeAlias("date", DateTime.class);
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

    public void setConverterRegistry(ConverterRegistry converterRegistry) {
        this.converterRegistry = converterRegistry;
    }

    public void setStatementCreator(StatementCreator statementCreator) {
        this.statementCreator = statementCreator;
    }

    public StatementCreator getStatementCreator() {
        return statementCreator;
    }

    public String convertExternalSqlToInternalSql(String externalSql) {
        return externalSql.toLowerCase();
    }

    public String convertInternalSqlToExternalSql(String internalSql) {
        return internalSql.toUpperCase();
    }

    public String getSqlSchemaAndTableNameFromResultSetMetaData(ResultSetMetaData metaData,
                                                                int columnIndex) throws SQLException {

        String schema = metaData.getSchemaName(columnIndex);
        String table =  metaData.getTableName(columnIndex);

        return schema.equals("") ? table : schema + "." + table;
    }

    public String getSqlColumnNameFromResultSetMetaData(ResultSetMetaData metaData,
                                                        int columnIndex) throws SQLException {
        return metaData.getColumnName(columnIndex);
    }

    public Optional<Object> extractGeneratedPk(Table table,
                                               ResultSet resultSet) throws SQLException {
        String pkName = table.getPrimaryKeyColumn().get();
        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String label = metaData.getColumnLabel(i);
            if (this.convertExternalSqlToInternalSql(label).equals(pkName)) {
                return of(table.column(pkName).getConverter()
                        .getValueFromResultSet(getlSql(), resultSet, i));
            }
        }
        return absent();
    }

}
