package com.w11k.lsql.cli.java;

import com.w11k.lsql.LSql;
import com.w11k.lsql.NoPrimaryKeyColumn;
import com.w11k.lsql.Table;
import com.w11k.lsql.TypedTable;
import com.w11k.lsql.cli.CodeGenUtils;

import java.io.File;

import static com.w11k.lsql.cli.CodeGenUtils.writeContent;

public class TableExporter {

    private final LSql lSql;
    private final JavaExporter javaExporter;
    private final Table table;
    private final DataClassExporter dataClassExporter;
    private StringBuilder content;


    public TableExporter(LSql lSql, JavaExporter javaExporter, Table table, DataClassExporter dataClassExporter) {
        this.lSql = lSql;
        this.javaExporter = javaExporter;
        this.table = table;
        this.dataClassExporter = dataClassExporter;
    }

    public String getClassName() {
        return this.dataClassExporter.getDataClassMeta().getClassName() + "_Table";
    }

    public void export() {
        this.content = new StringBuilder();
        this.createContent();

        File pojoSourceFile = CodeGenUtils.getOutputFile(
                javaExporter.getOutputDir(),
                this.dataClassExporter.getDataClassMeta().getPackageName(),
                this.getClassName() + ".java");

        writeContent(this.content.toString(), pojoSourceFile);
    }

    private void createContent() {
        DataClassMeta dcm = this.dataClassExporter.getDataClassMeta();
        content.append("package ").append(dcm.getPackageName()).append(";\n\n");
        content.append("public class ").append(getClassName()).append(" extends ").append(TypedTable.class.getCanonicalName());
        content.append("<").append(this.dataClassExporter.getClassName());
        content.append(", ");
        content.append(this.table.getPrimaryKeyType().or(NoPrimaryKeyColumn.class).getCanonicalName());
        content.append("> ");
        content.append(" {\n\n");

        contentConstructor();
        contentStaticFieldName();

        content.append("}\n");
    }

    private void contentStaticFieldName() {
        content.append("    public static final String NAME = ")
                .append("\"")
                .append(this.dataClassExporter.getDataClassMeta().getClassName())
                .append("\";\n\n");
    }

    private void contentConstructor() {
        if (this.javaExporter.isGuice()) {
            content.append("    @com.google.inject.Inject\n");
        }
        content.append("    public ").append(getClassName())
                .append("(").append(LSql.class.getCanonicalName()).append(" lSql) {\n");

        content.append("        super(lSql, \"")
                .append(this.dataClassExporter.getDataClassMeta().getClassName()).append("\", ")
                .append(this.dataClassExporter.getClassName()).append(".class);\n");

        content.append("    }\n\n");
    }

}
