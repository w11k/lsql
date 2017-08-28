package com.w11k.lsql.cli;

import com.w11k.lsql.LSql;
import com.w11k.lsql.jdbc.ConnectionProviders;
import org.apache.commons.dbcp.BasicDataSource;

import java.io.File;
import java.sql.Connection;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        File genJavaDir = SchemaExporter.pathRelativeToProjectRoot("pom.xml", "src/generated/java");

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:mem:" + UUID.randomUUID() + ";mode=postgresql");
        ds.setUsername("");
        ds.setPassword("");
        ds.setDefaultAutoCommit(false);

        Connection connection = ds.getConnection();

        this.lSql = new LSql(TestConfig.class, ConnectionProviders.fromInstance(this.connection));


    }

}
