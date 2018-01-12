package com.w11k.lsql.cli.java;

import com.google.common.collect.Lists;
import com.w11k.lsql.Row;
import com.w11k.lsql.TypedStatementCommand;
import com.w11k.lsql.TypedStatementQuery;

import java.util.List;

import static com.w11k.lsql.cli.CodeGenUtils.*;

public final class TypedStatementExporter {

    private final JavaExporter javaExporter;

    private final TypedStatementMeta typedStatementMeta;

    public TypedStatementExporter(JavaExporter javaExporter,
                                  TypedStatementMeta typedStatementMeta) {
        this.javaExporter = javaExporter;
        this.typedStatementMeta = typedStatementMeta;
    }

    public List<DataClassExporter> export(StringBuilder content) {
        List<DataClassExporter> dataClassExportersForQueryParams = Lists.newLinkedList();

        String queryClassName = this.typedStatementMeta.getStatement().getStatementName();

        boolean isVoid = this.typedStatementMeta.getStatement().getTypeAnnotation().toLowerCase().equals("void");
        String rowClassName = !isVoid ? firstCharUpperCase(queryClassName) : Void.class.getCanonicalName();

        // SQL string for query
        content.append("    private final String sql_")
                .append(queryClassName).append(" = ")
                .append("\"").append(escapeSqlStringForJavaSourceFile(this.typedStatementMeta.getStatement().getSqlString())).append("\";\n\n");

        // create query
        content.append("    /**");
        content.append("    ").append(escapeSqlStringForJavaDoc(this.typedStatementMeta.getStatement().getSqlString()));
        content.append("    */\n");
        content.append("    public ").append(queryClassName).append(" ").append(queryClassName).append("() {\n")
                .append("        return ").append("new ").append(queryClassName).append("();\n")
                .append("    }\n\n");

        // extends clause
        String extendsClause = " extends ";
        if (!isVoid) {
            extendsClause += TypedStatementQuery.class.getCanonicalName();
            extendsClause += "<";
            extendsClause += rowClassName;
            extendsClause += ">";
        } else {
            extendsClause += TypedStatementCommand.class.getCanonicalName();
        }

        // constructor body
        String constructorBody = "super(lSql, sql_" + queryClassName + ");";

        DataClassMeta dcm = new DataClassMeta(queryClassName, "");

        // fields
        for (String parameterName : this.typedStatementMeta.getParameters().keySet()) {
            Class<?> paramType = this.typedStatementMeta.getParameters().get(parameterName);

            String saveParameterName = parameterName.replace(".", "_");
            String javaName = this.javaExporter.getlSql().identifierSqlToJava(saveParameterName);
            dcm.addField(javaName, parameterName, paramType);
        }

        DataClassExporter dcExporter = new DataClassExporter(this.javaExporter, dcm, "");
        dataClassExportersForQueryParams.add(dcExporter);
        dcExporter.setIndent(4);
        dcExporter.createClass(
                content,
                true,
                true,
                extendsClause,
                constructorBody
        );

        // createTypedRow
        if (!isVoid) {
            content.append("        protected ").append(rowClassName);
            content.append(" createTypedRow(")
                    .append(Row.class.getCanonicalName())
                    .append(" row) {\n")
                    .append("            return new ").append(rowClassName)
                    .append("(row);\n");
            content.append("        }\n\n");
        }

        // getQueryParameters
        content.append("        protected java.util.Map<String, Object> ");
        content.append(" getQueryParameters() {\n")
                .append("            return this.toMap();\n");
        content.append("        }\n\n");

        content.append("    }\n\n");

        return dataClassExportersForQueryParams;
    }


}
