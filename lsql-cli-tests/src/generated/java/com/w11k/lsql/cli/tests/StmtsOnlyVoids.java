package com.w11k.lsql.cli.tests;

import com.w11k.lsql.cli.tests.structural_fields.*;
import java.util.*;

public class StmtsOnlyVoids {

    // Statement: statementA ----------------------------

    private final String sql_statementA = "UPDATE checks \n SET yesno = /*=*/ TRUE /**/ \n ;";

    /**    
     * UPDATE checks<br>
     * SET yesno = &#42;&#47;=&#47;&#42; TRUE &#42;&#47;&#47;&#42;<br>
     * ;<br>
    */
    public statementA statementA() {
        return new statementA();
    }

    public final class statementA extends com.w11k.lsql.TypedStatementCommand implements com.w11k.lsql.TableRow, Yesno_Boolean {

        // constructors ----------

        public statementA() {
            super(lSql, sql_statementA);
            this.yesno = null;
        }

        private statementA(
                java.lang.Boolean yesno) {
            super(lSql, sql_statementA);
            this.yesno = yesno;
    }

        public statementA(java.util.Map<String, Object> from) {
            super(lSql, sql_statementA);
            this.yesno = (java.lang.Boolean) from.get("yesno");
        }

        // fields ----------

        public final java.lang.Boolean yesno;

        public java.lang.Boolean isYesno() {
            return this.yesno;
        }

        public statementA withYesno(java.lang.Boolean yesno) {
            return new statementA(yesno);
        }

        // class methods ----------

        public java.util.Map<String, Object> toMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
            map.put("yesno", this.yesno);
            return map;
        }

        // Object methods ----------

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            statementA that = (statementA) o;
            return     Objects.equals(yesno, that.yesno);
        }

        @Override
        public int hashCode() {
            return Objects.hash(yesno);
        }

        @Override
        public String toString() {
            return "statementA{" + "yesno=" + yesno + "}";
        }

        protected java.util.Map<String, Object>  getQueryParameters() {
            return this.toMap();
        }

        public String getStatementFileName() {
            return "StmtsOnlyVoids.sql";
        }

        public String getStatementName() {
            return "statementA";
        }

    }

    // Statement: statementB ----------------------------

    private final String sql_statementB = "UPDATE checks \n SET yesno = /*=*/ TRUE /**/ \n ;";

    /**    
     * UPDATE checks<br>
     * SET yesno = &#42;&#47;=&#47;&#42; TRUE &#42;&#47;&#47;&#42;<br>
     * ;<br>
    */
    public statementB statementB() {
        return new statementB();
    }

    public final class statementB extends com.w11k.lsql.TypedStatementCommand implements com.w11k.lsql.TableRow, Yesno_Boolean {

        // constructors ----------

        public statementB() {
            super(lSql, sql_statementB);
            this.yesno = null;
        }

        private statementB(
                java.lang.Boolean yesno) {
            super(lSql, sql_statementB);
            this.yesno = yesno;
    }

        public statementB(java.util.Map<String, Object> from) {
            super(lSql, sql_statementB);
            this.yesno = (java.lang.Boolean) from.get("yesno");
        }

        // fields ----------

        public final java.lang.Boolean yesno;

        public java.lang.Boolean isYesno() {
            return this.yesno;
        }

        public statementB withYesno(java.lang.Boolean yesno) {
            return new statementB(yesno);
        }

        // class methods ----------

        public java.util.Map<String, Object> toMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
            map.put("yesno", this.yesno);
            return map;
        }

        // Object methods ----------

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            statementB that = (statementB) o;
            return     Objects.equals(yesno, that.yesno);
        }

        @Override
        public int hashCode() {
            return Objects.hash(yesno);
        }

        @Override
        public String toString() {
            return "statementB{" + "yesno=" + yesno + "}";
        }

        protected java.util.Map<String, Object>  getQueryParameters() {
            return this.toMap();
        }

        public String getStatementFileName() {
            return "StmtsOnlyVoids.sql";
        }

        public String getStatementName() {
            return "statementB";
        }

    }

    private final com.w11k.lsql.LSql lSql;

    @com.google.inject.Inject
    public StmtsOnlyVoids(com.w11k.lsql.LSql lSql) {
        this.lSql = lSql;
    }

}
