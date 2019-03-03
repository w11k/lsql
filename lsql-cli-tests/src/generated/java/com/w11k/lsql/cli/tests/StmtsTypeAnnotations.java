package com.w11k.lsql.cli.tests;

import com.w11k.lsql.cli.tests.structural_fields.*;
import com.w11k.lsql.cli.tests.stmtstypeannotations.*;
import java.util.*;

@javax.inject.Singleton
public class StmtsTypeAnnotations {

    // Statement: columnTypeAnnotation ----------------------------

    private final String sql_columnTypeAnnotation = "select \n person1.id as \"id: int\" \n from person1;";

    /**    
     * select<br>
     * person1.id as "id: int"<br>
     * from person1;<br>
    */
    public columnTypeAnnotationQuery columnTypeAnnotation() {
        return new columnTypeAnnotationQuery();
    }

    @SuppressWarnings({"Duplicates", "WeakerAccess"})
    public final class columnTypeAnnotationQuery extends com.w11k.lsql.TypedStatementQuery<ColumnTypeAnnotation> implements com.w11k.lsql.TableRow {

        // constructors ----------

        @SuppressWarnings("ConstantConditions")
        public columnTypeAnnotationQuery() {
            super(lSql, sql_columnTypeAnnotation);
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
            columnTypeAnnotationQuery that = (columnTypeAnnotationQuery) o;
            return true;
        }

        @Override
        public int hashCode() {
            return Objects.hash("columnTypeAnnotationQuery");
        }

        @Override
        public String toString() {
            return "columnTypeAnnotationQuery{" + "" + "}";
        }

        protected ColumnTypeAnnotation createTypedRow(com.w11k.lsql.Row row) {
            return ColumnTypeAnnotation.fromInternalMap(row);
        }

        protected java.util.Map<String, Object>  getQueryParameters() {
            return this.toInternalMap();
        }

        public String getStatementFileName() {
            return "StmtsTypeAnnotations.sql";
        }

        public String getStatementName() {
            return "columnTypeAnnotation";
        }

    }

    private final com.w11k.lsql.LSql lSql;

    @javax.inject.Inject
    public StmtsTypeAnnotations(com.w11k.lsql.LSql lSql) {
        this.lSql = lSql;
    }

}
