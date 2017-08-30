package com.w11k.lsql.cli.tests.test01;

import com.w11k.lsql.LSql;
import com.w11k.lsql.cli.Main;
import com.w11k.lsql.cli.SchemaExporter;
import com.w11k.lsql.jdbc.ConnectionProviders;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public final class Prj1Test {

    @Test
    public void gen() throws SQLException, ClassNotFoundException, IOException {

        File genJavaDir = SchemaExporter.pathRelativeToProjectRoot(
                "pom.xml", "../lsql-cli-tests/src/generated/java");
        FileUtils.deleteDirectory(genJavaDir);


        String driver = "org.h2.Driver";
        String url = "jdbc:h2:mem:" + UUID.randomUUID() + ";mode=postgresql";

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(driver);
        ds.setUrl(url);
        ds.setDefaultAutoCommit(false);
        Connection connection = ds.getConnection();
        LSql lSql = new LSql(TestConfig.class, ConnectionProviders.fromInstance(connection));

        lSql.executeRawSql("create table person1 (id integer, first_name text)");
        lSql.executeRawSql("create table person2 (id integer, first_name text, age integer)");

        String[] args = {
                TestConfig.class.getCanonicalName(),
                driver,
                url,
                genJavaDir.getAbsolutePath()};
        Main.main(args);
    }


}
