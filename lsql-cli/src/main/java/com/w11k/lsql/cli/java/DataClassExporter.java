package com.w11k.lsql.cli.java;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.w11k.lsql.TableRow;
import com.w11k.lsql.cli.CodeGenUtils;

import java.io.File;
import java.util.List;

import static com.w11k.lsql.cli.CodeGenUtils.*;
import static java.util.stream.Collectors.toList;

public class DataClassExporter {

    private final List<StructuralTypingField> structuralTypingFields;

    private final String constructorCallArgs;

    private final DataClassMeta dataClassMeta;

    private final String classNameSuffix;

    private final JavaExporter javaExporter;

    public DataClassExporter(JavaExporter javaExporter, DataClassMeta dataClassMeta, String classNameSuffix) {
        this.dataClassMeta = dataClassMeta;
        this.javaExporter = javaExporter;
        this.classNameSuffix = classNameSuffix;

        this.structuralTypingFields = createStructuralTypingFieldList();
        this.constructorCallArgs = Joiner.on(",").join(dataClassMeta.getFields().stream()
                .map(DataClassMeta.DataClassFieldMeta::getFieldName)
                .collect(toList()));
    }

    public DataClassMeta getDataClassMeta() {
        return dataClassMeta;
    }

    private List<StructuralTypingField> createStructuralTypingFieldList() {
        List<StructuralTypingField> list = Lists.newLinkedList();
        for (DataClassMeta.DataClassFieldMeta field : this.dataClassMeta.getFields()) {
            list.add(new StructuralTypingField(field.getFieldName(), field.getFieldType()));
        }
        return list;
    }

    public void export() {
        StringBuilder content = new StringBuilder();
        this.createContent(content);
        File pojoSourceFile = CodeGenUtils.getOutputFile(
                javaExporter.getOutputDir(),
                this.dataClassMeta.getPackageName(),
                this.getClassName() + ".java");
        writeContent(content.toString(), pojoSourceFile);
    }

    public String getClassName() {
        return this.dataClassMeta.getClassName() + this.classNameSuffix;
    }

    public List<StructuralTypingField> getStructuralTypingFields() {
        return structuralTypingFields;
    }

    public void createContent(StringBuilder content) {
        DataClassMeta dcm = this.dataClassMeta;
        content.append("package ").append(dcm.getPackageName()).append(";\n\n");

        String stmtFieldsPackageName = joinStringsAsPackageName(
                this.javaExporter.getPackageName(), "structural_fields");
        content.append("import ").append(stmtFieldsPackageName).append(".*;\n\n");

        createClass(content, false, false, "");
    }

    public void createClass(StringBuilder content, boolean skipStaticElements, boolean keepClassBodyOpen, String extendsClause) {
        DataClassMeta dcm = this.dataClassMeta;

        content.append("public class ").append(this.getClassName());
        content.append(extendsClause);
        content.append(" implements ").append(TableRow.class.getCanonicalName());
        contentImplements(content);
        content.append(" {\n\n");

        if (!skipStaticElements) {
            // static from
            contentFrom(content);
        }

        // constructors
        contentConstructors(content);

        // Field instances and getter/setter
        for (DataClassMeta.DataClassFieldMeta field : dcm.getFields()) {
            addSeperator(content);
            if (!skipStaticElements) {
                contentStaticFieldName(content, field);
            }
            contentField(content, field);
            content.append("\n");
            contentGetterSetterForField(content, field);
        }

        addSeperator(content);

        // StructuralTyping methods
        contentAsInstance(content);
        contentAsClass(content);

        // toMap
        contentToMap(content);

        if (!keepClassBodyOpen) {
            content.append("}\n");
        }

    }

    private void contentToMap(StringBuilder content) {
        content.append("    public java.util.Map<String, Object> toMap() {\n");
        content.append("        java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();\n");

        for (DataClassMeta.DataClassFieldMeta field : this.dataClassMeta.getFields()) {
            content.append("        ")
                    .append("map.put(\"").append(field.getFieldName()).append("\", this.").append(field.getFieldName()).append(");\n");
        }

        content.append("        return map;\n");
        content.append("    }\n\n");
    }

    private void contentAsInstance(StringBuilder content) {
        content.append("    @SuppressWarnings(\"unchecked\")\n");
        content.append("    public <T");

        if (this.dataClassMeta.getFields().size() > 0) {
            content.append(" extends \n            ");
            content.append(Joiner.on("\n            & ").join(getStructuralTypingFields().stream()
                    .map(StructuralTypingField::getInterfaceName).collect(toList())));
        }

        content.append("> T as(T targetStart) {\n");

        content.append("        Object target = targetStart;\n");
        for (StructuralTypingField stf : getStructuralTypingFields()) {
            content.append("        target = ")
                    .append("((").append(stf.getInterfaceName()).append(") target).with").append(stf.getUppercaseName())
                    .append("(this.").append(stf.getGetterMethodName()).append("());\n");
        }
        content.append("        return (T) target;\n");

        content.append("    }\n\n");
    }

    private void contentAsClass(StringBuilder content) {
        content.append("    @SuppressWarnings(\"unchecked\")\n");
        content.append("    public <T");

        if (this.dataClassMeta.getFields().size() > 0) {
            content.append(" extends \n            ");
            content.append(Joiner.on("\n            & ").join(getStructuralTypingFields().stream()
                    .map(StructuralTypingField::getInterfaceName).collect(toList())));
        }

        content.append("> T as(Class<? extends T> targetClass) {\n");

        content.append("        try {\n");
        content.append("            Object target = targetClass.newInstance();\n");
        content.append("            return this.as((T) target);\n");
        content.append("        } catch (Exception e) {throw new RuntimeException(e);}\n");

        content.append("    }\n\n");
    }

    private void contentFrom(StringBuilder content) {
        content.append("    @SuppressWarnings(\"unchecked\")\n");
        content.append("    public static <T");

        if (this.dataClassMeta.getFields().size() > 0) {
            content.append(" extends \n            ");
            content.append(Joiner.on("\n            & ").join(getStructuralTypingFields().stream()
                    .map(StructuralTypingField::getInterfaceName).collect(toList())));
        }
        content.append("> ");
        content.append(this.getClassName()).append(" from(T source) {\n");

        content.append("        Object target = new ").append(this.getClassName()).append("();\n");
        for (StructuralTypingField stf : getStructuralTypingFields()) {
            content.append("        target = ")
                    .append("((").append(stf.getInterfaceName()).append(") target).with").append(stf.getUppercaseName())
                    .append("(source.").append(stf.getGetterMethodName()).append("());\n");
        }
        content.append("        return (").append(this.getClassName()).append(") target;\n");

        content.append("    }\n\n");
    }

    private void contentImplements(StringBuilder content) {
        if (getStructuralTypingFields().size() > 0) {
            content.append(", ");
        }
        content.append(
                Joiner.on(", ")
                        .join(getStructuralTypingFields().stream()
                                .map(StructuralTypingField::getInterfaceName).collect(toList())));

    }

    private void contentConstructors(StringBuilder content) {
        // empty constructor
        content.append("    public ").append(this.getClassName()).append("() {\n");
        for (DataClassMeta.DataClassFieldMeta field : this.dataClassMeta.getFields()) {
            content.append("        ")
                    .append("this.").append(field.getFieldName())
                    .append(" = null;\n");
        }
        content.append("    }\n\n");

        // constructor with field initializer
        if (this.dataClassMeta.getFields().size() > 0) {
            content.append("    private ").append(this.getClassName()).append("(\n");

            String arguments = Joiner.on(",\n").join(this.dataClassMeta.getFields().stream().map(field ->
                    "            "
                            + field.getFieldType().getCanonicalName()
                            + " "
                            + field.getFieldName())
                    .collect(toList()));
            content.append(arguments);
            content.append(") {\n");

            // assign member
            for (DataClassMeta.DataClassFieldMeta field : this.dataClassMeta.getFields()) {
                content.append("        ")
                        .append("this.").append(field.getFieldName())
                        .append(" = ")
                        .append(field.getFieldName())
                        .append(";\n");
            }

            content.append("    }\n\n");
        }

        // constructor from map
        content.append("    public ").append(this.getClassName()).append("(java.util.Map<String, Object> from) {\n");
        // assign member
        for (DataClassMeta.DataClassFieldMeta field : this.dataClassMeta.getFields()) {
            content.append("        ")
                    .append("this.").append(field.getFieldName())
                    .append(" = ")
                    .append("(").append(field.getFieldType().getCanonicalName()).append(") ")
                    .append("from.get(\"")
                    .append(field.getFieldName())
                    .append("\");\n");
        }
        content.append("    }\n");
    }

    private void contentStaticFieldName(StringBuilder content, DataClassMeta.DataClassFieldMeta field) {
        String staticName = field.getFieldName().toUpperCase();

        content.append("    public static final String COL_").append(staticName).append(" = ")
                .append("\"")
                .append(field.getFieldName())
                .append("\";\n\n");
    }

    private void contentField(StringBuilder content, DataClassMeta.DataClassFieldMeta field) {
        content.append("    public final ");
        content.append(field.getFieldType().getCanonicalName()).append(" ").append(field.getFieldName());
        content.append(";\n");
    }

    private void contentGetterSetterForField(StringBuilder content, DataClassMeta.DataClassFieldMeta field) {
        // Getter
        content.append("    public ");
        content.append(field.getFieldType().getCanonicalName());
        content.append(" ");
        boolean isBool = Boolean.class.isAssignableFrom(field.getFieldType());
        String prefix = isBool ? "is" : "get";
        content.append(prefix).append(firstCharUpperCase(field.getFieldName())).append("() {\n");
        content.append("        return this.").append(field.getFieldName()).append(";\n");
        content.append("    }\n\n");

        // Setter
        content.append("    public ").append(this.getClassName()).append(" ");
        content.append("with").append(firstCharUpperCase(field.getFieldName())).append("(");
        content.append(field.getFieldType().getCanonicalName());
        content.append(" ").append(field.getFieldName());
        content.append(") {\n");

        // setter body
        content.append("        return new ").append(this.getClassName()).append("(");
        content.append(constructorCallArgs);
        content.append(");\n");
        content.append("    }\n");

    }

}
