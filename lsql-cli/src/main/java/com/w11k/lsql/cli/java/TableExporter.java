package com.w11k.lsql.cli.java;

import com.w11k.lsql.LSql;
import com.w11k.lsql.TableLike;
import com.w11k.lsql.TypedTable;

import static com.w11k.lsql.cli.CodeGenUtils.firstCharUpperCase;

public class TableExporter extends AbstractTableBasedExporter {

    public TableExporter(LSql lSql, TableLike tableLike, JavaExporter javaExporter) {
        super(lSql, tableLike, javaExporter);
    }

    @Override
    protected void createContent() {
        content.append("package ").append(this.getFullPackageName()).append(";\n\n");
        content.append("public class ").append(getClassName()).append(" extends ").append(TypedTable.class.getCanonicalName());
        content.append("<").append(getRowClassName());
        content.append(", ");
        content.append(this.getTableLike().getPrimaryKeyType().or(TableLike.NoPrimaryKeyColumn.class).getCanonicalName());
        content.append("> ");
        content.append(" {\n\n");

        contentConstructor();
        contentStaticFieldName();

        content.append("}");
    }

    private void contentStaticFieldName() {
        content.append("    public static final String NAME = ")
                .append("\"")
                .append(this.getTableLike().getSchemaAndTableName())
                .append("\";\n\n");
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
                .append(getTableLike().getSchemaAndTableName()).append("\", ")
                .append(getFullPackageName()).append(".").append(getRowClassName()).append(".class);\n");

        content.append("    }\n\n");
    }

    private String getRowClassName() {
        return firstCharUpperCase(this.getTableLike().getTableName() + "Row");
    }

    protected String getClassName() {
        return firstCharUpperCase(this.getTableLike().getTableName()) + "Table";
    }

}
