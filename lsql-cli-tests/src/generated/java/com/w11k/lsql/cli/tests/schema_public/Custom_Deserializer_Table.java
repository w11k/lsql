package com.w11k.lsql.cli.tests.schema_public;

public class Custom_Deserializer_Table extends com.w11k.lsql.TypedTable<Custom_Deserializer_Row, java.lang.Integer>  {

    @com.google.inject.Inject
    public Custom_Deserializer_Table(com.w11k.lsql.LSql lSql) {
        super(lSql, "CUSTOM_DESERIALIZER", Custom_Deserializer_Row.class);
    }

    public static final String NAME = "CUSTOM_DESERIALIZER";

    protected Custom_Deserializer_Row createFromInternalMap(java.util.Map<String, Object> internalMap) {
        return Custom_Deserializer_Row.fromInternalMap(internalMap);
    }

}
