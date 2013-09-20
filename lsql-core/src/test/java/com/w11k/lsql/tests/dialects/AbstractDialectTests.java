package com.w11k.lsql.tests.dialects;

import com.w11k.lsql.LSql;
import com.w11k.lsql.QueriedRow;
import com.w11k.lsql.Row;
import com.w11k.lsql.Table;
import com.w11k.lsql.dialects.BaseDialect;
import com.w11k.lsql.jdbc.ConnectionProviders;
import com.w11k.lsql.tests.TestUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.testng.Assert.assertEquals;

public abstract class AbstractDialectTests {

    protected LSql lSql;

    public abstract DataSource createDataSource() throws SQLException;

    public abstract BaseDialect createDialect();

    @BeforeMethod
    public void beforeMethod() throws SQLException {
        DataSource dataSource = createDataSource();
        TestUtils.clear(dataSource);
        Connection con = dataSource.getConnection();
        this.lSql = new LSql(createDialect(), ConnectionProviders.fromInstance(con));
    }

    @AfterMethod
    public void afterMethod() throws Exception {
        if (lSql != null) {
            lSql.getConnectionProvider().call().close();
        }
    }

    @Test
    public void insertGetDelete() throws SQLException {
        createTestTable();

        Table table1 = lSql.table("table1");

        // Insert
        Row row1 = new Row().addKeyVals("age", 10);
        Object id1 = table1.insert(row1).get();
        Row row2 = new Row().addKeyVals("age", 20);
        Object id2 = table1.insert(row2).get();

        // Verify insert
        int tableSize = lSql.executeRawQuery("SELECT * FROM table1;").asList().size();
        assertEquals(tableSize, 2);

        QueriedRow queried1 = table1.get(id1).get();
        assertEquals(queried1.getInt("id"), id1);
        assertEquals(queried1.getInt("age"), 10);
        assertEquals(row1.getInt("id"), id1);

        QueriedRow queried2 = table1.get(id2).get();
        assertEquals(queried2.getInt("id"), id2);
        assertEquals(queried2.getInt("age"), 20);
        assertEquals(row2.getInt("id"), id2);

        // Delete
        table1.delete(id2);

        // Verify delete
        tableSize = lSql.executeRawQuery("SELECT * FROM table1;").asList().size();
        assertEquals(tableSize, 1);
    }

    /**
     * Create a table like
     * CREATE TABLE table1 (id SERIAL PRIMARY KEY, age INTEGER)
     */
    protected abstract void createTestTable();

}
