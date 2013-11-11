package com.w11k.lsql.converter;

import com.google.common.collect.Maps;
import com.w11k.lsql.LSql;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Map;

/**
 * Note: This Class must behave immutable because by default one instance is shared
 * between LSql, all Tables and all Column.
 */
public class ByTypeConverter implements Converter {

    private final Map<Class<?>, Converter> javaValueToSqlConverters = Maps.newHashMap();

    private final Map<Integer, Converter> sqlValueToJavaConverters = Maps.newHashMap();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public ByTypeConverter() {
        init();
    }

    public Object getValueFromResultSet(LSql lSql, ResultSet rs, int index) throws SQLException {
        try {
            int columnType = rs.getMetaData().getColumnType(index);
            logger.trace("SQL type in ResultSet is {}", columnType);
            Converter converter = sqlValueToJavaConverters.get(columnType);
            //converter = converter == null ? defaultConverter : converter;
            if (converter == null) {
                throw new RuntimeException("No converter found for SQL type: " + columnType);
            }
            Object value = converter.getValueFromResultSet(lSql, rs, index);
            if (rs.wasNull()) {
                return null;
            } else {
                return value;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setValueInStatement(LSql lSql, PreparedStatement ps, int index,
                                    Object val) throws SQLException {
        if (val == null) {
            ps.setObject(index, null);
        } else {
            Converter converter = javaValueToSqlConverters.get(val.getClass());
            if (converter == null) {
                throw new RuntimeException("No converter found for Java type: " + val.getClass());
            }
            converter.setValueInStatement(lSql, ps, index, val);
        }
    }

    protected void init() {
        setConverter(
                new int[]{Types.BIT, Types.BOOLEAN},
                Boolean.class,
                new Converter() {
                    public void setValueInStatement(LSql lSql, PreparedStatement ps, int index,
                                                    Object val) throws SQLException {
                        ps.setBoolean(index, (Boolean) val);
                    }

                    public Object getValueFromResultSet(LSql lSql, ResultSet rs,
                                                        int index) throws SQLException {
                        return rs.getBoolean(index);
                    }
                });
        setConverter(
                new int[]{Types.TINYINT, Types.SMALLINT, Types.INTEGER, Types.BIGINT},
                Integer.class,
                new Converter() {
                    public void setValueInStatement(LSql lSql, PreparedStatement ps, int index,
                                                    Object val) throws SQLException {
                        ps.setInt(index, (Integer) val);
                    }

                    public Object getValueFromResultSet(LSql lSql, ResultSet rs,
                                                        int index) throws SQLException {
                        return rs.getInt(index);
                    }
                });
        setConverter(
                new int[]{Types.FLOAT},
                Float.class,
                new Converter() {
                    public void setValueInStatement(LSql lSql, PreparedStatement ps, int index,
                                                    Object val) throws SQLException {
                        ps.setFloat(index, (Float) val);
                    }

                    public Object getValueFromResultSet(LSql lSql, ResultSet rs,
                                                        int index) throws SQLException {
                        return rs.getFloat(index);
                    }
                });
        setConverter(
                new int[]{Types.DOUBLE, Types.REAL, Types.DECIMAL},
                Double.class,
                new Converter() {
                    public void setValueInStatement(LSql lSql, PreparedStatement ps, int index,
                                                    Object val) throws SQLException {
                        ps.setDouble(index, (Double) val);
                    }

                    public Object getValueFromResultSet(LSql lSql, ResultSet rs,
                                                        int index) throws SQLException {
                        return rs.getDouble(index);
                    }
                });
        setConverter(
                new int[]{Types.CHAR},
                Character.class,
                new Converter() {
                    public void setValueInStatement(LSql lSql, PreparedStatement ps, int index,
                                                    Object val) throws SQLException {
                        ps.setString(index, val.toString());
                    }

                    public Object getValueFromResultSet(LSql lSql, ResultSet rs,
                                                        int index) throws SQLException {
                        return rs.getString(index).charAt(0);
                    }
                });
        setConverter(
                new int[]{Types.LONGNVARCHAR, Types.LONGVARCHAR, Types.NCHAR, Types.NVARCHAR, Types.VARCHAR},
                String.class,
                new Converter() {
                    public void setValueInStatement(LSql lSql, PreparedStatement ps, int index,
                                                    Object val) throws SQLException {
                        ps.setString(index, val.toString());
                    }

                    public Object getValueFromResultSet(LSql lSql, ResultSet rs,
                                                        int index) throws SQLException {
                        return rs.getString(index);
                    }
                });
        setConverter(
                new int[]{Types.TIMESTAMP},
                DateTime.class,
                new Converter() {
                    public void setValueInStatement(LSql lSql, PreparedStatement ps, int index,
                                                    Object val) throws SQLException {
                        DateTime dt = (DateTime) val;
                        Timestamp ts = new Timestamp(dt.getMillis());
                        ps.setTimestamp(index, ts);
                    }

                    public Object getValueFromResultSet(LSql lSql, ResultSet rs,
                                                        int index) throws SQLException {
                        Timestamp timestamp = rs.getTimestamp(index);
                        if (timestamp != null) {
                            return new DateTime(timestamp.getTime());
                        } else {
                            return null;
                        }
                    }
                });
        setConverter(
                new int[]{Types.BLOB},
                com.w11k.lsql.Blob.class,
                new Converter() {
                    public void setValueInStatement(LSql lSql, PreparedStatement ps, int index,
                                                    Object val) throws SQLException {
                        com.w11k.lsql.Blob blob = (com.w11k.lsql.Blob) val;
                        ps.setBlob(index, blob.getInputStream());
                    }

                    public Object getValueFromResultSet(LSql lSql, ResultSet rs,
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

    protected void setConverter(int[] sqlTypes, Class javaType, Converter converter) {
        for (int sqlType : sqlTypes) {
            sqlValueToJavaConverters.put(sqlType, converter);
        }
        javaValueToSqlConverters.put(javaType, converter);
    }

}
