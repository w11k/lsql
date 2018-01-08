package com.w11k.lsql.tests;

import com.w11k.lsql.LinkedRow;
import com.w11k.lsql.Row;
import com.w11k.lsql.Table;
import com.w11k.lsql.tests.testdata.PersonTestData;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class TableColumnIgnoreTest extends AbstractLSqlTest {

    @Test
    public void ignoreColumnOnInsert() {
        PersonTestData.init(this.lSql, false);
        Table person = this.lSql.table("person");
        person.column("firstName").setIgnored(true);

        person.insert(Row.fromKeyVals(
                "id", 1,
                "firstName", "Adam"
        ));

        Row row = this.lSql.createSqlStatement(PersonTestData.SELECT_ALL_ORDER_BY_ID).query().first().get();
        assertNull(row.get("firstName"));
    }

    @Test
    public void ignoreColumnOnLoad() {
        PersonTestData.init(this.lSql, true);
        Table person = this.lSql.table("person");
        person.column("firstName").setIgnored(true);

        LinkedRow p1 = person.load(1).get();
        assertNull(p1.get("firstName"));
    }

    @Test
    public void ignoreColumnOnQuery() {
        PersonTestData.init(this.lSql, true);
        Table person = this.lSql.table("person");
        person.column("firstName").setIgnored(true);

        Row row = lSql.createSqlStatement("select * from person").query().toList().get(0);
        assertNull(row.get("firstName"));
    }

    @Test
    public void ignoreColumnOnUpdateFlag() {
        PersonTestData.init(this.lSql, true);
        Table person = this.lSql.table("person");

        person.column("id").setIgnoreOnUpdate(true);
        person.column("age").setIgnoreOnUpdate(true);
        person.column("title").setIgnoreOnUpdate(true);

        Row row = lSql.createSqlStatement("select * from person").query().toList().get(0);
        assertEquals(row.get("firstName"), "Adam");
        assertEquals(row.get("age"), 30);
        assertEquals(row.get("title"), "n/a");

        row.put("firstName", "Adam2");
        row.put("age", 32);
        row.put("title", "n/a2");
        person.update(row);

        row = lSql.createSqlStatement("select * from person").query().toList().get(0);
        assertEquals(row.get("firstName"), "Adam2");
        assertEquals(row.get("age"), 30);
        assertEquals(row.get("title"), "n/a");
    }

}
