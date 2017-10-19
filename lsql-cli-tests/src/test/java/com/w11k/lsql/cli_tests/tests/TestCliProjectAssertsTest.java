package com.w11k.lsql.cli_tests.tests;

import com.google.common.base.Optional;
import com.w11k.lsql.LSql;
import com.w11k.lsql.cli.tests.TestCliConfig;
import com.w11k.lsql.cli.tests.cli_tests_tests_subdir.Stmts2Row;
import com.w11k.lsql.cli.tests.cli_tests_tests_subdir.Stmts2Statement;
import com.w11k.lsql.cli.tests.schema_public.Person1Row;
import com.w11k.lsql.cli.tests.schema_public.Person1Table;
import com.w11k.lsql.cli.tests.schema_public.Person2Row;
import com.w11k.lsql.cli.tests.schema_public.Person2Table;
import com.w11k.lsql.jdbc.ConnectionProviders;
import org.apache.commons.dbcp.BasicDataSource;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static com.w11k.lsql.cli.tests.TestCliConfig.createTables;
import static org.testng.Assert.assertEquals;

public final class TestCliProjectAssertsTest {

    private LSql lSql;

    @BeforeMethod
    public void before() throws SQLException {
        String url = "jdbc:h2:mem:" + UUID.randomUUID() + ";mode=postgresql";
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(url);
        ds.setDefaultAutoCommit(false);
        Connection connection = ds.getConnection();
        this.lSql = new LSql(TestCliConfig.class, ConnectionProviders.fromInstance(connection));
    }

    @Test
    public void with() {
        Person1Row p1 = new Person1Row();
        p1 = p1
                .withId(1)
                .withFirstName("a");

        assertEquals(p1.getId(), new Integer(1));
        assertEquals(p1.getFirstName(), "a");

        Person2Row p2 = new Person2Row()
                .withId(2)
                .withFirstName("b")
                .withAge(50);

        assertEquals(p2.getId(), new Integer(2));
        assertEquals(p2.getFirstName(), "b");
        assertEquals(p2.getAge(), new Integer(50));
    }

    @Test
    public void assignIntoNew() {
        Person1Row p1 = new Person1Row().withId(1).withFirstName("a");
        Person2Row p2 = new Person2Row().withId(2).withFirstName("b").withAge(50);

        p2 = p1.assignIntoNew(p2);
        assertEquals(p2.getId(), new Integer(1));
        assertEquals(p2.getFirstName(), "a");
        assertEquals(p2.getAge(), new Integer(50));
    }

    @Test
    public void updatedWith() {
        Person1Row p1 = new Person1Row().withId(1).withFirstName("a");
        Person2Row p2 = new Person2Row().withId(2).withFirstName("b").withAge(50);

        p1 = p1.updatedWith(p2);
        assertEquals(p1.getId(), new Integer(2));
        assertEquals(p1.getFirstName(), "b");
    }

    @Test
    public void insert() throws SQLException {
        createTables(lSql);

        Person1Table person1Table = new Person1Table(lSql);
        Optional<Object> pk = person1Table.insert(new Person1Row().withId(1).withFirstName("a"));
        assertEquals(pk.get(), 1);
    }

    @Test
    public void insertAndLoad() throws SQLException {
        createTables(lSql);

        Person1Table person1Table = new Person1Table(lSql);
        Person1Row p1 = person1Table.insertAndLoad(new Person1Row().withId(1).withFirstName("a"));
        assertEquals(p1.getId(), new Integer(1));
        assertEquals(p1.getFirstName(), "a");
    }

    @Test
    public void load() throws SQLException {
        createTables(lSql);

        Person1Table person1Table = new Person1Table(lSql);
        person1Table.insert(new Person1Row().withId(1).withFirstName("a"));

        Optional<Person1Row> person1RowOptional = person1Table.load(1);
        Assert.assertTrue(person1RowOptional.isPresent());
        Person1Row person1Row = person1RowOptional.get();
        assertEquals(person1Row.getId(), new Integer(1));
        assertEquals(person1Row.getFirstName(), "a");
    }

    @Test
    public void delete() throws SQLException {
        createTables(lSql);

        Person1Table person1Table = new Person1Table(lSql);
        person1Table.insert(new Person1Row().withId(1).withFirstName("a"));
        person1Table.delete(new Person1Row().withId(1));

        Optional<Person1Row> person1RowOptional = person1Table.load(1);
        Assert.assertFalse(person1RowOptional.isPresent());
    }

    @Test
    public void update() throws SQLException {
        createTables(lSql);

        Person1Table person1Table = new Person1Table(lSql);
        Person1Row p1 = new Person1Row().withId(1).withFirstName("a");
        person1Table.insert(p1);

        person1Table.update(p1.withFirstName("b"));
        p1 = person1Table.load(1).get();

        assertEquals(p1.getFirstName(), "b");
    }

    @Test
    public void statement() throws SQLException {
        createTables(lSql);

        Person2Table person2Table = new Person2Table(lSql);
        person2Table.insert(new Person2Row().withId(1).withFirstName("a").withAge(50));

        Stmts2Statement statement = new Stmts2Statement(lSql);
        List<Stmts2Row> list = statement.newQuery()
                .age(49)
                .toList();

        assertEquals(list.size(), 1);

        Stmts2Row row = list.get(0);
        assertEquals(row.getId(), new Integer(1));
        assertEquals(row.getFirstName(), "a");
        assertEquals(row.getAge(), new Integer(50));
    }

}
