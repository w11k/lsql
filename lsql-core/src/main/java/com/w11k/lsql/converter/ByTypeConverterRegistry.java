package com.w11k.lsql.converter;

import com.google.common.collect.Maps;
import com.w11k.lsql.LSql;
import org.joda.time.DateTime;

import java.sql.*;
import java.util.Map;

public class ByTypeConverterRegistry {

    private final Map<Class<?>, Converter> javaValueToSqlConverters = Maps.newHashMap();

    private final Map<Integer, Converter> sqlValueToJavaConverters = Maps.newHashMap();


    public ByTypeConverterRegistry() {
        init();
    }

    public Converter getConverterForSqlType(int sqlType) {
        return sqlValueToJavaConverters.get(sqlType);
    }

    public Converter getConverterForJavaValue(Object value) {
        return javaValueToSqlConverters.get(value.getClass());
    }

    public void addConverter(Converter converter) {
        for (int sqlType : converter.getSupportedSqlTypes()) {
            sqlValueToJavaConverters.put(sqlType, converter);
        }
        javaValueToSqlConverters.put(converter.getSupportedJavaClass(), converter);
    }

    protected void init() {
        addConverter(
                new Converter() {
                    public int[] getSupportedSqlTypes() {
                        return new int[]{Types.BIT, Types.BOOLEAN};
                    }

                    public Class<?> getSupportedJavaClass() {
                        return Boolean.class;
                    }

                    public void setValue(LSql lSql, PreparedStatement ps, int index,
                                         Object val) throws SQLException {
                        ps.setBoolean(index, (Boolean) val);
                    }

                    public Object getValue(LSql lSql, ResultSet rs,
                                           int index) throws SQLException {
                        return rs.getBoolean(index);
                    }
                });
        addConverter(
                new Converter() {
                    public int[] getSupportedSqlTypes() {
                        return new int[]{Types.TINYINT, Types.SMALLINT, Types.INTEGER, Types.BIGINT};
                    }

                    public Class<?> getSupportedJavaClass() {
                        return Integer.class;
                    }

                    public void setValue(LSql lSql, PreparedStatement ps, int index,
                                         Object val) throws SQLException {
                        ps.setInt(index, (Integer) val);
                    }

                    public Object getValue(LSql lSql, ResultSet rs,
                                           int index) throws SQLException {
                        return rs.getInt(index);
                    }
                });
        addConverter(
                new Converter() {
                    public int[] getSupportedSqlTypes() {
                        return new int[]{Types.FLOAT};
                    }

                    public Class<?> getSupportedJavaClass() {
                        return Float.class;
                    }

                    public void setValue(LSql lSql, PreparedStatement ps, int index,
                                         Object val) throws SQLException {
                        ps.setFloat(index, (Float) val);
                    }

                    public Object getValue(LSql lSql, ResultSet rs,
                                           int index) throws SQLException {
                        return rs.getFloat(index);
                    }
                });
        addConverter(
                new Converter() {
                    public int[] getSupportedSqlTypes() {
                        return new int[]{Types.DOUBLE, Types.REAL, Types.DECIMAL};
                    }

                    public Class<?> getSupportedJavaClass() {
                        return Double.class;
                    }

                    public void setValue(LSql lSql, PreparedStatement ps, int index,
                                         Object val) throws SQLException {
                        ps.setDouble(index, (Double) val);
                    }

                    public Object getValue(LSql lSql, ResultSet rs,
                                           int index) throws SQLException {
                        return rs.getDouble(index);
                    }
                });
        addConverter(
                new Converter() {
                    public int[] getSupportedSqlTypes() {
                        return new int[]{Types.CHAR};
                    }

                    public Class<?> getSupportedJavaClass() {
                        return Character.class;
                    }

                    public void setValue(LSql lSql, PreparedStatement ps, int index,
                                         Object val) throws SQLException {
                        ps.setString(index, val.toString());
                    }

                    public Object getValue(LSql lSql, ResultSet rs,
                                           int index) throws SQLException {
                        return rs.getString(index).charAt(0);
                    }
                });
        addConverter(
                new Converter() {
                    public int[] getSupportedSqlTypes() {
                        return new int[]{Types.VARCHAR, Types.LONGNVARCHAR, Types.LONGVARCHAR, Types.NCHAR, Types.NVARCHAR};
                    }

                    public Class<?> getSupportedJavaClass() {
                        return String.class;
                    }

                    public void setValue(LSql lSql, PreparedStatement ps, int index,
                                         Object val) throws SQLException {
                        ps.setString(index, val.toString());
                    }

                    public Object getValue(LSql lSql, ResultSet rs,
                                           int index) throws SQLException {
                        return rs.getString(index);
                    }
                });
        addConverter(
                new Converter() {
                    public int[] getSupportedSqlTypes() {
                        return new int[]{Types.TIMESTAMP};
                    }

                    public Class<?> getSupportedJavaClass() {
                        return DateTime.class;
                    }

                    public void setValue(LSql lSql, PreparedStatement ps, int index,
                                         Object val) throws SQLException {
                        DateTime dt = (DateTime) val;
                        Timestamp ts = new Timestamp(dt.getMillis());
                        ps.setTimestamp(index, ts);
                    }

                    public Object getValue(LSql lSql, ResultSet rs,
                                           int index) throws SQLException {
                        Timestamp timestamp = rs.getTimestamp(index);
                        if (timestamp != null) {
                            return new DateTime(timestamp.getTime());
                        } else {
                            return null;
                        }
                    }
                });
        addConverter(
                new Converter() {
                    public int[] getSupportedSqlTypes() {
                        return new int[]{Types.BLOB};
                    }

                    public Class<?> getSupportedJavaClass() {
                        return com.w11k.lsql.Blob.class;
                    }

                    public void setValue(LSql lSql, PreparedStatement ps, int index,
                                         Object val) throws SQLException {
                        com.w11k.lsql.Blob blob = (com.w11k.lsql.Blob) val;
                        ps.setBlob(index, blob.getInputStream());
                    }

                    public Object getValue(LSql lSql, ResultSet rs,
                                           int index) throws SQLException {
                        Blob blob = rs.getBlob(index);
                        return new com.w11k.lsql.Blob(blob.getBinaryStream());
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
        // static int 	DATE;
        // static int 	VARBINARY;
    }

}
