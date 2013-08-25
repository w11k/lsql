package com.w11k.lsql.tests;

import com.beust.jcommander.internal.Lists;
import com.w11k.lsql.LSql;
import com.w11k.lsql.jdbc.ConnectionFactories;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractLSqlTest {

    private static final Pattern CREATE_TABLE_STATEMENT = Pattern.compile(
            "create table (\\w+).*",
            Pattern.CASE_INSENSITIVE);

    protected LSql lSql;

    protected DataSource dataSource;

    protected Connection connection;

    protected List<String> createdTables = Lists.newLinkedList();

    protected AbstractLSqlTest() {
        dataSource = TestsFactory.createPostgresDataSource();
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @BeforeMethod
    public void beforeMethod() throws SQLException {
        connection = dataSource.getConnection();
        lSql = new LSql(ConnectionFactories.fromInstance(connection));
    }

    @AfterMethod
    public void afterMethod() throws SQLException {
        dropCreatedTables();
        connection.close();
    }

    protected void createTable(String sql) {
        Matcher startMatcher = CREATE_TABLE_STATEMENT.matcher(sql);
        startMatcher.find();
        String tableName = startMatcher.group(1);
        lSql.executeRawSql(sql);
        createdTables.add(tableName);
    }

    protected void dropCreatedTables() {
        for (String createdTable : createdTables) {
            try {
                lSql.executeRawSql("drop table " + createdTable + ";");
            } catch (Exception ignored) {
            }
        }
    }

}
