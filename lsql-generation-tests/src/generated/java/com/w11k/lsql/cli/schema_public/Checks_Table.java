package com.w11k.lsql.cli.schema_public;

@javax.inject.Singleton
public class Checks_Table extends com.w11k.lsql.TypedTable<Checks_Row, com.w11k.lsql.NoPrimaryKeyColumn>  {

    @javax.inject.Inject
    public Checks_Table(com.w11k.lsql.LSql lSql) {
        super(lSql, "CHECKS", Checks_Row.class);
    }

    public static final String NAME = "CHECKS";

    protected Checks_Row createFromInternalMap(java.util.Map<String, Object> internalMap) {
        return Checks_Row.fromInternalMap(internalMap);
    }

}
