package com.w11k.lsql.cli.tests;

import com.w11k.lsql.cli.tests.structural_fields.*;
import com.w11k.lsql.cli.tests.stmts1.*;
import java.util.*;

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

    @SuppressWarnings({"Duplicates", "WeakerAccess"})
    public final class loadAllPersonsEscaped2 extends com.w11k.lsql.TypedStatementQuery<LoadAllPersonsEscaped2> implements com.w11k.lsql.TableRow {

        // constructors ----------

        @SuppressWarnings("ConstantConditions")
        public loadAllPersonsEscaped2() {
            super(lSql, sql_loadAllPersonsEscaped2);
        }

        @SuppressWarnings("unused")
        public loadAllPersonsEscaped2(java.util.Map<String, Object> from) {
            super(lSql, sql_loadAllPersonsEscaped2);
        }

        // fields ----------


        // class methods ----------

        public java.util.Map<String, Object> toMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            return map;
        }

        // Object methods ----------

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            loadAllPersonsEscaped2 that = (loadAllPersonsEscaped2) o;
            return true;
        }

        @Override
        public int hashCode() {
            return Objects.hash("loadAllPersonsEscaped2");
        }

        @Override
        public String toString() {
            return "loadAllPersonsEscaped2{" + "" + "}";
        }

        protected LoadAllPersonsEscaped2 createTypedRow(com.w11k.lsql.Row row) {
            return new LoadAllPersonsEscaped2(row);
        }

        protected java.util.Map<String, Object>  getQueryParameters() {
            return this.toMap();
        }

        public String getStatementFileName() {
            return "Stmts1.sql";
        }

        public String getStatementName() {
            return "loadAllPersonsEscaped2";
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

    @SuppressWarnings({"Duplicates", "WeakerAccess"})
    public final class loadAllPersons extends com.w11k.lsql.TypedStatementQuery<LoadAllPersons> implements com.w11k.lsql.TableRow {

        // constructors ----------

        @SuppressWarnings("ConstantConditions")
        public loadAllPersons() {
            super(lSql, sql_loadAllPersons);
        }

        @SuppressWarnings("unused")
        public loadAllPersons(java.util.Map<String, Object> from) {
            super(lSql, sql_loadAllPersons);
        }

        // fields ----------


        // class methods ----------

        public java.util.Map<String, Object> toMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            return map;
        }

        // Object methods ----------

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            loadAllPersons that = (loadAllPersons) o;
            return true;
        }

        @Override
        public int hashCode() {
            return Objects.hash("loadAllPersons");
        }

        @Override
        public String toString() {
            return "loadAllPersons{" + "" + "}";
        }

        protected LoadAllPersons createTypedRow(com.w11k.lsql.Row row) {
            return new LoadAllPersons(row);
        }

        protected java.util.Map<String, Object>  getQueryParameters() {
            return this.toMap();
        }

        public String getStatementFileName() {
            return "Stmts1.sql";
        }

        public String getStatementName() {
            return "loadAllPersons";
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

    @SuppressWarnings({"Duplicates", "WeakerAccess"})
    public final class loadAllPersonsEscaped1 extends com.w11k.lsql.TypedStatementQuery<LoadAllPersonsEscaped1> implements com.w11k.lsql.TableRow {

        // constructors ----------

        @SuppressWarnings("ConstantConditions")
        public loadAllPersonsEscaped1() {
            super(lSql, sql_loadAllPersonsEscaped1);
        }

        @SuppressWarnings("unused")
        public loadAllPersonsEscaped1(java.util.Map<String, Object> from) {
            super(lSql, sql_loadAllPersonsEscaped1);
        }

        // fields ----------


        // class methods ----------

        public java.util.Map<String, Object> toMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            return map;
        }

        // Object methods ----------

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            loadAllPersonsEscaped1 that = (loadAllPersonsEscaped1) o;
            return true;
        }

        @Override
        public int hashCode() {
            return Objects.hash("loadAllPersonsEscaped1");
        }

        @Override
        public String toString() {
            return "loadAllPersonsEscaped1{" + "" + "}";
        }

        protected LoadAllPersonsEscaped1 createTypedRow(com.w11k.lsql.Row row) {
            return new LoadAllPersonsEscaped1(row);
        }

        protected java.util.Map<String, Object>  getQueryParameters() {
            return this.toMap();
        }

        public String getStatementFileName() {
            return "Stmts1.sql";
        }

        public String getStatementName() {
            return "loadAllPersonsEscaped1";
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

    @SuppressWarnings({"Duplicates", "WeakerAccess"})
    public final class keepUnderscoreForCamelCase extends com.w11k.lsql.TypedStatementQuery<KeepUnderscoreForCamelCase> implements com.w11k.lsql.TableRow {

        // constructors ----------

        @SuppressWarnings("ConstantConditions")
        public keepUnderscoreForCamelCase() {
            super(lSql, sql_keepUnderscoreForCamelCase);
        }

        @SuppressWarnings("unused")
        public keepUnderscoreForCamelCase(java.util.Map<String, Object> from) {
            super(lSql, sql_keepUnderscoreForCamelCase);
        }

        // fields ----------


        // class methods ----------

        public java.util.Map<String, Object> toMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            return map;
        }

        // Object methods ----------

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            keepUnderscoreForCamelCase that = (keepUnderscoreForCamelCase) o;
            return true;
        }

        @Override
        public int hashCode() {
            return Objects.hash("keepUnderscoreForCamelCase");
        }

        @Override
        public String toString() {
            return "keepUnderscoreForCamelCase{" + "" + "}";
        }

        protected KeepUnderscoreForCamelCase createTypedRow(com.w11k.lsql.Row row) {
            return new KeepUnderscoreForCamelCase(row);
        }

        protected java.util.Map<String, Object>  getQueryParameters() {
            return this.toMap();
        }

        public String getStatementFileName() {
            return "Stmts1.sql";
        }

        public String getStatementName() {
            return "keepUnderscoreForCamelCase";
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

    @SuppressWarnings({"Duplicates", "WeakerAccess"})
    public final class queryParamsWithDot extends com.w11k.lsql.TypedStatementQuery<QueryParamsWithDot> implements com.w11k.lsql.TableRow, Person1Id_Number {

        // constructors ----------

        @SuppressWarnings("ConstantConditions")
        public queryParamsWithDot() {
            super(lSql, sql_queryParamsWithDot);
            this.person1Id = null;
        }

        @SuppressWarnings("NullableProblems")
        private queryParamsWithDot(
                java.lang.Number person1Id) {
            super(lSql, sql_queryParamsWithDot);
            this.person1Id = person1Id;
    }

        @SuppressWarnings("unused")
        public queryParamsWithDot(java.util.Map<String, Object> from) {
            super(lSql, sql_queryParamsWithDot);
            this.person1Id = (java.lang.Number) from.get("person1.id");
        }

        // fields ----------

        @javax.annotation.Nullable public final java.lang.Number person1Id;

        @javax.annotation.Nullable public java.lang.Number getPerson1Id() {
            return this.person1Id;
        }

        public queryParamsWithDot withPerson1Id(@javax.annotation.Nullable java.lang.Number person1Id) {
            return new queryParamsWithDot(person1Id);
        }

        // class methods ----------

        public java.util.Map<String, Object> toMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("person1.id", this.person1Id);
            return map;
        }

        // Object methods ----------

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            queryParamsWithDot that = (queryParamsWithDot) o;
            return     Objects.equals(person1Id, that.person1Id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(person1Id);
        }

        @Override
        public String toString() {
            return "queryParamsWithDot{" + "person1Id=" + person1Id + "}";
        }

        protected QueryParamsWithDot createTypedRow(com.w11k.lsql.Row row) {
            return new QueryParamsWithDot(row);
        }

        protected java.util.Map<String, Object>  getQueryParameters() {
            return this.toMap();
        }

        public String getStatementFileName() {
            return "Stmts1.sql";
        }

        public String getStatementName() {
            return "queryParamsWithDot";
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

    @SuppressWarnings({"Duplicates", "WeakerAccess"})
    public final class changeYesno extends com.w11k.lsql.TypedStatementCommand implements com.w11k.lsql.TableRow, Yesno_Boolean {

        // constructors ----------

        @SuppressWarnings("ConstantConditions")
        public changeYesno() {
            super(lSql, sql_changeYesno);
            this.yesno = null;
        }

        @SuppressWarnings("NullableProblems")
        private changeYesno(
                java.lang.Boolean yesno) {
            super(lSql, sql_changeYesno);
            this.yesno = yesno;
    }

        @SuppressWarnings("unused")
        public changeYesno(java.util.Map<String, Object> from) {
            super(lSql, sql_changeYesno);
            this.yesno = (java.lang.Boolean) from.get("yesno");
        }

        // fields ----------

        @javax.annotation.Nullable public final java.lang.Boolean yesno;

        @javax.annotation.Nullable public java.lang.Boolean isYesno() {
            return this.yesno;
        }

        public changeYesno withYesno(@javax.annotation.Nullable java.lang.Boolean yesno) {
            return new changeYesno(yesno);
        }

        // class methods ----------

        public java.util.Map<String, Object> toMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("yesno", this.yesno);
            return map;
        }

        // Object methods ----------

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            changeYesno that = (changeYesno) o;
            return     Objects.equals(yesno, that.yesno);
        }

        @Override
        public int hashCode() {
            return Objects.hash(yesno);
        }

        @Override
        public String toString() {
            return "changeYesno{" + "yesno=" + yesno + "}";
        }

        protected java.util.Map<String, Object>  getQueryParameters() {
            return this.toMap();
        }

        public String getStatementFileName() {
            return "Stmts1.sql";
        }

        public String getStatementName() {
            return "changeYesno";
        }

    }

    private final com.w11k.lsql.LSql lSql;

    @com.google.inject.Inject
    public Stmts1(com.w11k.lsql.LSql lSql) {
        this.lSql = lSql;
    }

}
