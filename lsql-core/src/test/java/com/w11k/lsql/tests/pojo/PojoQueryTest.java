package com.w11k.lsql.tests.pojo;

import com.w11k.lsql.tests.AbstractLSqlTest;
import com.w11k.lsql.tests.testdata.Person;
import com.w11k.lsql.tests.testdata.PersonTestData;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

public class PojoQueryTest extends AbstractLSqlTest {

    @Test
    public void executeRawQuery() {
        PersonTestData.init(this.lSql, true);

        List<Person> rows = this.lSql.executeRawQuery(
                PersonTestData.SELECT_ALL_ORDER_BY_ID,
                Person.class).toList();

        assertEquals(rows.size(), 2);
        assertEquals(rows.get(0).getFirstName(), "Adam");
        assertEquals(rows.get(1).getFirstName(), "Eve");
    }

//    @Test
//    public void fieldsUseConverterRegistry() {
//        createTable("CREATE TABLE table1 (name TEXT, age INT, ai INTEGER)");
//        this.lSql.executeRawSql("INSERT INTO table1 (id, name, ai) VALUES (1, 'name1', 88)");
//        this.lSql.executeRawSql("INSERT INTO table1 (id, name, ai) VALUES (2, 'name2', 99)");
//
//        this.lSql.getDialect().getConverterRegistry().addConverter(new AtomicIntegerConverter());
//        PojoQuery<Table1WithAtomicInteger> query =
//                this.lSql.executeRawQuery("select * from table1 order by id", Table1WithAtomicInteger.class);
//
//        List<Table1WithAtomicInteger> list = query.toList();
//        assertEquals(list.size(), 2);
//        assertEquals(list.get(0).getAi().get(), 88);
//        assertEquals(list.get(1).getAi().get(), 99);
//    }
//
//    @Test(
//            expectedExceptions = IllegalArgumentException.class,
//            expectedExceptionsMessageRegExp = ".*No converter.*"
//    )
//    public void failOnFieldsWithMissingConverters() {
//        createTable("CREATE TABLE table1 (name TEXT, age INT, ai INTEGER)");
//        this.lSql.executeRawSql("INSERT INTO table1 (id, name, ai) VALUES (1, 'name1', 88)");
//        PojoQuery<Table1WithAtomicInteger> query = this.lSql.executeRawQuery(
//                "select * from table1",
//                Table1WithAtomicInteger.class
//        );
//        query.toList();
//    }
//
//    @Test(
//            expectedExceptions = IllegalArgumentException.class,
//            expectedExceptionsMessageRegExp = ".*converter.*not support.*"
//    )
//    public void failWhenConverterCanNotConvertBetweenJavaAndSqlType() {
//        createTable("CREATE TABLE table1 (name TEXT, age INT, ai VARCHAR(10))");
//        this.lSql.table("table1").column("ai").setConverter(new AtomicIntegerConverter());
//        this.lSql.executeRawSql("INSERT INTO table1 (id, name, ai) VALUES (1, 'name1', 88)");
//        PojoQuery<Table1WithAtomicInteger> query = this.lSql.executeRawQuery(
//                "select * from table1",
//                Table1WithAtomicInteger.class
//        );
//        query.toList();
//    }

}
