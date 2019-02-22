package com.w11k.lsql;

import com.w11k.lsql.converter.Converter;

import java.sql.ResultSet;

public interface RowDeserializer<T> {

    RowDeserializer<Row> INSTANCE_BYPASS = new RowDeserializer<Row>() {
        @Override
        public Row createRow() {
            return new Row();
        }

        @Override
        public String getDeserializedFieldName(LSql lSql, String internalSqlName) {
            return internalSqlName;
        }

        @Override
        public void deserializeField(
                LSql lSql, Row row, Converter converter, String internalSqlColumnName, ResultSet resultSet, int resultSetColumnPosition)
                throws Exception {
            row.put(internalSqlColumnName,
                    converter.getValueFromResultSet(lSql, resultSet, resultSetColumnPosition));
        }
    };

    RowDeserializer<Row> INSTANCE_SPECIAL_ROWKEY = new RowDeserializer<Row>() {
        @Override
        public Row createRow() {
            return new Row();
        }

        @Override
        public String getDeserializedFieldName(LSql lSql, String internalSqlName) {
            return lSql.convertInternalSqlToRowKey(internalSqlName);
        }

        @Override
        public void deserializeField(
                LSql lSql, Row row, Converter converter, String internalSqlColumnName, ResultSet resultSet, int resultSetColumnPosition)
                throws Exception {
            row.put(this.getDeserializedFieldName(lSql, internalSqlColumnName),
                    converter.getValueFromResultSet(lSql, resultSet, resultSetColumnPosition));
        }
    };

    T createRow() throws Exception;

    String getDeserializedFieldName(LSql lSql, String internalSqlName);

    void deserializeField(LSql lSql, T row, Converter converter, String internalSqlColumnName, ResultSet resultSet, int resultSetColumnPosition) throws Exception;

}
