package com.w11k.lsql.tests;

import com.w11k.lsql.Row;
import com.w11k.lsql.Table;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.converter.JavaBoolToSqlStringConverter;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class ConverterTest extends AbstractLSqlTest {

    private final Converter javaBoolToSqlYesNoStringConverter = new JavaBoolToSqlStringConverter("yes", "no");

    @Test
    public void converterForColumnValue() {
        createTable("CREATE TABLE table1 (yesno1 TEXT, yesno2 TEXT)");
        Table t1 = lSql.table("table1");
        t1.column("yesno1").setConverter(javaBoolToSqlYesNoStringConverter);
        t1.insert(Row.fromKeyVals("yesno1", true, "yesno2", "true"));
        Row row = lSql.executeRawQuery("SELECT * FROM table1").getFirstRow().get();
        assertEquals(row.get("yesno1"), true);
        assertEquals(row.get("yesno2"), "true");
    }

}
