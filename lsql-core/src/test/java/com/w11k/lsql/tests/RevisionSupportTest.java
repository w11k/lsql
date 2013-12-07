package com.w11k.lsql.tests;

import com.w11k.lsql.Row;
import com.w11k.lsql.Table;
import com.w11k.lsql.exceptions.DeleteException;
import com.w11k.lsql.exceptions.UpdateException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class RevisionSupportTest extends AbstractLSqlTest {

    @Test
    public void insertSetsRevision() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT, revision INT DEFAULT 0)");
        Table table1 = lSql.table("table1");
        table1.enableRevisionSupport();

        Row row1 = Row.fromKeyVals("id", 1, "age", 1);
        table1.insert(row1);
        assertNotNull(row1.get("revision"));
    }

    @Test
    public void updateIncreasesRevision() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT, revision INT DEFAULT 0)");
        Table table1 = lSql.table("table1");
        table1.enableRevisionSupport();

        Row row = Row.fromKeyVals("id", 1, "age", 1);
        table1.insert(row);
        int r1 = row.getInt("revision");

        table1.save(row);
        int r2 = row.getInt("revision");
        assertTrue(r1 < r2);

        table1.save(row);
        int r3 = row.getInt("revision");
        assertTrue(r2 < r3);
    }

    @Test(expectedExceptions = UpdateException.class)
    public void updateFailsOnWrongRevision() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT, revision INT DEFAULT 0)");
        Table table1 = lSql.table("table1");
        table1.enableRevisionSupport();

        Row row = Row.fromKeyVals("id", 1, "age", 1);
        table1.insert(row);
        int r1 = row.getInt("revision");

        row.put("revision", r1 + 1);
        table1.save(row);
    }

    @Test(expectedExceptions = DeleteException.class)
    public void deleteFailsOnWrongRevision() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT, revision INT DEFAULT 0)");
        Table table1 = lSql.table("table1");
        table1.enableRevisionSupport();

        Row row = Row.fromKeyVals("id", 1, "age", 1);
        table1.insert(row);
        int r1 = row.getInt("revision");

        row.put("revision", r1 + 1);
        table1.delete(row);
    }

}
