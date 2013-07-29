package com.w11k.lsql.tests;

import com.google.common.base.CaseFormat;
import com.w11k.lsql.JavaSqlConverter;
import com.w11k.lsql.Row;
import com.w11k.lsql.Table;
import org.testng.annotations.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.testng.Assert.assertEquals;

public class ConverterTest extends AbstractLSqlTest {


    private final JavaSqlConverter javaBoolToSqlYesNoStringConverter = new JavaSqlConverter() {
        @Override
        public Object getColumnValue(ResultSet rs, int index) throws SQLException {
            String val = rs.getString(index);
            if (val.equals("yes")) {
                return true;
            } else if (val.equals("no")) {
                return false;
            } else {
                throw new IllegalArgumentException("Value must be yes or no");
            }
        }

        @Override public String javaToSqlStringRepr(Object obj) {
            return ((Boolean) obj) ? "'yes'" : "'no'";
        }
    };

    @Test
    public void caseFormatConversion() {
        lSql.setGlobalConverter(new JavaSqlConverter(CaseFormat.LOWER_CAMEL, CaseFormat.UPPER_UNDERSCORE));

        lSql.execute("CREATE TABLE table1 (test_name1 TEXT, TEST_NAME2 TEXT)");
        lSql.execute("INSERT INTO table1 (test_name1, TEST_NAME2) VALUES ('name1', 'name2')");

        Row row = lSql.executeQuery("select * from table1").getFirstRow();
        assertEquals(row.get("testName1"), "name1");
        assertEquals(row.get("testName2"), "name2");
    }

    @Test public void converterGlobal() {
        lSql.execute("CREATE TABLE table1 (yesno TEXT)");
        Table table1 = lSql.table("table1");
        lSql.setGlobalConverter(javaBoolToSqlYesNoStringConverter);
        table1.insert(Row.fromKeyVals("yesno", true));
        Row row = lSql.executeQuery("select * from table1").getFirstRow();
        assertEquals(row.get("yesno"), true);
    }

    @Test public void converterForTable() {
        lSql.execute("CREATE TABLE table1 (yesno TEXT)");
        lSql.execute("CREATE TABLE table2 (yesno TEXT)");
        Table t1 = lSql.table("table1");
        Table t2 = lSql.table("table2");
        lSql.table("table1").setTableConverter(javaBoolToSqlYesNoStringConverter);
        t1.insert(Row.fromKeyVals("yesno", true));
        t2.insert(Row.fromKeyVals("yesno", "true"));
        Row row1 = lSql.executeQuery("select * from table1").getFirstRow();
        Row row2 = lSql.executeQuery("select * from table2").getFirstRow();
        assertEquals(row1.get("yesno"), true);
        assertEquals(row2.get("yesno"), "true");
    }

    @Test public void converterForColumnValue() {
        lSql.execute("CREATE TABLE table1 (yesno1 TEXT, yesno2 TEXT)");
        Table t1 = lSql.table("table1");
        lSql.table("table1").column("yesno1").setColumnConverter(javaBoolToSqlYesNoStringConverter);
        t1.insert(Row.fromKeyVals("yesno1", true, "yesno2", "true"));
        Row row = lSql.executeQuery("select * from table1").getFirstRow();
        assertEquals(row.get("yesno1"), true);
        assertEquals(row.get("yesno2"), "true");
    }

    @Test public void converterForColumnName() {
        lSql.execute("CREATE TABLE table1 (first_name text)");

        lSql.table("table1").column("firstName").setColumnConverter(
                new JavaSqlConverter(CaseFormat.LOWER_CAMEL, CaseFormat.UPPER_UNDERSCORE));

        lSql.table("table1").insert(Row.fromKeyVals("firstName", "John"));

        // TODO add test

    }

}
