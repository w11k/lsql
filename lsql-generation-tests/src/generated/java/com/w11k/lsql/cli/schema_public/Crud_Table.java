package com.w11k.lsql.cli.schema_public;

@javax.inject.Singleton
public class Crud_Table extends com.w11k.lsql.TypedTable<Crud_Row, java.lang.Integer>  {

    @javax.inject.Inject
    public Crud_Table(com.w11k.lsql.LSql lSql) {
        super(lSql, "PUBLIC.CRUD", Crud_Row.class);
    }

    public static final String NAME = "CRUD";

    protected Crud_Row createFromInternalMap(java.util.Map<String, Object> internalMap) {
        return Crud_Row.fromInternalMap(internalMap);
    }

}
