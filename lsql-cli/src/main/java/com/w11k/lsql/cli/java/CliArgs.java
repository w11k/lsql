package com.w11k.lsql.cli.java;

public final class CliArgs {

    private String configClassName;

    private String url;

    private String user;

    private String password;

    private String genPackageName;

    private String sqlStatements;

    private boolean guice = false;

    private String outDirJava;

    private String outDirTypeScript;

    public CliArgs(String[] args) {
        for (String arg : args) {
            String value = getParamValue(arg);
            if (arg.startsWith("config:")) {
                this.configClassName = value;
            } else if (arg.startsWith("url:")) {
                this.url = value;
            } else if (arg.startsWith("user:")) {
                this.user = value;
            } else if (arg.startsWith("password:")) {
                this.password = value;
            } else if (arg.startsWith("package:")) {
                this.genPackageName = value;
            } else if (arg.startsWith("sqlStatements:")) {
                this.sqlStatements = value;
            } else if (arg.startsWith("outDirJava:")) {
                this.outDirJava = value;
            } else if (arg.startsWith("outDirTypeScript:")) {
                this.outDirTypeScript = value;
            } else if (arg.startsWith("di:")) {
                if (value.equalsIgnoreCase("guice")) {
                    this.guice = true;
                }
            }
        }
    }

    public String getConfigClassName() {
        return configClassName;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getGenPackageName() {
        return genPackageName;
    }

    public String getSqlStatements() {
        return sqlStatements;
    }

    public boolean isGuice() {
        return guice;
    }

    public String getOutDirJava() {
        return outDirJava;
    }

    public String getOutDirTypeScript() {
        return outDirTypeScript;
    }

    private String getParamValue(String param) {
        return param.substring(param.indexOf(":") + 1).trim();
    }
}
