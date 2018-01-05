package com.w11k.lsql.cli.tests.cli_tests_tests;

public class Stmts1 {

    public class loadAllPersonsEscaped2 extends com.w11k.lsql.TypedStatementQuery<LoadAllPersonsEscaped2Row> {

        private loadAllPersonsEscaped2(com.w11k.lsql.LSql lSql) {
            super(lSql,"select \n person1.id as \"theId\" /*:int*/ \n from person1;");
        }

        protected LoadAllPersonsEscaped2Row createTypedRow(com.w11k.lsql.Row row) {
            return new LoadAllPersonsEscaped2Row(row);
        }

    }

    public loadAllPersonsEscaped2 loadAllPersonsEscaped2() {
        return new loadAllPersonsEscaped2(this.lSql);
    }

    public class loadAllPersons extends com.w11k.lsql.TypedStatementQuery<LoadAllPersonsRow> {

        private loadAllPersons(com.w11k.lsql.LSql lSql) {
            super(lSql,"select * from person1;");
        }

        protected LoadAllPersonsRow createTypedRow(com.w11k.lsql.Row row) {
            return new LoadAllPersonsRow(row);
        }

    }

    public loadAllPersons loadAllPersons() {
        return new loadAllPersons(this.lSql);
    }

    public class loadAllPersonsEscaped1 extends com.w11k.lsql.TypedStatementQuery<LoadAllPersonsEscaped1Row> {

        private loadAllPersonsEscaped1(com.w11k.lsql.LSql lSql) {
            super(lSql,"select \n person1.id as theId /*:int*/ \n from person1;");
        }

        protected LoadAllPersonsEscaped1Row createTypedRow(com.w11k.lsql.Row row) {
            return new LoadAllPersonsEscaped1Row(row);
        }

    }

    public loadAllPersonsEscaped1 loadAllPersonsEscaped1() {
        return new loadAllPersonsEscaped1(this.lSql);
    }

    private final com.w11k.lsql.LSql lSql;

    @com.google.inject.Inject
    public Stmts1(com.w11k.lsql.LSql lSql) {
        this.lSql = lSql;
    }

}
