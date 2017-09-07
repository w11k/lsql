package com.w11k.lsql.cli.java;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.w11k.lsql.ColumnsContainer;
import com.w11k.lsql.LSql;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import static com.w11k.lsql.cli.CodeGenUtils.log;

public class JavaExporter {

    private final LSql lSql;

    private File outDirJava = null;

    private String packageName;

    private boolean guice = false;

    public JavaExporter(LSql lSql) throws SQLException {
        this.lSql = lSql;
    }


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public File getOutDirJava() {
        return outDirJava;
    }

    public void setOutputPath(File outputDir) {
        this.outDirJava = outputDir;
    }

    public boolean isGuice() {
        return guice;
    }

    public void setGuice(boolean guice) {
        this.guice = guice;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void export() {
        String packageFolder = this.packageName.replaceAll("\\.", File.separator);
        File packageFolderFile = new File(this.outDirJava, packageFolder);

        packageFolderFile.mkdirs();
        assert packageFolderFile.isDirectory();
        assert packageFolderFile.exists();

        List<JavaRowClassExporter> tableRowClassExporters = Lists.newLinkedList();
        Iterable<? extends ColumnsContainer> tables = this.lSql.getTables();

        // Tables
        for (ColumnsContainer cc : tables) {
            // collect all row classes
            JavaRowClassExporter rowClassExporter = new JavaRowClassExporter(cc, this, packageFolderFile);
            tableRowClassExporters.add(rowClassExporter);

            // generate table classes
            new TableExporter(cc, this, packageFolderFile).export();
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
    }


}
