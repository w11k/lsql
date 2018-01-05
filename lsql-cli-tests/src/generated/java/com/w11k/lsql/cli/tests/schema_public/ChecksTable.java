package com.w11k.lsql.cli.tests.schema_public;

public class ChecksTable extends com.w11k.lsql.TypedTable<ChecksRow, com.w11k.lsql.TableLike.NoPrimaryKeyColumn>  {

    @com.google.inject.Inject
    public ChecksTable(com.w11k.lsql.LSql lSql) {
        super(lSql, "public.checks", com.w11k.lsql.cli.tests.schema_public.ChecksRow.class);
    }

    public static final String NAME = "public.checks";

}
