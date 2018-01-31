package com.w11k.lsql.dialects;

import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;
import org.postgresql.jdbc.PgResultSetMetaData;

import java.sql.*;

public class PostgresDialect extends GenericDialect {

    private static class BooleanConverter extends Converter {

        public BooleanConverter(int sqlType) {
            super(Boolean.class, sqlType);
        }

        @Override
        protected void setValue(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException {
            ps.setBoolean(index, (Boolean) val);
        }

        @Override
        protected Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException {
            if (rs.getMetaData().getColumnType(index) == Types.BOOLEAN) {
                return rs.getBoolean(index);
            } else if (rs.getMetaData().getColumnType(index) == Types.BIT) {
                return rs.getString(index).trim().equalsIgnoreCase("t");
            } else {
                throw new IllegalStateException("Database boolean column is neither BOOLEAN nor BIT.");
            }
        }
    }

    public PostgresDialect() {
        for (int sqlType : com.w11k.lsql.converter.types.BooleanConverter.SQL_TYPES) {
            getConverterRegistry().addSqlToJavaConverter(new BooleanConverter(sqlType), true);
        }
        getConverterRegistry().addJavaToSqlConverter(
                getConverterRegistry().getConverterForSqlType(Types.BOOLEAN), true);

    }

    @Override
    public String getSqlSchemaAndTableNameFromResultSetMetaData(ResultSetMetaData metaData,
                                                                int columnIndex) throws SQLException {

        PgResultSetMetaData postgresMetaData = (PgResultSetMetaData) metaData;

        String schema = postgresMetaData.getBaseSchemaName(columnIndex);
        String table =  postgresMetaData.getBaseTableName(columnIndex);

        return schema.equals("") ? table : schema + "." + table;
    }

    @Override
    public String getSqlColumnNameFromResultSetMetaData(ResultSetMetaData metaData,
                                                        int columnIndex) throws SQLException {
        PgResultSetMetaData postgresMetaData = (PgResultSetMetaData) metaData;
        return postgresMetaData.getBaseColumnName(columnIndex);
    }

}
