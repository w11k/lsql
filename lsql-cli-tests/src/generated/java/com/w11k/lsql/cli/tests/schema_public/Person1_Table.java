package com.w11k.lsql.cli.tests.schema_public;

public class Person1_Table extends com.w11k.lsql.TypedTable<Person1_Row, java.lang.Integer>  {

    @com.google.inject.Inject
    public Person1_Table(com.w11k.lsql.LSql lSql) {
        super(lSql, "PERSON1", Person1_Row.class);
    }

    public static final String NAME = "PERSON1";

    protected Person1_Row createFromInternalMap(java.util.Map<String, Object> internalMap) {
        return Person1_Row.fromInternalMap(internalMap);
    }

}
