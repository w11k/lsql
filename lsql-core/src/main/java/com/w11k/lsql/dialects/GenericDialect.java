package com.w11k.lsql.dialects;

import com.google.common.base.Optional;
import com.w11k.lsql.LSql;
import com.w11k.lsql.Table;
import com.w11k.lsql.converter.ConverterRegistry;
import com.w11k.lsql.converter.sqltypes.*;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class GenericDialect {

    private LSql lSql;

    private StatementCreator statementCreator = new StatementCreator();

    private ConverterRegistry converterRegistry = new ConverterRegistry();

    private IdentifierConverter identifierConverter = IdentifierConverter.JAVA_CAMEL_CASE_TO_SQL_LOWER_UNDERSCORE;

    public GenericDialect() {
        // http://docs.oracle.com/javase/1.5.0/docs/guide/jdbc/getstart/mapping.html

        this.converterRegistry.addConverter(IntConverter.INSTANCE);
        this.converterRegistry.addConverter(DoubleConverter.INSTANCE);
        this.converterRegistry.addConverter(FloatConverter.INSTANCE);
        this.converterRegistry.addConverter(BooleanConverter.INSTANCE);
        this.converterRegistry.addConverter(StringConverter.INSTANCE);
        this.converterRegistry.addConverter(BinaryConverter.INSTANCE);
        this.converterRegistry.addConverter(ByteConverter.INSTANCE);
        this.converterRegistry.addConverter(BlobConverter.INSTANCE);
        this.converterRegistry.addConverter(ClobConverter.INSTANCE);
        this.converterRegistry.addConverter(JodaDateConverter.INSTANCE);


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

    public IdentifierConverter getIdentifierConverter() {
        return identifierConverter;
    }

    public void setIdentifierConverter(IdentifierConverter identifierConverter) {
        this.identifierConverter = identifierConverter;
    }

    public String getTableNameFromResultSetMetaData(ResultSetMetaData metaData,
                                                    int columnIndex) throws SQLException {
        return getIdentifierConverter().sqlToJava(metaData.getTableName(columnIndex));
    }

    public String getColumnNameFromResultSetMetaData(ResultSetMetaData metaData,
                                                     int columnIndex) throws SQLException {
        return getIdentifierConverter().sqlToJava(metaData.getColumnName(columnIndex));
    }

    public Optional<Object> extractGeneratedPk(Table table,
                                               ResultSet resultSet) throws SQLException {
        String pkName = table.getPrimaryKeyColumn().get();
        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String label = metaData.getColumnLabel(i);
            if (getIdentifierConverter().sqlToJava(label).equals(pkName)) {
                return of(table.column(pkName).getConverter()
                        .getValueFromResultSet(getlSql(), resultSet, i));
            }
        }
        return absent();
    }

}
