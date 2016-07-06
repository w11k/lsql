package com.w11k.lsql.tests;

import com.w11k.lsql.Table;
import org.testng.annotations.Test;

import static com.w11k.lsql.Row.fromKeyVals;
import static com.w11k.lsql.dialects.IdentifierConverter.JAVA_LOWER_UNDERSCORE_TO_SQL_UPPER_UNDERSCORE;
import static org.testng.Assert.assertEquals;

public class IdentifierConverterTest extends AbstractLSqlTest {

    @Test
    public void testTableNameCamelCase() {
        createTable("CREATE TABLE aaa_bbb (ccc_ddd INT NULL)");
        lSql.table("aaaBbb");
    }

    @Test
    public void testTableNameUnderscore() {
        lSql.getDialect().setIdentifierConverter(JAVA_LOWER_UNDERSCORE_TO_SQL_UPPER_UNDERSCORE);
        createTable("CREATE TABLE AAA_BBB (CCC_DDD INT NULL)");
        lSql.table("aaa_bbb");
    }

    @Test
    public void testColumnNameCamelCase() {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY, ccc_ddd INT NULL)");
        Table table1 = lSql.table("table1");
        table1.insert(fromKeyVals("id", 1, "cccDdd", 2));
        assertEquals(table1.load(1).get().getInt("cccDdd"), new Integer(2));
    }

    @Test
    public void testColumnNameUnderscore() {
        lSql.getDialect().setIdentifierConverter(JAVA_LOWER_UNDERSCORE_TO_SQL_UPPER_UNDERSCORE);
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY, CCC_DDD INT NULL)");
        Table table1 = lSql.table("table1");
        table1.insert(fromKeyVals("id", 1, "ccc_ddd", 2));
        assertEquals(table1.load(1).get().getInt("ccc_ddd"), new Integer(2));
    }
}
