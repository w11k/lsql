package com.w11k.lsql.dialects;

import com.google.common.base.Optional;
import com.w11k.lsql.Blob;
import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class PostgresDialect extends BaseDialect {

    public PostgresDialect() {
        getConverterRegistry().addConverter(
                new Converter() {
                    @Override
                    public int[] getSupportedSqlTypes() {
                        return new int[]{Types.BIT, Types.BOOLEAN};
                    }

                    @Override
                    public Optional<Class<Boolean>> getSupportedJavaClass() {
                        return Optional.of(Boolean.class);
                    }

                    public void setValue(LSql lSql, PreparedStatement ps,
                                         int index,
                                         Object val) throws SQLException {
                        ps.setBoolean(index, (Boolean) val);
                    }

                    public Object getValue(LSql lSql, ResultSet rs,
                                           int index) throws SQLException {
                        if (rs.getMetaData().getColumnType(index) == Types.BOOLEAN) {
                            return rs.getBoolean(index);
                        } else if (rs.getMetaData().getColumnType(index) == Types.BIT) {
                            return rs.getString(index).trim().equalsIgnoreCase("t");
                        } else {
                            throw new IllegalStateException("Database boolean column is neither BOOLEAN nor BIT.");
                        }
                    }
                });
        getConverterRegistry().addConverter(
                new Converter() {
                    @Override
                    public int[] getSupportedSqlTypes() {
                        return new int[]{Types.BINARY};
                    }

                    @Override
                    public Optional<Class<Blob>> getSupportedJavaClass() {
                        return Optional.of(Blob.class);
                    }

                    public void setValue(LSql lSql, PreparedStatement ps,
                                         int index,
                                         Object val) throws SQLException {
                        Blob blob = (Blob) val;
                        ps.setBytes(index, blob.getData());
                    }

                    public Object getValue(LSql lSql, ResultSet rs,
                                           int index) throws SQLException {
                        return new Blob(rs.getBytes(index));
                    }
                });
    }

//    @Override
//    public String getTableNameFromResultSetMetaData(ResultSetMetaData metaData,
//                                                    int columnIndex) throws SQLException {
//        Jdbc4ResultSetMetaData postgresMetaData = (Jdbc4ResultSetMetaData) metaData;
//        return postgresMetaData.getBaseTableName(columnIndex);
//    }

}
