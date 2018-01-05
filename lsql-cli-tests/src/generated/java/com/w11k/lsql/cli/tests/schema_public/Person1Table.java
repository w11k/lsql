package com.w11k.lsql.cli.tests.schema_public;

public class Person1Table extends com.w11k.lsql.TypedTable<Person1Row, java.lang.Integer>  {

    @com.google.inject.Inject
    public Person1Table(com.w11k.lsql.LSql lSql) {
        super(lSql, "public.person1", com.w11k.lsql.cli.tests.schema_public.Person1Row.class);
    }

    public static final String NAME = "public.person1";

}
