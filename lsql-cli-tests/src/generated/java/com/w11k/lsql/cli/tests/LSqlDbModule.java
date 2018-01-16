package com.w11k.lsql.cli.tests;

public class LSqlDbModule implements com.google.inject.Module {

    @Override
    public void configure(com.google.inject.Binder binder) {
        binder.bind(com.w11k.lsql.cli.tests.schema_public.Person1_Table.class).in(com.google.inject.Scopes.SINGLETON);
        binder.bind(com.w11k.lsql.cli.tests.schema_public.A_table_Table.class).in(com.google.inject.Scopes.SINGLETON);
        binder.bind(com.w11k.lsql.cli.tests.schema_public.Checks_Table.class).in(com.google.inject.Scopes.SINGLETON);
        binder.bind(com.w11k.lsql.cli.tests.schema_public.Person2_Table.class).in(com.google.inject.Scopes.SINGLETON);
        binder.bind(com.w11k.lsql.cli.tests.schema_public.Custom_converter_Table.class).in(com.google.inject.Scopes.SINGLETON);
        binder.bind(com.w11k.lsql.cli.tests.StmtsWithCustomConverter.class).in(com.google.inject.Scopes.SINGLETON);
        binder.bind(com.w11k.lsql.cli.tests.StmtsOnlyVoids.class).in(com.google.inject.Scopes.SINGLETON);
        binder.bind(com.w11k.lsql.cli.tests.Stmts1.class).in(com.google.inject.Scopes.SINGLETON);
        binder.bind(com.w11k.lsql.cli.tests.subdir.subsubdir.StmtsCamelCase2.class).in(com.google.inject.Scopes.SINGLETON);
    }

}
