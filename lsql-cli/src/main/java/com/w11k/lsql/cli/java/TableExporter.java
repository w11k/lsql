package com.w11k.lsql.cli.java;

import com.w11k.lsql.ColumnsContainer;
import com.w11k.lsql.LSql;
import com.w11k.lsql.TypedTable;

import java.io.File;

import static com.w11k.lsql.cli.CodeGenUtils.lowerCamelToUpperCamel;

public class TableExporter extends AbstractTableBasedExporter {

    public TableExporter(ColumnsContainer columnsContainer, JavaExporter javaExporter, File rootPackage) {
        super(columnsContainer, javaExporter, rootPackage);
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
                .append(getColumnsContainer().getSchemaAndTableName()).append("\", ")
                .append(getFullPackageName()).append(".").append(getRowClassName()).append(".class);\n");

        content.append("    }\n\n");
    }

    private String getRowClassName() {
        return lowerCamelToUpperCamel(this.getColumnsContainer().getTableName() + "Row");
    }

    protected String getClassName() {
        return lowerCamelToUpperCamel(this.getColumnsContainer().getTableName()) + "Table";
    }

}
