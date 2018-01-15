package com.w11k.lsql.cli.tests;

public class LSqlDbModule implements com.google.inject.Module {

    @Override
    public void configure(com.google.inject.Binder binder) {
        binder.bind(com.w11k.lsql.cli.tests.schema_public.Person1_Table.class).asEagerSingleton();
        binder.bind(com.w11k.lsql.cli.tests.schema_public.A_table_Table.class).asEagerSingleton();
        binder.bind(com.w11k.lsql.cli.tests.schema_public.Checks_Table.class).asEagerSingleton();
        binder.bind(com.w11k.lsql.cli.tests.schema_public.Person2_Table.class).asEagerSingleton();
        binder.bind(com.w11k.lsql.cli.tests.StmtsOnlyVoids.class).asEagerSingleton();
        binder.bind(com.w11k.lsql.cli.tests.Stmts1.class).asEagerSingleton();
        binder.bind(com.w11k.lsql.cli.tests.subdir.subsubdir.StmtsCamelCase2.class).asEagerSingleton();
    }

}
