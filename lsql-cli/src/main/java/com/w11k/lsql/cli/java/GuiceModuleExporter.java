package com.w11k.lsql.cli.java;

import com.w11k.lsql.cli.CodeGenUtils;

import java.io.File;
import java.util.List;

import static com.w11k.lsql.cli.CodeGenUtils.writeContent;

public final class GuiceModuleExporter {

    private final JavaExporter javaExporter;

    private final List<String> guiceModuleClasses;

    public GuiceModuleExporter(JavaExporter javaExporter, List<String> guiceModuleClasses) {
        this.javaExporter = javaExporter;
        this.guiceModuleClasses = guiceModuleClasses;
    }

    public void export() {
        StringBuilder content = new StringBuilder();
        createFileContent(content);

        File pojoSourceFile = CodeGenUtils.getOutputFile(
                javaExporter.getOutputDir(),
                this.javaExporter.getPackageName(),
                "LSqlDbModule" + ".java");

        writeContent(content.toString(), pojoSourceFile);
    }

    private void createFileContent(StringBuilder content) {
        content.append("package ").append(this.javaExporter.getPackageName()).append(";\n\n");
        content.append("public class ").append("LSqlDbModule ");
        content.append("implements com.google.inject.Module {\n\n");
        content.append("    @Override\n");
        content.append("    public void configure(com.google.inject.Binder binder) {\n");
        for (String className : this.guiceModuleClasses) {
            content.append("        binder.bind(").append(className).append(".class).asEagerSingleton();\n");
        }
        content.append("    }\n\n");
        content.append("}\n");
    }

}
