package com.w11k.lsql.cli.schema_public;

@javax.inject.Singleton
public class Person1_Table extends com.w11k.lsql.TypedTable<Person1_Row, java.lang.Integer>  {

    @javax.inject.Inject
    public Person1_Table(com.w11k.lsql.LSql lSql) {
        super(lSql, "PUBLIC", "PERSON1", null, Person1_Row.class);
    }

    public static final String NAME = "PERSON1";

    protected Person1_Row createFromInternalMap(java.util.Map<String, Object> internalMap) {
        return Person1_Row.fromInternalMap(internalMap);
    }

}