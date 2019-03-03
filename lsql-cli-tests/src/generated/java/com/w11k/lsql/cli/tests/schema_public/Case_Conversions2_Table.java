package com.w11k.lsql.cli.tests.schema_public;

@javax.inject.Singleton
public class Case_Conversions2_Table extends com.w11k.lsql.TypedTable<Case_Conversions2_Row, java.lang.Integer>  {

    @javax.inject.Inject
    public Case_Conversions2_Table(com.w11k.lsql.LSql lSql) {
        super(lSql, "CASE_CONVERSIONS2", Case_Conversions2_Row.class);
    }

    public static final String NAME = "CASE_CONVERSIONS2";

    protected Case_Conversions2_Row createFromInternalMap(java.util.Map<String, Object> internalMap) {
        return Case_Conversions2_Row.fromInternalMap(internalMap);
    }

}
