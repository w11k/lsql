package com.w11k.lsql.cli;

import com.w11k.lsql.Config;
import com.w11k.lsql.LSql;
import com.w11k.lsql.jdbc.ConnectionProviders;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.w11k.lsql.cli.CodeGenUtils.log;

public class Main {

    private String configClassName;
    private String url;
    private String user;
    private String password;
    private String packageName;
    private String output;

    public Main(String[] args) throws ClassNotFoundException, SQLException {
        log("=================");
        log("LSql CLI Exporter");
        log("=================\n");

        this.parseArgs(args);

        File outDir = new File(this.output);

        // Config
        @SuppressWarnings("unchecked")
        Class<? extends Config> configClass =
                (Class<? extends Config>) Main.class.getClassLoader().loadClass(configClassName);

        log("Config class:", configClass.getCanonicalName(), "\n");

        Connection connection = DriverManager.getConnection(url, user, password);
        connection.setAutoCommit(false);

        log("JDBC URL:", url, "\n");

        LSql lSql = new LSql(configClass, ConnectionProviders.fromInstance(connection));
        lSql.fetchMetaDataForAllTables();

        SchemaExporter schemaExporter = new SchemaExporter(lSql);
        schemaExporter.setPackageName(this.packageName);
        schemaExporter.setOutputPath(outDir);
        schemaExporter.export();
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        new Main(args);
    }

    private void parseArgs(String[] args) {
        for (String arg : args) {
            String value = getParamValue(arg);
            if (arg.startsWith("config:")) {
                this.configClassName = value;
            } else if (arg.startsWith("url:")) {
                this.url = value;
            } else if (arg.startsWith("user:")) {
                this.user = value;
            } else if (arg.startsWith("password:")) {
                this.password = value;
            } else if (arg.startsWith("package:")) {
                this.packageName = value;
            } else if (arg.startsWith("output:")) {
                this.output = value;
            }
        }
    }

    private String getParamValue(String param) {
        return param.substring(param.indexOf(":") + 1);
    }
}
