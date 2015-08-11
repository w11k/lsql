package com.w11k.lsql.dialects;

import com.google.common.base.CaseFormat;
import com.google.common.base.Optional;
import com.google.common.io.CharStreams;
import com.w11k.lsql.LSql;
import com.w11k.lsql.Table;
import com.w11k.lsql.converter.Converter;

import javax.sql.rowset.serial.SerialClob;
import java.io.IOException;
import java.io.Reader;
import java.sql.*;

public class H2Dialect extends BaseDialect {

    public H2Dialect() {
        getConverterRegistry().addConverter(
                new Converter() {
                    @Override
                    public int[] getSupportedSqlTypes() {
                        return new int[]{Types.CLOB};
                    }

                    @Override
                    public Optional<Class<String>> getSupportedJavaClass() {
                        return Optional.of(String.class);
                    }

                    public void setValue(LSql lSql, PreparedStatement ps,
                                         int index,
                                         Object val) throws SQLException {
                        ps.setClob(index, new SerialClob(val.toString().toCharArray()));
                    }

                    public Object getValue(LSql lSql, ResultSet rs,
                                           int index) throws SQLException {
                        Clob clob = rs.getClob(index);
                        if (clob != null) {
                            Reader reader = clob.getCharacterStream();
                            try {
                                return CharStreams.toString(reader);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            return null;
                        }
                    }
                });
    }

    @Override
    public CaseFormat getSqlCaseFormat() {
        return CaseFormat.UPPER_UNDERSCORE;
    }

//    @Override
//    public String getTableNameFromResultSetMetaData(ResultSetMetaData metaData, int columnIndex) throws SQLException {
//        JdbcResultSetMetaData h2meta = (JdbcResultSetMetaData) metaData;
//        return h2meta.getTableName(columnIndex);
//        return super.getTableNameFromResultSetMetaData(metaData, columnIndex);
//    }

    public Optional<Object> extractGeneratedPk(Table table,
                                               ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        if (columnCount == 0) {
            return Optional.absent();
        } else if (columnCount > 1) {
            throw new IllegalStateException("ResultSet for retrieval of the generated " +
                    "ID contains more than one column.");
        }

        return Optional.of(table.column(table.getPrimaryKeyColumn().get())
                .getConverter().getValueFromResultSet(getlSql(), resultSet, 1));
    }

}
