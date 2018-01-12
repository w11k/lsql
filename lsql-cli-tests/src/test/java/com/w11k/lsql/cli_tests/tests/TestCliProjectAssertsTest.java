package com.w11k.lsql.cli_tests.tests;

import com.google.common.base.Optional;
import com.w11k.lsql.LSql;
import com.w11k.lsql.Row;
import com.w11k.lsql.Table;
import com.w11k.lsql.cli.tests.TestCliConfig;
import com.w11k.lsql.cli.tests.schema_public.Person1_Row;
import com.w11k.lsql.cli.tests.schema_public.Person1_Table;
import com.w11k.lsql.cli.tests.schema_public.Person2_Row;
import com.w11k.lsql.cli.tests.schema_public.Person2_Table;
import com.w11k.lsql.cli.tests.subdir.subsubdir.StmtsCamelCase2;
import com.w11k.lsql.cli.tests.subdir.subsubdir.stmtscamelcase2.LoadPersonsByAgeAndFirstName;
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
import static org.testng.Assert.*;

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
    public void useStaticUtilFields() {
        createTables(lSql);

        // insert with static util fields
        Table table = this.lSql.table(Person1_Table.NAME);
        Row person1Row = Row.fromKeyVals(
                Person1_Row.COL_ID, 1,
                Person1_Row.COL_FIRST_NAME, "a"
        );
        table.insert(person1Row);

        // valid
        Person1_Table Person1_Table = new Person1_Table(this.lSql);
        Person1_Row loaded = Person1_Table.load(1).get();
        assertEquals(loaded.getId(), new Integer(1));
        assertEquals(loaded.getFirstName(), "a");
    }

    @Test
    public void with() {
        Person1_Row p1 = new Person1_Row();
        p1 = p1
                .withId(1)
                .withFirstName("a");

        assertEquals(p1.getId(), new Integer(1));
        assertEquals(p1.getFirstName(), "a");

        Person2_Row p2 = new Person2_Row()
                .withId(2)
                .withFirstName("b")
                .withAge(50);

        assertEquals(p2.getId(), new Integer(2));
        assertEquals(p2.getFirstName(), "b");
        assertEquals(p2.getAge(), new Integer(50));
    }

    @Test
    public void asWithInstance() {
        Person1_Row p1 = new Person1_Row().withId(1).withFirstName("a");
        Person2_Row p2 = new Person2_Row().withId(2).withFirstName("b").withAge(50);

        p2 = p1.as(p2);
        assertEquals(p2.getId(), new Integer(1));
        assertEquals(p2.getFirstName(), "a");
        assertEquals(p2.getAge(), new Integer(50));
    }

    @Test
    public void asWithClass() {
        Person1_Row p1 = new Person1_Row().withId(1).withFirstName("a");

        Person2_Row p2 = p1.as(Person2_Row.class);
        assertEquals(p2.getId(), new Integer(1));
        assertEquals(p2.getFirstName(), "a");
        assertNull(p2.getAge());
    }

    @Test
    public void updatedWith() {
        Person2_Row p2 = new Person2_Row().withId(2).withFirstName("b").withAge(50);

        Person1_Row p1 = Person1_Row.from(p2);
        assertEquals(p1.getId(), new Integer(2));
        assertEquals(p1.getFirstName(), "b");
    }

    @Test
    public void insert() {
        createTables(lSql);

        Person1_Table Person1_Table = new Person1_Table(lSql);
        Optional<Integer> pk = Person1_Table.insert(new Person1_Row().withId(1).withFirstName("a"));
        assertEquals(pk.get(), new Integer(1));
    }

    @Test
    public void insertAndLoad() {
        createTables(lSql);

        Person1_Table Person1_Table = new Person1_Table(lSql);
        Person1_Row p1 = Person1_Table.insertAndLoad(new Person1_Row().withId(1).withFirstName("a"));
        assertEquals(p1.getId(), new Integer(1));
        assertEquals(p1.getFirstName(), "a");
    }

    @Test
    public void load() {
        createTables(lSql);

        Person1_Table Person1_Table = new Person1_Table(lSql);
        Person1_Table.insert(new Person1_Row().withId(1).withFirstName("a"));

        Optional<Person1_Row> person1RowOptional = Person1_Table.load(1);
        Assert.assertTrue(person1RowOptional.isPresent());
        Person1_Row person1Row = person1RowOptional.get();
        assertEquals(person1Row.getId(), new Integer(1));
        assertEquals(person1Row.getFirstName(), "a");
    }

    @Test
    public void delete() {
        createTables(lSql);

        Person1_Table Person1_Table = new Person1_Table(lSql);
        Person1_Table.insert(new Person1_Row().withId(1).withFirstName("a"));
        Person1_Table.delete(new Person1_Row().withId(1));

        Optional<Person1_Row> person1RowOptional = Person1_Table.load(1);
        Assert.assertFalse(person1RowOptional.isPresent());
    }

    @Test
    public void deleteById() {
        createTables(lSql);

        Person1_Table Person1_Table = new Person1_Table(lSql);
        Person1_Table.insert(new Person1_Row().withId(1).withFirstName("a"));
        Person1_Table.deleteById(1);

        Optional<Person1_Row> person1RowOptional = Person1_Table.load(1);
        Assert.assertFalse(person1RowOptional.isPresent());
    }

    @Test
    public void update() {
        createTables(lSql);

        Person1_Table Person1_Table = new Person1_Table(lSql);
        Person1_Row p1 = new Person1_Row().withId(1).withFirstName("a");
        Person1_Table.insert(p1);

        Person1_Table.update(p1.withFirstName("b"));
        p1 = Person1_Table.load(1).get();

        assertEquals(p1.getFirstName(), "b");
    }

    @Test
    public void statementSelect() {
        createTables(lSql);

        Person2_Table person2Table = new Person2_Table(lSql);
        person2Table.insert(new Person2_Row()
                .withId(1)
                .withFirstName("a")
                .withAge(50));

        StmtsCamelCase2 statement = new StmtsCamelCase2(lSql);
        List<LoadPersonsByAgeAndFirstName> list = statement.loadPersonsByAgeAndFirstName()
                .withFirstName("a")
                .withAge(50)
                .toList();

        assertEquals(list.size(), 1);

        LoadPersonsByAgeAndFirstName row = list.get(0);
        assertEquals(row.getId(), new Integer(1));
        assertEquals(row.getFirstName(), "a");
        assertEquals(row.getAge(), new Integer(50));
    }

    @Test
    public void statementDelete() {
        createTables(lSql);

        // insert
        Person2_Table person2Table = new Person2_Table(lSql);
        person2Table.insert(new Person2_Row()
                .withId(1)
                .withFirstName("a")
                .withAge(50));

        // validate insert
        StmtsCamelCase2 statement = new StmtsCamelCase2(lSql);
        Optional<LoadPersonsByAgeAndFirstName> row = statement.loadPersonsByAgeAndFirstName()
                .withFirstName("a")
                .withAge(50)
                .first();
        assertTrue(row.isPresent());
        assertEquals(row.get().getFirstName(), "a");
        assertEquals(row.get().getAge(), new  Integer(50));

        // delete
        statement.deletePersonByFirstName().withFirstName("a").execute();

        // validate delete
        row = statement.loadPersonsByAgeAndFirstName()
                .withFirstName("a")
                .withAge(50)
                .first();
        assertFalse(row.isPresent());
    }

}
