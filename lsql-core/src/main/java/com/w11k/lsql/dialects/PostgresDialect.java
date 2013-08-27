package com.w11k.lsql.dialects;

import com.google.common.base.Optional;
import com.w11k.lsql.converter.ByTypeConverter;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.relational.Table;
import org.postgresql.jdbc4.Jdbc4ResultSetMetaData;

import java.sql.*;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

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
                            public void setValueInStatement(PreparedStatement ps, int index, Object val) throws SQLException {
                                ps.setBoolean(index, (Boolean) val);
                            }

                            public Object getValueFromResultSet(ResultSet rs, int index) throws SQLException {
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
                        com.w11k.lsql.relational.Blob.class,
                        new Converter() {
                            public void setValueInStatement(PreparedStatement ps, int index, Object val) throws SQLException {
                                com.w11k.lsql.relational.Blob blob = (com.w11k.lsql.relational.Blob) val;
                                ps.setBytes(index, blob.getData());
                            }

                            public Object getValueFromResultSet(ResultSet rs, int index) throws SQLException {
                                return new com.w11k.lsql.relational.Blob(rs.getBytes(index));
                            }
                        });

            }
        };
    }

    @Override
    public Optional<Object> extractGeneratedPk(Table table, ResultSet resultSet) throws Exception {
        String pkName = table.getPrimaryKeyColumn().get();
        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String label = metaData.getColumnLabel(i);
            if (table.getlSql().identifierSqlToJava(label).equals(pkName)) {
                return of(table.column(pkName).getColumnConverter().getValueFromResultSet(resultSet, i));
            }
        }
        return absent();
    }

    @Override
    public String getTableNameFromResultSetMetaData(ResultSetMetaData metaData, int columnIndex) throws SQLException {
        Jdbc4ResultSetMetaData postgresMetaData = (Jdbc4ResultSetMetaData) metaData;
        return postgresMetaData.getBaseTableName(columnIndex);
    }
}
