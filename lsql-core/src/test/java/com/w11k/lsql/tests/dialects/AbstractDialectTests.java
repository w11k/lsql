package com.w11k.lsql.tests.dialects;

import com.w11k.lsql.*;
import com.w11k.lsql.converter.JavaBoolToSqlStringConverter;
import com.w11k.lsql.dialects.BaseDialect;
import com.w11k.lsql.jdbc.ConnectionProviders;
import com.w11k.lsql.sqlfile.LSqlFile;
import com.w11k.lsql.tests.TestUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
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

    private Exception initException;

    @BeforeClass
    public void beforeClass() throws SQLException {
        try {
            properties = readProperties();
            dataSource = createDataSource();
            TestUtils.clear(dataSource);
            Connection con = dataSource.getConnection();
            this.lSql = new LSql(createDialect(), ConnectionProviders.fromInstance(con));
            setupTestTable();
            setupTableForAliasTests();
            this.initOk = true;
        } catch (Exception e) {
            initException = e;
        }
    }

    @AfterClass
    public void afterClass() throws Exception {
        if (!initOk) {
            return;
        }
        lSql.getConnectionProvider().call().commit();
        lSql.getConnectionProvider().call().close();
        TestUtils.clear(dataSource);
    }

    @BeforeMethod
    public void beforeMethod() {
        if (!initOk) {
            return;
        }
        lSql.executeRawSql("DELETE FROM table1;");
    }

    @Test
    public void insertGetDelete() throws SQLException {
        skipOnConfigError();

        Table<?> table1 = lSql.table("table1");

        // Insert
        Row row1 = new Row().addKeyVals("age", 10);
        Object id1 = table1.insert(row1).get();
        Row row2 = new Row().addKeyVals("age", 20);
        Object id2 = table1.insert(row2).get();

        // Verify insert
        int tableSize = lSql.executeRawQuery("SELECT * FROM table1;").asList().size();
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
        tableSize = lSql.executeRawQuery("SELECT * FROM table1;").asList().size();
        assertEquals(tableSize, 1);
    }

    @Test
    public void blob() {
        skipOnConfigError();
        byte[] data = "123456789".getBytes();
        Blob blob = new Blob(data);
        TestUtils.testType(lSql, getBlobColumnType(), blob, blob);
    }

    @Test
    public void resolveTableAliasWhenReadingResultSet() {
        skipOnConfigError();
        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "aliases.sql");
        List<QueriedRow> list = lSqlFile.statement("resolveTableAliasWhenReadingResultSet").query().asList();
        assertEquals(list.size(), 2);

        QueriedRow row1 = list.get(0);
        assertEquals(row1.getInt("id"), 1);
        assertEquals(row1.getBoolean("yesno"), Boolean.TRUE);

        QueriedRow row2 = list.get(1);
        assertEquals(row2.getInt("id"), 2);
        assertEquals(row2.getBoolean("yesno"), Boolean.FALSE);
    }

    @Test
    public void resolveTableAliasWithWildcardWhenReadingResultSet() {
        skipOnConfigError();
        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "aliases.sql");
        List<QueriedRow> list = lSqlFile.statement("resolveTableAliasWithWildcardWhenReadingResultSet").query().asList();
        assertEquals(list.size(), 2);

        QueriedRow row1 = list.get(0);
        assertEquals(row1.getInt("id"), 1);
        assertEquals(row1.getBoolean("yesno"), Boolean.TRUE);

        QueriedRow row2 = list.get(1);
        assertEquals(row2.getInt("id"), 2);
        assertEquals(row2.getBoolean("yesno"), Boolean.FALSE);
    }

    @Test
    public void resolveJoinedTableAliasWhenReadingResultSet() {
        skipOnConfigError();
        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "aliases.sql");
        List<QueriedRow> list = lSqlFile.statement("resolveJoinedTableAliasWhenReadingResultSet").query().asList();
        assertEquals(list.size(), 2);

        QueriedRow row1 = list.get(0);
        assertEquals(row1.getInt("t1_id"), 1);
        assertEquals(row1.getBoolean("t1_yesno"), Boolean.TRUE);
        assertEquals(row1.getInt("t2_id"), 1);
        assertEquals(row1.getBoolean("t2_yesno"), Boolean.TRUE);

        QueriedRow row2 = list.get(1);
        assertEquals(row2.getInt("t1_id"), 2);
        assertEquals(row2.getBoolean("t1_yesno"), Boolean.FALSE);
        assertEquals(row2.getInt("t2_id"), 2);
        assertEquals(row2.getBoolean("t2_yesno"), Boolean.FALSE);
    }

    @Test
    public void resolveColumnAliasWhenReadingResultSet() {
        skipOnConfigError();
        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "aliases.sql");
        List<QueriedRow> list = lSqlFile.statement("resolveColumnAliasWhenReadingResultSet").query().asList();
        assertEquals(list.size(), 2);

        QueriedRow row1 = list.get(0);
        assertEquals(row1.getInt("ta_id"), 1);
        assertEquals(row1.getBoolean("ta_yesno"), Boolean.TRUE);

        QueriedRow row2 = list.get(1);
        assertEquals(row2.getInt("ta_id"), 2);
        assertEquals(row2.getBoolean("ta_yesno"), Boolean.FALSE);
    }

    @Test
    public void resolveTableAndColumnAliasWhenReadingResultSet() {
        skipOnConfigError();
        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "aliases.sql");
        List<QueriedRow> list = lSqlFile.statement("resolveTableAndColumnAliasWhenReadingResultSet").query().asList();
        assertEquals(list.size(), 2);

        QueriedRow row1 = list.get(0);
        assertEquals(row1.getInt("ta_id"), 1);
        assertEquals(row1.getBoolean("ta_yesno"), Boolean.TRUE);

        QueriedRow row2 = list.get(1);
        assertEquals(row2.getInt("ta_id"), 2);
        assertEquals(row2.getBoolean("ta_yesno"), Boolean.FALSE);
    }

    @Test
    public void resolveTableAliasWhenSettingParameter() {
        skipOnConfigError();
        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "aliases.sql");
        List<QueriedRow> list = lSqlFile.statement("resolveTableAliasWhenSettingParameter").query(
                "t1.yesno", false
        ).asList();
        assertEquals(list.size(), 1);

        QueriedRow row1 = list.get(0);
        assertEquals(row1.getInt("id"), 2);
        assertEquals(row1.getBoolean("yesno"), Boolean.FALSE);
    }

    protected void setupTableForAliasTests() {
        lSql.executeRawSql("CREATE TABLE ta (id INT PRIMARY KEY, yesno VARCHAR(3))");
        Table taTable = lSql.table("ta");
        taTable.column("yesno").setConverter(new JavaBoolToSqlStringConverter("yes", "no"));
        taTable.newLinkedRow("id", 1, "yesno", true).save();
        taTable.newLinkedRow("id", 2, "yesno", false).save();
    }

    protected void skipOnConfigError() {
        if (!initOk) {
            throw new SkipException("Init failed: " + initException.getMessage(), initException) {
                @Override
                public boolean isSkip() {
                    return true;
                }
            };
        }
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

}
