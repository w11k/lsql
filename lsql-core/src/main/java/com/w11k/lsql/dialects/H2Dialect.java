package com.w11k.lsql.dialects;

import com.google.common.base.CaseFormat;
import com.google.common.base.Optional;
import com.google.common.io.CharStreams;
import com.w11k.lsql.LSql;
import com.w11k.lsql.Table;
import com.w11k.lsql.converter.ByTypeConverter;
import com.w11k.lsql.converter.Converter;

import javax.sql.rowset.serial.SerialClob;
import java.io.IOException;
import java.io.Reader;
import java.sql.*;

public class H2Dialect extends BaseDialect {

    @Override
    public Converter getConverter() {
        return new ByTypeConverter() {
            @Override
            protected void init() {
                super.init();
                setConverter(
                        new int[]{Types.CLOB},
                        String.class,
                        new Converter() {
                            public void setValueInStatement(LSql lSql, PreparedStatement ps,
                                                            int index,
                                                            Object val) throws SQLException {
                                ps.setClob(index, new SerialClob(val.toString().toCharArray()));
                            }

                            public Object getValueFromResultSet(LSql lSql, ResultSet rs,
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
        };
    }

    @Override
    public CaseFormat getSqlCaseFormat() {
        return CaseFormat.UPPER_UNDERSCORE;
    }

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
                .getColumnConverter().getValueFromResultSet(getlSql(), resultSet, 1));
    }

}
