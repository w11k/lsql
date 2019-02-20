package com.w11k.lsql.cli_tests.tests;

import com.google.common.base.Optional;
import com.google.common.reflect.Invokable;
import com.w11k.lsql.Row;
import com.w11k.lsql.Table;
import com.w11k.lsql.cli.tests.Stmts1;
import com.w11k.lsql.cli.tests.schema_public.Person1_Row;
import com.w11k.lsql.cli.tests.schema_public.Person1_Table;
import com.w11k.lsql.cli.tests.schema_public.Person2_Row;
import com.w11k.lsql.cli.tests.schema_public.Person2_Table;
import com.w11k.lsql.cli.tests.stmts1.QueryParamsWithDot;
import com.w11k.lsql.cli.tests.subdir.subsubdir.StmtsCamelCase2;
import com.w11k.lsql.cli.tests.subdir.subsubdir.stmtscamelcase2.LoadPersonsByAgeAndFirstName;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.valueOf;
import static org.testng.Assert.*;

public final class TestCliProjectAssertsTest extends AbstractTestCliTest {

    @Test
    public void useStaticUtilFields() {
        // insert with static util fields
        Table table = this.lSql.table(Person1_Table.NAME);
        Row person1Row = Row.fromKeyVals(
                Person1_Row.INTERNAL_FIELD_ID, 1,
                Person1_Row.INTERNAL_FIELD_FIRST_NAME, "a"
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
    public void dataClassEquals() {
        Person2_Row p1 = new Person2_Row().withId(2).withFirstName("b").withAge(50);
        Person2_Row p2 = new Person2_Row().withId(2).withFirstName("b").withAge(50);
        assertEquals(p1, p2);
    }

    @Test
    public void dataClassHashCode() {
        Person2_Row p1 = new Person2_Row().withId(2).withFirstName("b").withAge(50);
        Person2_Row p2 = new Person2_Row().withId(2).withFirstName("b").withAge(50);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    public void dataClassToString() {
        Person2_Row o = new Person2_Row().withId(123456789).withFirstName("abcdefghi").withAge(987654321);
        assertTrue(o.toString().contains("Person2_Row"));
        assertTrue(o.toString().contains("id="));
        assertTrue(o.toString().contains("12345678"));
        assertTrue(o.toString().contains("firstName="));
        assertTrue(o.toString().contains("abcdefghi"));
        assertTrue(o.toString().contains("age="));
        assertTrue(o.toString().contains("987654321"));
    }

    @Test
    public void toInternalMap() {
        Person1_Row person1Row = new Person1_Row()
                .withFirstName("Max");

        Map<String, Object> map = person1Row.toInternalMap();

        assertTrue(map.containsKey("first_name"));
        assertTrue(map.containsKey(Person1_Row.INTERNAL_FIELD_FIRST_NAME));
    }

    @Test
    public void toMap() {
        Person1_Row person1Row = new Person1_Row()
                .withFirstName("Max");

        Map<String, Object> map = person1Row.toRow();
        assertTrue(map.containsKey("firstName"));
        assertTrue(map.containsKey(Person1_Row.FIELD_FIRST_NAME));
    }

    @Test
    public void nullableAndNonnullAnnotation() throws NoSuchMethodException {
        Invokable<?, Object> withId = Invokable.from(Person2_Row.class.getMethod("withId", Integer.class));
        assertTrue(withId.getParameters().get(0).isAnnotationPresent(Nonnull.class));

        Invokable<?, Object> withFirstName = Invokable.from(Person2_Row.class.getMethod("withFirstName", String.class));
        assertTrue(withFirstName.getParameters().get(0).isAnnotationPresent(Nullable.class));
    }

    @Test
    public void insert() {
        Person1_Table person1Table = new Person1_Table(lSql);
        Optional<Integer> pk = person1Table.insert(new Person1_Row().withId(1).withFirstName("a"));
        assertEquals(pk.get(), valueOf(1));
    }

    @Test
    public void insertAndLoad() {
        Person1_Table person1Table = new Person1_Table(lSql);
        Person1_Row p1 = person1Table.insertAndLoad(new Person1_Row().withId(1).withFirstName("a"));
        assertEquals(p1.getId(), valueOf(1));
        assertEquals(p1.getFirstName(), "a");
    }

    @Test
    public void load() {
        Person1_Table person1Table = new Person1_Table(lSql);
        person1Table.insert(new Person1_Row().withId(1).withFirstName("a"));

        Optional<Person1_Row> person1RowOptional = person1Table.load(1);
        Assert.assertTrue(person1RowOptional.isPresent());
        Person1_Row person1Row = person1RowOptional.get();
        assertEquals(person1Row.getId(), new Integer(1));
        assertEquals(person1Row.getFirstName(), "a");
    }

    @Test
    public void delete() {
        Person1_Table person1Table = new Person1_Table(lSql);
        person1Table.insert(new Person1_Row().withId(1).withFirstName("a"));
        person1Table.delete(new Person1_Row().withId(1));

        Optional<Person1_Row> person1RowOptional = person1Table.load(1);
        Assert.assertFalse(person1RowOptional.isPresent());
    }

    @Test
    public void deleteById() {
        Person1_Table person1Table = new Person1_Table(lSql);
        person1Table.insert(new Person1_Row().withId(1).withFirstName("a"));
        person1Table.deleteById(1);

        Optional<Person1_Row> person1RowOptional = person1Table.load(1);
        Assert.assertFalse(person1RowOptional.isPresent());
    }

    @Test
    public void update() {
        Person1_Table person1Table = new Person1_Table(lSql);
        Person1_Row p1 = new Person1_Row().withId(1).withFirstName("a");
        person1Table.insert(p1);

        person1Table.update(p1.withFirstName("b"));
        p1 = person1Table.load(1).get();

        assertEquals(p1.getFirstName(), "b");
    }

    @Test
    public void statementSelect() {
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
        assertEquals(row.id, new Integer(1));
        assertEquals(row.getFirstName(), "a");
        assertEquals(row.firstName, "a");
        assertEquals(row.getAge(), new Integer(50));
    }

    @Test
    public void queryParameterWithDot() {
        Person1_Table person1Table = new Person1_Table(lSql);
        person1Table.insert(new Person1_Row()
                .withId(99)
                .withFirstName("a"));

        Stmts1 statement = new Stmts1(lSql);
        List<QueryParamsWithDot> list = statement.queryParamsWithDot()
                .withPerson1Id(99)
                .toList();

        assertEquals(list.size(), 1);

        QueryParamsWithDot row = list.get(0);
        assertEquals(row.getId(), new Integer(99));
        assertEquals(row.getFirstName(), "a");
    }

    @Test
    public void statementDelete() {
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
        assertEquals(row.get().getAge(), new Integer(50));

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
