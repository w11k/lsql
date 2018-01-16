package com.w11k.lsql.cli.tests;

import com.w11k.lsql.Config;
import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.dialects.H2Dialect;
import com.w11k.lsql.dialects.IdentifierConverter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public final class TestCliConfig extends Config {

    public static class CustomType {

        private int value;

        public CustomType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class CustomConverter extends Converter {

        public CustomConverter() {
            super(CustomType.class, Types.INTEGER);
        }

        @Override
        protected void setValue(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException {
            ps.setInt(index, ((CustomType) val).getValue());
        }

        @Override
        protected CustomType getValue(LSql lSql, ResultSet rs, int index) throws SQLException {
            return new CustomType(rs.getInt(index));
        }
    }

    public static void createTables(LSql lSql) {
        lSql.executeRawSql("create table person1 (id integer primary key, first_name text)");
        lSql.executeRawSql("create table person2 (id integer primary key, first_name text, age integer)");
        lSql.executeRawSql("create table a_table (id integer primary key)");
        lSql.executeRawSql("create table checks (yesno BOOLEAN NOT NULL);");
        lSql.executeRawSql("create table custom_converter (field INTEGER);");
    }

    public TestCliConfig() {
        setDialect(new H2Dialect());
        getDialect().setIdentifierConverter(IdentifierConverter.JAVA_LOWER_UNDERSCORE_TO_SQL_UPPER_UNDERSCORE);

        getDialect().getConverterRegistry().addTypeAlias("custom", new CustomConverter());
    }

}
