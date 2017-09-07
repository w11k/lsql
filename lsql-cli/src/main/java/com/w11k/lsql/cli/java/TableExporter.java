package com.w11k.lsql.cli.java;

import com.w11k.lsql.LSql;
import com.w11k.lsql.Table;
import com.w11k.lsql.TypedTable;

import java.io.File;

import static com.w11k.lsql.cli.CodeGenUtils.lowerCamelToUpperCamel;

public class TableExporter extends AbstractTableBasedExporter {

//    private final RowExporter rowClassExporter;
//    private final SchemaExporter schemaExporter;
//    private final File rootPackage;
//    private StringBuilder content = new StringBuilder();

    public TableExporter(Table table, JavaExporter javaExporter, File rootPackage) {
        super(table, javaExporter, rootPackage);
    }

    @Override
    protected void createContent() {
        content.append("package ").append(this.getFullPackageName()).append(";\n\n");
        content.append("public class ").append(getClassName()).append(" extends ").append(TypedTable.class.getCanonicalName());
        content.append("<").append(getRowClassName()).append("> ");
        content.append(" {\n\n");

        contentConstructor();

        content.append("}");
    }

    @Override
    public String getOutputFileName() {
        return getClassName() + ".java";
    }

    private void contentConstructor() {
        if (this.javaExporter.isGuice()) {
            content.append("    @com.google.inject.Inject\n");
        }
        content.append("    public ").append(getClassName())
                .append("(").append(LSql.class.getCanonicalName()).append(" lSql) {\n");

        content.append("        super(lSql, \"")
                .append(getTable().getSchemaAndTableName()).append("\", ")
                .append(getFullPackageName()).append(".").append(getRowClassName()).append(".class);\n");

        content.append("    }\n\n");
    }

    private String getRowClassName() {
        return lowerCamelToUpperCamel(this.getTable().getTableName() + "Row");
    }

    protected String getClassName() {
        return lowerCamelToUpperCamel(this.getTable().getTableName()) + "Table";
    }

}
