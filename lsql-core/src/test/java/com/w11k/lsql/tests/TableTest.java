package com.w11k.lsql.tests;

import com.google.common.base.Optional;
import com.w11k.lsql.exceptions.InsertException;
import com.w11k.lsql.exceptions.UpdateException;
import com.w11k.lsql.relational.QueriedRow;
import com.w11k.lsql.relational.Row;
import com.w11k.lsql.relational.Table;
import org.testng.annotations.Test;

import java.sql.SQLException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TableTest extends AbstractLSqlTest {

    @Test(dataProvider = "lSqlProvider")
    public void getById(LSqlProvider provider) {
        provider.init(this);

        createTable("CREATE TABLE table1 (id SERIAL PRIMARY KEY, age INT)");
        Table table1 = lSql.table("table1");

        Object id1 = table1.insert(Row.fromKeyVals("age", 1)).get();
        Object id2 = table1.insert(Row.fromKeyVals("age", 2)).get();
        Object id3 = table1.insert(Row.fromKeyVals("age", 3)).get();

        assertEquals(table1.get(id1).getInt("age"), 1);
        assertEquals(table1.get(id2).getInt("age"), 2);
        assertEquals(table1.get(id3).getInt("age"), 3);
    }

    @Test(dataProvider = "lSqlProvider")
    public void insertRow(LSqlProvider provider) throws SQLException {
        provider.init(this);

        createTable("CREATE TABLE table1 (name TEXT)");
        Table table1 = lSql.table("table1");

        Row row = new Row().addKeyVals("name", "cus1");
        table1.insert(row);

        Row insertedRow = lSql.executeRawQuery("select * from table1").getFirstRow();
        assertEquals(insertedRow.getString("name"), "cus1");
    }

    @Test(dataProvider = "lSqlProvider")
    public void insertShouldReturnGeneratedKey(LSqlProvider provider) {
        provider.init(this);

        createTable("CREATE TABLE table1 (id SERIAL PRIMARY KEY, age INT)");
        Table table1 = lSql.table("table1");
        Object newId = table1.insert(new Row().addKeyVals("age", 1)).get();

        Row query = lSql.executeRawQuery("select * from table1 where id = " + newId).getFirstRow();
        assertEquals(query.getInt("age"), 1);
    }

    @Test(dataProvider = "lSqlProvider")
    public void insertShouldPutIdIntoRowObject(LSqlProvider provider) {
        provider.init(this);

        createTable("CREATE TABLE table1 (id SERIAL PRIMARY KEY, age INT)");
        Table table1 = lSql.table("table1");
        Row row = new Row().addKeyVals("age", 1);
        Optional<Object> optional = table1.insert(row);
        assertTrue(optional.isPresent());
        assertEquals(optional.get(), row.get("id"));
    }

    @Test(dataProvider = "lSqlProvider", expectedExceptions = InsertException.class)
    public void insertShouldFailIfPrimaryKeyIsAlreadyPresent(LSqlProvider provider) {
        provider.init(this);

        createTable("CREATE TABLE table1 (id SERIAL PRIMARY KEY, age INT)");
        Table table1 = lSql.table("table1");
        Row row = new Row().addKeyVals("id", 1, "age", 1);
        table1.insert(row);
    }

    @Test(dataProvider = "lSqlProvider", expectedExceptions = UpdateException.class)
    public void updateShouldFailWhenIdNotPresent(LSqlProvider provider) throws SQLException {
        provider.init(this);

        createTable("CREATE TABLE table1 (id SERIAL PRIMARY KEY, name TEXT)");
        Table table1 = lSql.table("table1");
        Row row = new Row().addKeyVals("name", "Max");
        table1.update(row);
    }

    @Test(dataProvider = "lSqlProvider")
    public void updateById(LSqlProvider provider) throws SQLException {
        provider.init(this);

        createTable("CREATE TABLE table1 (id SERIAL PRIMARY KEY, name TEXT)");
        Table table1 = lSql.table("table1");
        Row row = new Row().addKeyVals("name", "Max");
        Object id = table1.insert(row).get();
        QueriedRow queriedRow = table1.get(id);
        assertEquals(queriedRow, row);

        row.put("name", "John");
        table1.update(row);
        queriedRow = table1.get(id);
        assertEquals(queriedRow, row);
    }

    @Test(dataProvider = "lSqlProvider", expectedExceptions = InsertException.class)
    public void updateWithWrongId(LSqlProvider provider) throws SQLException {
        provider.init(this);

        createTable("CREATE TABLE table1 (id SERIAL PRIMARY KEY, name TEXT)");
        Table table1 = lSql.table("table1");
        Row row = new Row().addKeyVals("name", "Max");
        Object id = table1.insert(row).get();
        QueriedRow queriedRow = table1.get(id);
        assertEquals(queriedRow, row);

        row.put("id", 999);
        row.put("name", "John");
        table1.update(row);
    }

    @Test(dataProvider = "lSqlProvider")
    public void insertOrUpdate(LSqlProvider provider) throws SQLException {
        provider.init(this);

        createTable("CREATE TABLE table1 (id SERIAL PRIMARY KEY, name TEXT)");
        Table table1 = lSql.table("table1");

        // Insert
        Row row = new Row().addKeyVals("name", "Max");
        Object id = table1.insertOrUpdate(row).get();

        // Verify insert
        QueriedRow queriedRow = table1.get(id);
        assertEquals(queriedRow, row);

        // Update
        row.put("name", "John");
        id = table1.insertOrUpdate(row).get();

        // Verify update
        queriedRow = table1.get(id);
        assertEquals(queriedRow, row);
    }

    @Test(dataProvider = "lSqlProvider")
    public void delete(LSqlProvider provider) throws SQLException {
        provider.init(this);
        createTable("CREATE TABLE table1 (id SERIAL PRIMARY KEY, name TEXT)");
        Table table1 = lSql.table("table1");

        // Insert
        Row row = new Row().addKeyVals("name", "Max");
        Object id = table1.insert(row).get();

        // Verify insert
        int tableSize = lSql.executeRawQuery("select * from table1;").asList().size();
        assertEquals(tableSize, 1);

        // Insert 2nd row
        table1.insert(new Row().addKeyVals("name", "Phil"));

        // Delete
        table1.delete(id);

        // Verify delete
        tableSize = lSql.executeRawQuery("select * from table1;").asList().size();
        assertEquals(tableSize, 1);
    }

}
