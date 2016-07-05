package com.w11k.lsql.dialects;

import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;
import org.postgresql.jdbc4.Jdbc4ResultSetMetaData;

import java.sql.*;

public class PostgresDialect extends GenericDialect {

    private static class BooleanConverter extends Converter {

        public BooleanConverter() {
            super(Boolean.class,
                    new int[]{Types.BIT, Types.BOOLEAN},
                    Types.BIT);
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
        getConverterRegistry().addConverter(new BooleanConverter());
    }

    @Override
    public String getTableNameFromResultSetMetaData(ResultSetMetaData metaData,
                                                    int columnIndex) throws SQLException {
        Jdbc4ResultSetMetaData postgresMetaData = (Jdbc4ResultSetMetaData) metaData;
        return postgresMetaData.getBaseTableName(columnIndex);
    }

    @Override
    public String getColumnNameFromResultSetMetaData(ResultSetMetaData metaData,
                                                    int columnIndex) throws SQLException {
        Jdbc4ResultSetMetaData postgresMetaData = (Jdbc4ResultSetMetaData) metaData;
        return postgresMetaData.getBaseColumnName(columnIndex);
    }

}
