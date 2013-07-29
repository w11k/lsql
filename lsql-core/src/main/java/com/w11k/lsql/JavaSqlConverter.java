package com.w11k.lsql;

import com.google.common.collect.Maps;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * Note: This Class must behave immutable because by default one instance is shared
 * between LSql, all Tables and all Column.
 */
public class JavaSqlConverter {

    public static class Converter {
        public String javaValueToSql(Object value){
            return value.toString();
        }

        public Object sqlValueToJava(ResultSet rs, int index) throws SQLException {
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
        // TODO add map to statically override IDs

        this.defaultConverter = defaultConverter != null ? defaultConverter : new Converter();

        addConverter(
                asList(Types.BIT, Types.TINYINT, Types.SMALLINT, Types.INTEGER, Types.BIGINT),
                Integer.class,
                new Converter() {
                    @Override public Object sqlValueToJava(ResultSet rs, int index) throws SQLException {
                        return rs.getInt(index);
                    }
                });
        addConverter(
                asList(Types.FLOAT),
                Float.class,
                new Converter() {
                    @Override public Object sqlValueToJava(ResultSet rs, int index) throws SQLException {
                        return rs.getFloat(index);
                    }
                });
        addConverter(
                asList(Types.DATE),
                DateTime.class,
                new Converter() {
                    @Override public Object sqlValueToJava(ResultSet rs, int index) throws SQLException {
                        return new DateTime(rs.getDate(index).getTime());
                    }
                });
        addConverter(
                asList(Types.LONGNVARCHAR, Types.LONGVARCHAR, Types.NCHAR, Types.NVARCHAR, Types.VARCHAR, Types.CLOB),
                String.class,
                new Converter() {
                    @Override public String javaValueToSql(Object value) {
                        return "'" + ((String) value).replaceAll("'", "\'") + "'";
                    }

                    @Override public Object sqlValueToJava(ResultSet rs, int index) throws SQLException {
                        return rs.getString(index);
                    }
                });

        // TODO add more types
    }

    public Object getColumnValue(ResultSet rs, int index) throws SQLException {
        try {
            int columnType = rs.getMetaData().getColumnType(index);
            Converter converter = sqlValueToJavaConverters.get(columnType);
            converter = converter == null ? defaultConverter : converter;
            return converter.sqlValueToJava(rs, index);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String javaToSqlStringRepr(Object obj) {
        Converter converter = javaValueToSqlConverters.get(obj.getClass());
        converter = converter == null ? defaultConverter : converter;
        return converter.javaValueToSql(obj);
    }

    private void addConverter(List<Integer> sqlTypes, Class<?> javaType, Converter converter) {
        for (int sqlType : sqlTypes) {
            sqlValueToJavaConverters.put(sqlType, converter);
        }
        javaValueToSqlConverters.put(javaType, converter);
    }


}
