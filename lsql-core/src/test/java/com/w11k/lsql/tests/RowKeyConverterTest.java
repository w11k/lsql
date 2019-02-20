package com.w11k.lsql.tests;

import com.w11k.lsql.LinkedRow;
import com.w11k.lsql.Row;
import com.w11k.lsql.Table;
import com.w11k.lsql.dialects.RowKeyConverter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.w11k.lsql.Row.fromKeyVals;
import static org.testng.Assert.assertEquals;

public class RowKeyConverterTest extends AbstractLSqlTest {

    @BeforeMethod
    public void setRowKeyConverter() {
        addConfigHook(config -> {
            config.setRowKeyConverter(RowKeyConverter.JAVA_CAMEL_CASE_TO_SQL_LOWER_UNDERSCORE);
        });
    }

    @Test
    public void testTableNameCamelCase() {
        createTable("CREATE TABLE aaa_bbb (ccc_ddd INT NULL)");
        lSql.table("AAA_BBb");
    }

    @Test
    public void testColumnNameCamelCase() {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY, ccc_ddd INT NULL)");
        Table table1 = lSql.table("table1");
        table1.insert(fromKeyVals("id", 1, "cccDdd", 2));
        assertEquals(table1.load(1).get().getInt("cccDdd"), new Integer(2));
    }

    @Test
    public void rowKeyConversions_JavaCamelCase() throws SQLException {
        createTable("CREATE TABLE table1 (id_pk INTEGER PRIMARY KEY, aaa_bbb INT)");
        Table table1 = lSql.table("table1");
        table1.insert(Row.fromKeyVals("idPk", 1, "aaaBbb", 10));

        // validate insert
        Statement st = this.lSql.createStatement();
        ResultSet resultSet = st.executeQuery("select * from table1;");
        int id_pk = -1;
        int aaa_bbb = -1;
        while (resultSet.next()) {
            id_pk = resultSet.getInt("id_pk");
            aaa_bbb = resultSet.getInt("aaa_bbb");
            break;
        }
        assertEquals(id_pk, 1);
        assertEquals(aaa_bbb, 10);


        // validate load
        LinkedRow loadedRow = table1.load(1).get();
        assertEquals(loadedRow.get("idPk"), 1);
        assertEquals(loadedRow.get("aaaBbb"), 10);
    }

    @Test
    public void rowKeyConversionsForPrimaryColumn_JavaCamelCase() throws SQLException {
        createTable("CREATE TABLE table1 (id_pk INTEGER PRIMARY KEY, aaa_bbb INT)");
        Table table1 = lSql.table("table1");
        table1.insert(Row.fromKeyVals("idPk", 1, "aaaBbb", 10));

        // validate insert
        Statement st = this.lSql.createStatement();
        ResultSet resultSet = st.executeQuery("select * from table1;");
        int id_pk = -1;
        int aaa_bbb = -1;
        while (resultSet.next()) {
            id_pk = resultSet.getInt("id_pk");
            aaa_bbb = resultSet.getInt("aaa_bbb");
            break;
        }
        assertEquals(id_pk, 1);
        assertEquals(aaa_bbb, 10);

        // validate load
        LinkedRow loadedRow = table1.load(1).get();
        assertEquals(loadedRow.get("idPk"), 1);
        assertEquals(loadedRow.get("aaaBbb"), 10);
    }

    @Test
    public void rowKeyConversions_Noop() throws SQLException {
        addConfigHook(config -> {
            config.setRowKeyConverter(RowKeyConverter.NOOP);
        });
        createTable("CREATE TABLE table1 (id_pk INTEGER PRIMARY KEY, aaa_bbb INT)");
        Table table1 = lSql.table("table1");
        table1.insert(Row.fromKeyVals("id_pk", 1, "aaa_bbb", 10));

        // validate insert
        Statement st = this.lSql.createStatement();
        ResultSet resultSet = st.executeQuery("select * from table1;");
        int id_pk = -1;
        int aaa_bbb = -1;
        while (resultSet.next()) {
            id_pk = resultSet.getInt("id_pk");
            aaa_bbb = resultSet.getInt("aaa_bbb");
            break;
        }
        assertEquals(id_pk, 1);
        assertEquals(aaa_bbb, 10);

        // validate load
        LinkedRow loadedRow = table1.load(1).get();
        assertEquals(loadedRow.get("id_pk"), 1);
        assertEquals(loadedRow.get("aaa_bbb"), 10);
    }

}
