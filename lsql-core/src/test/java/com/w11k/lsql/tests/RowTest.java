package com.w11k.lsql.tests;

import com.w11k.lsql.Row;
import org.testng.annotations.Test;

import static junit.framework.Assert.assertFalse;
import static org.testng.Assert.assertEquals;

public class RowTest extends AbstractLSqlTest {

    @Test public void addKeyVals() {
        Row r = new Row().addKeyVals("a", 1, "b", "val");
        assertEquals(r.get("a"), 1);
        assertEquals(r.get("b"), "val");
    }

    @Test public void constructorCreatesDefensiveCopy() {
        Row row1 = new Row().addKeyVals("key1", "value1");
        Row row2 = new Row(row1);

        row1.put("key2", "value2");
        assertFalse(row2.containsKey("key2"));
    }

    @Test(expectedExceptions = ClassCastException.class)
    public void getAsThrowsClassCastExceptionOnWrongType() {
        Row r = new Row().addKeyVals("a", "1");
        assertEquals(r.getInt("a"), 1);
    }

}
