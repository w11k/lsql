package com.w11k.lsql.tests;

import com.w11k.lsql.query.RowQuery;
import com.w11k.lsql.Row;
import com.w11k.lsql.Table;
import com.w11k.lsql.converter.predefined.JavaBoolToSqlStringConverter;
import com.w11k.lsql.converter.sqltypes.IntConverter;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class RowQueryConverterTest extends AbstractLSqlTest {

    @Test
    public void normalColumn() {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY , field VARCHAR(10))");

        Table table1 = lSql.table("table1");
        table1.column("field").setConverter(new JavaBoolToSqlStringConverter("ja", "nein"));

        lSql.executeRawSql("INSERT INTO table1 (id, field) VALUES (1, 'ja')");
        Row row = lSql.executeRawQuery("SELECT * FROM table1").first().get();
        assertEquals(row.getBoolean("field"), Boolean.TRUE);
    }

    @Test
    public void aliasedColumn() {
        JavaBoolToSqlStringConverter converter = new JavaBoolToSqlStringConverter("ja", "nein");

        createTable("CREATE TABLE table1 (id INT PRIMARY KEY , field VARCHAR(10))");

        Table table1 = lSql.table("table1");
        table1.column("field").setConverter(converter);

        lSql.executeRawSql("INSERT INTO table1 (id, field) VALUES (1, 'ja')");
        RowQuery query = lSql.executeRawQuery("SELECT id, field as aaa FROM table1");

        // Set converter for aliased column
        query.addConverter("aaa", converter);

        Row row = query.first().get();
        assertEquals(row.getBoolean("aaa"), Boolean.TRUE);
    }

    @Test
    public void settingNullAsConverterUsesTypeBasedConverter() {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY , field INT)");

        lSql.executeRawSql("INSERT INTO table1 (id, field) VALUES (1, 2)");
        RowQuery query = lSql.executeRawQuery("SELECT id, field as aaa FROM table1");

        // Set converter for aliased column
        query.addConverter("aaa", null);

        Row row = query.first().get();
        assertEquals(row.getInt("aaa"), new Integer(2));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void failOnMissingConverter() {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY , field INT)");
        lSql.executeRawSql("INSERT INTO table1 (id, field) VALUES (1, 2)");
        RowQuery query = lSql.executeRawQuery("SELECT id, field as aaa FROM table1");
        query.first().get();
    }

    @Test
    public void configSettingsToUseColumnTypeForConverter() {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY , field INT)");

        lSql.executeRawSql("INSERT INTO table1 (id, field) VALUES (1, 2)");
        RowQuery query = lSql.executeRawQuery("SELECT id, field as aaa FROM table1");

        Row row;
        try {
            lSql.getConfig().setUseColumnTypeForConverterLookupInQueries(true);
            row = query.first().get();
        } finally {
            lSql.getConfig().setUseColumnTypeForConverterLookupInQueries(false);
        }

        assertEquals(row.getInt("aaa"), new Integer(2));
    }

    @Test
    public void aliasedColumnForAggregationFunction() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        RowQuery query = lSql.executeRawQuery("SELECT count(*) AS c FROM table1");
        query.addConverter("c", IntConverter.INSTANCE);
        Row row = query.first().get();
        assertEquals(row.getInt("c"), (Integer) 2);
    }

    @Test
    public void canUseCalculatedColumnsTogetherWithNormalColumnsOneTable() {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY, name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (id, name, age) VALUES (1, 'cus1', 20)");
        RowQuery query = lSql.executeRawQuery("SELECT id, name, age, count(*) AS c FROM table1 GROUP BY id;");
        query.addConverter("c", IntConverter.INSTANCE);
        Row row = query.first().get();
        assertEquals(row.getString("name"), "cus1");
        assertEquals(row.getInt("age"), (Integer) 20);
        assertEquals(row.getInt("c"), (Integer) 1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void failOnUnusedConverter() {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY , field INT)");

        lSql.executeRawSql("INSERT INTO table1 (id, field) VALUES (1, 2)");
        RowQuery query = lSql.executeRawQuery("SELECT id, field FROM table1");

        query.addConverter("aaa", null);

        query.first().get();
    }

}
