package com.w11k.relda.tests;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.w11k.relda.ConnectionFactories;
import com.w11k.relda.LMap;
import com.w11k.relda.LSql;
import org.h2.jdbcx.JdbcDataSource;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.testng.Assert.*;

public class LSqlTest {

    private LSql lSql;

    private JdbcDataSource dataSource = new JdbcDataSource();

    public LSqlTest() {
        dataSource.setURL("jdbc:h2:mem:testdb;MODE=PostgreSQL");
    }

    @BeforeMethod
    public void beforeTest() throws SQLException {
        final Connection connection = dataSource.getConnection();
        connection.setAutoCommit(true);
        lSql = new LSql(ConnectionFactories.fromInstance(connection));
    }

    @AfterMethod
    public void afterTest() throws SQLException {
        lSql.execute("drop table if exists table1");
        lSql.getConnection().commit();
    }

    @Test
    public void getConnectionFromConnectionFactory() throws SQLException {
        assertNotNull(lSql.getConnection());
    }

    @Test
    public void testSelectFieldAccess() throws SQLException {
        lSql.execute("create table table1 (name text, age int);" +
                "insert into table1 (name, age) values ('cus1', 20);" +
                "insert into table1 (name, age) values ('cus2', 30)");

        List<Integer> ages = lSql.executeQuery("select * from table1")
                .map(new Function<LMap, Integer>() {
                    public Integer apply(LMap input) {
                        return input.getInt("age");
                    }
                });

        int sum = 0;
        for (int age : ages) {
            sum += age;
        }
        assertEquals(sum, 50);
    }

    //@Test
    /*
    // TODO reimplement test logic
    public void testFetchAllValues() throws SQLException {
        lSql.execute("create table table1 (name text, age int);" +
                "insert into table1 (name, age) values ('cus1', '20');");

        // All key->values
        final List<String> entries = Lists.newArrayList();

        lSql.executeQuery(
                "select * from table1",
                new Function<LMap, Integer>() {
                    public Integer apply(LMap input) {
                        for (Map.Entry<String, Object> entry : input.entrySet()) {
                            entries.add(entry.getKey() + "->" + entry.getValue());
                        }
                        return null;
                    }
                });

        assertTrue(entries.contains("name->cus1"));
        assertTrue(entries.contains("age->20"));
    }
    */

    @Test
    public void testNameConversions() {
        lSql.getJavaSqlConverter().setJavaIdentifierCaseFormat(CaseFormat.LOWER_CAMEL);

        lSql.execute("create table table1 (test_name1 text, TEST_NAME2 text);" +
                "insert into table1 (test_name1, TEST_NAME2) values ('name1', 'name2');");

        LMap row = lSql.executeQuery("select * from table1").getFirstRow();
        assertEquals(row.get("testName1"), "name1");
        assertEquals(row.get("testName2"), "name2");
    }

    @Test
    public void testInsertAndKeyRetrieval() {
        lSql.execute("create table table1 (id serial, test_name1 text, age int)");
        Object newId = lSql.executeInsert("table1", LMap.fromKeyVals(
                "test_name1", "a name",
                "age", 2)).get();

        LMap query = lSql.executeQuery("select * from table1 where id = " + newId).getFirstRow();
        assertEquals(query.getString("test_name1"), "a name");
        assertEquals(query.getInt("age"), 2);
    }

    @Test
    public void testExecuteQueryAndGetFirstRow() {
        lSql.execute("create table table1 (id serial, number int)");
        lSql.executeInsert("table1", LMap.fromKeyVals("number", 1));
        lSql.executeInsert("table1", LMap.fromKeyVals("number", 2));
        lSql.executeInsert("table1", LMap.fromKeyVals("number", 3));

        LMap map = lSql.executeQuery("select sum(number) as X from table1").getFirstRow();
        assertEquals(map.getInt("x"), 6);
    }

}
