package com.w11k.lsql.cli;

public final class CliConfig {

    private String generatedPackageName = null;

    private String sqlStatementFiles = null;

    public String getGeneratedPackageName() {
        return generatedPackageName;
    }

    public void setGeneratedPackageName(String generatedPackageName) {
        this.generatedPackageName = generatedPackageName;
    }

    public String getSqlStatementFiles() {
        return sqlStatementFiles;
    }

    public void setSqlStatementFiles(String sqlStatementFiles) {
        this.sqlStatementFiles = sqlStatementFiles;
    }
}
