package com.w11k.lsql.cli;

import com.w11k.lsql.cli.structural_fields.*;
import com.w11k.lsql.cli.stmtswithcustomconverter.*;
import java.util.*;

@javax.inject.Singleton
public class StmtsWithCustomConverter {

    // Statement: load ----------------------------

    private final String sql_load = "select * from custom_converter \n where field = /*: custom =*/ 1 /**/ \n ;";

    /**    
     * select * from custom_converter<br>
     * where field = &#42;&#47;: custom =&#47;&#42; 1 &#42;&#47;&#47;&#42;<br>
     * ;<br>
    */
    public loadQuery load() {
        return new loadQuery();
    }

    @SuppressWarnings({"Duplicates", "WeakerAccess"})
    public final class loadQuery extends com.w11k.lsql.TypedStatementQuery<Load_Row> implements com.w11k.lsql.TableRow, Field_Com_W11k_Lsql_Cli_Testcliconfig_Customtype {

        // constructors ----------

        @SuppressWarnings("ConstantConditions")
        public loadQuery() {
            super(lSql, sql_load);
            this.field = null;
        }

        @SuppressWarnings("NullableProblems")
        private loadQuery(
                com.w11k.lsql.cli.TestCliConfig.CustomType field) {
            super(lSql, sql_load);
            this.field = field;
    }

        // fields ----------

        @javax.annotation.Nullable public final com.w11k.lsql.cli.TestCliConfig.CustomType field;

        @javax.annotation.Nullable public com.w11k.lsql.cli.TestCliConfig.CustomType getField() {
            return this.field;
        }

        public loadQuery withField(@javax.annotation.Nullable com.w11k.lsql.cli.TestCliConfig.CustomType field) {
            return new loadQuery(field);
        }

        // class methods ----------

        public java.util.Map<String, Object> toInternalMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("field", this.field);
            return map;
        }

        public java.util.Map<String, Object> toRowMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("field", this.field);
            return map;
        }

        // Object methods ----------

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            loadQuery that = (loadQuery) o;
            return     Objects.equals(field, that.field);
        }

        @Override
        public int hashCode() {
            return Objects.hash(field);
        }

        @Override
        public String toString() {
            return "loadQuery{" + "field=" + field + "}";
        }

        protected Load_Row createTypedRow(com.w11k.lsql.Row row) {
            return Load_Row.fromInternalMap(row);
        }

        protected java.util.Map<String, Object>  getQueryParameters() {
            return this.toInternalMap();
        }

        public String getStatementFileName() {
            return "StmtsWithCustomConverter.sql";
        }

        public String getStatementName() {
            return "load";
        }

    }

    // Statement: testQueryParamter ----------------------------

    private final String sql_testQueryParamter = "select \n * \n from \n person1 \n WHERE id = /*=*/ 1 /**/;";

    /**    
     * select<br>
     * *<br>
     * from<br>
     * person1<br>
     * WHERE id = &#42;&#47;=&#47;&#42; 1 &#42;&#47;&#47;&#42;;<br>
    */
    public testQueryParamterQuery testQueryParamter() {
        return new testQueryParamterQuery();
    }

    @SuppressWarnings({"Duplicates", "WeakerAccess"})
    public final class testQueryParamterQuery extends com.w11k.lsql.TypedStatementQuery<TestQueryParamter_Row> implements com.w11k.lsql.TableRow, Id_Number {

        // constructors ----------

        @SuppressWarnings("ConstantConditions")
        public testQueryParamterQuery() {
            super(lSql, sql_testQueryParamter);
            this.id = null;
        }

        @SuppressWarnings("NullableProblems")
        private testQueryParamterQuery(
                java.lang.Number id) {
            super(lSql, sql_testQueryParamter);
            this.id = id;
    }

        // fields ----------

        @javax.annotation.Nullable public final java.lang.Number id;

        @javax.annotation.Nullable public java.lang.Number getId() {
            return this.id;
        }

        public testQueryParamterQuery withId(@javax.annotation.Nullable java.lang.Number id) {
            return new testQueryParamterQuery(id);
        }

        // class methods ----------

        public java.util.Map<String, Object> toInternalMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", this.id);
            return map;
        }

        public java.util.Map<String, Object> toRowMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", this.id);
            return map;
        }

        // Object methods ----------

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            testQueryParamterQuery that = (testQueryParamterQuery) o;
            return     Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return "testQueryParamterQuery{" + "id=" + id + "}";
        }

        protected TestQueryParamter_Row createTypedRow(com.w11k.lsql.Row row) {
            return TestQueryParamter_Row.fromInternalMap(row);
        }

        protected java.util.Map<String, Object>  getQueryParameters() {
            return this.toInternalMap();
        }

        public String getStatementFileName() {
            return "StmtsWithCustomConverter.sql";
        }

        public String getStatementName() {
            return "testQueryParamter";
        }

    }

    private final com.w11k.lsql.LSql lSql;

    @javax.inject.Inject
    public StmtsWithCustomConverter(com.w11k.lsql.LSql lSql) {
        this.lSql = lSql;
    }

}
