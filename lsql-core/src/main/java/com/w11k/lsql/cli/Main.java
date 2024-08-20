package com.w11k.lsql.cli;

import com.w11k.lsql.Config;
import com.w11k.lsql.LSql;
import com.w11k.lsql.cli.java.CliArgs;
import com.w11k.lsql.cli.java.DataClassMeta;
import com.w11k.lsql.cli.java.JavaExporter;
import com.w11k.lsql.cli.java.StatementFileExporter;
import com.w11k.lsql.cli.typescript.TypeScriptExporter;
import com.w11k.lsql.jdbc.ConnectionProviders;
import com.w11k.lsql.query.PlainQuery;
import com.w11k.lsql.sqlfile.LSqlFile;
import com.w11k.lsql.statement.AnnotatedSqlStatementToQuery;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            try (java.util.stream.Stream<Path> paths = Files.walk(statementRootDir)) {
                list.addAll(paths
                        .map(Path::toFile)
                        .filter(File::isFile)
                        .filter(f -> f.getName().endsWith(".sql"))
                        .map(f -> {
                            LSqlFile lSqlFile = new LSqlFile(lSql, f.toString(), f.toString());
                            Map<String, AnnotatedSqlStatementToQuery<PlainQuery>> statementToQueries
                                    = lSqlFile.getStatementToQueries();
                            return new StatementFileExporter(
                                    lSql,
                                    javaExporter,
                                    cliArgs.getSqlStatements(), f.toPath().toString(), f.getName(),
                                    statementToQueries
                            );
                        })
                        .collect(Collectors.toList()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return list;
    }

}
