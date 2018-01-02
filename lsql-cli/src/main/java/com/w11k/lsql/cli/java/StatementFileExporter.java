package com.w11k.lsql.cli.java;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.MoreFiles;
import com.w11k.lsql.LSql;
import com.w11k.lsql.query.RowQuery;
import com.w11k.lsql.sqlfile.LSqlFile;
import com.w11k.lsql.statement.AbstractSqlStatement;
import com.w11k.lsql.statement.SqlStatementToPreparedStatement;

import java.io.File;
import java.util.List;
import java.util.Set;

import static com.w11k.lsql.cli.CodeGenUtils.*;

public final class StatementFileExporter {

    private final LSql lSql;

    private final File sourceFile;

    private final JavaExporter javaExporter;

    private final String sqlStatementsRootDir;

    private final List<TypedStatementMeta> typedStatementMetas = Lists.newLinkedList();

    private final List<StatementRowColumnContainer> statementRows = Lists.newLinkedList();

    private final String packageName;

    private final String className;

    public StatementFileExporter(LSql lSql,
                                 File sourceFile,
                                 JavaExporter javaExporter,
                                 String sqlStatementsRootDir) {

        this.lSql = lSql;
        this.sourceFile = sourceFile;
        this.javaExporter = javaExporter;
        this.sqlStatementsRootDir = sqlStatementsRootDir;

        this.packageName = this.getPackageNameFromStmtPath(this.sqlStatementsRootDir, this.sourceFile);

        this.className = MoreFiles.getNameWithoutExtension(this.sourceFile.toPath());

        LSqlFile lSqlFile = new LSqlFile(lSql, sourceFile.getAbsolutePath(), sourceFile.getAbsolutePath());

        ImmutableMap<String, SqlStatementToPreparedStatement> statements = lSqlFile.getStatements();

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
                    sqlStatementsRootDir,
                    sourceFile
            );
            this.typedStatementMetas.add(typedStatementMeta);

            // out
            if (!stmt.getTypeAnnotation().toLowerCase().equals("void")) {
                this.statementRows.add(new StatementRowColumnContainer(
                        this,
                        typedStatementMeta,
                        lSql,
                        query,
                        sqlStatementsRootDir,
                        sourceFile));
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
        File outputFile = this.getOutputFile(packageName, className);
        StringBuilder content = new StringBuilder();

        content.append("package ").append(this.getFullPackageName(packageName)).append(";\n\n");

        content.append("public class ")
                .append(className)
                .append(" {\n\n");

        for (TypedStatementMeta typedStatementMeta : this.typedStatementMetas) {
            TypedStatementExporter typedStatementExporter = new TypedStatementExporter(
                    typedStatementMeta, this);
            typedStatementExporter.export(content);
        }

        // constructor
        content.append("    private final ").append(LSql.class.getCanonicalName()).append(" lSql;\n\n");
        if (this.javaExporter.isGuice()) {
            content.append("    @com.google.inject.Inject\n");
        }
        content.append("    public ").append(className).append("(")
                .append(LSql.class.getCanonicalName()).append(" lSql) {\n")
                .append("        this.lSql = lSql;\n")
                .append("    }\n\n");

        content.append("}\n");

        writeContentIfChanged(content.toString(), outputFile);
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    private void exportStatementRowClasses(Set<StructuralTypingField> structuralTypingFields) {
        for (StatementRowColumnContainer statementRow : this.statementRows) {
            StatementRowExporter stmtInRowClass = new StatementRowExporter(
                    this.lSql, statementRow, this.javaExporter);

            structuralTypingFields.addAll(stmtInRowClass.getStructuralTypingFields());
            stmtInRowClass.export();
        }
    }

    private String getFullPackageName(String localPackageName) {
        return this.javaExporter.getPackageName() + "." + localPackageName;
    }

    private File getOutputFile(String packageName, String className) {
        File packageFolder = new File(
                this.javaExporter.getOutputRootPackageDir(), packageName);

        return new File(packageFolder, className + ".java");
    }

    public List<StatementRowColumnContainer> getStatementRows() {
        return statementRows;
    }

    private String getPackageNameFromStmtPath(String sqlStatementsRootDir, File sourceFile) {
        String filePath = sourceFile.getAbsolutePath();
        int start = filePath.lastIndexOf(sqlStatementsRootDir) + sqlStatementsRootDir.length();
        String relativePath = filePath.substring(start);
        Iterable<String> pathSlitIt = Splitter.on(File.separatorChar).omitEmptyStrings().split(relativePath);
        List<String> pathSplit = Lists.newLinkedList(pathSlitIt);
        pathSplit.remove(pathSplit.size() - 1);
        return Joiner.on("_").join(pathSplit);
    }

}
