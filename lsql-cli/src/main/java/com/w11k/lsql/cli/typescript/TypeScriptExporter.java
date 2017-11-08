package com.w11k.lsql.cli.typescript;

import com.w11k.lsql.Column;
import com.w11k.lsql.TableLike;
import com.w11k.lsql.cli.CodeGenUtils;

import java.io.File;
import java.sql.SQLException;
import java.util.LinkedList;

import static com.w11k.lsql.cli.CodeGenUtils.firstCharUpperCase;

public class TypeScriptExporter {

    private final LinkedList<? extends TableLike> tables;

    private File outputDir = null;

    public TypeScriptExporter(LinkedList<? extends TableLike> tables) throws SQLException {
        this.tables = tables;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    public void export() {
        StringBuilder content = new StringBuilder();
        this.exportTables(content);

        //noinspection ResultOfMethodCallIgnored
        this.outputDir.mkdirs();
        File output = new File(this.outputDir, "domain.ts");
        CodeGenUtils.writeContentIfChanged(content.toString(), output);
    }

    private void exportTables(StringBuilder content) {
        for (TableLike table : this.tables) {
            this.exportTable(content, table);
        }
    }

    private void exportTable(StringBuilder content, TableLike table) {
        content.append("export namespace schema_").append(table.getSchemaName()).append(" {\n");
        content.append("    export interface ").append(firstCharUpperCase(table.getTableName())).append(" {\n");
        this.exportColumns(content, table);
        content.append("    }\n");
        content.append("}\n\n");
    }

    private void exportColumns(StringBuilder content, TableLike table) {
        for (Column column : table.getColumns().values()) {
            this.exportColumn(content, column);
        }
    }

    private void exportColumn(StringBuilder content, Column column) {
        Class<?> javaType = column.getConverter().getJavaType();
        String tsTypeName = this.getTypeScriptTypeNameForJavaType(javaType);
        content.append("        ")
                .append(column.getJavaColumnName()).append(": ").append(tsTypeName).append(";\n");
    }

    private String getTypeScriptTypeNameForJavaType(Class<?> javaType) {
        if (String.class.isAssignableFrom(javaType)) {
            return "string";
        } else if (Integer.class.isAssignableFrom(javaType)) {
            return "number";
        }  else if (Long.class.isAssignableFrom(javaType)) {
            return "number";
        }  else if (Float.class.isAssignableFrom(javaType)) {
            return "number";
        }  else if (Double.class.isAssignableFrom(javaType)) {
            return "number";
        } else if (Boolean.class.isAssignableFrom(javaType)) {
            return "boolean";
        }


        return "any";
    }

}
