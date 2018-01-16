package com.w11k.lsql.cli.tests.schema_public;

public class A_Table_Table extends com.w11k.lsql.TypedTable<A_Table_Row, java.lang.Integer>  {

    @com.google.inject.Inject
    public A_Table_Table(com.w11k.lsql.LSql lSql) {
        super(lSql, "a_table", A_Table_Row.class);
    }

    public static final String NAME = "a_table";

}
