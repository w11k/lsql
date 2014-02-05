package com.w11k.lsql.tests.dialects;

import com.w11k.lsql.QueriedRow;
import com.w11k.lsql.ResultSetColumn;
import com.w11k.lsql.dialects.BaseDialect;
import com.w11k.lsql.dialects.H2Dialect;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class H2Test extends AbstractDialectTests {

    @Override
    public BaseDialect createDialect() {
        return new H2Dialect();
    }

    @Override
    protected void setupTestTable() {
        lSql.executeRawSql("CREATE TABLE table1 (id SERIAL PRIMARY KEY, age INTEGER)");
    }

    @Override
    protected String getBlobColumnType() {
        return "BLOB";
    }

    @Override
    protected void validateColumnAliasBehaviour(QueriedRow queriedRow) {
        List<ResultSetColumn> resultSetColumns = queriedRow.getResultSetColumns();
        assertEquals(resultSetColumns.size(), 2);
        ResultSetColumn col = resultSetColumns.get(0);
        assertEquals(col.getPosition(), 1);
        assertEquals(col.getName(), "a");
        assertFalse(col.getColumn().getTable().isPresent());
    }
}
