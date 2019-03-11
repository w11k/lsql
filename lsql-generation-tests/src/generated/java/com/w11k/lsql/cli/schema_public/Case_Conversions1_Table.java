package com.w11k.lsql.cli.schema_public;

@javax.inject.Singleton
public class Case_Conversions1_Table extends com.w11k.lsql.TypedTable<Case_Conversions1_Row, java.lang.Integer>  {

    @javax.inject.Inject
    public Case_Conversions1_Table(com.w11k.lsql.LSql lSql) {
        super(lSql, "PUBLIC.CASE_CONVERSIONS1", Case_Conversions1_Row.class);
    }

    public static final String NAME = "CASE_CONVERSIONS1";

    protected Case_Conversions1_Row createFromInternalMap(java.util.Map<String, Object> internalMap) {
        return Case_Conversions1_Row.fromInternalMap(internalMap);
    }

}
