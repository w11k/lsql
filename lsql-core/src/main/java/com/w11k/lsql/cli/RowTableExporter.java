package com.w11k.lsql.cli;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

import static com.w11k.lsql.cli.CodeGenUtils.lowerCamelToUpperCamel;

public class RowTableExporter {

    private final TableRowClassExporter rowClassExporter;
    private final SchemaExporter schemaExporter;
    private final File rootPackage;
    private StringBuilder content = new StringBuilder();

    public RowTableExporter(TableRowClassExporter rowClassExporter, SchemaExporter schemaExporter, File rootPackage) {
        this.rowClassExporter = rowClassExporter;
        this.schemaExporter = schemaExporter;
        this.rootPackage = rootPackage;
    }

    public void export() {
        content.append("package ").append(this.rowClassExporter.getFullPackageName()).append(";\n\n");
        content.append("public class ").append(getClassName()).append(" {\n\n");

        content.append("}");


        File pojoSourceFile = getOutputFile();
        try {
            // TODO only write if content changed
            Files.write(content.toString().getBytes(Charsets.UTF_8), pojoSourceFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String getClassName() {
        return lowerCamelToUpperCamel(this.rowClassExporter.getTable().getTableName()) + "Table";
    }

    protected File getOutputFile() {
        File packageWithSchema = new File(this.rootPackage, this.rowClassExporter.getLastPackageSegmentForSchema());
        //noinspection ResultOfMethodCallIgnored
        packageWithSchema.mkdirs();
        return new File(packageWithSchema, getClassName() + ".java");
    }

}
