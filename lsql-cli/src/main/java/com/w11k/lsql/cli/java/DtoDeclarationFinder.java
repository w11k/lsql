package com.w11k.lsql.cli.java;

import com.google.common.io.MoreFiles;
import com.w11k.lsql.cli.CodeGenUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.google.common.collect.Lists.newLinkedList;
import static com.w11k.lsql.cli.CodeGenUtils.joinStringsAsPackageName;

public final class DtoDeclarationFinder {

    private static final String DTO_MARKER = "dto:";

    private final JavaExporter javaExporter;

    private final String dtoDeclarationLocation;

    public DtoDeclarationFinder(JavaExporter javaExporter, String dtoDeclarationLocation) {
        this.javaExporter = javaExporter;
        this.dtoDeclarationLocation = dtoDeclarationLocation;
    }

    public List<DataClassExporter> getDataClassExporters() {
        List<DataClassExporter> dataClassExporters = newLinkedList();

        Path rootDir = new File(this.dtoDeclarationLocation).toPath();
        Iterable<Path> children = MoreFiles.directoryTreeTraverser().preOrderTraversal(rootDir);
        for (Path child : children) {
            File file = child.toFile();
            if (file.isFile() && file.getName().endsWith(".java")) {
                dataClassExporters.addAll(this.checkFileForDtoDeclarations(file));
            }
        }

        return dataClassExporters;
    }

    private List<DataClassExporter> checkFileForDtoDeclarations(File file) {
        String packageName = this.getDtoPackageNameFromFile(file);

        List<DataClassExporter> exporters = newLinkedList();
        DataClassMeta last = null;

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                line = line.trim();
                if (!line.startsWith("*")) {
                    continue;
                }

                if (line.toLowerCase().contains(DTO_MARKER)) {
                    last = new DataClassMeta(
                            this.javaExporter.getlSql().getConfig(),
                            this.getDtoNameFromLine(line),
                            packageName);

                } else if (last != null && (line.startsWith("*") && line.contains("-"))) {
                    this.addField(line, last);
                } else if (last != null) {
                    DataClassExporter exporter = new DataClassExporter(this.javaExporter, last, "");
                    exporters.add(exporter);
                } else {
                    last = null;
                }
            }

            return exporters;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addField(String line, DataClassMeta last) {
        line = line.substring(line.lastIndexOf("-") + 1).trim();
        int colon = line.lastIndexOf(':');
        String fieldName = line.substring(0, colon);
        String fieldType = line.substring(colon + 1).trim();
        last.addField(
                fieldName,
                fieldName,
                this.javaExporter.getlSql().getConverterForAlias(fieldType).getJavaType());
    }

    private String getDtoPackageNameFromFile(File file) {
        String subPathFromFileInParent = CodeGenUtils.getSubPathFromFileInParent(
                this.dtoDeclarationLocation,
                file.getAbsolutePath()
        );
        return joinStringsAsPackageName(this.javaExporter.getPackageName(), subPathFromFileInParent);
    }

    private String getDtoNameFromLine(String line) {
        int i = line.toLowerCase().indexOf(DTO_MARKER);
        return line.substring(i + DTO_MARKER.length()).trim();
    }

}
