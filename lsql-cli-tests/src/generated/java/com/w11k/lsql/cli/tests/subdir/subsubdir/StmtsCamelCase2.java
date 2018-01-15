package com.w11k.lsql.cli.tests.subdir.subsubdir;

import com.w11k.lsql.cli.tests.structural_fields.*;
import com.w11k.lsql.cli.tests.subdir.subsubdir.stmtscamelcase2.*;
import java.util.*;

public class StmtsCamelCase2 {

    // Statement: loadPersonsByAgeAndFirstName ----------------------------

    private final String sql_loadPersonsByAgeAndFirstName = "select \n * \n from person2 \n where \n age = /*=*/ 100 /**/ \n -- test comment embedded \n and first_name = /*: string =*/ 'name' /**/ \n ;";

    /**    
     * select<br>
     * *<br>
     * from person2<br>
     * where<br>
     * age = &#42;&#47;=&#47;&#42; 100 &#42;&#47;&#47;&#42;<br>
     * -- test comment embedded<br>
     * and first_name = &#42;&#47;: string =&#47;&#42; 'name' &#42;&#47;&#47;&#42;<br>
     * ;<br>
    */
    public loadPersonsByAgeAndFirstName loadPersonsByAgeAndFirstName() {
        return new loadPersonsByAgeAndFirstName();
    }

    public final class loadPersonsByAgeAndFirstName extends com.w11k.lsql.TypedStatementQuery<LoadPersonsByAgeAndFirstName> implements com.w11k.lsql.TableRow, First_Name_String, Age_Number {

        // constructors ----------

        public loadPersonsByAgeAndFirstName() {
            super(lSql, sql_loadPersonsByAgeAndFirstName);
            this.firstName = null;
            this.age = null;
        }

        private loadPersonsByAgeAndFirstName(
                java.lang.String firstName,
            java.lang.Number age) {
            super(lSql, sql_loadPersonsByAgeAndFirstName);
            this.firstName = firstName;
            this.age = age;
    }

        public loadPersonsByAgeAndFirstName(java.util.Map<String, Object> from) {
            super(lSql, sql_loadPersonsByAgeAndFirstName);
            this.firstName = (java.lang.String) from.get("first_name");
            this.age = (java.lang.Number) from.get("age");
        }

        // fields ----------

        public final java.lang.String firstName;

        public java.lang.String getFirstName() {
            return this.firstName;
        }

        public loadPersonsByAgeAndFirstName withFirstName(java.lang.String firstName) {
            return new loadPersonsByAgeAndFirstName(firstName,age);
        }
        public final java.lang.Number age;

        public java.lang.Number getAge() {
            return this.age;
        }

        public loadPersonsByAgeAndFirstName withAge(java.lang.Number age) {
            return new loadPersonsByAgeAndFirstName(firstName,age);
        }

        // class methods ----------

        public java.util.Map<String, Object> toMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
            map.put("first_name", this.firstName);
            map.put("age", this.age);
            return map;
        }

        // Object methods ----------

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            loadPersonsByAgeAndFirstName that = (loadPersonsByAgeAndFirstName) o;
            return     Objects.equals(firstName, that.firstName) && 
                Objects.equals(age, that.age);
        }

        @Override
        public int hashCode() {
            return Objects.hash(firstName, age);
        }

        @Override
        public String toString() {
            return "loadPersonsByAgeAndFirstName{" + "firstName=" + firstName
                + ", " + "age=" + age + "}";
        }

        protected LoadPersonsByAgeAndFirstName createTypedRow(com.w11k.lsql.Row row) {
            return new LoadPersonsByAgeAndFirstName(row);
        }

        protected java.util.Map<String, Object>  getQueryParameters() {
            return this.toMap();
        }

        public String getStatementFileName() {
            return "StmtsCamelCase2.sql";
        }

        public String getStatementName() {
            return "loadPersonsByAgeAndFirstName";
        }

    }

    // Statement: deletePersonByFirstName ----------------------------

    private final String sql_deletePersonByFirstName = "delete from person2 where first_name = /*=*/ 'name' /**/;";

    /**    
     * delete from person2 where first_name = &#42;&#47;=&#47;&#42; 'name' &#42;&#47;&#47;&#42;;<br>
    */
    public deletePersonByFirstName deletePersonByFirstName() {
        return new deletePersonByFirstName();
    }

    public final class deletePersonByFirstName extends com.w11k.lsql.TypedStatementCommand implements com.w11k.lsql.TableRow, First_Name_String {

        // constructors ----------

        public deletePersonByFirstName() {
            super(lSql, sql_deletePersonByFirstName);
            this.firstName = null;
        }

        private deletePersonByFirstName(
                java.lang.String firstName) {
            super(lSql, sql_deletePersonByFirstName);
            this.firstName = firstName;
    }

        public deletePersonByFirstName(java.util.Map<String, Object> from) {
            super(lSql, sql_deletePersonByFirstName);
            this.firstName = (java.lang.String) from.get("first_name");
        }

        // fields ----------

        public final java.lang.String firstName;

        public java.lang.String getFirstName() {
            return this.firstName;
        }

        public deletePersonByFirstName withFirstName(java.lang.String firstName) {
            return new deletePersonByFirstName(firstName);
        }

        // class methods ----------

        public java.util.Map<String, Object> toMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
            map.put("first_name", this.firstName);
            return map;
        }

        // Object methods ----------

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            deletePersonByFirstName that = (deletePersonByFirstName) o;
            return     Objects.equals(firstName, that.firstName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(firstName);
        }

        @Override
        public String toString() {
            return "deletePersonByFirstName{" + "firstName=" + firstName + "}";
        }

        protected java.util.Map<String, Object>  getQueryParameters() {
            return this.toMap();
        }

        public String getStatementFileName() {
            return "StmtsCamelCase2.sql";
        }

        public String getStatementName() {
            return "deletePersonByFirstName";
        }

    }

    private final com.w11k.lsql.LSql lSql;

    @com.google.inject.Inject
    public StmtsCamelCase2(com.w11k.lsql.LSql lSql) {
        this.lSql = lSql;
    }

}
