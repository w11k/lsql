package com.w11k.lsql.cli.java;

import com.w11k.lsql.LSql;
import com.w11k.lsql.Row;
import com.w11k.lsql.TypedStatementCommand;
import com.w11k.lsql.TypedStatementQuery;

import static com.w11k.lsql.cli.CodeGenUtils.firstCharUpperCase;
import static com.w11k.lsql.cli.CodeGenUtils.escapeSqlStringForJavaSourceFile;

public final class TypedStatementExporter {

    private final TypedStatementMeta typedStatementMeta;

    private final StatementFileExporter statementFileExporter;

    public TypedStatementExporter(TypedStatementMeta typedStatementMeta, StatementFileExporter statementFileExporter) {
        this.typedStatementMeta = typedStatementMeta;
        this.statementFileExporter = statementFileExporter;
    }

    public void export(StringBuilder content) {
        String queryClassName = this.typedStatementMeta.getStatement().getStatementName();

        boolean isVoid = this.typedStatementMeta.getStatement().getTypeAnnotation().toLowerCase().equals("void");
        String rowClassName = !isVoid ? firstCharUpperCase(queryClassName + "Row") : Void.class.getCanonicalName();

        // query class
        content.append("    public class ")
                .append(queryClassName)
                .append(" extends ");

        if (!isVoid) {
            content.append(TypedStatementQuery.class.getCanonicalName())
                    .append("<")
                    .append(rowClassName)
                    .append(">");
        } else {
            content.append(TypedStatementCommand.class.getCanonicalName());
        }

        content.append(" {\n\n");

        for (String parameterName : this.typedStatementMeta.getParameters().keySet()) {
            Class<?> paramType = this.typedStatementMeta.getParameters().get(parameterName);

            String setterName = parameterName.replaceAll("\\.", "_"); 
            setterName = this.typedStatementMeta.getlSql().identifierSqlToJava(setterName);

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
                .append("\"").append(escapeSqlStringForJavaSourceFile(this.typedStatementMeta.getStatement().getSqlString())).append("\")")
                .append(";\n")
                .append("        }\n\n");

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

        content.append("    }\n\n");

        // new query
        content.append("    public ").append(queryClassName).append(" ").append(queryClassName).append("() {\n")
                .append("        return ").append("new ").append(queryClassName).append("(this.lSql);\n")
                .append("    }\n\n");


    }


}
