package com.w11k.lsql.cli;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.w11k.lsql.Column;
import com.w11k.lsql.Table;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static com.w11k.lsql.cli.CodeGenUtils.lowerCamelToUpperCamel;
import static java.util.stream.Collectors.toList;

abstract public class AbstractTableExporter {

    private final List<StructuralTypingField> structuralTypingFields;

    protected final Table table;

    protected final List<Column> columns;

    protected final String constructorCallArgs;

    protected final SchemaExporter schemaExporter;

    protected final File rootPackage;

    protected StringBuilder content = new StringBuilder();

    public AbstractTableExporter(Table table, SchemaExporter schemaExporter, File rootPackage) {
        this.table = table;
        this.schemaExporter = schemaExporter;
        this.rootPackage = rootPackage;
        this.columns = Lists.newLinkedList(table.getColumns().values());
        this.columns.sort(Comparator.comparing(Column::getJavaColumnName));

        this.structuralTypingFields = createStructuralTypingFieldList();
        this.constructorCallArgs = Joiner.on(",").join(columns.stream().map(Column::getJavaColumnName).collect(toList()));
    }

    public Table getTable() {
        return table;
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
        try {
            // TODO only write if content changed
            Files.write(content.toString().getBytes(Charsets.UTF_8), pojoSourceFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void createContent();

    protected File getOutputFile() {
        File packageWithSchema = new File(this.rootPackage, this.getLastPackageSegmentForSchema());
        //noinspection ResultOfMethodCallIgnored
        packageWithSchema.mkdirs();
        return new File(packageWithSchema, getClassName() + ".java");
    }

    public String getLastPackageSegmentForSchema() {
        String schemaName = this.table.getSchemaName();

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
                ? this.schemaExporter.getPackageName()
                : this.schemaExporter.getPackageName() + "." + packageSchemaSegment;
    }

    protected String getClassName() {
        return lowerCamelToUpperCamel(this.table.getTableName()) + "Row";
    }

    protected void contentSeperator() {
        content.append("\n    // ------------------------------------------------------------").append("\n\n");
    }

    public List<StructuralTypingField> getStructuralTypingFields() {
        return structuralTypingFields;
    }
}
