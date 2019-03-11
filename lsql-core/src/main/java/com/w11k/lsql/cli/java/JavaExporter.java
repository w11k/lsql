package com.w11k.lsql.cli.java;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.w11k.lsql.LSql;
import com.w11k.lsql.Table;
import com.w11k.lsql.jdbc.ConnectionUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.List;
import java.util.Set;

import static com.w11k.lsql.cli.CodeGenUtils.*;
import static java.util.Collections.emptyList;

public class JavaExporter {

    private final LSql lSql;

    private List<StatementFileExporter> statementFileExporters = emptyList();

    private File outputDir = null;

    private String packageName;

    private CliArgs.DependencyInjection dependencyInjection;

    private List<DataClassMeta> generatedDataClasses = emptyList();

    /**
     * @param lSql
     * @param schemas pass null to featch all schemas
     */
    public JavaExporter(LSql lSql, String schemas) {
        this.lSql = lSql;
        this.fetchMetaDataForAllTables(schemas);
    }

    private void fetchMetaDataForAllTables(String schemas) {
        try {
            Connection con = ConnectionUtils.getConnection(this.lSql);
            DatabaseMetaData md = con.getMetaData();

            List<String> foundTables = Lists.newLinkedList();

            if (schemas == null) {
                foundTables.addAll(this.fetchMetaDataForSchema(md, null));
            } else {
                Iterable<String> schemaList = Splitter.on(",").omitEmptyStrings().trimResults().split(schemas);
                for (String schema : schemaList) {
                    foundTables.addAll(this.fetchMetaDataForSchema(md, schema));
                }
            }

            for (String foundTable : foundTables) {
                this.lSql.tableBySqlName(foundTable);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> fetchMetaDataForSchema(DatabaseMetaData md, String schema) {
        try {
            ResultSet tables = md.getTables(null, schema, null, new String[]{"TABLE"});
            List<String> foundTables = Lists.newLinkedList();
            while (tables.next()) {
                String sqlSchemaName = tables.getString(2);
                String sqlTableName = tables.getString(3);

                if (sqlSchemaName != null) {
                    foundTables.add(sqlSchemaName + "." + sqlTableName);
                } else {
                    foundTables.add(sqlTableName);
                }
            }
            return foundTables;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void setStatementFileExporters(List<StatementFileExporter> statementFileExporters) {
        this.statementFileExporters = statementFileExporters;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void export() {
        this.generatedDataClasses = Lists.newLinkedList();
        List<DataClassExporter> dataClassExporters = Lists.newLinkedList();
        Set<StructuralTypingField> structuralTypingFields = Sets.newHashSet();

        List<String> guiceModuleClasses = Lists.newLinkedList();

        // Tables
        for (Table table : this.lSql.getTables()) {
            // collect all row classes

            String schemaName = table.getSchemaName();
            String lastPackageSegment = schemaName.length() == 0
                    ? ""
                    : Joiner.on("_").skipNulls().join("schema", schemaName.toLowerCase());
            String fullPackageName = joinStringsAsPackageName(packageName, lastPackageSegment);
            String tableName = getJavaCodeName(table.getTableName().toLowerCase(), true, true);
            String className = getJavaCodeName(tableName, true, true);

            DataClassMeta dcm = new DataClassMeta(className, fullPackageName);
            table.getColumns().values()
                    .forEach(c -> {
                        String colName = c.getColumnName();
                        String fieldName = getJavaCodeName(colName, false, false);

                        dcm.addField(
                                colName,
                                fieldName,
                                this.lSql.convertInternalSqlToRowKey(colName),
                                c.getConverter().getJavaType()
                        ).setNullable(c.isNullable());
                    });

            this.generatedDataClasses.add(dcm);
            DataClassExporter dataClassExporter = new DataClassExporter(this, dcm, "_Row");
            dataClassExporters.add(dataClassExporter);

            // generate table classes
            TableExporter tableExporter = new TableExporter(this, table, dataClassExporter);
            guiceModuleClasses.add(dcm.getPackageName() + "." + tableExporter.getClassName());
            tableExporter.export();
        }

        // StatementFileExporter
        for (StatementFileExporter statementFileExporter : this.statementFileExporters) {
            Set<StructuralTypingField> stfs = statementFileExporter.exportAndGetStructuralTypingFields();
            structuralTypingFields.addAll(stfs);
            List<DataClassMeta> stmtRowDataClassMetaList = statementFileExporter.getStmtRowDataClassMetaList();
            this.generatedDataClasses.addAll(stmtRowDataClassMetaList);
            guiceModuleClasses.add(statementFileExporter.getTargetPackageName() + "." + statementFileExporter.getStmtFileClassName());
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

        // generate Guice module
        if (this.dependencyInjection.equals(CliArgs.DependencyInjection.GUICE)) {
            new GuiceModuleExporter(this, guiceModuleClasses).export();
        }

        log("");
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

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    public CliArgs.DependencyInjection getDependencyInjection() {
        return dependencyInjection;
    }

    public void setDependencyInjection(CliArgs.DependencyInjection dependencyInjection) {
        this.dependencyInjection = dependencyInjection;
    }

    public LSql getlSql() {
        return lSql;
    }

    public List<DataClassMeta> getGeneratedDataClasses() {
        return generatedDataClasses;
    }

}
