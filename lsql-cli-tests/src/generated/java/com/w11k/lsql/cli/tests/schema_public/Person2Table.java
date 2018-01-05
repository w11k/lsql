package com.w11k.lsql.cli.tests.schema_public;

public class Person2Table extends com.w11k.lsql.TypedTable<Person2Row, java.lang.Integer>  {

    @com.google.inject.Inject
    public Person2Table(com.w11k.lsql.LSql lSql) {
        super(lSql, "public.person2", com.w11k.lsql.cli.tests.schema_public.Person2Row.class);
    }

    public static final String NAME = "public.person2";

}
