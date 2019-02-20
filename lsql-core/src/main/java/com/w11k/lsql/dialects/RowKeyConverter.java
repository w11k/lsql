package com.w11k.lsql.dialects;

import static com.google.common.base.CaseFormat.*;

public abstract class RowKeyConverter {

    public static RowKeyConverter NOOP = new RowKeyConverter() {
        public String sqlToJava(String sqlName) {
            return sqlName;
        }

        public String javaToSql(String javaName) {
            return javaName;
        }
    };

    public static RowKeyConverter JAVA_CAMEL_CASE_TO_SQL_LOWER_UNDERSCORE = new RowKeyConverter() {
        public String sqlToJava(String sqlName) {
            sqlName = sqlName.toLowerCase();
            return LOWER_UNDERSCORE.to(LOWER_CAMEL, sqlName);
        }

        public String javaToSql(String javaName) {
            return LOWER_CAMEL.to(LOWER_UNDERSCORE, javaName);
        }
    };

    public static RowKeyConverter JAVA_CAMEL_CASE_TO_SQL_UPPER_UNDERSCORE = new RowKeyConverter() {
        public String sqlToJava(String sqlName) {
            sqlName = sqlName.toUpperCase();
            return UPPER_UNDERSCORE.to(LOWER_CAMEL, sqlName);
        }

        public String javaToSql(String javaName) {
            return LOWER_CAMEL.to(UPPER_UNDERSCORE, javaName);
        }
    };

    public static RowKeyConverter JAVA_LOWER_UNDERSCORE_TO_SQL_LOWER_UNDERSCORE = new RowKeyConverter() {
        public String sqlToJava(String sqlName) {
            return sqlName.toLowerCase();
        }

        public String javaToSql(String javaName) {
            return javaName.toLowerCase();
        }
    };

    public static RowKeyConverter JAVA_LOWER_UNDERSCORE_TO_SQL_UPPER_UNDERSCORE = new RowKeyConverter() {
        public String sqlToJava(String sqlName) {
            return sqlName.toLowerCase();
        }

        public String javaToSql(String javaName) {
            return javaName.toUpperCase();
        }
    };

    public static RowKeyConverter JAVA_UPPER_UNDERSCORE_TO_SQL_UPPER_UNDERSCORE = new RowKeyConverter() {
        public String sqlToJava(String sqlName) {
            return sqlName.toUpperCase();
        }

        public String javaToSql(String javaName) {
            return javaName.toUpperCase();
        }
    };

    public abstract String sqlToJava(String sqlName);

    public abstract String javaToSql(String javaName);

}
