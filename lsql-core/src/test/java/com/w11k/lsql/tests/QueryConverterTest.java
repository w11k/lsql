package com.w11k.lsql.tests;

import com.w11k.lsql.Query;
import com.w11k.lsql.Row;
import com.w11k.lsql.Table;
import com.w11k.lsql.converter.predefined.JavaBoolToSqlStringConverter;
import com.w11k.lsql.converter.sqltypes.IntConverter;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class QueryConverterTest extends AbstractLSqlTest {

    @Test
    public void normalColumn() {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY , field VARCHAR(10))");

        Table table1 = lSql.table("table1");
        table1.column("field").setConverter(new JavaBoolToSqlStringConverter("ja", "nein"));

        lSql.executeRawSql("INSERT INTO table1 (id, field) VALUES (1, 'ja')");
        Row row = lSql.executeRawQuery("SELECT * FROM table1").firstRow().get();
        assertEquals(row.getBoolean("field"), Boolean.TRUE);
    }

    @Test
    public void aliasedColumn() {
        JavaBoolToSqlStringConverter converter = new JavaBoolToSqlStringConverter("ja", "nein");

        createTable("CREATE TABLE table1 (id INT PRIMARY KEY , field VARCHAR(10))");

        Table table1 = lSql.table("table1");
        table1.column("field").setConverter(converter);

        lSql.executeRawSql("INSERT INTO table1 (id, field) VALUES (1, 'ja')");
        Query query = lSql.executeRawQuery("SELECT id, field as aaa FROM table1");

        // Set converter for aliased column
        query.addConverter("aaa", converter);

        Row row = query.firstRow().get();
        assertEquals(row.getBoolean("aaa"), Boolean.TRUE);
    }

    @Test
    public void aliasedColumnForAggregationFunction() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        Query query = lSql.executeRawQuery("SELECT count(*) AS c FROM table1");
        query.addConverter("c", IntConverter.INSTANCE);
        Row row = query.firstRow().get();
        assertEquals(row.getInt("c"), (Integer) 2);
    }

    @Test
    public void canUseCalculatedColumnsTogetherWithNormalColumnsOneTable() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        Query query = lSql.executeRawQuery("SELECT name, age, count(*) AS c FROM table1");
        query.addConverter("c", IntConverter.INSTANCE);
        Row row = query.firstRow().get();
        assertEquals(row.getString("name"), "cus1");
        assertEquals(row.getInt("age"), (Integer) 20);
        assertEquals(row.getInt("c"), (Integer) 1);
    }

}
