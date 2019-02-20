package com.w11k.lsql.cli.java;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.w11k.lsql.LSql;
import com.w11k.lsql.cli.CodeGenUtils;
import com.w11k.lsql.query.RowQuery;
import com.w11k.lsql.sqlfile.LSqlFile;
import com.w11k.lsql.statement.AbstractSqlStatement;
import com.w11k.lsql.statement.SqlStatementToPreparedStatement;

import java.io.File;
import java.util.List;
import java.util.Set;

import static com.google.common.io.MoreFiles.getNameWithoutExtension;
import static com.w11k.lsql.cli.CodeGenUtils.*;

public final class StatementFileExporter {

    private final File stmtSourceFile;

    private final LSql lSql;
    private final JavaExporter javaExporter;

    private final String stmtFilesRootDir;

    private final List<TypedStatementMeta> typedStatementMetas = Lists.newLinkedList();

    private final List<DataClassMeta> stmtRowDataClassMetaList = Lists.newLinkedList();

    private final boolean containsOnlyVoidStatements;

    private final String targetPackageName;

    private final String stmtFileClassName;

    public StatementFileExporter(LSql lSql,
                                 JavaExporter javaExporter,
                                 File stmtSourceFile,
                                 String stmtFilesRootDir) {
        this.lSql = lSql;
        this.javaExporter = javaExporter;
        this.stmtSourceFile = stmtSourceFile;
        this.stmtFilesRootDir = stmtFilesRootDir;

        this.targetPackageName = this.getPackageNameForStatement();
        this.stmtFileClassName = getNameWithoutExtension(this.stmtSourceFile.toPath());

        // read statements in file
        LSqlFile lSqlFile = new LSqlFile(lSql, stmtSourceFile.getAbsolutePath(), stmtSourceFile.getAbsolutePath());
        ImmutableMap<String, SqlStatementToPreparedStatement> statements = lSqlFile.getStatements();

        // process statements
        boolean containsOnlyVoidStatements = true;
        for (String stmtName : statements.keySet()) {
            AbstractSqlStatement<RowQuery> query = lSqlFile.statement(stmtName);
            SqlStatementToPreparedStatement stmt = lSqlFile.getSqlStatementToPreparedStatement(stmtName);

            if (stmt.getTypeAnnotation().toLowerCase().equals("nogen")) {
                continue;
            }

            log("Probing statement: " + stmt.getStatementSourceName() + "#" + stmt.getStatementName());

            // in
            TypedStatementMeta typedStatementMeta = new TypedStatementMeta(
                    lSql,
                    query,
                    stmt,
                    stmtSourceFile.getName()
            );
            this.typedStatementMetas.add(typedStatementMeta);

            // out
            if (!stmt.getTypeAnnotation().toLowerCase().equals("void")) {
                containsOnlyVoidStatements = false;
                DataClassMeta dcm = new DataClassMeta(
                        getJavaCodeName(stmt.getStatementName(), false, true),
                        joinStringsAsPackageName(
                                this.javaExporter.getPackageName(), this.getSubPackageName(), this.stmtFileClassName.toLowerCase()));
                query.query().createResultSetWithColumns().getColumns().forEach(c -> {
                    String colName = c.getName();
                    String fieldName = getJavaCodeName(colName, false, false);
                    String rowKeyName = this.lSql.convertInternalSqlToRowKey(colName);

                    dcm.addField(
                            colName,
                            fieldName,
                            rowKeyName,
                            c.getConverter().getJavaType()
                    ).setNullable(c.isNullable());
                });
                this.stmtRowDataClassMetaList.add(dcm);
            }
            rollback(lSql);
        }

        this.containsOnlyVoidStatements = containsOnlyVoidStatements;
    }

    public Set<StructuralTypingField> exportAndGetStructuralTypingFields() {
        Set<StructuralTypingField> structuralTypingFields = Sets.newHashSet();
        exportTypedStatementClass(structuralTypingFields);
        exportStatementRowClasses(structuralTypingFields);
        return structuralTypingFields;
    }

    private void exportTypedStatementClass(Set<StructuralTypingField> structuralTypingFields) {
        StringBuilder content = new StringBuilder();

        content.append("package ").append(this.targetPackageName).append(";\n\n");

        content.append("import ")
                .append(this.javaExporter.getPackageName())
                .append(".structural_fields")
                .append(".*;\n");

        if (!this.containsOnlyVoidStatements) {
            content.append("import ")
                    .append(joinStringsAsPackageName(
                            this.javaExporter.getPackageName(), this.getSubPackageName(), this.stmtFileClassName.toLowerCase()))
                    .append(".*;\n");
        }

        content.append("import java.util.*;\n");

        content.append("\n");

        content.append("public class ")
                .append(stmtFileClassName)
                .append(" {\n\n");

        for (TypedStatementMeta typedStatementMeta : this.typedStatementMetas) {
            content.append("    // Statement: ")
                    .append(typedStatementMeta.getStatement().getStatementName())
                    .append(" ----------------------------\n\n");

            TypedStatementExporter typedStatementExporter = new TypedStatementExporter(
                    this.javaExporter, typedStatementMeta);
            List<DataClassExporter> dataClassExportersForQueryParams = typedStatementExporter.export(content);
            dataClassExportersForQueryParams
                    .forEach(e -> structuralTypingFields.addAll(e.getStructuralTypingFields()));
        }

        // constructor
        content.append("    private final ").append(LSql.class.getCanonicalName()).append(" lSql;\n\n");
        if (this.javaExporter.isGuice()) {
            content.append("    @com.google.inject.Inject\n");
        }
        content.append("    public ").append(stmtFileClassName).append("(")
                .append(LSql.class.getCanonicalName()).append(" lSql) {\n")
                .append("        this.lSql = lSql;\n")
                .append("    }\n\n");

        content.append("}\n");

        File outputFile = this.getOutputFile();
        writeContent(content.toString(), outputFile);
    }

    private void exportStatementRowClasses(Set<StructuralTypingField> structuralTypingFields) {
        for (DataClassMeta dataClassMeta : this.stmtRowDataClassMetaList) {
            DataClassExporter dataClassExporter = new DataClassExporter(this.javaExporter, dataClassMeta, "");
            structuralTypingFields.addAll(dataClassExporter.getStructuralTypingFields());
            dataClassExporter.export();
        }
    }

    private File getOutputFile() {
        File baseDir = getFileFromBaseDirAndPackageName(javaExporter.getOutputDir(), this.targetPackageName);
        return new File(baseDir, this.stmtFileClassName + ".java");
    }

    public String getSubPackageName() {
        return CodeGenUtils.getSubPathFromFileInParent(
                this.stmtFilesRootDir,
                this.stmtSourceFile.getAbsolutePath()
        );
    }

    private String getPackageNameForStatement() {
        return joinStringsAsPackageName(javaExporter.getPackageName(), getSubPackageName());
    }

    public List<DataClassMeta> getStmtRowDataClassMetaList() {
        return stmtRowDataClassMetaList;
    }

    public String getTargetPackageName() {
        return targetPackageName;
    }

    public String getStmtFileClassName() {
        return stmtFileClassName;
    }
}

