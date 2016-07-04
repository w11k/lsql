package com.w11k.lsql.utils;

import com.google.common.collect.Maps;

import java.sql.Types;
import java.util.Map;

public class SqlTypesNames {

    static private final Map<Integer, String> TYPES_NAMES = Maps.newHashMap();

    static {
        TYPES_NAMES.put(Types.BIT, "BIT");
        TYPES_NAMES.put(Types.TINYINT, "TINYINT");
        TYPES_NAMES.put(Types.SMALLINT, "SMALLINT");
        TYPES_NAMES.put(Types.INTEGER, "INTEGER");
        TYPES_NAMES.put(Types.BIGINT, "BIGINT");
        TYPES_NAMES.put(Types.FLOAT, "FLOAT");
        TYPES_NAMES.put(Types.REAL, "REAL");
        TYPES_NAMES.put(Types.DOUBLE, "DOUBLE");
        TYPES_NAMES.put(Types.NUMERIC, "NUMERIC");
        TYPES_NAMES.put(Types.DECIMAL, "DECIMAL");
        TYPES_NAMES.put(Types.CHAR, "CHAR");
        TYPES_NAMES.put(Types.VARCHAR, "VARCHAR");
        TYPES_NAMES.put(Types.LONGVARCHAR, "LONGVARCHAR");
        TYPES_NAMES.put(Types.DATE, "DATE");
        TYPES_NAMES.put(Types.TIME, "TIME");
        TYPES_NAMES.put(Types.TIMESTAMP, "TIMESTAMP");
        TYPES_NAMES.put(Types.BINARY, "BINARY");
        TYPES_NAMES.put(Types.VARBINARY, "VARBINARY");
        TYPES_NAMES.put(Types.LONGVARBINARY, "LONGVARBINARY");
        TYPES_NAMES.put(Types.NULL, "NULL");
        TYPES_NAMES.put(Types.OTHER, "OTHER");
        TYPES_NAMES.put(Types.JAVA_OBJECT, "JAVA_OBJECT");
        TYPES_NAMES.put(Types.DISTINCT, "DISTINCT");
        TYPES_NAMES.put(Types.STRUCT, "STRUCT");
        TYPES_NAMES.put(Types.ARRAY, "ARRAY");
        TYPES_NAMES.put(Types.BLOB, "BLOB");
        TYPES_NAMES.put(Types.CLOB, "CLOB");
        TYPES_NAMES.put(Types.REF, "REF");
        TYPES_NAMES.put(Types.DATALINK, "DATALINK");
        TYPES_NAMES.put(Types.BOOLEAN, "BOOLEAN");
        TYPES_NAMES.put(Types.ROWID, "ROWID");
        TYPES_NAMES.put(Types.NCHAR, "NCHAR");
        TYPES_NAMES.put(Types.NVARCHAR, "NVARCHAR");
        TYPES_NAMES.put(Types.LONGNVARCHAR, "LONGNVARCHAR");
        TYPES_NAMES.put(Types.NCLOB, "NCLOB");
        TYPES_NAMES.put(Types.SQLXML, "SQLXML");
    }

    static public String getName(int sqlType) {
        String name = TYPES_NAMES.get(sqlType);
        return name != null ? name : sqlType + "";
    }
}
