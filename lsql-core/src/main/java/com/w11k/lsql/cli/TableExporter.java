package com.w11k.lsql.cli;

import com.google.common.base.Joiner;
import com.w11k.lsql.Column;
import com.w11k.lsql.Table;

import java.io.File;

import static java.util.stream.Collectors.toList;

public class TableExporter extends AbstractTableExporter {

    public TableExporter(Table table, SchemaExporter schemaExporter, File rootPackage) {
        super(table, schemaExporter, rootPackage);
    }

    public void createContent() {
        content.append("package ").append(getFullPackageName()).append(";\n\n");
        content.append("import ").append(this.schemaExporter.getPackageName()).append(".*;\n\n");
        content.append("public class ").append(getClassName());
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

        // assignInto
        contentAssignIntoNew();
        contentUpdatedWith();

        content.append("}\n");
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
                    .append("(this.get").append(stf.getUppercaseName()).append("());\n");
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
                    .append("(source.get").append(stf.getUppercaseName()).append("());\n");
        }
        content.append("        return (").append(getClassName()).append(") target;\n");

        content.append("    }\n\n");
    }

    private void contentImplements() {
        content.append(" implements ");
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
