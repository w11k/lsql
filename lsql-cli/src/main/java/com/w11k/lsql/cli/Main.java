package com.w11k.lsql.cli;

import com.w11k.lsql.Config;
import com.w11k.lsql.LSql;
import com.w11k.lsql.jdbc.ConnectionProviders;
import org.apache.commons.dbcp.BasicDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        String configClassName = args[0];
        String driver = args[1];
        String url = args[2];
        String outDirPath = args[3];
        File outDir = new File(outDirPath);

        // Config
        @SuppressWarnings("unchecked")
        Class<? extends Config> configClass =
                (Class<? extends Config>) Main.class.getClassLoader().loadClass(configClassName);

        // Out dir
        //noinspection ResultOfMethodCallIgnored
        outDir.mkdirs();

        // connection
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(driver);
        ds.setUrl(url);
        ds.setDefaultAutoCommit(false);


        Connection connection = ds.getConnection();
        System.out.println(connection);

        LSql lSql = new LSql(configClass, ConnectionProviders.fromInstance(connection));
        lSql.fetchMetaDataForAllTables();

        SchemaExporter schemaExporter = new SchemaExporter(lSql);
        schemaExporter.setPackageName(lSql.getGeneratedPackageName());
        schemaExporter.setOutputPath(outDir);
        schemaExporter.export();
    }

}
