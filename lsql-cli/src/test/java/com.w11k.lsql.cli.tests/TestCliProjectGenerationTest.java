package com.w11k.lsql.cli.tests;

import com.google.common.io.MoreFiles;
import com.w11k.lsql.LSql;
import com.w11k.lsql.cli.Main;
import com.w11k.lsql.cli.java.JavaExporter;
import com.w11k.lsql.jdbc.ConnectionProviders;
import org.apache.commons.dbcp.BasicDataSource;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import static com.w11k.lsql.cli.tests.TestCliConfig.createTables;


public final class TestCliProjectGenerationTest {

    private static File pathRelativeToProjectRoot(String fileInProjectRoot, String folderRelativeToProjectRoot) {
        try {
            URL resource = JavaExporter.class.getResource("/");
            File folder = new File(resource.toURI());

            while (folder != null) {
                File maybeFile = new File(folder, fileInProjectRoot);
                if (maybeFile.exists()) {
                    return new File(folder, folderRelativeToProjectRoot);
                }
                folder = folder.getParentFile();
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalArgumentException("No parent folder with file '" + fileInProjectRoot + "' found");
    }

    @Test
    public void runCli() throws SQLException, ClassNotFoundException, IOException {

        File genJavaDir = pathRelativeToProjectRoot("pom.xml", "../lsql-cli-tests/src/generated/java");
        if (genJavaDir.exists()) {
            MoreFiles.deleteRecursively(genJavaDir.toPath());
        }

        String url = "jdbc:h2:mem:" + UUID.randomUUID() + ";mode=postgresql";

        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(url);
        ds.setDefaultAutoCommit(false);
        Connection connection = ds.getConnection();
        LSql lSql = new LSql(TestCliConfig.class, ConnectionProviders.fromInstance(connection));
        createTables(lSql);
        connection.close();

        String[] args = {
                "config:" + TestCliConfig.class.getCanonicalName(),
                "url:" + url,
                "user:",
                "password:",
                "sqlStatements:" + pathRelativeToProjectRoot("pom.xml", "../lsql-cli-tests/src/test/java/com/w11k/lsql"),
                "package:" + TestCliConfig.class.getPackage().getName(),
                "di:guice",
                "outDirJava:" + genJavaDir.getAbsolutePath()
        };
        Main.main(args);
    }


}
