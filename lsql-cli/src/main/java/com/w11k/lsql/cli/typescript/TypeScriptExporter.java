package com.w11k.lsql.cli.typescript;

import com.w11k.lsql.LSql;

import java.io.File;
import java.sql.SQLException;

public class TypeScriptExporter {

    private final LSql lSql;

    private File outDirTypeScript = null;

    public TypeScriptExporter(LSql lSql) throws SQLException {
        this.lSql = lSql;
    }

    public File getOutDirTypeScript() {
        return outDirTypeScript;
    }

    public void setOutputPath(File outputDir) {
        this.outDirTypeScript = outputDir;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void export() {
/*
        String packageFolder = this.packageName.replaceAll("\\.", File.separator);
        File packageFolderFile = new File(this.outDirTypeScript, packageFolder);

        packageFolderFile.mkdirs();
        assert packageFolderFile.isDirectory();
        assert packageFolderFile.exists();

        List<JavaRowClassExporter> tableRowClassExporters = Lists.newLinkedList();
        Iterable<Table> tables = this.lSql.getTables();

        // Tables
        for (Table table : tables) {
            // collect all row classes
            JavaRowClassExporter rowClassExporter = new JavaRowClassExporter(table, this, packageFolderFile);
            tableRowClassExporters.add(rowClassExporter);

            // generate table classes
            new TableExporter(table, this, packageFolderFile).export();
        }

        // find unique StructuralTypingFields
        Set<StructuralTypingField> structuralTypingFields = Sets.newHashSet();
        for (JavaRowClassExporter tableRowClassExporter : tableRowClassExporters) {
            structuralTypingFields.addAll(tableRowClassExporter.getStructuralTypingFields());
        }

        // generate StructuralTypingField
        for (StructuralTypingField structuralTypingField : structuralTypingFields) {
            new StructuralTypingFieldExporter(structuralTypingField, this, packageFolderFile).export();
        }

        // generate row classes
        for (JavaRowClassExporter tableRowClassExporter : tableRowClassExporters) {
            tableRowClassExporter.export();
        }

        log("");
        */
    }

}
