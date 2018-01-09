package com.w11k.lsql.cli;

import com.google.common.collect.Lists;
import com.w11k.lsql.Config;
import com.w11k.lsql.LSql;
import com.w11k.lsql.TableLike;
import com.w11k.lsql.cli.java.CliArgs;
import com.w11k.lsql.cli.java.JavaExporter;
import com.w11k.lsql.jdbc.ConnectionProviders;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

import static com.w11k.lsql.cli.CodeGenUtils.log;

public class Main {


//    private List<StatementFileExporter> statementFileExporters = Lists.newLinkedList();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Main(String[] args) throws ClassNotFoundException, SQLException {
        log("========================================================");
        log("LSql CLI Exporter");
        log("========================================================\n");

        CliArgs cliArgs = new CliArgs(args);

        // Config
        @SuppressWarnings("unchecked")
        Class<? extends Config> configClass =
                (Class<? extends Config>) Main.class.getClassLoader().loadClass(cliArgs.getConfigClassName());

        log("Config class:", configClass.getCanonicalName(), "\n");

        Connection connection = DriverManager.getConnection(cliArgs.getUrl(), cliArgs.getUser(), cliArgs.getPassword());
        connection.setAutoCommit(false);

        log("JDBC URL:", cliArgs.getUrl(), "\n");

        LSql lSql = new LSql(configClass, ConnectionProviders.fromInstance(connection));
        lSql.fetchMetaDataForAllTables();

        LinkedList<TableLike> tables = Lists.newLinkedList(lSql.getTables());

        if (cliArgs.getOutDirJava() != null) {
            JavaExporter javaExporter = new JavaExporter(lSql, tables);
            javaExporter.setPackageName(cliArgs.getGenPackageName());
            File outputRootPackageDir = new File(cliArgs.getOutDirJava());
            javaExporter.setOutputDir(outputRootPackageDir);
            javaExporter.setGuice(cliArgs.isGuice());

            // Java statements
//            processStatements(lSql, javaExporter);
//            javaExporter.setStatementFileExporters(this.statementFileExporters);

            javaExporter.export();
        }

//        if (this.outDirTypeScript != null) {
            // add statement rows
//            for (StatementFileExporter statementFileExporter : this.statementFileExporters) {
//                List<StatementRowColumnContainer> statementRows = statementFileExporter.getStatementRows();
//                tables.addAll(statementRows);
//            }
//
//            TypeScriptExporter tse = new TypeScriptExporter(tables);
//            File outDirTs = new File(this.outDirTypeScript);
//            tse.setOutputDir(outDirTs);
//            tse.export();
//        }

    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        new Main(args);
    }

    /*private void processStatements(LSql lSql, JavaExporter javaExporter, CliArgs cliArgs) {
        if (cliArgs.getSqlStatements() != null) {
            Path statementRootDir = new File(cliArgs.getSqlStatements()).toPath();
            Iterable<Path> children = MoreFiles.directoryTreeTraverser().preOrderTraversal(statementRootDir);
            for (Path child : children) {
                File file = child.toFile();
                if (file.isFile() && file.getName().endsWith(".sql")) {
                    StatementFileExporter statementFileExporter =
                            new StatementFileExporter(lSql, file, javaExporter, cliArgs.getSqlStatements());
                    this.statementFileExporters.add(statementFileExporter);
                }
            }
        }
    }
*/


}
