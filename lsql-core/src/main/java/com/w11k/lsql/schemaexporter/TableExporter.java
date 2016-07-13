package com.w11k.lsql.schemaexporter;

import com.google.common.io.Files;
import com.w11k.lsql.Column;
import com.w11k.lsql.Table;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

public class TableExporter {

    private final Table table;

    private final SchemaExporter schemaExporter;

    private final File packageFolderFile;

    public TableExporter(Table table, SchemaExporter schemaExporter, File packageFolderFile) {
        this.table = table;
        this.schemaExporter = schemaExporter;
        this.packageFolderFile = packageFolderFile;
    }

    public void export() {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(this.schemaExporter.getPackageName()).append(";\n\n");
        sb.append("class ").append(nameToIdentifier(this.table.getTableName())).append(" {\n\n");

        // Field instances
        for (Map.Entry<String, Column> entry : this.table.getColumns().entrySet()) {
            Class<?> javaType = entry.getValue().getConverter().getJavaType();
            sb.append("    ");
            sb.append(javaType.getCanonicalName()).append(" ").append(entry.getKey()).append(";\n");
        }

        sb.append("\n");

        // Getter/setter
        for (Map.Entry<String, Column> entry : this.table.getColumns().entrySet()) {
            generateGetterSetter(sb, entry);
        }

        sb.append("}\n");

        File pojoSourceFile = getPojoSourceFile();
        try {
            Files.write(sb, pojoSourceFile, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File getPojoSourceFile() {
        return new File(this.packageFolderFile, nameToIdentifier(this.table.getTableName()) + ".java");
    }

    private void generateGetterSetter(StringBuilder sb, Map.Entry<String, Column> entry) {
        // Getter
        sb.append("    public ");
        sb.append(entry.getValue().getConverter().getJavaType().getCanonicalName());
        sb.append(" ");
        sb.append("get").append(nameToIdentifier(entry.getKey())).append("() {\n");
        sb.append("        return this.").append(entry.getKey()).append(";\n");
        sb.append("    }\n\n");

        // Setter
        sb.append("    public void ");
        sb.append("set").append(nameToIdentifier(entry.getKey())).append("(");
        sb.append(entry.getValue().getConverter().getJavaType().getCanonicalName());
        sb.append(" ").append(entry.getKey());
        sb.append(") {\n");

        sb.append("        this.").append(entry.getKey()).append(" = ");
        sb.append(entry.getKey()).append(";\n");
        sb.append("    }\n\n");
    }

    public String nameToIdentifier(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
