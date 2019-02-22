package com.w11k.lsql;

import com.w11k.lsql.converter.Converter;

import java.sql.PreparedStatement;

public interface RowSerializer<T> {

    RowSerializer<Row> INSTANCE = new Serializer();

    RowSerializer<Row> INSTANCE_SPECIAL_ROWKEY = new RowSerializer<Row>() {
        @Override
        public String getSerializedFieldName(LSql lSql, String fieldName) {
            return lSql.convertRowKeyToInternalSql(fieldName);
        }

        @Override
        public void serializeField(
                LSql lSql, Row row, Converter converter, String fieldName, PreparedStatement preparedStatement, int parameterIndex)
                throws Exception {

            converter.setValueInStatement(lSql, preparedStatement, parameterIndex, row.get(fieldName));
        }
    };

    String getSerializedFieldName(LSql lSql, String fieldName);

    void serializeField(LSql lSql,
                        T row,
                        Converter converter,
                        String fieldName,
                        PreparedStatement preparedStatement,
                        int parameterIndex) throws Exception;

    class Serializer implements RowSerializer<Row> {
        @Override
        public String getSerializedFieldName(LSql lSql, String fieldName) {
            return fieldName;
        }

        @Override
        public void serializeField(
                LSql lSql, Row row, Converter converter, String fieldName, PreparedStatement preparedStatement, int parameterIndex)
                throws Exception {

            converter.setValueInStatement(lSql, preparedStatement, parameterIndex, row.get(fieldName));
        }
    }

}
