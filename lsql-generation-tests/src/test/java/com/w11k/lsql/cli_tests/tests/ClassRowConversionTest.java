package com.w11k.lsql.cli_tests.tests;

import com.w11k.lsql.cli.schema_public.Person1_Row;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

public final class ClassRowConversionTest extends AbstractTestCliTest {

    @Test
    public void classToRow() {
        Person1_Row p1 = new Person1_Row()
                .withId(1)
                .withFirstName("Max");

        Map<String, Object> p1asRow = p1.toRowMap();
        assertEquals(p1asRow.get(this.lSql.convertInternalSqlToRowKey("id")), 1);
        assertEquals(p1asRow.get(this.lSql.convertInternalSqlToRowKey("first_name")), "Max");
    }

    @Test
    public void rowToClass() {
        Map<String, Object> p1asRow = new HashMap<>();
        p1asRow.put(this.lSql.convertInternalSqlToRowKey("id"), 1);
        p1asRow.put(this.lSql.convertInternalSqlToRowKey("first_name"), "Max");

        Person1_Row p1 = Person1_Row.fromRow(p1asRow);
        assertEquals(p1.id, Integer.valueOf(1));
        assertEquals(p1.firstName, "Max");
    }

}
