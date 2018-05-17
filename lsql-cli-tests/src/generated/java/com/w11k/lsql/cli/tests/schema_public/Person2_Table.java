package com.w11k.lsql.cli.tests.schema_public;

public class Person2_Table extends com.w11k.lsql.TypedTable<Person2_Row, java.lang.Integer>  {

    @com.google.inject.Inject
    public Person2_Table(com.w11k.lsql.LSql lSql) {
        super(lSql, "PERSON2", Person2_Row.class);
    }

    public static final String NAME = "PERSON2";

    protected Person2_Row createFromInternalMap(java.util.Map<String, Object> internalMap) {
        return Person2_Row.fromInternalMap(internalMap);
    }

}
