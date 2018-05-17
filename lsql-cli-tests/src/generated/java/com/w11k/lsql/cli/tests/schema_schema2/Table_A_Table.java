package com.w11k.lsql.cli.tests.schema_schema2;

public class Table_A_Table extends com.w11k.lsql.TypedTable<Table_A_Row, java.lang.Integer>  {

    @com.google.inject.Inject
    public Table_A_Table(com.w11k.lsql.LSql lSql) {
        super(lSql, "table_a", Table_A_Row.class);
    }

    public static final String NAME = "table_a";

    protected Table_A_Row createFromInternalMap(java.util.Map<String, Object> internalMap) {
        return Table_A_Row.fromInternalMap(internalMap);
    }

}
