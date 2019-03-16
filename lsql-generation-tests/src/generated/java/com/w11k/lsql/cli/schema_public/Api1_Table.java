package com.w11k.lsql.cli.schema_public;

@javax.inject.Singleton
public class Api1_Table extends com.w11k.lsql.TypedTable<Api1_Row, java.lang.Integer>  {

    @javax.inject.Inject
    public Api1_Table(com.w11k.lsql.LSql lSql) {
        super(lSql, "PUBLIC", "API1", null, Api1_Row.class);
    }

    public static final String NAME = "API1";

    protected Api1_Row createFromInternalMap(java.util.Map<String, Object> internalMap) {
        return Api1_Row.fromInternalMap(internalMap);
    }

}
