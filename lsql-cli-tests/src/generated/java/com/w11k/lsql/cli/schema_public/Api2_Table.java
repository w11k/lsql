package com.w11k.lsql.cli.schema_public;

@javax.inject.Singleton
public class Api2_Table extends com.w11k.lsql.TypedTable<Api2_Row, java.lang.Integer>  {

    @javax.inject.Inject
    public Api2_Table(com.w11k.lsql.LSql lSql) {
        super(lSql, "API2", Api2_Row.class);
    }

    public static final String NAME = "API2";

    protected Api2_Row createFromInternalMap(java.util.Map<String, Object> internalMap) {
        return Api2_Row.fromInternalMap(internalMap);
    }

}
