package com.w11k.lsql.dialects;

import com.w11k.lsql.Blob;
import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.ByTypeConverterRegistry;
import com.w11k.lsql.converter.Converter;
import org.postgresql.jdbc4.Jdbc4ResultSetMetaData;

import java.sql.*;

public class PostgresDialect extends BaseDialect {

    @Override
    public ByTypeConverterRegistry getConverterRegistry() {
        return new ByTypeConverterRegistry() {
            @Override
            protected void init() {
                super.init();
                addConverter(
                        new Converter() {
                            @Override
                            public int[] getSupportedSqlTypes() {
                                return new int[]{Types.BIT, Types.BOOLEAN};
                            }

                            @Override
                            public Class<?> getSupportedJavaClass() {
                                return Boolean.class;
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
                addConverter(
                        new Converter() {
                            @Override
                            public int[] getSupportedSqlTypes() {
                                return new int[]{Types.BINARY};
                            }

                            @Override
                            public Class<?> getSupportedJavaClass() {
                                return com.w11k.lsql.Blob.class;
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
        };
    }

    @Override
    public String getTableNameFromResultSetMetaData(ResultSetMetaData metaData,
                                                    int columnIndex) throws SQLException {
        Jdbc4ResultSetMetaData postgresMetaData = (Jdbc4ResultSetMetaData) metaData;
        return postgresMetaData.getBaseTableName(columnIndex);
    }

}
