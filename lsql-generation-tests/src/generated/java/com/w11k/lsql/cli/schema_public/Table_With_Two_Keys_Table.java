package com.w11k.lsql.cli.schema_public;

@javax.inject.Singleton
public class Table_With_Two_Keys_Table extends com.w11k.lsql.TypedTable<Table_With_Two_Keys_Row, com.w11k.lsql.NoPrimaryKeyColumn>  {

    @javax.inject.Inject
    public Table_With_Two_Keys_Table(com.w11k.lsql.LSql lSql) {
        super(lSql, "PUBLIC", "TABLE_WITH_TWO_KEYS", null, Table_With_Two_Keys_Row.class);
    }

    public static final String NAME = "TABLE_WITH_TWO_KEYS";

    protected Table_With_Two_Keys_Row createFromInternalMap(java.util.Map<String, Object> internalMap) {
        return Table_With_Two_Keys_Row.fromInternalMap(internalMap);
    }

}
