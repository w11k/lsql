package com.w11k.lsql.cli.java;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Set;

import static com.w11k.lsql.cli.CodeGenUtils.log;
import static java.lang.String.format;

public final class CliArgs {

    private static Map<String, String> VALID_ARGS = ImmutableMap.<String, String>builder()
            .put("config", "fully qualified class name of the config")
            .put("url", "JDBC database url")
            .put("user", "JDBC database user")
            .put("password", "JDBC database password")
            .put("schema", "comma separated list of schemas, omit to use default schema")
            .put("package", "root package for all generated classes")
            .put("sqlStatements", "root directory that contains SQL statement files")
            .put("dto", "root directory that contains Java files with embedded DTO declarations")
            .put("outDirJava", "output directory for all generated Java classes")
            .put("outDirTypeScript", "output directory for the generated TypeScript file domain.d.ts")
            .put("di", "Specify in which DI container the generated classes are used. Valid values: guice, java")
            .build();

    public static Set<String> VALID_ARG_NAMES = VALID_ARGS.keySet();

    private String configClassName;

    private String url;

    private String user;

    private String password;

    private String schema;

    private String genPackageName;

    private String sqlStatements;

    private String dto;

    private boolean diGuice = false;

    private String outDirJava;

    private String outDirTypeScript;

    public CliArgs(String[] args) {
        boolean hadErrors = false;

        if (args == null || args.length == 0) {
            hadErrors = true;
        }

        if (args != null) {
            for (String arg : args) {
                String key = getParamKey(arg);

                if (!VALID_ARG_NAMES.contains(key)) {
                    hadErrors = true;
                    log("Invalid parameter: " + key);
                }

                String value = getParamValue(arg);

                //noinspection IfCanBeSwitch
                if (key.equals("config")) {
                    this.configClassName = value;
                } else if (key.equals("url")) {
                    this.url = value;
                } else if (key.equals("user")) {
                    this.user = value;
                } else if (key.equals("password")) {
                    this.password = value;
                } else if (key.equals("schema")) {
                    this.schema = value;
                } else if (key.equals("package")) {
                    this.genPackageName = value;
                } else if (key.equals("sqlStatements")) {
                    this.sqlStatements = value;
                } else if (key.equals("dto")) {
                    this.dto = value;
                } else if (key.equals("outDirJava")) {
                    this.outDirJava = value;
                } else if (key.equals("outDirTypeScript")) {
                    this.outDirTypeScript = value;
                } else if (key.equals("di")) {
                    if (value.equalsIgnoreCase("guice")) {
                        this.diGuice = true;
                    }
                }
            }
        }

        if (hadErrors) {
            printHelpAndExit();
        }
    }

    private void printHelpAndExit() {
        log("\nValid parameters:\n");
        VALID_ARGS.forEach((k, v) -> {
            log(format("%20s = %s", k, v));
        });
        log();
        log("Example: mvn exec:java -Dexec.mainClass=\"com.w11k.lsql.cli.Main\" -Dexec.args=\"config:com.acme.LSqlConfig url:jdbc:postgresql://localhost/foodb user:foo password:bar package:com.acme.db di:guice sqlStatements:src/main/java/com/acme outDirJava:src/generated/java outDirTypeScript:target/typescript");
        log();

        throw new RuntimeException();
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

    public String getSchema() {
        return schema;
    }

    public String getGenPackageName() {
        return genPackageName;
    }

    public String getSqlStatements() {
        return sqlStatements;
    }

    public boolean isDiGuice() {
        return diGuice;
    }

    public String getOutDirJava() {
        return outDirJava;
    }

    public String getOutDirTypeScript() {
        return outDirTypeScript;
    }

    public String getDto() {
        return dto;
    }

    private String getParamKey(String param) {
        return param.substring(0, param.indexOf(":")).trim();
    }

    private String getParamValue(String param) {
        return param.substring(param.indexOf(":") + 1).trim();
    }
}
