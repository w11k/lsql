package com.w11k.lsql.cli;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

public class StructuralTypingFieldExporter {

    private final StructuralTypingField stf;

    private final SchemaExporter schemaExporter;

    private final File rootPackage;

    private StringBuilder content = new StringBuilder();

    public StructuralTypingFieldExporter(StructuralTypingField stf, SchemaExporter schemaExporter, File rootPackage) {
        this.stf = stf;
        this.schemaExporter = schemaExporter;
        this.rootPackage = rootPackage;
    }

    public void export() {
        content.append("package ").append(schemaExporter.getPackageName()).append(";\n\n");
        content.append("public interface ").append(stf.getInterfaceName()).append(" {\n\n");

        content.append("    ")
                .append(stf.getFieldClass().getCanonicalName())
                .append(" ").append("get").append(stf.getName())
                .append("();\n\n");

        content.append("    ")
                .append(stf.getInterfaceName())
                .append(" ").append("with").append(stf.getName()).append("(")
                .append(stf.getFieldClass().getCanonicalName()).append(" val")
                .append(");\n\n");

        content.append("}");


        File pojoSourceFile = getOutputFile();
        try {
            // TODO only write if content changed
            Files.write(content.toString().getBytes(Charsets.UTF_8), pojoSourceFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected File getOutputFile() {
        return new File(rootPackage, this.stf.getInterfaceName() + ".java");
    }

}
