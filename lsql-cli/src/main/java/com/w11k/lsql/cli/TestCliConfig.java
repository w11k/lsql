package com.w11k.lsql.cli;

import com.w11k.lsql.Config;
import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.dialects.H2Dialect;
import com.w11k.lsql.dialects.RowKeyConverter;

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
        public void setValue(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException {
            ps.setInt(index, ((CustomType) val).getValue());
        }

        @Override
        public CustomType getValue(LSql lSql, ResultSet rs, int index) throws SQLException {
            return new CustomType(rs.getInt(index));
        }
    }

    public static void createTables(LSql lSql) {
        lSql.executeRawSql("create table api1 (id integer primary key, first_name text)");
        lSql.executeRawSql("create table api2 (id integer primary key, first_name text, age integer)");

        lSql.executeRawSql("create table crud (id serial primary key, field_a int, field_b text)");
        lSql.executeRawSql("create table person1 (id integer primary key, first_name text)");
        lSql.executeRawSql("create table person2 (id integer primary key, first_name text, age integer)");
        lSql.executeRawSql("create table case_conversions1 (id integer primary key, aaa_bbb int, aaaBbb int)");
        lSql.executeRawSql("create table case_conversions2 (id integer primary key, col1 int)");
        lSql.executeRawSql("create table a_table (id_pk integer primary key)");
        lSql.executeRawSql("create table checks (yesno BOOLEAN NOT NULL);");
        lSql.executeRawSql("create table table_with_two_keys (key1 integer, key2 integer, PRIMARY KEY (key1, key2))");
        lSql.executeRawSql("create table custom_converter (field INTEGER);");
        lSql.executeRawSql("create table custom_deserializer (id integer primary key, data longvarbinary);");
        lSql.executeRawSql("create schema schema2;");
        lSql.executeRawSql("create table schema2.table_a (id integer primary key, col1 text)");
    }

    public TestCliConfig() {
        setDialect(new H2Dialect());
        setRowKeyConverter(RowKeyConverter.JAVA_CAMEL_CASE_TO_SQL_LOWER_UNDERSCORE);
//        setRowKeyConverter(RowKeyConverter.NOOP);
        getDialect().getConverterRegistry().addTypeAlias("custom", new CustomConverter());
    }

}
