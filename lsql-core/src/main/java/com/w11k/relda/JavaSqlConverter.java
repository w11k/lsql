package com.w11k.relda;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class JavaSqlConverter {

    public static class Converter {
        public String javaValueToSql(Object value) {
            return value.toString();
        }

        public Object sqlValueToJava(ResultSet rs, int index) throws SQLException {
            return rs.getObject(index);
        }
    }

    private final Converter defaultConverter = new Converter();

    private CaseFormat javaIdentifierCaseFormat = CaseFormat.LOWER_UNDERSCORE;

    private CaseFormat sqlIdentifierCaseFormat = CaseFormat.UPPER_UNDERSCORE;

    private Map<Class<?>, Converter> javaValueToSqlConverters = Maps.newHashMap();

    private Map<Integer, Converter> sqlValueToJavaConverters = Maps.newHashMap();

    public CaseFormat getJavaIdentifierCaseFormat() {
        return javaIdentifierCaseFormat;
    }

    public void setJavaIdentifierCaseFormat(CaseFormat javaIdentifierCaseFormat) {
        this.javaIdentifierCaseFormat = javaIdentifierCaseFormat;
    }

    public CaseFormat getSqlIdentifierCaseFormat() {
        return sqlIdentifierCaseFormat;
    }

    public void setSqlIdentifierCaseFormat(CaseFormat sqlIdentifierCaseFormat) {
        this.sqlIdentifierCaseFormat = sqlIdentifierCaseFormat;
    }

    public JavaSqlConverter() {
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
    }

    public void addConverter(List<Integer> sqlTypes, Class<?> javaType, Converter converter) {
        for (int sqlType : sqlTypes) {
            sqlValueToJavaConverters.put(sqlType, converter);
        }
        javaValueToSqlConverters.put(javaType, converter);
    }

    public String identifierSqlToJava(String sqlName) {
        return sqlIdentifierCaseFormat.to(javaIdentifierCaseFormat, sqlName);
    }

    public String identifierJavaToSql(String javaName) {
        return javaIdentifierCaseFormat.to(sqlIdentifierCaseFormat, javaName);
    }

    public Object getColumnValue(ResultSet rs, int index) {
        try {
            int columnType = rs.getMetaData().getColumnType(index);
            Converter converter = sqlValueToJavaConverters.get(columnType);
            converter = converter == null ? defaultConverter : converter;
            return converter.sqlValueToJava(rs, index);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String escapeJavaObjectForSqlStatement(Object obj) {
        Converter converter = javaValueToSqlConverters.get(obj.getClass());
        converter = converter == null ? defaultConverter : converter;
        return converter.javaValueToSql(obj);
    }

}
