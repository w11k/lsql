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

    // Statement: queryParamsWithDot ----------------------------

    private final String sql_queryParamsWithDot = "select \n person1.* \n from person1 \n WHERE \n person1.id = /*=*/ 1 /**/;";

    /**    
     * select<br>
     * person1.*<br>
     * from person1<br>
     * WHERE<br>
     * person1.id = &#42;&#47;=&#47;&#42; 1 &#42;&#47;&#47;&#42;;<br>
    */
    public queryParamsWithDot queryParamsWithDot() {
        return new queryParamsWithDot();
    }

    public final class queryParamsWithDot extends com.w11k.lsql.TypedStatementQuery<QueryParamsWithDot> implements com.w11k.lsql.TableRow, Person1_Id_Number {

        public queryParamsWithDot() {
            super(lSql, sql_queryParamsWithDot);
            this.person1Id = null;
        }

        private queryParamsWithDot(
                java.lang.Number person1Id) {
            super(lSql, sql_queryParamsWithDot);
            this.person1Id = person1Id;
    }

        public queryParamsWithDot(java.util.Map<String, Object> from) {
            super(lSql, sql_queryParamsWithDot);
            this.person1Id = (java.lang.Number) from.get("person1_id");
        }

        public final java.lang.Number person1Id;

        public java.lang.Number getPerson1Id() {
            return this.person1Id;
        }

        public queryParamsWithDot withPerson1Id(java.lang.Number person1Id) {
            return new queryParamsWithDot(person1Id);
        }

        // class methods ----------

        public java.util.Map<String, Object> toMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
            map.put("person1_id", this.person1Id);
            return map;
        }

        protected QueryParamsWithDot createTypedRow(com.w11k.lsql.Row row) {
            return new QueryParamsWithDot(row);
        }

        protected java.util.Map<String, Object>  getQueryParameters() {
            return this.toMap();
        }

    }

    // Statement: changeYesno ----------------------------

    private final String sql_changeYesno = "UPDATE checks \n SET yesno = /*=*/ TRUE /**/ \n ;";

    /**    
     * UPDATE checks<br>
     * SET yesno = &#42;&#47;=&#47;&#42; TRUE &#42;&#47;&#47;&#42;<br>
     * ;<br>
    */
    public changeYesno changeYesno() {
        return new changeYesno();
    }

    public final class changeYesno extends com.w11k.lsql.TypedStatementCommand implements com.w11k.lsql.TableRow, Yesno_Object {

        public changeYesno() {
            super(lSql, sql_changeYesno);
            this.yesno = null;
        }

        private changeYesno(
                java.lang.Object yesno) {
            super(lSql, sql_changeYesno);
            this.yesno = yesno;
    }

        public changeYesno(java.util.Map<String, Object> from) {
            super(lSql, sql_changeYesno);
            this.yesno = (java.lang.Object) from.get("yesno");
        }

        public final java.lang.Object yesno;

        public java.lang.Object getYesno() {
            return this.yesno;
        }

        public changeYesno withYesno(java.lang.Object yesno) {
            return new changeYesno(yesno);
        }

        // class methods ----------

        public java.util.Map<String, Object> toMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
            map.put("yesno", this.yesno);
            return map;
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
