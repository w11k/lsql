package com.w11k.lsql.tests.pojo;

import com.w11k.lsql.query.PojoQuery;
import com.w11k.lsql.tests.AbstractLSqlTest;
import com.w11k.lsql.typemapper.predefined.AtomicIntegerTypeMapper;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.assertEquals;

public class PojoQueryTest extends AbstractLSqlTest {

    public static class Table1 {

        private String name;

        private int age;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return this.age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    public static class Table1WithAtomicInteger extends PojoTableTest.Table1Pojo {

        private AtomicInteger ai = new AtomicInteger(1);

        public AtomicInteger getAi() {
            return this.ai;
        }

        public void setAi(AtomicInteger ai) {
            this.ai = ai;
        }
    }

    @Test
    public void executeRawQuery() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        this.lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('name1', 20)");
        this.lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('name2', 30)");

        List<Table1> rows = this.lSql.executeRawQuery(
                "SELECT * FROM table1 order by age",
                Table1.class).toList();

        assertEquals(rows.size(), 2);
        assertEquals(rows.get(0).getAge(), 20);
        assertEquals(rows.get(0).getName(), "name1");
        assertEquals(rows.get(1).getAge(), 30);
        assertEquals(rows.get(1).getName(), "name2");
    }

    @Test
    public void fieldsUseConverterRegistry() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, name TEXT, ai INTEGER)");
        this.lSql.executeRawSql("INSERT INTO table1 (id, name, ai) VALUES (1, 'name1', 88)");
        this.lSql.executeRawSql("INSERT INTO table1 (id, name, ai) VALUES (2, 'name2', 99)");

        this.lSql.getDialect().getConverterRegistry().addConverter(new AtomicIntegerTypeMapper());
        PojoQuery<Table1WithAtomicInteger> query =
                this.lSql.executeRawQuery("select * from table1 order by id", Table1WithAtomicInteger.class);

        List<Table1WithAtomicInteger> list = query.toList();
        assertEquals(list.size(), 2);
        assertEquals(list.get(0).getAi().get(), 88);
        assertEquals(list.get(1).getAi().get(), 99);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*No converter.*")
    public void failOnFieldsWithMissingConverters() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, first_name TEXT, ai INTEGER)");
        this.lSql.table("table1", PojoTableTest.Table1WithAtomicInteger.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*converter.*not support.*")
    public void failWhenConverterCanNotConvertBetweenJavaAndSqlType() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, first_name TEXT, ai VARCHAR(10))");
        this.lSql.getDialect().getConverterRegistry()
                .addConverter(new AtomicIntegerTypeMapper());
        this.lSql.table("table1", PojoTableTest.Table1WithAtomicInteger.class);
    }

}
