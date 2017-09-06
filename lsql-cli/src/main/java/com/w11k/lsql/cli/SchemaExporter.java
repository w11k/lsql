package com.w11k.lsql.cli;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.w11k.lsql.LSql;
import com.w11k.lsql.Table;

import java.io.File;
import java.util.List;
import java.util.Set;

import static com.w11k.lsql.cli.CodeGenUtils.log;

public class SchemaExporter {

    private final LSql lSql;

    private File outputDir = null;

    private String packageName;

    private boolean guice = false;

    public SchemaExporter(LSql lSql) {
        this.lSql = lSql;
    }


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public void setOutputPath(File outputDir) {
        this.outputDir = outputDir;
    }

    public boolean isGuice() {
        return guice;
    }

    public void setGuice(boolean guice) {
        this.guice = guice;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void export() {
        this.outputDir.mkdirs();

        String packageFolder = this.packageName.replaceAll("\\.", File.separator);
        File packageFolderFile = new File(this.outputDir, packageFolder);

        packageFolderFile.mkdirs();
        assert packageFolderFile.isDirectory();
        assert packageFolderFile.exists();

        List<JavaRowClassExporter> tableRowClassExporters = Lists.newLinkedList();
        Iterable<Table> tables = this.lSql.getTables();

        for (Table table : tables) {
            JavaRowClassExporter rowClassExporter = new JavaRowClassExporter(table, this, packageFolderFile);
            tableRowClassExporters.add(rowClassExporter);



            new TableExporter(table, this, packageFolderFile).export();
        }

        Set<StructuralTypingField> structuralTypingFields = Sets.newHashSet();
        for (JavaRowClassExporter tableRowClassExporter : tableRowClassExporters) {
            structuralTypingFields.addAll(tableRowClassExporter.getStructuralTypingFields());
        }
        for (StructuralTypingField structuralTypingField : structuralTypingFields) {
            new StructuralTypingFieldExporter(structuralTypingField, this, packageFolderFile).export();
        }


        for (JavaRowClassExporter tableRowClassExporter : tableRowClassExporters) {
            tableRowClassExporter.export();
        }

        log("");
    }


}
