package com.w11k.lsql.cli.java;

import com.w11k.lsql.LSql;
import com.w11k.lsql.Row;
import com.w11k.lsql.TypedStatementQuery;

import static com.w11k.lsql.cli.CodeGenUtils.lowerCamelToUpperCamel;

public final class TypedStatementExporter {

    private final TypedStatementMeta typedStatementMeta;
    private final StatementFileExporter statementFileExporter;

    public TypedStatementExporter(TypedStatementMeta typedStatementMeta, StatementFileExporter statementFileExporter) {
        this.typedStatementMeta = typedStatementMeta;
        this.statementFileExporter = statementFileExporter;
    }

    public void export(StringBuilder content) {

//        String originalSqlString = this.typedStatementMeta.getStatement().getOriginalSqlString();
//        originalSqlString = originalSqlString
//                .replaceAll("/\\*", "/°")
//                .replaceAll("\\*/", "°/");
//        content.append("/**\n");
//        String[] lines = originalSqlString.split("\n");
//        for (String line : lines) {
//            content.append(" * ").append(line).append("\n");
//        }
//        content.append(" */\n");

        String queryClassName = this.typedStatementMeta.getStatement().getStatementName();
        String rowClassName = lowerCamelToUpperCamel(queryClassName + "Row");

        // query class
        content.append("    public class ")
                .append(queryClassName)
                .append(" extends ")
                .append(TypedStatementQuery.class.getCanonicalName())
                .append("<")
                .append(rowClassName)
                .append("> ")
                .append("{\n\n");

        for (String parameterName : this.typedStatementMeta.getParameters().keySet()) {
            Class<?> paramType = this.typedStatementMeta.getParameters().get(parameterName);

            String setterName = this.typedStatementMeta.getlSql().identifierSqlToJava(parameterName);
            content.append("        public ").append(queryClassName).append(" ").append(setterName)
                    .append("(").append(paramType.getCanonicalName()).append(" value) {\n");

            content.append("            ")
                    .append("parameterValues.put(\"").append(parameterName).append("\", value);\n");

            content.append("            return this;\n        }\n\n");
        }

        // constructor
        content.append("        private ").append(queryClassName).append("(")
                .append(LSql.class.getCanonicalName()).append(" lSql) {\n")
                .append("            super(lSql,")
                .append("\"").append(this.typedStatementMeta.getStatement().getSqlString()).append("\")")
                .append(";\n")
                .append("        }\n\n");

        // createTypedRow
        content.append("        protected ").append(rowClassName);
        content.append(" createTypedRow(")
                .append(Row.class.getCanonicalName())
                .append(" row) {\n")
                .append("            return new ").append(rowClassName)
                .append("(row);\n");
        content.append("        }\n\n");

        content.append("    }\n\n");

        // new query
        content.append("    public ").append(queryClassName).append(" ").append(queryClassName).append("() {\n")
                .append("        return new ").append(queryClassName).append("(this.lSql);\n")
                .append("    }\n\n");


    }


}
