package com.w11k.lsql.dialects;

import com.google.common.base.CaseFormat;
import com.google.common.base.Optional;
import com.google.common.io.CharStreams;
import com.w11k.lsql.converter.ByTypeConverter;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.relational.Table;

import javax.sql.rowset.serial.SerialClob;
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
                            public void setValueInStatement(PreparedStatement ps, int index, Object val) throws Exception {
                                ps.setClob(index, new SerialClob(val.toString().toCharArray()));
                            }

                            public Object getValueFromResultSet(ResultSet rs, int index) throws Exception {
                                Clob clob = rs.getClob(index);
                                if (clob != null) {
                                    Reader reader = clob.getCharacterStream();
                                    return CharStreams.toString(reader);
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

    public Optional<Object> extractGeneratedPk(Table table, ResultSet resultSet) throws Exception {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        if (columnCount == 0) {
            return Optional.absent();
        } else if (columnCount > 1) {
            throw new IllegalStateException("ResultSet for retrieval of the generated " +
                    "ID contains more than one column.");
        }

        return Optional.of(table.column(
                table.getPrimaryKeyColumn().get()).getColumnConverter().getValueFromResultSet(resultSet, 1));
    }

}
