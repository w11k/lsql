package com.w11k.lsql.cli.java;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
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

    private int indent = 0;

    public DataClassExporter(JavaExporter javaExporter, DataClassMeta dataClassMeta, String classNameSuffix) {
        this.dataClassMeta = dataClassMeta;
        this.javaExporter = javaExporter;
        this.classNameSuffix = classNameSuffix;

        this.structuralTypingFields = createStructuralTypingFieldList();
        this.constructorCallArgs = Joiner.on(",").join(dataClassMeta.getFields().stream()
                .map(DataClassMeta.DataClassFieldMeta::getColumnJavaCodeName)
                .collect(toList()));
    }

    public DataClassMeta getDataClassMeta() {
        return dataClassMeta;
    }

    private List<StructuralTypingField> createStructuralTypingFieldList() {
        List<StructuralTypingField> list = Lists.newLinkedList();
        for (DataClassMeta.DataClassFieldMeta field : this.dataClassMeta.getFields()) {
            list.add(new StructuralTypingField(field.getColumnJavaCodeName(), field.getFieldType()));
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
        content.append("import ").append(stmtFieldsPackageName).append(".*;\n");
        content.append("import java.util.*;\n");
        content.append("\n");

        createClass(content);
    }

    public void createClass(StringBuilder content) {
        this.createClass(
                content,
                false,
                false,
                "",
                "");
    }

    public void createClass(StringBuilder content,
                            boolean skipStaticAndUtilElements,
                            boolean keepClassBodyOpen,
                            String extendsClause,
                            String constructorBody) {
        DataClassMeta dcm = this.dataClassMeta;

        content.append(indentString()).append("@SuppressWarnings({\"Duplicates\", \"WeakerAccess\"})\n");
        content.append(indentString()).append("public final class ").append(this.getClassName());
        content.append(extendsClause);
        content.append(" implements ").append(TableRow.class.getCanonicalName());
        contentImplements(content);
        content.append(" {\n\n");

        if (!skipStaticAndUtilElements) {
            content.append(indentString()).append("    // static methods ----------\n\n");

            // static from
            contentFrom(content);
        }

        // constructors
        content.append(indentString()).append("    // constructors ----------\n\n");
        contentConstructors(content, constructorBody);

        // Field instances and getter/setter
        content.append(indentString()).append("    // fields ----------\n\n");
        for (DataClassMeta.DataClassFieldMeta field : dcm.getFields()) {
//            addSeperator(content);
            if (!skipStaticAndUtilElements) {
                contentStaticFieldNames(content, field);
            }
            contentField(content, field);
            content.append("\n");
            contentGetterSetterForField(content, field);
        }

        content.append("\n");
        content.append(indentString()).append("    // class methods ----------\n\n");

        // StructuralTyping methods
        if (!skipStaticAndUtilElements) {
            contentAsInstance(content);
            contentAsClass(content);
        }

        // toInternalMap
        contentToInternalMap(content);
        contentToRow(content);

        content.append(indentString()).append("    // Object methods ----------\n\n");
        contentEquals(content);
        contentHash(content);
        contentToString(content);

        if (!keepClassBodyOpen) {
            content.append("}\n");
        }

    }

    private void contentEquals(StringBuilder content) {
        content.append(indentString()).append("    @Override\n");
        content.append(indentString()).append("    public boolean equals(Object o) {\n");
        content.append(indentString()).append("        if (this == o) return true;\n");
        content.append(indentString()).append("        if (o == null || getClass() != o.getClass()) return false;\n");
        content.append(indentString()).append("        ").append(this.getClassName()).append(" that = ")
                .append("(").append(this.getClassName()).append(") o;\n");

        if (this.dataClassMeta.getFields().size() == 0) {
            content.append(indentString()).append("        return true;\n");
        } else {
            content.append(indentString()).append("        return ");

            List<String> fieldEquals = this.dataClassMeta.getFields().stream()
                    .map(f -> "    Objects.equals(" + f.getColumnJavaCodeName() + ", that." + f.getColumnJavaCodeName() + ")")
                    .collect(toList());

            content.append(Joiner.on(" && \n" + indentString() + "        ").join(fieldEquals));
            content.append(";\n");
        }

        content.append(indentString()).append("    }\n\n");
    }

    private void contentHash(StringBuilder content) {
        content.append(indentString()).append("    @Override\n");
        content.append(indentString()).append("    public int hashCode() {\n");
        content.append(indentString()).append("        return Objects.hash(");

        if (this.dataClassMeta.getFields().size() == 0) {
            content.append("\"").append(this.getClassName()).append("\"");
        } else {
            List<String> fieldNames = this.dataClassMeta.getFields().stream()
                    .map(DataClassMeta.DataClassFieldMeta::getColumnJavaCodeName)
                    .collect(toList());

            content.append(Joiner.on(", ").join(fieldNames));
        }
        content.append(");\n");
        content.append(indentString()).append("    }\n\n");
    }

    private void contentToString(StringBuilder content) {
        content.append(indentString()).append("    @Override\n");
        content.append(indentString()).append("    public String toString() {\n");
        content.append(indentString()).append("        return \"").append(getClassName()).append("{\" + ");

        if (this.dataClassMeta.getFields().size() == 0) {
            content.append("\"\"");
        } else {
            List<String> fieldNames = this.dataClassMeta.getFields().stream()
                    .map(f -> "\"" + f.getColumnJavaCodeName() + "=\" + " + f.getColumnJavaCodeName())
                    .collect(toList());

            content.append(Joiner.on("\n" + this.indentString() + "            + \", \" + ").join(fieldNames));
        }
        content.append(" + \"}\";\n");
        content.append(indentString()).append("    }\n\n");
    }

    private void contentToInternalMap(StringBuilder content) {
        content.append(indentString()).append("    public java.util.Map<String, Object> toInternalMap() {\n");
        content.append(indentString()).append("        java.util.Map<String, Object> map = new java.util.HashMap<>();\n");

        for (DataClassMeta.DataClassFieldMeta field : this.dataClassMeta.getFields()) {
            content.append(indentString()).append("        ")
                    .append("map.put(\"").append(field.getColumnInternalSqlName()).append("\", this.").append(field.getColumnJavaCodeName()).append(");\n");
        }

        content.append(indentString()).append("        return map;\n");
        content.append(indentString()).append("    }\n\n");
    }

    private void contentToRow(StringBuilder content) {
        content.append(indentString()).append("    public java.util.Map<String, Object> toRowMap() {\n");
        content.append(indentString()).append("        java.util.Map<String, Object> map = new java.util.HashMap<>();\n");

        for (DataClassMeta.DataClassFieldMeta field : this.dataClassMeta.getFields()) {
            content.append(indentString()).append("        ")
                    .append("map.put(\"").append(field.getColumnRowKeyName()).append("\", this.").append(field.getColumnJavaCodeName()).append(");\n");
        }

        content.append(indentString()).append("        return map;\n");
        content.append(indentString()).append("    }\n\n");
    }

    private void contentAsInstance(StringBuilder content) {
        content.append(indentString()).append("    @SuppressWarnings(\"unchecked\")\n");
        content.append(indentString()).append("    public <T");

        if (this.dataClassMeta.getFields().size() > 0) {
            content.append(" extends \n            ");
            content.append(indentString()).append(Joiner.on("\n            & ").join(getStructuralTypingFields().stream()
                    .map(StructuralTypingField::getInterfaceName).collect(toList())));
        }

        content.append("> T as(T targetStart) {\n");

        content.append(indentString()).append("        Object target = targetStart;\n");
        for (StructuralTypingField stf : getStructuralTypingFields()) {
            content.append(indentString()).append("        target = ")
                    .append("((").append(stf.getInterfaceName()).append(") target).with").append(stf.getUppercaseName())
                    .append("(this.").append(stf.getGetterMethodName()).append("());\n");
        }
        content.append(indentString()).append("        return (T) target;\n");
        content.append(indentString()).append("    }\n\n");
    }

    private void contentAsClass(StringBuilder content) {
        content.append(indentString()).append("    @SuppressWarnings(\"unchecked\")\n");
        content.append(indentString()).append("    public <T");

        if (this.dataClassMeta.getFields().size() > 0) {
            content.append(" extends \n            ");
            content.append(Joiner.on("\n            & ").join(getStructuralTypingFields().stream()
                    .map(StructuralTypingField::getInterfaceName).collect(toList())));
        }

        content.append("> T as(Class<? extends T> targetClass) {\n");

        content.append(indentString()).append("        try {\n");
        content.append(indentString()).append("            Object target = targetClass.newInstance();\n");
        content.append(indentString()).append("            return this.as((T) target);\n");
        content.append(indentString()).append("        } catch (Exception e) {throw new RuntimeException(e);}\n");
        content.append(indentString()).append("    }\n\n");
    }

    private void contentFrom(StringBuilder content) {
        // from object
        content.append(indentString()).append("    @SuppressWarnings(\"unchecked\")\n");
        content.append(indentString()).append("    public static <T");

        if (this.dataClassMeta.getFields().size() > 0) {
            content.append(" extends \n            ");
            content.append(Joiner.on("\n            & ").join(getStructuralTypingFields().stream()
                    .map(StructuralTypingField::getInterfaceName).collect(toList())));
        }
        content.append("> ");
        content.append(this.getClassName()).append(" from(T source) {\n");

        content.append(indentString()).append("        Object target = new ").append(this.getClassName()).append("();\n");
        for (StructuralTypingField stf : getStructuralTypingFields()) {
            content.append(indentString()).append("        target = ")
                    .append("((").append(stf.getInterfaceName()).append(") target).with").append(stf.getUppercaseName())
                    .append("(source.").append(stf.getGetterMethodName()).append("());\n");
        }
        content.append(indentString()).append("        return (").append(this.getClassName()).append(") target;\n");
        content.append(indentString()).append("    }\n\n");

        // fromInternalMap
        this.addSuppressWarningsUnused(4, content);
        content.append(indentString())
                .append("    public static ")
                .append(this.getClassName())
                .append(" fromInternalMap(java.util.Map<String, Object> internalMap) {\n");

        // ... assign member
        content.append(indentString()).append("        ")
                .append("return new ").append(this.getClassName()).append("(");
        content.append(Joiner.on(", ").join(dataClassMeta.getFields().stream()
                .map(f -> "(" + f.getFieldType().getCanonicalName() + ") internalMap.get(\"" + f.getColumnInternalSqlName() + "\")")
                .collect(toList())));
        content.append(");\n");
        content.append(indentString()).append("    }\n\n");

        // fromRow
        this.addSuppressWarningsUnused(4, content);
        content.append(indentString())
                .append("    public static ")
                .append(this.getClassName())
                .append(" fromRow(java.util.Map<String, Object> map) {\n");

        // ... assign member
        content.append(indentString()).append("        ")
                .append("return new ").append(this.getClassName()).append("(");
        content.append(Joiner.on(", ").join(dataClassMeta.getFields().stream()
                .map(f -> "(" + f.getFieldType().getCanonicalName() + ") map.get(\"" + f.getColumnRowKeyName() + "\")")
                .collect(toList())));
        content.append(");\n");
        content.append(indentString()).append("    }\n\n");
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

    private void contentConstructors(StringBuilder content, String constructorBody) {
        // empty constructor
        content.append(indentString()).append("    @SuppressWarnings(\"ConstantConditions\")\n");
        content.append(indentString()).append("    public ").append(this.getClassName()).append("() {\n");
        if (!Strings.isNullOrEmpty(constructorBody)) {
            content.append(indentString()).append("        ").append(constructorBody).append("\n");
        }
        for (DataClassMeta.DataClassFieldMeta field : this.dataClassMeta.getFields()) {
            content.append(indentString()).append("        ")
                    .append("this.").append(field.getColumnJavaCodeName())
                    .append(" = null;\n");
        }
        content.append(indentString()).append("    }\n\n");

        // constructor with field initializer
        if (this.dataClassMeta.getFields().size() > 0) {
            content.append(indentString()).append("    @SuppressWarnings(\"NullableProblems\")\n");
            content.append(indentString()).append("    private ").append(this.getClassName()).append("(\n");
            String arguments = Joiner.on(",\n").join(this.dataClassMeta.getFields().stream().map(field ->
                    "            "
                            + field.getFieldType().getCanonicalName()
                            + " "
                            + field.getColumnJavaCodeName())
                    .collect(toList()));
            content.append(indentString()).append(arguments);
            content.append(") {\n");

            // assign member
            if (!Strings.isNullOrEmpty(constructorBody)) {
                content.append(indentString()).append("        ").append(constructorBody).append("\n");
            }
            for (DataClassMeta.DataClassFieldMeta field : this.dataClassMeta.getFields()) {
                content.append(indentString()).append("        ")
                        .append("this.").append(field.getColumnJavaCodeName())
                        .append(" = ")
                        .append(field.getColumnJavaCodeName())
                        .append(";\n");
            }

            content.append("    }\n\n");
        }
    }

    private void contentStaticFieldNames(StringBuilder content, DataClassMeta.DataClassFieldMeta field) {
        String staticName = field.getColumnInternalSqlName();

        this.addSuppressWarningsUnused(4, content);
        content.append(indentString()).append("    public static final String INTERNAL_FIELD_").append(staticName.toUpperCase())
                .append(" = ")
                .append("\"")
                .append(field.getColumnInternalSqlName())
                .append("\";\n\n");

        this.addSuppressWarningsUnused(4, content);
        content.append(indentString()).append("    public static final String ROW_KEY_").append(staticName.toUpperCase())
                .append(" = ")
                .append("\"")
                .append(field.getColumnRowKeyName())
                .append("\";\n\n");
    }

    private void addSuppressWarningsUnused(int indent, StringBuilder content) {
        content
                .append(this.indentString())
                .append(Strings.repeat(" ", indent))
                .append("@SuppressWarnings(\"unused\")\n");
    }

    private void contentField(StringBuilder content, DataClassMeta.DataClassFieldMeta field) {
        content.append(indentString()).append("    ").append(this.getNullableOrNonnullAnnotation(field)).append("public final ");
        content.append(field.getFieldType().getCanonicalName()).append(" ").append(field.getColumnJavaCodeName());
        content.append(";\n");
    }

    private void contentGetterSetterForField(StringBuilder content, DataClassMeta.DataClassFieldMeta field) {
        // Getter
        content.append(indentString()).append("    ").append(this.getNullableOrNonnullAnnotation(field)).append("public ");
        content.append(field.getFieldType().getCanonicalName());
        content.append(" ");
        boolean isBool = Boolean.class.isAssignableFrom(field.getFieldType());
        String prefix = isBool ? "is" : "get";
        content.append(prefix).append(firstCharUpperCase(field.getColumnJavaCodeName())).append("() {\n");
        content.append(indentString()).append("        return this.").append(field.getColumnJavaCodeName()).append(";\n");
        content.append(indentString()).append("    }\n\n");

        // Setter
        content.append(indentString()).append("    public ").append(this.getClassName()).append(" ");
        content.append("with").append(firstCharUpperCase(field.getColumnJavaCodeName())).append("(");
        content.append(this.getNullableOrNonnullAnnotation(field));
        content.append(field.getFieldType().getCanonicalName());
        content.append(" ").append(field.getColumnJavaCodeName());
        content.append(") {\n");

        // setter body
        content.append(indentString()).append("        return new ").append(this.getClassName()).append("(");
        content.append(constructorCallArgs);
        content.append(");\n");
        content.append(indentString()).append("    }\n");
    }

    private String getNullableOrNonnullAnnotation(DataClassMeta.DataClassFieldMeta field) {
        if (field.isNullable()) {
            return "@javax.annotation.Nullable ";
        } else {
            return "@javax.annotation.Nonnull ";
        }
    }

    private String indentString() {
        return Strings.repeat(" ", this.indent);
    }

    public int getIndent() {
        return indent;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }
}
