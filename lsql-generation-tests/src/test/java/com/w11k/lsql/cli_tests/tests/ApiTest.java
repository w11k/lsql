package com.w11k.lsql.cli_tests.tests;

import com.google.common.reflect.Invokable;
import com.w11k.lsql.cli.schema_public.Api1_Row;
import com.w11k.lsql.cli.schema_public.Api2_Row;
import org.testng.annotations.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

import static org.testng.Assert.*;

public final class ApiTest extends AbstractTestCliTest {

    @Test
    public void useStaticUtilFields() {
        assertEquals(Api1_Row.INTERNAL_FIELD_ID, "id");
        assertEquals(Api1_Row.ROW_KEY_ID, "id");
        assertEquals(Api1_Row.INTERNAL_FIELD_FIRST_NAME, "first_name");
        assertEquals(Api1_Row.ROW_KEY_FIRST_NAME, "firstName");
    }

    @Test
    public void with() {
        Api1_Row r1 = new Api1_Row();
        r1 = r1
                .withId(1)
                .withFirstName("a");

        assertEquals(r1.getId(), new Integer(1));
        assertEquals(r1.getFirstName(), "a");

        Api2_Row r2 = new Api2_Row()
                .withId(2)
                .withFirstName("b")
                .withAge(50);

        assertEquals(r2.getId(), new Integer(2));
        assertEquals(r2.getFirstName(), "b");
        assertEquals(r2.getAge(), new Integer(50));
    }

    @Test
    public void asWithInstance() {
        Api1_Row p1 = new Api1_Row().withId(1).withFirstName("a");
        Api2_Row p2 = new Api2_Row().withId(2).withFirstName("b").withAge(50);

        p2 = p1.as(p2);
        assertEquals(p2.getId(), new Integer(1));
        assertEquals(p2.getFirstName(), "a");
        assertEquals(p2.getAge(), new Integer(50));
    }

    @Test
    public void asWithClass() {
        Api1_Row p1 = new Api1_Row().withId(1).withFirstName("a");

        Api2_Row p2 = p1.as(Api2_Row.class);
        assertEquals(p2.getId(), new Integer(1));
        assertEquals(p2.getFirstName(), "a");
        assertNull(p2.getAge());
    }

    @Test
    public void updatedWith() {
        Api2_Row p2 = new Api2_Row().withId(2).withFirstName("b").withAge(50);

        Api1_Row p1 = Api1_Row.from(p2);
        assertEquals(p1.getId(), new Integer(2));
        assertEquals(p1.getFirstName(), "b");
    }

    @Test
    public void dataClassEquals() {
        Api2_Row p1 = new Api2_Row().withId(2).withFirstName("b").withAge(50);
        Api2_Row p2 = new Api2_Row().withId(2).withFirstName("b").withAge(50);
        assertEquals(p1, p2);
    }

    @Test
    public void dataClassHashCode() {
        Api2_Row p1 = new Api2_Row().withId(2).withFirstName("b").withAge(50);
        Api2_Row p2 = new Api2_Row().withId(2).withFirstName("b").withAge(50);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    public void dataClassToString() {
        Api2_Row o = new Api2_Row().withId(123456789).withFirstName("abcdefghi").withAge(987654321);
        assertTrue(o.toString().contains("Api2_Row"));
        assertTrue(o.toString().contains("id="));
        assertTrue(o.toString().contains("12345678"));
        assertTrue(o.toString().contains("firstName="));
        assertTrue(o.toString().contains("abcdefghi"));
        assertTrue(o.toString().contains("age="));
        assertTrue(o.toString().contains("987654321"));
    }

    @Test
    public void toInternalMap() {
        Api1_Row person1Row = new Api1_Row()
                .withFirstName("Max");

        Map<String, Object> map = person1Row.toInternalMap();

        assertTrue(map.containsKey("first_name"));
        assertTrue(map.containsKey(Api1_Row.INTERNAL_FIELD_FIRST_NAME));
    }

    @Test
    public void toRow() {
        Api1_Row person1Row = new Api1_Row()
                .withFirstName("Max");

        Map<String, Object> map = person1Row.toRowMap();
        assertTrue(map.containsKey(Api1_Row.ROW_KEY_FIRST_NAME));
    }

    @Test
    public void nullableAndNonnullAnnotation() throws NoSuchMethodException {
        Invokable<?, Object> withId = Invokable.from(Api2_Row.class.getMethod("withId", Integer.class));
        assertTrue(withId.getParameters().get(0).isAnnotationPresent(Nonnull.class));

        Invokable<?, Object> withFirstName = Invokable.from(Api2_Row.class.getMethod("withFirstName", String.class));
        assertTrue(withFirstName.getParameters().get(0).isAnnotationPresent(Nullable.class));
    }

}
