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

        String userPass = args[2];
        int idxDivider = userPass.indexOf(':');
        String username = userPass.substring(0, idxDivider);
        String password = userPass.substring(idxDivider + 1);

        String outDirPath = args[3];
        File outDir = new File(outDirPath);

        // Config
        @SuppressWarnings("unchecked")
        Class<? extends Config> configClass =
                (Class<? extends Config>) Main.class.getClassLoader().loadClass(configClassName);

        Connection connection = DriverManager.getConnection(url, username, password);
        connection.setAutoCommit(false);

        LSql lSql = new LSql(configClass, ConnectionProviders.fromInstance(connection));
        lSql.fetchMetaDataForAllTables();

        SchemaExporter schemaExporter = new SchemaExporter(lSql);
        schemaExporter.setPackageName(lSql.getCliConfig().getGeneratedPackageName());
        schemaExporter.setOutputPath(outDir);
        schemaExporter.export();
    }

}
