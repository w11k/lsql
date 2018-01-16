package com.w11k.lsql.cli.tests.schema_public;

public class Custom_Converter_Table extends com.w11k.lsql.TypedTable<Custom_Converter_Row, com.w11k.lsql.NoPrimaryKeyColumn>  {

    @com.google.inject.Inject
    public Custom_Converter_Table(com.w11k.lsql.LSql lSql) {
        super(lSql, "custom_converter", Custom_Converter_Row.class);
    }

    public static final String NAME = "custom_converter";

}
