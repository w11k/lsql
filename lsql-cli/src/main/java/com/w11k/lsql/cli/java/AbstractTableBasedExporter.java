package com.w11k.lsql.cli.java;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.w11k.lsql.Column;
import com.w11k.lsql.ColumnsContainer;

import java.io.File;
import java.util.Comparator;
import java.util.List;

import static com.w11k.lsql.cli.CodeGenUtils.writeContentIfChanged;
import static java.util.stream.Collectors.toList;

abstract public class AbstractTableBasedExporter {

    private final List<StructuralTypingField> structuralTypingFields;

    protected final List<Column> columns;

    protected final String constructorCallArgs;

    private final ColumnsContainer columnsContainer;

    protected final JavaExporter javaExporter;

    protected StringBuilder content = new StringBuilder();

    public AbstractTableBasedExporter(ColumnsContainer cc, JavaExporter javaExporter) {
        this.columnsContainer = cc;
        this.javaExporter = javaExporter;
        this.columns = Lists.newLinkedList(cc.getColumns().values());
        this.columns.sort(Comparator.comparing(Column::getJavaColumnName));

        this.structuralTypingFields = createStructuralTypingFieldList();
        this.constructorCallArgs = Joiner.on(",").join(columns.stream().map(Column::getJavaColumnName).collect(toList()));
    }

    public ColumnsContainer getColumnsContainer() {
        return columnsContainer;
    }

    private List<StructuralTypingField> createStructuralTypingFieldList() {
        List<StructuralTypingField> list = Lists.newLinkedList();
        for (Column column : columns) {
            list.add(new StructuralTypingField(column.getJavaColumnName(), column.getConverter().getJavaType()));
        }
        return list;
    }

    final public void export() {
        this.createContent();
        File pojoSourceFile = getOutputFile();
        writeContentIfChanged(this.content.toString(), pojoSourceFile);
    }

    protected abstract void createContent();

    protected File getOutputFile() {
        File packageWithSchema = new File(
                this.javaExporter.getOutputRootPackageDir(), this.getLastPackageSegmentForSchema());

        return new File(packageWithSchema, getOutputFileName());
    }

    abstract public String getOutputFileName();

    public String getLastPackageSegmentForSchema() {
        String schemaName = this.columnsContainer.getSchemaName();

        // no schema
        if (schemaName.length() == 0) {
            return "";
        }

        // with schema nma
        return "schema_" + schemaName.toLowerCase();
    }

    protected String getFullPackageName() {
        String packageSchemaSegment = this.getLastPackageSegmentForSchema();
        return packageSchemaSegment.equals("")
                ? this.javaExporter.getPackageName()
                : this.javaExporter.getPackageName() + "." + packageSchemaSegment;
    }

    protected void contentSeperator() {
        content.append("\n    // ------------------------------------------------------------").append("\n\n");
    }

    public List<StructuralTypingField> getStructuralTypingFields() {
        return structuralTypingFields;
    }
}
