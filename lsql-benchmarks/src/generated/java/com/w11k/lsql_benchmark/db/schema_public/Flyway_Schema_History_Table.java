package com.w11k.lsql_benchmark.db.schema_public;

public class Flyway_Schema_History_Table extends com.w11k.lsql.TypedTable<Flyway_Schema_History_Row, java.lang.Integer>  {

    public Flyway_Schema_History_Table(com.w11k.lsql.LSql lSql) {
        super(lSql, "flyway_schema_history", Flyway_Schema_History_Row.class);
    }

    public static final String NAME = "flyway_schema_history";

    protected Flyway_Schema_History_Row createFromInternalMap(java.util.Map<String, Object> internalMap) {
        return Flyway_Schema_History_Row.fromInternalMap(internalMap);
    }

}
