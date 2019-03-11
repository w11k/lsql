package com.w11k.lsql.cli.schema_public;

@javax.inject.Singleton
public class A_Table_Table extends com.w11k.lsql.TypedTable<A_Table_Row, java.lang.Integer>  {

    @javax.inject.Inject
    public A_Table_Table(com.w11k.lsql.LSql lSql) {
        super(lSql, "PUBLIC.A_TABLE", A_Table_Row.class);
    }

    public static final String NAME = "A_TABLE";

    protected A_Table_Row createFromInternalMap(java.util.Map<String, Object> internalMap) {
        return A_Table_Row.fromInternalMap(internalMap);
    }

}
