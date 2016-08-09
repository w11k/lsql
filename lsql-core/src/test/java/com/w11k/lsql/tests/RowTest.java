package com.w11k.lsql.tests;

import com.w11k.lsql.Row;
import com.w11k.lsql.WrongTypeException;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class RowTest extends AbstractLSqlTest {

    @Test
    public void addKeyVals() {
        Row r = new Row().addKeyVals("a", 1, "b", "val");
        assertEquals(r.get("a"), 1);
        assertEquals(r.get("b"), "val");
    }

    @Test
    public void constructorCreatesDefensiveCopy() {
        Row row1 = new Row().addKeyVals("key1", "value1");
        Row row2 = new Row(row1);

        row1.put("key2", "value2");
        assertFalse(row2.containsKey("key2"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void getAsThrowsExceptionOnWrongKey() {
        Row r = new Row().addKeyVals("a", "1");
        assertEquals(r.getInt("x"), (Integer) 1);
    }

    @Test
    public void testNull() {
        Row row = new Row();
        assertNull(row.get("x"));
        row.addKeyVals("x", 1d);
        assertEquals(1d, row.getDouble("x"));
    }

    @Test
    public void testNullValues() {
        Row row = new Row();
        row.put("string", null);
        assertNull(row.getString("string"));
    }

    @Test
    public void stringToDateTimeConversion() {
        LocalDateTime dt = new DateTime().toLocalDateTime();
        Row r = Row.fromKeyVals(
                "dateTime", dt.toString()
        );
        assertTrue(r.get("dateTime") instanceof String);
        LocalDateTime dateTime = r.getDateTime("dateTime").toLocalDateTime();
        assertEquals(dt, dateTime);
    }

    @Test
    public void longToDateTime() {
        Row r = new Row();
        DateTime dt1 = new DateTime();
        r.put("date", dt1.getMillis());
        assertTrue(r.get("date") instanceof Long);
        DateTime dt2 = r.getDateTime("date");
        assertEquals(dt1, dt2);
    }

    @Test(expectedExceptions = WrongTypeException.class)
    public void failOnWrongType() {
        Row r = Row.fromKeyVals(
                "a", 1
        );
        r.getString("a");
    }

    @Test
    public void explicitlyConvertValue() {
        Row r = Row.fromKeyVals(
                "a", 1
        );
        String a = r.getAs(String.class, "a", true);
        assertEquals(a, "1");
    }

    @Test
    public void implicitlyConvertValue() {
        Row r = Row.fromKeyVals(
                "a", 1
        );
        Row.LEGACY_CONVERT_VALUE_ON_GET = true;
        String a = r.getAs(String.class, "a");
        assertEquals(a, "1");
        Row.LEGACY_CONVERT_VALUE_ON_GET = false;
    }

}
