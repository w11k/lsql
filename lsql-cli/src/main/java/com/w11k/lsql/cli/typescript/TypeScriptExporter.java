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
        StringBuilder content = new StringBuilder();
        this.exportClasses(content);

        //noinspection ResultOfMethodCallIgnored
        this.outputDir.mkdirs();
        File output = new File(this.outputDir, "lsql.ts");
        CodeGenUtils.writeContent(content.toString(), output);
    }

    private void exportClasses(StringBuilder content) {
        for (DataClassMeta dcm : this.dataClassMetaList) {
            this.exportDataClass(content, dcm);
        }
    }

    private void exportDataClass(StringBuilder content, DataClassMeta dcMeta) {
        content.append("export namespace ").append(createNamespaceNameFromPackage(dcMeta)).append(" {\n");
        content.append("    export interface ").append(firstCharUpperCase(dcMeta.getClassName())).append(" {\n");
        this.exportFields(content, dcMeta);
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
        content.append("        ").append(field.getFieldName());
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
