package com.w11k.lsql.cli.java;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.w11k.lsql.LSql;
import com.w11k.lsql.Table;

import java.io.File;
import java.util.List;
import java.util.Set;

import static com.w11k.lsql.cli.CodeGenUtils.createSaveNameForClass;
import static com.w11k.lsql.cli.CodeGenUtils.joinStringsAsPackageName;
import static com.w11k.lsql.cli.CodeGenUtils.log;
import static java.util.Collections.emptyList;

public class JavaExporter {

    private final LSql lSql;

    private List<StatementFileExporter> statementFileExporters = emptyList();

    private File outputDir = null;

    private String packageName;

    private boolean guice = false;

    public JavaExporter(LSql lSql) {
        this.lSql = lSql;
    }

    public void setStatementFileExporters(List<StatementFileExporter> statementFileExporters) {
        this.statementFileExporters = statementFileExporters;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void export() {
        List<DataClassExporter> dataClassExporters = Lists.newLinkedList();

        Set<StructuralTypingField> structuralTypingFields = Sets.newHashSet();

        // Tables
        for (Table table : this.lSql.getTables()) {
            // collect all row classes

            String schemaName = table.getSchemaName();
            String lastPackageSegment = schemaName.length() == 0
                    ? ""
                    : Joiner.on("_").skipNulls().join("schema", schemaName.toLowerCase());
            String fullPackageName = joinStringsAsPackageName(packageName, lastPackageSegment);
            String className = createSaveNameForClass(table.getTableName());

            DataClassMeta dcm = new DataClassMeta(className, fullPackageName);
            table.getColumns().values()
                    .forEach(c -> {
                        dcm.addField(c.getJavaColumnName(), c.getConverter().getJavaType());
                    });

            DataClassExporter dataClassExporter = new DataClassExporter(this, dcm, "_Row");
            dataClassExporters.add(dataClassExporter);

            // generate table classes
            new TableExporter(this.lSql, this, table, dataClassExporter).export();
        }

        // StatementFileExporter
        for (StatementFileExporter statementFileExporter : this.statementFileExporters) {
            Set<StructuralTypingField> stfs = statementFileExporter.exportAndGetStructuralTypingFields();
            structuralTypingFields.addAll(stfs);
        }

        // find unique StructuralTypingFields
        for (DataClassExporter tableRowClassExporter : dataClassExporters) {
            structuralTypingFields.addAll(tableRowClassExporter.getStructuralTypingFields());
        }

        // generate StructuralTypingField
        for (StructuralTypingField structuralTypingField : structuralTypingFields) {
            new StructuralTypingFieldExporter(structuralTypingField, this).export();
        }

        // generate row classes
        for (DataClassExporter tableRowClassExporter : dataClassExporters) {
            tableRowClassExporter.export();
        }

        log("");
    }


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public boolean isGuice() {
        return guice;
    }

    public void setGuice(boolean guice) {
        this.guice = guice;
    }

}
