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

        Row row = this.lSql.executeRawQuery(PersonTestData.SELECT_ALL_ORDER_BY_ID).first().get();
        assertNull(row.get("firstName"));
    }

    @Test
    public void ignoreColumnOnUpdate() {
        PersonTestData.init(this.lSql, true);
        Table person = this.lSql.table("person");
        person.column("firstName").setIgnored(true);

        person.update(Row.fromKeyVals(
                "id", 1,
                "firstName", "CHANGED"
        ));

        Row row = this.lSql.executeRawQuery(PersonTestData.SELECT_ALL_ORDER_BY_ID).first().get();
        assertEquals(row.get("firstName"), "Adam");
    }

    @Test
    public void ignoreColumnOnLoad() {
        PersonTestData.init(this.lSql, true);
        Table person = this.lSql.table("person");
        person.column("firstName").setIgnored(true);

        LinkedRow p1 = person.load(1).get();
        assertNull(p1.get("firstName"));
    }

}
