package com.w11k.lsql.cli.tests.schema_public;

@javax.inject.Singleton
public class Custom_Converter_Table extends com.w11k.lsql.TypedTable<Custom_Converter_Row, com.w11k.lsql.NoPrimaryKeyColumn>  {

    @javax.inject.Inject
    public Custom_Converter_Table(com.w11k.lsql.LSql lSql) {
        super(lSql, "CUSTOM_CONVERTER", Custom_Converter_Row.class);
    }

    public static final String NAME = "CUSTOM_CONVERTER";

    protected Custom_Converter_Row createFromInternalMap(java.util.Map<String, Object> internalMap) {
        return Custom_Converter_Row.fromInternalMap(internalMap);
    }

}
