package com.w11k.lsql.cli.schema_schema2;

@javax.inject.Singleton
public class Table_A_Table extends com.w11k.lsql.TypedTable<Table_A_Row, java.lang.Integer>  {

    @javax.inject.Inject
    public Table_A_Table(com.w11k.lsql.LSql lSql) {
        super(lSql, "SCHEMA2", "TABLE_A", null, Table_A_Row.class);
    }

    public static final String NAME = "TABLE_A";

    protected Table_A_Row createFromInternalMap(java.util.Map<String, Object> internalMap) {
        return Table_A_Row.fromInternalMap(internalMap);
    }

}
