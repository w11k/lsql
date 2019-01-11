package com.w11k.lsql.cli.java;

import com.google.common.collect.Lists;
import com.w11k.lsql.Row;
import com.w11k.lsql.TypedStatementCommand;
import com.w11k.lsql.TypedStatementQuery;

import java.util.List;

import static com.w11k.lsql.cli.CodeGenUtils.*;

public final class TypedStatementExporter {

    private final String statementClassNameSuffix = "Query";

    private final JavaExporter javaExporter;

    private final TypedStatementMeta typedStatementMeta;

    public TypedStatementExporter(JavaExporter javaExporter,
                                  TypedStatementMeta typedStatementMeta) {
        this.javaExporter = javaExporter;
        this.typedStatementMeta = typedStatementMeta;
    }

    public List<DataClassExporter> export(StringBuilder content) {
        List<DataClassExporter> dataClassExportersForQueryParams = Lists.newLinkedList();

        String statementName = this.typedStatementMeta.getStatement().getStatementName();
        String statementQueryClassName = statementName + this.statementClassNameSuffix;

        boolean isVoid = this.typedStatementMeta.getStatement().getTypeAnnotation().toLowerCase().equals("void");
        String rowClassName = !isVoid ? firstCharUpperCase(statementName) : Void.class.getCanonicalName();

        // SQL string for query
        content.append("    private final String sql_")
                .append(statementName).append(" = ")
                .append("\"").append(escapeSqlStringForJavaSourceFile(this.typedStatementMeta.getStatement().getSqlString())).append("\";\n\n");

        // factory function for query
        content.append("    /**");
        content.append("    ").append(escapeSqlStringForJavaDoc(this.typedStatementMeta.getStatement().getSqlString()));
        content.append("    */\n");
        content.append("    public ").append(statementQueryClassName).append(" ").append(statementName).append("() {\n")
                .append("        return ").append("new ").append(statementQueryClassName).append("();\n")
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
        String constructorBody = "super(lSql, sql_" + statementName + ");";

        DataClassMeta dcm = new DataClassMeta(
                statementName, "");

        // query parameter
        for (String parameterName : this.typedStatementMeta.getParameters().keySet()) {
            Class<?> paramType = this.typedStatementMeta.getParameters().get(parameterName);

            String saveParameterName = parameterName.replace(".", "__");
            String javaName = this.javaExporter.getlSql().identifierSqlToJava(saveParameterName);
            javaName = getJavaCodeName(javaName, false, false);
            dcm.addField(javaName, parameterName, paramType);
        }

        DataClassExporter dcExporter = new DataClassExporter(this.javaExporter, dcm, this.statementClassNameSuffix);
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
                    .append("            return ").append(rowClassName)
                    .append(".fromInternalMap")
                    .append("(row);\n");
            content.append("        }\n\n");
        }

        // getQueryParameters
        content.append("        protected java.util.Map<String, Object> ");
        content.append(" getQueryParameters() {\n")
                .append("            return this.toInternalMap();\n");
        content.append("        }\n\n");

        // statement name
        content.append("        public String getStatementFileName() {\n");
        content.append("            return \"")
                .append(this.typedStatementMeta.getSourceFileName())
                .append("\";\n");
        content.append("        }\n\n");
        content.append("        public String getStatementName() {\n");
        content.append("            return \"")
                .append(this.typedStatementMeta.getStatement().getStatementName())
                .append("\";\n");
        content.append("        }\n\n");

        // end
        content.append("    }\n\n");

        return dataClassExportersForQueryParams;
    }


}
