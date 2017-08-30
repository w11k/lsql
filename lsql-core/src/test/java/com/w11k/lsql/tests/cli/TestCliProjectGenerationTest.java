package com.w11k.lsql.tests.cli;

import com.google.common.io.MoreFiles;
import com.w11k.lsql.LSql;
import com.w11k.lsql.cli.Main;
import com.w11k.lsql.cli.SchemaExporter;
import com.w11k.lsql.jdbc.ConnectionProviders;
import org.apache.commons.dbcp.BasicDataSource;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public final class TestCliProjectGenerationTest {

    @Test
    public void runCli() throws SQLException, ClassNotFoundException, IOException {

        File genJavaDir = SchemaExporter.pathRelativeToProjectRoot(
                "pom.xml", "../lsql-cli-tests/src/generated/java");
        if (genJavaDir.exists()) {
            MoreFiles.deleteRecursively(genJavaDir.toPath());
        }

        String url = "jdbc:h2:mem:" + UUID.randomUUID() + ";mode=postgresql";

        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(url);
        ds.setDefaultAutoCommit(false);
        Connection connection = ds.getConnection();
        LSql lSql = new LSql(TestCliConfig.class, ConnectionProviders.fromInstance(connection));
        lSql.executeRawSql("create table person1 (id integer, first_name text)");
        lSql.executeRawSql("create table person2 (id integer, first_name text, age integer)");
        connection.close();

        String[] args = {
                TestCliConfig.class.getCanonicalName(),
                url,
                genJavaDir.getAbsolutePath()};
        Main.main(args);
    }


}
