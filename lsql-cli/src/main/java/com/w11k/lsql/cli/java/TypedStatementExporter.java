package com.w11k.lsql.cli.java;

import com.w11k.lsql.TypedStatementQuery;

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


        // query class
        content.append("    public class ")
                .append(this.typedStatementMeta.getStatement().getStatementName())
                .append(" extends ")
                .append(TypedStatementQuery.class.getCanonicalName())
                .append("<")
                .append(this.statementFileExporter.getPackageName()).append(".")
//                .append(this.typedStatementMeta.getClassName()).append("Row")
                .append("> ")
                .append("{\n\n");

//        for (String parameterName : this.typedStatementMeta.getParameters().keySet()) {
//            Class<?> paramType = this.typedStatementMeta.getParameters().get(parameterName);
//
//            content.append("        public Query ").append(parameterName)
//                    .append("(").append(paramType.getCanonicalName()).append(" value) {\n");
//
//            content.append("            ")
//                    .append("parameterValues.put(\"").append(parameterName).append("\", value);\n");
//
//            content.append("            return this;\n        }\n\n");
//        }

        // constructor
//        content.append("        private Query(")
//                .append(LSql.class.getCanonicalName()).append(" lSql) {\n")
//                .append("            super(lSql,")
//                .append("\"").append(this.typedStatementMeta.getStatement().getSqlString()).append("\")")
//                .append(";\n")
//                .append("        }\n\n");

        // createTypedRow
//        content.append("        protected ").append(this.typedStatementMeta.getClassName()).append("Row ");
//        content.append("createTypedRow(")
//                .append(Row.class.getCanonicalName())
//                .append(" row) {\n")
//                .append("            return new ").append(this.typedStatementMeta.getClassName()).append("Row")
//                .append("(row);\n");
//        content.append("        }\n\n");
//
//        content.append("    }\n\n");

        // new query
//        content.append("    public Query newQuery() {\n")
//                .append("        return new Query(this.lSql);\n")
//                .append("    }\n\n");


    }


}
