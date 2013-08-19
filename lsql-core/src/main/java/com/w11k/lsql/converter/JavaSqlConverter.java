package com.w11k.lsql.converter;

import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.*;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * Note: This Class must behave immutable because by default one instance is shared
 * between LSql, all Tables and all Column.
 */
public class JavaSqlConverter {

    public static class Converter {
        public void setValueInStatement(PreparedStatement ps, int index, Object val) throws Exception {
            ps.setObject(index, val);
        }

        public Object getValueFromResultSet(ResultSet rs, int index) throws Exception {
            return rs.getObject(index);
        }
    }

    private final Map<Class<?>, Converter> javaValueToSqlConverters = Maps.newHashMap();

    private final Map<Integer, Converter> sqlValueToJavaConverters = Maps.newHashMap();

    private final Converter defaultConverter;


    public JavaSqlConverter() {
        this(null);
    }

    public JavaSqlConverter(@Nullable Converter defaultConverter) {
        this.defaultConverter = defaultConverter != null ? defaultConverter : new Converter();
        addConverter(
                asList(Types.BIT, Types.TINYINT, Types.SMALLINT, Types.INTEGER, Types.BIGINT),
                Integer.class,
                new Converter() {
                    @Override
                    public void setValueInStatement(PreparedStatement ps, int index, Object val) throws Exception {
                        ps.setInt(index, (Integer) val);
                    }

                    @Override
                    public Object getValueFromResultSet(ResultSet rs, int index) throws SQLException {
                        return rs.getInt(index);
                    }
                });
        addConverter(
                asList(Types.FLOAT),
                Float.class,
                new Converter() {
                    @Override
                    public void setValueInStatement(PreparedStatement ps, int index, Object val) throws Exception {
                        ps.setFloat(index, (Float) val);
                    }

                    @Override
                    public Object getValueFromResultSet(ResultSet rs, int index) throws SQLException {
                        return rs.getFloat(index);
                    }
                });
        addConverter(
                asList(Types.DOUBLE, Types.REAL, Types.DECIMAL),
                Double.class,
                new Converter() {
                    @Override
                    public void setValueInStatement(PreparedStatement ps, int index, Object val) throws Exception {
                        ps.setDouble(index, (Double) val);
                    }

                    @Override
                    public Object getValueFromResultSet(ResultSet rs, int index) throws Exception {
                        return rs.getDouble(index);
                    }
                });
        addConverter(
                asList(Types.BOOLEAN),
                boolean.class,
                new Converter() {
                    @Override
                    public void setValueInStatement(PreparedStatement ps, int index, Object val) throws SQLException {
                        Boolean b = (Boolean) val;
                        ps.setBoolean(index, b);
                    }

                    @Override
                    public Object getValueFromResultSet(ResultSet rs, int index) throws SQLException {
                        return rs.getBoolean(index);
                    }
                });
        addConverter(
                asList(Types.CHAR),
                byte.class,
                new Converter() {
                    @Override
                    public void setValueInStatement(PreparedStatement ps, int index, Object val) throws SQLException {
                        Byte b = (Byte) val;
                        ps.setByte(index, b);
                    }

                    @Override
                    public Object getValueFromResultSet(ResultSet rs, int index) throws SQLException {
                        return rs.getByte(index);
                    }
                });
        addConverter(
                asList(Types.CLOB),
                String.class,
                new Converter() {
                    @Override
                    public void setValueInStatement(PreparedStatement ps, int index, Object val) throws Exception {
                        ps.setClob(index, new StringReader(val.toString()));
                    }

                    @Override
                    public Object getValueFromResultSet(ResultSet rs, int index) throws Exception {
                        Reader reader = rs.getClob(index).getCharacterStream();
                        return CharStreams.toString(reader);
                    }
                });
        addConverter(
                asList(Types.BLOB),
                byte[].class,
                new Converter() {
                    @Override
                    public void setValueInStatement(PreparedStatement ps, int index, Object val) throws SQLException {
                        byte[] b = (byte[]) val;
                        ByteArrayInputStream bais = new ByteArrayInputStream(b);
                        ps.setBlob(index, bais);
                    }

                    @Override
                    public Object getValueFromResultSet(ResultSet rs, int index) throws SQLException {
                        Blob blob = rs.getBlob(index);
                        return blob.getBytes(0, (int) blob.length());
                    }
                });
        addConverter(
                asList(Types.DATE),
                DateTime.class,
                new Converter() {
                    @Override
                    public Object getValueFromResultSet(ResultSet rs, int index) throws SQLException {
                        return new DateTime(rs.getDate(index).getTime());
                    }
                });
        addConverter(
                asList(Types.LONGNVARCHAR, Types.LONGVARCHAR, Types.NCHAR, Types.NVARCHAR, Types.VARCHAR),
                String.class,
                new Converter() {
                    @Override
                    public Object getValueFromResultSet(ResultSet rs, int index) throws SQLException {
                        return rs.getString(index);
                    }
                });
        addConverter(
                asList(Types.LONGNVARCHAR, Types.LONGVARCHAR, Types.NCHAR, Types.NVARCHAR, Types.VARCHAR),
                String.class,
                new Converter() {
                    @Override
                    public Object getValueFromResultSet(ResultSet rs, int index) throws SQLException {
                        return rs.getString(index);
                    }
                });

        // TODO add more types
        // static int 	DATALINK;
        // static int 	DISTINCT;
        // static int 	JAVA_OBJECT;
        // static int 	LONGVARBINARY;
        // static int 	NULL;
        // static int 	NUMERIC;
        // static int 	OTHER;
        // static int 	REF;
        // static int 	STRUCT;
        // static int 	TIME;
        // static int 	TIMESTAMP;
        // static int 	VARBINARY;
    }

    public Object getValueFromResultSet(ResultSet rs, int index) throws Exception {
        try {
            int columnType = rs.getMetaData().getColumnType(index);
            Converter converter = sqlValueToJavaConverters.get(columnType);
            converter = converter == null ? defaultConverter : converter;
            return converter.getValueFromResultSet(rs, index);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setValueInStatement(PreparedStatement ps, int index, Object val) throws Exception {
        Converter converter = javaValueToSqlConverters.get(val.getClass());
        converter = converter == null ? defaultConverter : converter;
        converter.setValueInStatement(ps, index, val);
    }

    private void addConverter(List<Integer> sqlTypes, Class<?> javaType, Converter converter) {
        for (int sqlType : sqlTypes) {
            sqlValueToJavaConverters.put(sqlType, converter);
        }
        javaValueToSqlConverters.put(javaType, converter);
    }


}
