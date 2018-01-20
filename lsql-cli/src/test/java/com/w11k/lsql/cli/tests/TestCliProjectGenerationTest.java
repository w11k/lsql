package com.w11k.lsql.cli.tests;

import com.google.common.io.MoreFiles;
import com.w11k.lsql.LSql;
import com.w11k.lsql.cli.Main;
import com.w11k.lsql.cli.java.CliArgs;
import com.w11k.lsql.jdbc.ConnectionProviders;
import org.apache.commons.dbcp.BasicDataSource;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import static com.w11k.lsql.cli.tests.TestCliConfig.createTables;
import static com.w11k.lsql.cli.tests.TestUtils.pathRelativeToProjectRoot;


public final class TestCliProjectGenerationTest {

    @Test
    public void createTestFiles() throws SQLException, ClassNotFoundException, IOException {
        File genJavaDir = pathRelativeToProjectRoot("pom.xml", "../lsql-cli-tests/src/generated/java");
        if (genJavaDir.exists()) {
            MoreFiles.deleteRecursively(genJavaDir.toPath());
        }

        File genTSDir = pathRelativeToProjectRoot("pom.xml", "../lsql-cli-tests/src/generated/ts");
        if (genTSDir.exists()) {
            MoreFiles.deleteRecursively(genTSDir.toPath());
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
                "sqlStatements:" + pathRelativeToProjectRoot("pom.xml", "./src/test/java/com/w11k/lsql/cli/tests"),
                "dto:" + pathRelativeToProjectRoot("pom.xml", "./src/test/java/com/w11k/lsql/cli/tests"),
                "package:" + TestCliConfig.class.getPackage().getName(),
                "di:guice",
                "outDirJava:" + genJavaDir.getAbsolutePath(),
                "outDirTypeScript:" + genTSDir.getAbsolutePath()
        };
        Main.main(args);
    }

    @Test
    public void printHelpOnMissingParameters() throws SQLException, ClassNotFoundException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(byteArrayOutputStream));

        Main.main(null);
        System.setOut(originalOut);

        String output = new String(byteArrayOutputStream.toByteArray(), Charset.defaultCharset());
        CliArgs.VALID_ARG_NAMES.forEach(arg -> Assert.assertTrue(output.contains(arg)));
    }

    @Test
    public void printHelpOnWrongParameters() throws SQLException, ClassNotFoundException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(byteArrayOutputStream));

        String[] args = {"wrong:value"};
        Main.main(args);
        System.setOut(originalOut);

        String output = new String(byteArrayOutputStream.toByteArray(), Charset.defaultCharset());
        CliArgs.VALID_ARG_NAMES.forEach(arg -> Assert.assertTrue(output.contains(arg)));
    }

    /**
     * dto: DummyDto
     * - fieldA: string
     */
    @SuppressWarnings("unused")
    public void dummyForDto() {
    }

}
