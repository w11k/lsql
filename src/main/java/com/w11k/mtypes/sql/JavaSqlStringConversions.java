package com.w11k.mtypes.sql;

import com.google.common.base.CaseFormat;

import static com.google.common.base.Preconditions.checkNotNull;

public class JavaSqlStringConversions {

    public String identifierSqlToJava(String sqlName) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, sqlName);
    }

    public String identifierJavaToSql(String javaName) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, javaName);
    }

    public String escapeJavaObjectForSqlStatement(Object obj) {
        checkNotNull(obj);
        if (obj instanceof String) {
            // TODO check escaping
            return "'" + ((String) obj).replaceAll("'", "\'") + "'";
        } else {
            return obj.toString();
        }
    }

}
