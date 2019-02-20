package com.w11k.lsql_benchmark.db.schema_public;

public class Table1_Table extends com.w11k.lsql.TypedTable<Table1_Row, java.lang.Integer>  {

    public Table1_Table(com.w11k.lsql.LSql lSql) {
        super(lSql, "table1", Table1_Row.class);
    }

    public static final String NAME = "table1";

    protected Table1_Row createFromInternalMap(java.util.Map<String, Object> internalMap) {
        return Table1_Row.fromInternalMap(internalMap);
    }

}
