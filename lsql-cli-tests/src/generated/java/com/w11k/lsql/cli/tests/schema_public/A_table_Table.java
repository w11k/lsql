package com.w11k.lsql.cli.tests.schema_public;

public class A_table_Table extends com.w11k.lsql.TypedTable<A_table_Row, java.lang.Integer>  {

    @com.google.inject.Inject
    public A_table_Table(com.w11k.lsql.LSql lSql) {
        super(lSql, "A_table", A_table_Row.class);
    }

    public static final String NAME = "A_table";

}
