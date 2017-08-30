package com.w11k.lsql.cli;

import com.w11k.lsql.Config;
import com.w11k.lsql.LSql;
import com.w11k.lsql.jdbc.ConnectionProviders;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        String configClassName = args[0];
        String url = args[1];
        String outDirPath = args[2];
        File outDir = new File(outDirPath);

        // Config
        @SuppressWarnings("unchecked")
        Class<? extends Config> configClass =
                (Class<? extends Config>) Main.class.getClassLoader().loadClass(configClassName);

        // connection
//        BasicDataSource ds = new BasicDataSource();
//        ds.setDriverClassName(driver);
//        ds.setUrl(url);
//        ds.setDefaultAutoCommit(false);

        Connection connection = DriverManager.getConnection(url);
        connection.setAutoCommit(false);

        LSql lSql = new LSql(configClass, ConnectionProviders.fromInstance(connection));
        lSql.fetchMetaDataForAllTables();

        SchemaExporter schemaExporter = new SchemaExporter(lSql);
        schemaExporter.setPackageName(lSql.getCliConfig().getGeneratedPackageName());
        schemaExporter.setOutputPath(outDir);
        schemaExporter.export();
    }

}
