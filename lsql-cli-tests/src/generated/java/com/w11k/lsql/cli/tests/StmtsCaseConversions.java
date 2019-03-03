package com.w11k.lsql.cli.tests;

import com.w11k.lsql.cli.tests.structural_fields.*;
import com.w11k.lsql.cli.tests.stmtscaseconversions.*;
import java.util.*;

@javax.inject.Singleton
public class StmtsCaseConversions {

    // Statement: saveCaseConversions ----------------------------

    private final String sql_saveCaseConversions = "select \n id, \n col1 as \"one_two: int\", \n col1 as \"onetwo: int\" \n from case_conversions2;";

    /**    
     * select<br>
     * id,<br>
     * col1 as "one_two: int",<br>
     * col1 as "onetwo: int"<br>
     * from case_conversions2;<br>
    */
    public saveCaseConversionsQuery saveCaseConversions() {
        return new saveCaseConversionsQuery();
    }

    @SuppressWarnings({"Duplicates", "WeakerAccess"})
    public final class saveCaseConversionsQuery extends com.w11k.lsql.TypedStatementQuery<SaveCaseConversions> implements com.w11k.lsql.TableRow {

        // constructors ----------

        @SuppressWarnings("ConstantConditions")
        public saveCaseConversionsQuery() {
            super(lSql, sql_saveCaseConversions);
        }

        // fields ----------


        // class methods ----------

        public java.util.Map<String, Object> toInternalMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            return map;
        }

        public java.util.Map<String, Object> toRow() {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            return map;
        }

        // Object methods ----------

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            saveCaseConversionsQuery that = (saveCaseConversionsQuery) o;
            return true;
        }

        @Override
        public int hashCode() {
            return Objects.hash("saveCaseConversionsQuery");
        }

        @Override
        public String toString() {
            return "saveCaseConversionsQuery{" + "" + "}";
        }

        protected SaveCaseConversions createTypedRow(com.w11k.lsql.Row row) {
            return SaveCaseConversions.fromInternalMap(row);
        }

        protected java.util.Map<String, Object>  getQueryParameters() {
            return this.toInternalMap();
        }

        public String getStatementFileName() {
            return "StmtsCaseConversions.sql";
        }

        public String getStatementName() {
            return "saveCaseConversions";
        }

    }

    private final com.w11k.lsql.LSql lSql;

    @javax.inject.Inject
    public StmtsCaseConversions(com.w11k.lsql.LSql lSql) {
        this.lSql = lSql;
    }

}
