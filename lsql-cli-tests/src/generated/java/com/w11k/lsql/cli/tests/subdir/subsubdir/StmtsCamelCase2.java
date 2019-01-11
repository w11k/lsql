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
    public loadPersonsByAgeAndFirstNameQuery loadPersonsByAgeAndFirstName() {
        return new loadPersonsByAgeAndFirstNameQuery();
    }

    @SuppressWarnings({"Duplicates", "WeakerAccess"})
    public final class loadPersonsByAgeAndFirstNameQuery extends com.w11k.lsql.TypedStatementQuery<LoadPersonsByAgeAndFirstName> implements com.w11k.lsql.TableRow, First_Name_String, Age_Number {

        // constructors ----------

        @SuppressWarnings("ConstantConditions")
        public loadPersonsByAgeAndFirstNameQuery() {
            super(lSql, sql_loadPersonsByAgeAndFirstName);
            this.firstName = null;
            this.age = null;
        }

        @SuppressWarnings("NullableProblems")
        private loadPersonsByAgeAndFirstNameQuery(
                java.lang.String firstName,
            java.lang.Number age) {
            super(lSql, sql_loadPersonsByAgeAndFirstName);
            this.firstName = firstName;
            this.age = age;
    }

        // fields ----------

        @javax.annotation.Nullable public final java.lang.String firstName;

        @javax.annotation.Nullable public java.lang.String getFirstName() {
            return this.firstName;
        }

        public loadPersonsByAgeAndFirstNameQuery withFirstName(@javax.annotation.Nullable java.lang.String firstName) {
            return new loadPersonsByAgeAndFirstNameQuery(firstName,age);
        }
        @javax.annotation.Nullable public final java.lang.Number age;

        @javax.annotation.Nullable public java.lang.Number getAge() {
            return this.age;
        }

        public loadPersonsByAgeAndFirstNameQuery withAge(@javax.annotation.Nullable java.lang.Number age) {
            return new loadPersonsByAgeAndFirstNameQuery(firstName,age);
        }

        // class methods ----------

        public java.util.Map<String, Object> toInternalMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("first_name", this.firstName);
            map.put("age", this.age);
            return map;
        }

        public java.util.Map<String, Object> toMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("firstName", this.firstName);
            map.put("age", this.age);
            return map;
        }

        // Object methods ----------

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            loadPersonsByAgeAndFirstNameQuery that = (loadPersonsByAgeAndFirstNameQuery) o;
            return     Objects.equals(firstName, that.firstName) && 
                Objects.equals(age, that.age);
        }

        @Override
        public int hashCode() {
            return Objects.hash(firstName, age);
        }

        @Override
        public String toString() {
            return "loadPersonsByAgeAndFirstNameQuery{" + "firstName=" + firstName
                + ", " + "age=" + age + "}";
        }

        protected LoadPersonsByAgeAndFirstName createTypedRow(com.w11k.lsql.Row row) {
            return LoadPersonsByAgeAndFirstName.fromInternalMap(row);
        }

        protected java.util.Map<String, Object>  getQueryParameters() {
            return this.toInternalMap();
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
    public deletePersonByFirstNameQuery deletePersonByFirstName() {
        return new deletePersonByFirstNameQuery();
    }

    @SuppressWarnings({"Duplicates", "WeakerAccess"})
    public final class deletePersonByFirstNameQuery extends com.w11k.lsql.TypedStatementCommand implements com.w11k.lsql.TableRow, First_Name_String {

        // constructors ----------

        @SuppressWarnings("ConstantConditions")
        public deletePersonByFirstNameQuery() {
            super(lSql, sql_deletePersonByFirstName);
            this.firstName = null;
        }

        @SuppressWarnings("NullableProblems")
        private deletePersonByFirstNameQuery(
                java.lang.String firstName) {
            super(lSql, sql_deletePersonByFirstName);
            this.firstName = firstName;
    }

        // fields ----------

        @javax.annotation.Nullable public final java.lang.String firstName;

        @javax.annotation.Nullable public java.lang.String getFirstName() {
            return this.firstName;
        }

        public deletePersonByFirstNameQuery withFirstName(@javax.annotation.Nullable java.lang.String firstName) {
            return new deletePersonByFirstNameQuery(firstName);
        }

        // class methods ----------

        public java.util.Map<String, Object> toInternalMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("first_name", this.firstName);
            return map;
        }

        public java.util.Map<String, Object> toMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("firstName", this.firstName);
            return map;
        }

        // Object methods ----------

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            deletePersonByFirstNameQuery that = (deletePersonByFirstNameQuery) o;
            return     Objects.equals(firstName, that.firstName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(firstName);
        }

        @Override
        public String toString() {
            return "deletePersonByFirstNameQuery{" + "firstName=" + firstName + "}";
        }

        protected java.util.Map<String, Object>  getQueryParameters() {
            return this.toInternalMap();
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
