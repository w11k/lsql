package com.w11k.lsql.tests.dialects;

import com.w11k.lsql.*;
import com.w11k.lsql.dialects.BaseDialect;
import com.w11k.lsql.jdbc.ConnectionProviders;
import com.w11k.lsql.sqlfile.LSqlFile;
import com.w11k.lsql.tests.TestUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static org.testng.Assert.assertEquals;

public abstract class AbstractDialectTests {

    protected LSql lSql;

    protected Properties properties;

    protected DataSource dataSource;

    private boolean initOk = false;

    @BeforeMethod
    public void beforeMethod() throws SQLException {
        try {
            properties = readProperties();
            dataSource = createDataSource();
            TestUtils.clear(dataSource);
            Connection con = dataSource.getConnection();
            this.lSql = new LSql(createDialect(), ConnectionProviders.fromInstance(con));
            this.initOk = true;
        } catch (Exception e) {
            Reporter.getCurrentTestResult().setStatus(ITestResult.SKIP);
            e.printStackTrace();
        }
    }

    @AfterMethod
    public void afterMethod() throws Exception {
        if (!initOk) {
            return;
        }
        if (lSql != null) {
            lSql.getConnectionProvider().call().close();
        }
    }

    @Test
    public void run() throws SQLException {
        if (!initOk) {
            return;
        }
        // TODO insertGetDelete();
        // TODO blob();
        columnAliasBehaviour();
    }

    public void insertGetDelete() throws SQLException {
        setupTestTable();

        Table table1 = lSql.table("table1");

        // Insert
        Row row1 = new Row().addKeyVals("age", 10);
        Object id1 = table1.insert(row1).get();
        Row row2 = new Row().addKeyVals("age", 20);
        Object id2 = table1.insert(row2).get();

        // Verify insert
        int tableSize = lSql.executeRawQuery("SELECT * FROM table1;").asRawList().size();
        assertEquals(tableSize, 2);

        LinkedRow queried1 = table1.get(id1).get();
        assertEquals(queried1.get("id"), id1);
        assertEquals(queried1.get("age"), 10);
        assertEquals(row1.get("id"), id1);

        LinkedRow queried2 = table1.get(id2).get();
        assertEquals(queried2.get("id"), id2);
        assertEquals(queried2.getInt("age"), 20);
        assertEquals(row2.get("id"), id2);

        // Delete
        table1.delete(row2);

        // Verify delete
        tableSize = lSql.executeRawQuery("SELECT * FROM table1;").asRawList().size();
        assertEquals(tableSize, 1);
    }

    public void blob() {
        byte[] data = "123456789".getBytes();
        Blob blob = new Blob(data);
        TestUtils.testType(lSql, getBlobColumnType(), blob, blob);
    }

    protected String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    protected Properties readProperties() {
        Properties p = new Properties();
        String className = getClass().getSimpleName();
        String fileName = className + "_" + getHostname() + ".dblocal";
        InputStream inputStream = getClass().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new RuntimeException(
                    "File '" + getClass().getPackage().getName().replace('.', '/') + "/" +
                            fileName + "' not found");
        }
        try {
            p.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return p;
    }

    protected String getDatabaseUrl() {
        return properties.getProperty("url");
    }

    protected String getDatabaseDriver() {
        return properties.getProperty("driver");
    }

    protected String getDatabaseUser() {
        return properties.getProperty("user");
    }

    protected String getDatabasePassword() {
        return properties.getProperty("password");
    }

    protected DataSource createDataSource() throws SQLException {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(getDatabaseDriver());
        ds.setUrl(getDatabaseUrl());
        ds.setUsername(getDatabaseUser());
        ds.setPassword(getDatabasePassword());
        ds.setDefaultAutoCommit(false);
        return ds;
    }

    protected abstract BaseDialect createDialect();

    protected abstract void setupTestTable();

    protected abstract String getBlobColumnType();

    private void columnAliasBehaviour() {
        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "statements.sql");
        lSqlFile.statement("create2").execute();
        lSqlFile.statement("insert2").execute();

        List<QueriedRow> list = lSqlFile.statement("columnAliasBehaviour").query().asRawList();
        assertEquals(list.size(), 1);
        QueriedRow queriedRow = list.get(0);

        List<ResultSetColumn> resultSetColumns = queriedRow.getResultSetColumns();
        assertEquals(resultSetColumns.size(), 2);
        ResultSetColumn col = resultSetColumns.get(0);
        assertEquals(col.getPosition(), 1);
        assertEquals(col.getName(), "a");

        // TODO: alias support not implemented yet
        // assertTrue(col.getColumn().getTable().isPresent());
    }

}
