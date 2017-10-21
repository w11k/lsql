package com.w11k.lsql.cli;

import com.google.common.collect.Lists;
import com.google.common.io.MoreFiles;
import com.w11k.lsql.ColumnsContainer;
import com.w11k.lsql.Config;
import com.w11k.lsql.LSql;
import com.w11k.lsql.cli.java.JavaExporter;
import com.w11k.lsql.cli.java.StatementFileExporter;
import com.w11k.lsql.jdbc.ConnectionProviders;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import static com.w11k.lsql.cli.CodeGenUtils.log;

public class Main {

    private String configClassName;
    private String url;
    private String user;
    private String password;

    private String packageName;
    private String sqlStatements;
    private boolean guice = false;
    private String outDirJava;
    private List<StatementFileExporter> statementFileExporters = Lists.newLinkedList();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Main(String[] args) throws ClassNotFoundException, SQLException {
        log("========================================================");
        log("LSql CLI Exporter");
        log("========================================================\n");

        this.parseArgs(args);

        log("output dir: " + this.outDirJava);
        log("sql statements dir: " + this.sqlStatements);


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

        // Java output dir
        String packageFolder = this.packageName.replaceAll("\\.", File.separator);
        File outputRootPackageDir = new File(this.outDirJava, packageFolder);
        outputRootPackageDir.mkdirs();
        assert outputRootPackageDir.isDirectory();

        // Java table and row classes
        LinkedList<? extends ColumnsContainer> tables = Lists.newLinkedList(lSql.getTables());
        JavaExporter javaExporter = new JavaExporter(tables);
        javaExporter.setPackageName(this.packageName);
        javaExporter.setOutputRootPackageDir(outputRootPackageDir);
        javaExporter.setGuice(this.guice);

        // Java statements
        processStatements(lSql, javaExporter);
        javaExporter.setStatementFileExporters(this.statementFileExporters);

        javaExporter.export();


        //        TypeScriptExporter tse = new TypeScriptExporter(lSql);
//        File outDirTs = new File(this.outDirTypeScript);
//        tse.setOutputRootPackageDir(outDirTs);
//        tse.export();
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        new Main(args);
    }

    private void processStatements(LSql lSql, JavaExporter javaExporter) {
        if (this.sqlStatements != null) {
            Iterable<Path> children = MoreFiles.directoryTreeTraverser().preOrderTraversal(new File(this.sqlStatements).toPath());
            for (Path child : children) {
                File file = child.toFile();
                if (file.isFile() && file.getName().endsWith(".sql")) {
                    StatementFileExporter statementFileExporter =
                            new StatementFileExporter(lSql, file, javaExporter, this.sqlStatements);
                    this.statementFileExporters.add(statementFileExporter);
                }
            }
        }
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
            } else if (arg.startsWith("sqlStatements:")) {
                this.sqlStatements = value;
            } else if (arg.startsWith("outDirJava:")) {
                this.outDirJava = value;
            } else if (arg.startsWith("di:")) {
                if (value.equalsIgnoreCase("guice")) {
                    this.guice = true;
                }
            }
        }
    }

    private String getParamValue(String param) {
        return param.substring(param.indexOf(":") + 1).trim();
    }
}
