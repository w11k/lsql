package com.w11k.lsql.dialects;

import com.w11k.lsql.Blob;
import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.ByTypeConverter;
import com.w11k.lsql.converter.Converter;
import org.postgresql.jdbc4.Jdbc4ResultSetMetaData;

import java.sql.*;

public class PostgresDialect extends BaseDialect {

    @Override
    public Converter getConverter() {
        return new ByTypeConverter() {
            @Override
            protected void init() {
                super.init();
                setConverter(
                        new int[]{Types.BIT, Types.BOOLEAN},
                        Boolean.class,
                        new Converter() {
                            public void setValueInStatement(LSql lSql, PreparedStatement ps,
                                                            int index,
                                                            Object val) throws SQLException {
                                ps.setBoolean(index, (Boolean) val);
                            }

                            public Object getValueFromResultSet(LSql lSql, ResultSet rs,
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
                setConverter(
                        new int[]{Types.BINARY},
                        com.w11k.lsql.Blob.class,
                        new Converter() {
                            public void setValueInStatement(LSql lSql, PreparedStatement ps,
                                                            int index,
                                                            Object val) throws SQLException {
                                Blob blob = (Blob) val;
                                ps.setBytes(index, blob.getData());
                            }

                            public Object getValueFromResultSet(LSql lSql, ResultSet rs,
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
