package com.w11k.lsql.cli.tests.subdir;

public class Stmts2 {

    public class loadPersonsByAgeAndFirstName extends com.w11k.lsql.TypedStatementQuery<LoadPersonsByAgeAndFirstNameRow> {

        public loadPersonsByAgeAndFirstName firstName(java.lang.String value) {
            parameterValues.put("first_name", value);
            return this;
        }

        public loadPersonsByAgeAndFirstName age(java.lang.Number value) {
            parameterValues.put("age", value);
            return this;
        }

        private loadPersonsByAgeAndFirstName(com.w11k.lsql.LSql lSql) {
            super(lSql,"select \n * \n from person2 \n where \n age = /*=*/ 100 /**/ \n -- test comment embedded \n and first_name = /*: string =*/ 'name' /**/ \n ;");
        }

        protected LoadPersonsByAgeAndFirstNameRow createTypedRow(com.w11k.lsql.Row row) {
            return new LoadPersonsByAgeAndFirstNameRow(row);
        }

    }

    public loadPersonsByAgeAndFirstName loadPersonsByAgeAndFirstName() {
        return new loadPersonsByAgeAndFirstName(this.lSql);
    }

    public class deletePersonByFirstName extends com.w11k.lsql.TypedStatementCommand {

        public deletePersonByFirstName firstName(java.lang.String value) {
            parameterValues.put("first_name", value);
            return this;
        }

        private deletePersonByFirstName(com.w11k.lsql.LSql lSql) {
            super(lSql,"delete from person2 where first_name = /*=*/ 'name' /**/;");
        }

    }

    public deletePersonByFirstName deletePersonByFirstName() {
        return new deletePersonByFirstName(this.lSql);
    }

    private final com.w11k.lsql.LSql lSql;

    @com.google.inject.Inject
    public Stmts2(com.w11k.lsql.LSql lSql) {
        this.lSql = lSql;
    }

}
