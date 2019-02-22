package com.w11k.lsql;

import com.w11k.lsql.converter.Converter;

import java.sql.PreparedStatement;

public interface RowSerializer<T> {

    RowSerializer<Row> INSTANCE_BYPASS = new RowSerializer<Row>() {
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
    };

    RowSerializer<Row> INSTANCE_SPECIAL_ROWKEY = new RowSerializer<Row>() {
        @Override
        public String getSerializedFieldName(LSql lSql, String fieldName) {
            return lSql.convertRowKeyToInternalSql(fieldName);
        }

        @Override
        public void serializeField(
                LSql lSql, Row row, Converter converter, String fieldName, PreparedStatement preparedStatement, int parameterIndex)
                throws Exception {

            Object value = row.get(this.getSerializedFieldName(lSql, fieldName));
            converter.setValueInStatement(lSql, preparedStatement, parameterIndex, value);
        }
    };

    String getSerializedFieldName(LSql lSql, String fieldName);

    void serializeField(LSql lSql,
                        T row,
                        Converter converter,
                        String fieldName,
                        PreparedStatement preparedStatement,
                        int parameterIndex) throws Exception;

}
