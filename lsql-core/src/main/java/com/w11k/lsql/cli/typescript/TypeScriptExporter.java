package com.w11k.lsql.cli.typescript;

import com.w11k.lsql.cli.CodeGenUtils;
import com.w11k.lsql.cli.java.DataClassMeta;

import java.io.File;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.w11k.lsql.cli.CodeGenUtils.firstCharUpperCase;

public class TypeScriptExporter {

    private static final List<String> RESERVED_TS_NAMES = newArrayList(
            "public",
            "private",
            "protected"
    );
    private final List<DataClassMeta> dataClassMetaList;

    private File outputDir = null;

    public TypeScriptExporter(List<DataClassMeta> dataClassMetaList) {
        this.dataClassMetaList = dataClassMetaList;
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    public void export() {
        //noinspection ResultOfMethodCallIgnored
        this.outputDir.mkdirs();
        this.exportClasses();
    }

    private void exportClasses() {
        for (DataClassMeta dcm : this.dataClassMetaList) {
            File output = new File(this.outputDir, createNamespaceNameFromPackage(dcm) + ".ts");
            StringBuilder content = new StringBuilder();
            this.exportDataClassRow(content, dcm);
            this.exportDataClassRowMap(content, dcm);
            CodeGenUtils.writeContent(content.toString(), output);
        }
    }

    private void exportDataClassRow(StringBuilder content, DataClassMeta dcMeta) {
        content.append("export type ").append(firstCharUpperCase(dcMeta.getClassName() + "_Row = {\n"));
        this.exportFields(content, dcMeta);
        content.append("};\n\n");
    }

    private void exportDataClassRowMap(StringBuilder content, DataClassMeta dcMeta) {
        content.append("export namespace ").append(createNamespaceNameFromPackage(dcMeta)).append(" {\n");
        content.append("    export interface ").append(firstCharUpperCase(dcMeta.getClassName() + "_RowMap")).append(" {\n");
        for (DataClassMeta.DataClassFieldMeta field : dcMeta.getFields()) {
            Class<?> javaType = field.getFieldType();
            String tsTypeName = this.getTypeScriptTypeNameForJavaType(javaType);
            content.append("        ").append(field.getColumnRowKeyName());
            if (field.isNullable()) {
                content.append("?");
            }
            content.append(": ").append(tsTypeName).append(";\n");
        }
        content.append("    }\n");
        content.append("}\n\n");
    }

    private String createNamespaceNameFromPackage(DataClassMeta dcMeta) {
        return mayPrefixSchemaName(dcMeta).replace('.', '_');
    }

    private String mayPrefixSchemaName(DataClassMeta dcMeta) {
        String schema = dcMeta.getPackageName();
        return RESERVED_TS_NAMES.contains(schema) ? schema + "_" : schema;
    }

    private void exportFields(StringBuilder content, DataClassMeta dcMeta) {
        for (DataClassMeta.DataClassFieldMeta field : dcMeta.getFields()) {
            this.exportField(content, field);
        }
    }

    private void exportField(StringBuilder content, DataClassMeta.DataClassFieldMeta field) {
        Class<?> javaType = field.getFieldType();
        String tsTypeName = this.getTypeScriptTypeNameForJavaType(javaType);
        content.append("        ").append(field.getColumnJavaCodeName());
        if (field.isNullable()) {
            content.append("?");
        }
        content.append(": ").append(tsTypeName).append(";\n");
    }

    private String getTypeScriptTypeNameForJavaType(Class<?> javaType) {
        if (String.class.isAssignableFrom(javaType)) {
            return "string";
        } else if (Number.class.isAssignableFrom(javaType)) {
            return "number";
        } else if (Boolean.class.isAssignableFrom(javaType)) {
            return "boolean";
        }

        return "any";
    }

}
