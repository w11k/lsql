package com.w11k.lsql.cli;

import com.google.common.base.Joiner;
import com.w11k.lsql.Column;
import com.w11k.lsql.Table;
import com.w11k.lsql.TableRow;

import java.io.File;

import static com.w11k.lsql.cli.CodeGenUtils.lowerCamelToUpperCamel;
import static java.util.stream.Collectors.toList;

public class RowExporter extends AbstractTableBasedExporter {

    public RowExporter(Table table, SchemaExporter schemaExporter, File rootPackage) {
        super(table, schemaExporter, rootPackage);
    }

    public void createContent() {
        content.append("package ").append(getFullPackageName()).append(";\n\n");
        content.append("import ").append(this.schemaExporter.getPackageName()).append(".*;\n\n");
        content.append("public class ").append(getClassName());
        content.append(" implements ").append(TableRow.class.getCanonicalName());
        contentImplements();
        content.append(" {\n\n");

        // constructors
        contentConstructors();

        // Field instances and getter/setter
        for (Column column : this.columns) {
            contentSeperator();
            Class<?> javaType = column.getConverter().getJavaType();
            contentField(column, javaType);
            content.append("\n");
            contentGetterSetterForField(column);
        }

        contentSeperator();

        // StructuralTyping methods
        contentAssignIntoNew();
        contentUpdatedWith();

        // toMap
        contentToMap();

        content.append("}\n");
    }

    @Override
    public String getOutputFileName() {
        return getClassName() + ".java";
    }

    protected String getClassName() {
        return lowerCamelToUpperCamel(this.table.getTableName()) + "Row";
    }

    private void contentToMap() {
        content.append("    public java.util.Map<String, Object> toMap() {\n");
        content.append("        java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();\n");

        for (Column column : columns) {
            content.append("        ")
                    .append("map.put(\"").append(column.getJavaColumnName()).append("\", this.").append(column.getJavaColumnName()).append(");\n");
        }

        content.append("        return map;\n");
        content.append("    }\n\n");
    }

    private void contentAssignIntoNew() {
        content.append("    @SuppressWarnings(\"unchecked\")\n");
        content.append("    public <T extends \n            ");
        content.append(Joiner.on("\n            & ").join(getStructuralTypingFields().stream()
                .map(StructuralTypingField::getInterfaceName).collect(toList())));
        content.append("> T assignIntoNew(T targetStart) {\n");

        content.append("        Object target = targetStart;\n");
        for (StructuralTypingField stf : getStructuralTypingFields()) {
            content.append("        target = ")
                    .append("((").append(stf.getInterfaceName()).append(") target).with").append(stf.getUppercaseName())
                    .append("(this.").append(stf.getGetterMethodName()).append("());\n");
        }
        content.append("        return (T) target;\n");

        content.append("    }\n\n");
    }

    private void contentUpdatedWith() {
        content.append("    @SuppressWarnings(\"unchecked\")\n");
        content.append("    public <T extends \n            ");
        content.append(Joiner.on("\n            & ").join(getStructuralTypingFields().stream()
                .map(StructuralTypingField::getInterfaceName).collect(toList())));
        content.append("> ").append(getClassName()).append(" updatedWith(T source) {\n");

        content.append("        Object target = this;\n");
        for (StructuralTypingField stf : getStructuralTypingFields()) {
            content.append("        target = ")
                    .append("((").append(stf.getInterfaceName()).append(") target).with").append(stf.getUppercaseName())
                    .append("(source.").append(stf.getGetterMethodName()).append("());\n");
        }
        content.append("        return (").append(getClassName()).append(") target;\n");

        content.append("    }\n\n");
    }

    private void contentImplements() {
        if (getStructuralTypingFields().size() > 0) {
            content.append(",");
        }
        content.append(
                Joiner.on(",")
                        .join(getStructuralTypingFields().stream()
                                .map(StructuralTypingField::getInterfaceName).collect(toList())));

    }

    private void contentConstructors() {
        // empty constructor
        content.append("    public ").append(getClassName()).append("() {}\n\n");

        // constructor with field initializer
        content.append("    private ").append(getClassName()).append("(\n");

        String arguments = Joiner.on(",\n").join(columns.stream().map(column ->
                "            "
                        + column.getConverter().getJavaType().getCanonicalName()
                        + " "
                        + column.getJavaColumnName())
                .collect(toList()));
        content.append(arguments);
        content.append(") {\n");

        // assign member
        for (Column column : columns) {
            content.append("        ")
                    .append("this.").append(column.getJavaColumnName())
                    .append(" = ")
                    .append(column.getJavaColumnName())
                    .append(";\n");
        }

        content.append("    }\n\n");

        // constructor from map
        content.append("    public ").append(getClassName()).append("(java.util.Map<String, Object> from) {\n");
        // assign member
        for (Column column : columns) {
            content.append("        ")
                    .append("this.").append(column.getJavaColumnName())
                    .append(" = ")
                    .append("(").append(column.getConverter().getJavaType().getCanonicalName()).append(") ")
                    .append("from.get(\"")
                    .append(column.getJavaColumnName())
                    .append("\");\n");
        }
        content.append("    }\n");
    }

    private void contentField(Column column, Class<?> javaType) {
        content.append("    private ");
        content.append(javaType.getCanonicalName()).append(" ").append(column.getJavaColumnName()).append(";\n");
    }

    private void contentGetterSetterForField(Column column) {
        // Getter
        content.append("    public ");
        content.append(column.getConverter().getJavaType().getCanonicalName());
        content.append(" ");

        boolean isBool = Boolean.class.isAssignableFrom(column.getConverter().getJavaType());
        String prefix = isBool ? "is" : "get";
        content.append(prefix).append(lowerCamelToUpperCamel(column.getJavaColumnName())).append("() {\n");
        content.append("        return this.").append(column.getJavaColumnName()).append(";\n");
        content.append("    }\n\n");

        // Setter
        content.append("    public ").append(this.getClassName()).append(" ");
        content.append("with").append(lowerCamelToUpperCamel(column.getJavaColumnName())).append("(");
        content.append(column.getConverter().getJavaType().getCanonicalName());
        content.append(" ").append(column.getJavaColumnName());
        content.append(") {\n");

        // setter body
        content.append("        return new ").append(this.getClassName()).append("(");
        content.append(constructorCallArgs);
        content.append(");\n");
        content.append("    }\n");

    }

}
