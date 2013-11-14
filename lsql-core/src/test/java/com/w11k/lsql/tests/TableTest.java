package com.w11k.lsql.tests;

import com.google.common.base.Optional;
import com.google.gson.GsonBuilder;
import com.w11k.lsql.QueriedRow;
import com.w11k.lsql.Row;
import com.w11k.lsql.Table;
import com.w11k.lsql.exceptions.InsertException;
import com.w11k.lsql.exceptions.UpdateException;
import com.w11k.lsql.validation.AbstractValidationError;
import com.w11k.lsql.validation.KeyError;
import com.w11k.lsql.validation.TypeError;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

public class TableTest extends AbstractLSqlTest {

    @Test
    public void getById() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT)");
        Table table1 = lSql.table("table1");

        table1.insert(Row.fromKeyVals("id", 1, "age", 1));
        table1.insert(Row.fromKeyVals("id", 2, "age", 2));
        table1.insert(Row.fromKeyVals("id", 3, "age", 3));

        assertEquals(table1.get(1).get().getInt("age"), 1);
        assertEquals(table1.get(2).get().getInt("age"), 2);
        assertEquals(table1.get(3).get().getInt("age"), 3);
    }

    @Test
    public void getByIdReturnAbsentOnWrongId() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT)");
        Table table1 = lSql.table("table1");

        assertFalse(table1.get(999).isPresent());
    }

    @Test
    public void insertRow() throws SQLException {
        createTable("CREATE TABLE table1 (name TEXT)");
        Table table1 = lSql.table("table1");

        Row row = new Row().addKeyVals("name", "cus1");
        table1.insert(row);

        Row insertedRow = lSql.executeRawQuery("SELECT * FROM table1").getFirstRow().get();
        assertEquals(insertedRow.getString("name"), "cus1");
    }

    @Test
    public void insertShouldReturnGeneratedKey() {
        createTable("CREATE TABLE table1 (id SERIAL PRIMARY KEY, age INT)");
        Table table1 = lSql.table("table1");
        Object newId = table1.insert(new Row().addKeyVals("age", 1)).get();

        Row query = lSql.executeRawQuery("select * from table1 where id = " + newId).getFirstRow()
                .get();
        assertEquals(query.getInt("age"), 1);
    }

    @Test
    public void insertShouldPutIdIntoRowObject() {
        createTable("CREATE TABLE table1 (id SERIAL PRIMARY KEY, age INT)");
        Table table1 = lSql.table("table1");
        Row row = new Row().addKeyVals("age", 1);
        Optional<Object> optional = table1.insert(row);
        assertTrue(optional.isPresent());
        assertEquals(optional.get(), row.get("id"));
    }

    @Test(expectedExceptions = InsertException.class)
    public void insertShouldFailOnWrongKeys() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT)");
        Table table1 = lSql.table("table1");
        Row row = new Row().addKeyVals("age", 1, "wrong", "value");
        Optional<Object> optional = table1.insert(row);
        assertTrue(optional.isPresent());
        assertEquals(optional.get(), row.get("id"));
    }

    @Test(expectedExceptions = UpdateException.class)
    public void updateShouldFailWhenIdNotPresent() throws SQLException {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, name TEXT)");
        Table table1 = lSql.table("table1");
        Row row = new Row().addKeyVals("name", "Max");
        table1.update(row);
    }

    @Test(expectedExceptions = UpdateException.class)
    public void updateShouldFailOnWrongKeys() throws SQLException {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, name TEXT)");
        Table table1 = lSql.table("table1");
        Row row = new Row().addKeyVals("id", 1, "name", "Max");
        table1.insert(row);

        row.put("wrong", "value");
        table1.update(row);
    }

    @Test
    public void updateById() throws SQLException {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, name TEXT)");
        Table table1 = lSql.table("table1");
        Row row = new Row().addKeyVals("id", 1, "name", "Max");
        table1.insert(row);
        QueriedRow queriedRow = table1.get(1).get();
        assertEquals(queriedRow, row);

        row.put("name", "John");
        table1.update(row);
        queriedRow = table1.get(1).get();
        assertEquals(queriedRow, row);
    }

    @Test(expectedExceptions = UpdateException.class)
    public void updateWithWrongId() throws SQLException {
        createTable("CREATE TABLE table1 (id SERIAL PRIMARY KEY, name TEXT)");
        Table table1 = lSql.table("table1");
        Row row = new Row().addKeyVals("name", "Max");
        Object id = table1.insert(row).get();
        QueriedRow queriedRow = table1.get(id).get();
        assertEquals(queriedRow, row);

        row.put("id", 999);
        row.put("name", "John");
        table1.update(row);
    }

    @Test
    public void save() throws SQLException {
        createTable("CREATE TABLE table1 (id SERIAL PRIMARY KEY, name TEXT)");
        Table table1 = lSql.table("table1");

        // Insert
        Row row = Row.fromKeyVals("name", "Max");
        Object id = table1.save(row).get();
        assertEquals(id, row.get(table1.getPrimaryKeyColumn().get()));

        // Verify insert
        QueriedRow queriedRow = table1.get(id).get();
        assertEquals(queriedRow, row);

        // Update
        row.put("name", "John");
        id = table1.save(row).get();

        // Verify update
        queriedRow = table1.get(id).get();
        assertEquals(queriedRow, row);
    }

    @Test
    public void saveWithoutAutoIncrement() throws SQLException {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY, name TEXT)");
        Table table1 = lSql.table("table1");

        // Insert
        Row row = Row.fromKeyVals("id", 1, "name", "Max");
        Object id = table1.save(row).get();
        assertEquals(id, row.get(table1.getPrimaryKeyColumn().get()));

        // Verify insert
        QueriedRow queriedRow = table1.get(id).get();
        assertEquals(queriedRow, row);

        // Update
        row.put("name", "John");
        id = table1.save(row).get();

        // Verify update
        queriedRow = table1.get(id).get();
        assertEquals(queriedRow, row);

        List<QueriedRow> rows = lSql.executeRawQuery("SELECT * FROM table1").asList();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void delete() throws SQLException {
        createTable("CREATE TABLE table1 (id SERIAL PRIMARY KEY, name TEXT)");
        Table table1 = lSql.table("table1");

        // Insert
        Row row = new Row().addKeyVals("name", "Max");
        Object id = table1.insert(row).get();

        // Verify insert
        int tableSize = lSql.executeRawQuery("SELECT * FROM table1;").asList().size();
        assertEquals(tableSize, 1);

        // Insert 2nd row
        table1.insert(new Row().addKeyVals("name", "Phil"));

        // Delete
        table1.delete(id);

        // Verify delete
        tableSize = lSql.executeRawQuery("SELECT * FROM table1;").asList().size();
        assertEquals(tableSize, 1);
    }

    @Test
    public void fetchColumns() throws SQLException {
        createTable("CREATE TABLE table1 (id SERIAL PRIMARY KEY, name TEXT, age INT)");
        Table table1 = lSql.table("table1");
        assertEquals(table1.getColumns().size(), 3);
        assertTrue(table1.getColumns().containsKey("id"));
        assertTrue(table1.getColumns().containsKey("name"));
        assertTrue(table1.getColumns().containsKey("age"));
    }

    @Test
    public void validate() throws SQLException {
        createTable("CREATE TABLE table1 (id SERIAL PRIMARY KEY, name TEXT, field1 INT, field2 INT)");
        Table table1 = lSql.table("table1");

        Row r = Row.fromKeyVals(
                "field1", 1,
                "field2", "2",
                "field3", 3
        );

        Map<String, AbstractValidationError> validate = table1.validate(r);
        assertEquals(validate.size(), 2);
        assertEquals(validate.get("field2").getClass(), TypeError.class);
        assertEquals(validate.get("field3").getClass(), KeyError.class);

        String s = new GsonBuilder().setPrettyPrinting().create().toJson(validate);
        System.out.println(s);
    }

}
