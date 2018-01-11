package com.w11k.lsql.cli.java;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.w11k.lsql.LSql;
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

    private final JavaExporter javaExporter;

    private final String stmtFilesRootDir;

    private final List<TypedStatementMeta> typedStatementMetas = Lists.newLinkedList();

    private final List<DataClassMeta> stmtRowDataClassMetaList = Lists.newLinkedList();

    private final String targetPackageName;

    private final String stmtFileClassName;

    public StatementFileExporter(LSql lSql,
                                 JavaExporter javaExporter,
                                 File stmtSourceFile,
                                 String stmtFilesRootDir) {

        this.javaExporter = javaExporter;
        this.stmtSourceFile = stmtSourceFile;
        this.stmtFilesRootDir = stmtFilesRootDir;

        this.targetPackageName = this.getPackageNameForStatement();
        this.stmtFileClassName = getNameWithoutExtension(this.stmtSourceFile.toPath());

        // read statements in file
        LSqlFile lSqlFile = new LSqlFile(lSql, stmtSourceFile.getAbsolutePath(), stmtSourceFile.getAbsolutePath());
        ImmutableMap<String, SqlStatementToPreparedStatement> statements = lSqlFile.getStatements();

        // process statements
        for (String stmtName : statements.keySet()) {
            AbstractSqlStatement<RowQuery> query = lSqlFile.statement(stmtName);
            SqlStatementToPreparedStatement stmt = lSqlFile.getSqlStatementToPreparedStatement(stmtName);

            if (stmt.getTypeAnnotation().toLowerCase().equals("nogen")) {
                continue;
            }

            // in
            TypedStatementMeta typedStatementMeta = new TypedStatementMeta(
                    lSql,
                    query,
                    stmt,
                    stmtFilesRootDir,
                    stmtSourceFile
            );
            this.typedStatementMetas.add(typedStatementMeta);

            // out
            if (!stmt.getTypeAnnotation().toLowerCase().equals("void")) {
                DataClassMeta dcm = new DataClassMeta(
                        firstCharUpperCase(stmt.getStatementName()),
                        joinStringsAsPackageName(this.javaExporter.getPackageName(), this.getSubPackageName()));
                query.query().createResultSetWithColumns().getColumns().forEach(c -> {
                    dcm.addField(c.getName(), c.getConverter().getJavaType());
                });
                this.stmtRowDataClassMetaList.add(dcm);
            }

            rollback(lSql);
        }
    }

    public Set<StructuralTypingField> exportAndGetStructuralTypingFields() {
        Set<StructuralTypingField> structuralTypingFields = Sets.newHashSet();
        exportTypedStatementClass();
        exportStatementRowClasses(structuralTypingFields);
        return structuralTypingFields;
    }

    private void exportTypedStatementClass() {
        StringBuilder content = new StringBuilder();

        content.append("package ").append(this.targetPackageName).append(";\n\n");

        content.append("public class ")
                .append(stmtFileClassName)
                .append(" {\n\n");

        for (TypedStatementMeta typedStatementMeta : this.typedStatementMetas) {
            content.append("    // ------------------------------------------------------------------\n\n");
            TypedStatementExporter typedStatementExporter = new TypedStatementExporter(
                    this.javaExporter, typedStatementMeta, this);
            typedStatementExporter.export(content);
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

    public String getTargetPackageName() {
        return targetPackageName;
    }

    //    public List<StatementRowColumnContainer> getStatementRows() {
//        return stmtRowDataClassMetaList;
//    }

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
        String sourceFilePath = this.stmtSourceFile.getAbsolutePath();
        int start = sourceFilePath.lastIndexOf(this.stmtFilesRootDir) + this.stmtFilesRootDir.length();
        String relativePath = sourceFilePath.substring(start);
        Iterable<String> pathSlitIt = Splitter.on(File.separatorChar).omitEmptyStrings().split(relativePath);
        List<String> pathSplit = Lists.newLinkedList(pathSlitIt);
        pathSplit.remove(pathSplit.size() - 1); // remove filename
        String subPart = Joiner.on(".").join(pathSplit);
        return subPart;
    }

    private String getPackageNameForStatement() {
        return joinStringsAsPackageName(javaExporter.getPackageName(), getSubPackageName());
    }

}

