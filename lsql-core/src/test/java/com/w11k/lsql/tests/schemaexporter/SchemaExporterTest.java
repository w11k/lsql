package com.w11k.lsql.tests.schemaexporter;

import com.w11k.lsql.schemaexporter.SchemaExporter;
import com.w11k.lsql.tests.AbstractLSqlTest;
import org.testng.annotations.Test;

import java.io.File;

import static com.w11k.lsql.schemaexporter.SchemaExporter.pathRelativeToProjectRoot;

public class SchemaExporterTest extends AbstractLSqlTest {

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*No parent folder.*")
    public void pathRelativeToProjectRootFailsOnMissingProjectRootFile() {
        SchemaExporter.pathRelativeToProjectRoot("missing_missing_missing", "");
    }

    @Test
    public void createPojos() {
        createTable("CREATE TABLE table_aaa (id INTEGER PRIMARY KEY, field_a INT)");
        createTable("CREATE TABLE table_bbb (id INTEGER PRIMARY KEY, field_bbb VARCHAR(100), yesno BOOLEAN)");

        lSql.table("tableAaa");
        lSql.table("tableBbb");

        SchemaExporter schemaExporter = new SchemaExporter(this.lSql);
        schemaExporter.setPackageName("setest");
        File outputDir = pathRelativeToProjectRoot("pom.xml", "target/schemaexporter");
        schemaExporter.setOutputPath(outputDir);

        schemaExporter.export();

    }


}
