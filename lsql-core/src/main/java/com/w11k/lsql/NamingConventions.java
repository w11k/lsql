package com.w11k.lsql;

import com.google.common.base.CaseFormat;

public class NamingConventions {

    private CaseFormat javaCodeFormat = CaseFormat.LOWER_UNDERSCORE;

    private CaseFormat sqlCodeFormat = CaseFormat.UPPER_UNDERSCORE;

    public CaseFormat getJavaCodeFormat() {
        return javaCodeFormat;
    }

    public void setJavaCodeFormat(CaseFormat javaCodeFormat) {
        this.javaCodeFormat = javaCodeFormat;
    }

    public CaseFormat getSqlCodeFormat() {
        return sqlCodeFormat;
    }

    public void setSqlCodeFormat(CaseFormat sqlCodeFormat) {
        this.sqlCodeFormat = sqlCodeFormat;
    }

    public String identifierSqlToJava(String sqlName) {
        return sqlCodeFormat.to(javaCodeFormat, sqlName);
    }

    public String identifierJavaToSql(String javaName) {
        return javaCodeFormat.to(sqlCodeFormat, javaName);
    }

}
