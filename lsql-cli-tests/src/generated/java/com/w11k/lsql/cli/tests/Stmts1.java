package com.w11k.lsql.cli.tests;

import com.w11k.lsql.cli.tests.structural_fields.*;

import com.w11k.lsql.cli.tests.stmts1.*;

public class Stmts1 {

    // Statement: loadAllPersonsEscaped2 ----------------------------

    private final String sql_loadAllPersonsEscaped2 = "select \n person1.id as \"theId\" /*:int*/ \n from person1;";

    /**    
     * select<br>
     * person1.id as "theId" &#42;&#47;:int&#47;&#42;<br>
     * from person1;<br>
    */
    public loadAllPersonsEscaped2 loadAllPersonsEscaped2() {
        return new loadAllPersonsEscaped2();
    }

    public final class loadAllPersonsEscaped2 extends com.w11k.lsql.TypedStatementQuery<LoadAllPersonsEscaped2> implements com.w11k.lsql.TableRow {

        public loadAllPersonsEscaped2() {
            super(lSql, sql_loadAllPersonsEscaped2);
        }

        public loadAllPersonsEscaped2(java.util.Map<String, Object> from) {
            super(lSql, sql_loadAllPersonsEscaped2);
        }


        // class methods ----------

        public java.util.Map<String, Object> toMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
            return map;
        }

        protected LoadAllPersonsEscaped2 createTypedRow(com.w11k.lsql.Row row) {
            return new LoadAllPersonsEscaped2(row);
        }

        protected java.util.Map<String, Object>  getQueryParameters() {
            return this.toMap();
        }

    }

    // Statement: loadAllPersons ----------------------------

    private final String sql_loadAllPersons = "select * from person1;";

    /**    
     * select * from person1;<br>
    */
    public loadAllPersons loadAllPersons() {
        return new loadAllPersons();
    }

    public final class loadAllPersons extends com.w11k.lsql.TypedStatementQuery<LoadAllPersons> implements com.w11k.lsql.TableRow {

        public loadAllPersons() {
            super(lSql, sql_loadAllPersons);
        }

        public loadAllPersons(java.util.Map<String, Object> from) {
            super(lSql, sql_loadAllPersons);
        }


        // class methods ----------

        public java.util.Map<String, Object> toMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
            return map;
        }

        protected LoadAllPersons createTypedRow(com.w11k.lsql.Row row) {
            return new LoadAllPersons(row);
        }

        protected java.util.Map<String, Object>  getQueryParameters() {
            return this.toMap();
        }

    }

    // Statement: loadAllPersonsEscaped1 ----------------------------

    private final String sql_loadAllPersonsEscaped1 = "select \n person1.id as theId /*:int*/ \n from person1;";

    /**    
     * select<br>
     * person1.id as theId &#42;&#47;:int&#47;&#42;<br>
     * from person1;<br>
    */
    public loadAllPersonsEscaped1 loadAllPersonsEscaped1() {
        return new loadAllPersonsEscaped1();
    }

    public final class loadAllPersonsEscaped1 extends com.w11k.lsql.TypedStatementQuery<LoadAllPersonsEscaped1> implements com.w11k.lsql.TableRow {

        public loadAllPersonsEscaped1() {
            super(lSql, sql_loadAllPersonsEscaped1);
        }

        public loadAllPersonsEscaped1(java.util.Map<String, Object> from) {
            super(lSql, sql_loadAllPersonsEscaped1);
        }


        // class methods ----------

        public java.util.Map<String, Object> toMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
            return map;
        }

        protected LoadAllPersonsEscaped1 createTypedRow(com.w11k.lsql.Row row) {
            return new LoadAllPersonsEscaped1(row);
        }

        protected java.util.Map<String, Object>  getQueryParameters() {
            return this.toMap();
        }

    }

    // Statement: keepUnderscoreForCamelCase ----------------------------

    private final String sql_keepUnderscoreForCamelCase = "select \n person1.id as \"a_field\" /*:int*/, \n person1.first_name as \"aField\" /*:string*/ \n from person1;";

    /**    
     * select<br>
     * person1.id as "a_field" &#42;&#47;:int&#47;&#42;,<br>
     * person1.first_name as "aField" &#42;&#47;:string&#47;&#42;<br>
     * from person1;<br>
    */
    public keepUnderscoreForCamelCase keepUnderscoreForCamelCase() {
        return new keepUnderscoreForCamelCase();
    }

    public final class keepUnderscoreForCamelCase extends com.w11k.lsql.TypedStatementQuery<KeepUnderscoreForCamelCase> implements com.w11k.lsql.TableRow {

        public keepUnderscoreForCamelCase() {
            super(lSql, sql_keepUnderscoreForCamelCase);
        }

        public keepUnderscoreForCamelCase(java.util.Map<String, Object> from) {
            super(lSql, sql_keepUnderscoreForCamelCase);
        }


        // class methods ----------

        public java.util.Map<String, Object> toMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
            return map;
        }

        protected KeepUnderscoreForCamelCase createTypedRow(com.w11k.lsql.Row row) {
            return new KeepUnderscoreForCamelCase(row);
        }

        protected java.util.Map<String, Object>  getQueryParameters() {
            return this.toMap();
        }

    }

    private final com.w11k.lsql.LSql lSql;

    @com.google.inject.Inject
    public Stmts1(com.w11k.lsql.LSql lSql) {
        this.lSql = lSql;
    }

}
