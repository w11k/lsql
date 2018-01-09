package com.w11k.lsql.cli.java;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.w11k.lsql.Column;
import com.w11k.lsql.LSql;
import com.w11k.lsql.TableLike;

import java.io.File;
import java.util.Comparator;
import java.util.List;

import static com.w11k.lsql.cli.CodeGenUtils.createFileFromBaseDirAndPackageName;
import static com.w11k.lsql.cli.CodeGenUtils.createSaveNameForClass;
import static com.w11k.lsql.cli.CodeGenUtils.writeContent;
import static java.util.stream.Collectors.toList;

abstract public class AbstractDataClassExporter {

    private final List<StructuralTypingField> structuralTypingFields;

    protected final List<Column> columns;

    protected final String constructorCallArgs;

    private final LSql lSql;

    private final TableLike tableLike;

    protected final JavaExporter javaExporter;

    private final String fullPackageName;

    private final String className;

    protected StringBuilder content = new StringBuilder();

    public AbstractDataClassExporter(LSql lSql, TableLike cc, JavaExporter javaExporter) {
        this.lSql = lSql;
        this.tableLike = cc;
        this.javaExporter = javaExporter;
        this.fullPackageName = javaExporter.createFullPackageNameForTableLike(getTableLike());
        this.className = createSaveNameForClass(tableLike.getTableName() + getClassNameSuffix());

        this.columns = Lists.newLinkedList(cc.getColumns().values());
        this.columns.sort(Comparator.comparing(Column::getJavaColumnName));

        this.structuralTypingFields = createStructuralTypingFieldList();
        this.constructorCallArgs = Joiner.on(",").join(columns.stream().map(Column::getJavaColumnName).collect(toList()));
    }

    public LSql getlSql() {
        return lSql;
    }

    public TableLike getTableLike() {
        return tableLike;
    }

    public String getFullPackageName() {
        return fullPackageName;
    }

    private List<StructuralTypingField> createStructuralTypingFieldList() {
        List<StructuralTypingField> list = Lists.newLinkedList();
        for (Column column : columns) {
            list.add(new StructuralTypingField(column.getJavaColumnName(), column.getConverter().getJavaType()));
        }
        return list;
    }

    protected String getClassNameSuffix() {
        return "";
    }

    public String getClassName() {
        return className;
    }

    public void export() {
        this.createContent();
        File pojoSourceFile = getOutputFile();
        writeContent(this.content.toString(), pojoSourceFile);
    }

    protected abstract void createContent();

    public File getOutputFile() {
        File baseDir = createFileFromBaseDirAndPackageName(javaExporter.getOutputDir(), fullPackageName);
        return new File(baseDir, getClassName() + ".java");
    }

    public List<StructuralTypingField> getStructuralTypingFields() {
        return structuralTypingFields;
    }
}
