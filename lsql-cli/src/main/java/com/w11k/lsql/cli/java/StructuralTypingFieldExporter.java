package com.w11k.lsql.cli.java;

import com.w11k.lsql.cli.CodeGenUtils;

import java.io.File;

import static com.w11k.lsql.cli.CodeGenUtils.getFileFromBaseDirAndPackageName;
import static com.w11k.lsql.cli.CodeGenUtils.joinStringsAsPackageName;

public class StructuralTypingFieldExporter {

    private final StructuralTypingField stf;

    private final JavaExporter javaExporter;

    private StringBuilder content = new StringBuilder();

    public StructuralTypingFieldExporter(StructuralTypingField stf, JavaExporter javaExporter) {
        this.stf = stf;
        this.javaExporter = javaExporter;
    }

    public void export() {
        content.append("package ").append(joinStringsAsPackageName(javaExporter.getPackageName(), "structural_fields")).append(";\n\n");
        content.append("public interface ").append(stf.getInterfaceName()).append(" {\n\n");

        content.append("    ")
                .append(stf.getFieldType().getCanonicalName())
                .append(" ").append(stf.getGetterMethodName())
                .append("();\n\n");

        content.append("    ")
                .append(stf.getInterfaceName())
                .append(" ").append("with").append(stf.getUppercaseName()).append("(")
                .append(stf.getFieldType().getCanonicalName()).append(" val")
                .append(");\n\n");

        content.append("}");


        File pojoSourceFile = getOutputFile();
        CodeGenUtils.writeContent(content.toString(), pojoSourceFile);
    }

    protected File getOutputFile() {
        File baseDir = getFileFromBaseDirAndPackageName(
                javaExporter.getOutputDir(),
                joinStringsAsPackageName(javaExporter.getPackageName(), "structural_fields"));

        return new File(baseDir, this.stf.getInterfaceName() + ".java");
    }

}
