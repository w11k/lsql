package com.w11k.lsql.cli;

import com.google.common.io.MoreFiles;
import com.w11k.lsql.Config;
import com.w11k.lsql.LSql;
import com.w11k.lsql.cli.java.CliArgs;
import com.w11k.lsql.cli.java.DataClassMeta;
import com.w11k.lsql.cli.java.JavaExporter;
import com.w11k.lsql.cli.java.StatementFileExporter;
import com.w11k.lsql.cli.typescript.TypeScriptExporter;
import com.w11k.lsql.jdbc.ConnectionProviders;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static com.google.common.collect.Lists.newLinkedList;
import static com.w11k.lsql.cli.CodeGenUtils.log;

public class Main {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Main(String[] args) throws ClassNotFoundException, SQLException {
        log("========================================================");
        log("LSql CLI");
        log("========================================================\n");

        CliArgs cliArgs;
        try {
            cliArgs = new CliArgs(args);
        } catch (Exception e) {
            return;
        }

        // Config
        @SuppressWarnings("unchecked")
        Class<? extends Config> configClass =
                (Class<? extends Config>) Main.class.getClassLoader().loadClass(cliArgs.getConfigClassName());

        log("Config class:", configClass.getCanonicalName(), "\n");

        Connection connection = DriverManager.getConnection(cliArgs.getUrl(), cliArgs.getUser(), cliArgs.getPassword());
        connection.setAutoCommit(false);

        log("JDBC URL:", cliArgs.getUrl(), "\n");

        LSql lSql = new LSql(configClass, ConnectionProviders.fromInstance(connection));
//        lSql.fetchMetaDataForAllTables();

        JavaExporter javaExporter = null;
        if (cliArgs.getOutDirJava() != null) {
            javaExporter = new JavaExporter(lSql, cliArgs.getSchema());
            javaExporter.setPackageName(cliArgs.getGenPackageName());
            javaExporter.setOutputDir(new File(cliArgs.getOutDirJava()));
            javaExporter.setDependencyInjection(cliArgs.getDependencyInjection());

            // Java statements
            List<StatementFileExporter> statementFileExporters = createStatements(lSql, javaExporter, cliArgs);
            javaExporter.setStatementFileExporters(statementFileExporters);

            javaExporter.export();
        }

        if (cliArgs.getOutDirTypeScript() != null && javaExporter != null) {
            List<DataClassMeta> allDataClasses = javaExporter.getGeneratedDataClasses();
            TypeScriptExporter tse = new TypeScriptExporter(allDataClasses);

            File outDirTs = new File(cliArgs.getOutDirTypeScript());
            tse.setOutputDir(outDirTs);
            tse.export();
        }

    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        new Main(args);
    }

    private List<StatementFileExporter> createStatements(LSql lSql, JavaExporter javaExporter, CliArgs cliArgs) {
        List<StatementFileExporter> list = newLinkedList();

        if (cliArgs.getSqlStatements() != null) {
            Path statementRootDir = new File(cliArgs.getSqlStatements()).toPath();
            Iterable<Path> children = MoreFiles.directoryTreeTraverser().preOrderTraversal(statementRootDir);
            for (Path child : children) {
                File file = child.toFile();
                if (file.isFile() && file.getName().endsWith(".sql")) {
                    StatementFileExporter statementFileExporter =
                            new StatementFileExporter(lSql, javaExporter, file, cliArgs.getSqlStatements());

                    list.add(statementFileExporter);
                }
            }
        }

        return list;
    }

}
