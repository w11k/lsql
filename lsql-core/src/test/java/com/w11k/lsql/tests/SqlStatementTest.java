package com.w11k.lsql.tests;

import com.w11k.lsql.*;
import com.w11k.lsql.converter.JavaBoolToSqlStringConverter;
import com.w11k.lsql.exceptions.QueryException;
import org.testng.annotations.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

public class SqlStatementTest extends AbstractLSqlTest {

    @Test
    public void statementNoParameter() {
        setup();
        Rows rows = statement("SELECT * FROM person").query().rows();
        assertEquals(rows.size(), 5);
    }

    @Test(expectedExceptions = QueryException.class)
    public void statementUnusedParameter() {
        setup();
        Rows rows = statement("SELECT * FROM person WHERE id = /*xxxxx=*/ -1 /**/;").query(
                "id", 1
        ).rows();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementOneParameter() {
        setup();
        Rows rows = statement("SELECT * FROM person WHERE id = /*id=*/ -1 /**/;").query("id", 1).rows();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementOneUnnamed() {
        setup();
        Rows rows = statement("SELECT * FROM person WHERE id = /*=*/ -1 /**/;").query("id", 1).rows();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementOneUnnamed2() {
        setup();
        Rows rows = statement("SELECT * FROM person WHERE id != /*=*/ -1 /**/;").query("id", 1).rows();
        assertEquals(rows.size(), 4);
    }

    @Test
    public void statementOneUnnamed3() {
        setup();
        Rows rows = statement("SELECT * FROM person WHERE id!= /*=*/ -1 /**/;").query("id", 1).rows();
        assertEquals(rows.size(), 4);
    }

    @Test
    public void statementOneUnnamedWithTableNameInQuery() {
        setup();
        Rows rows = statement("SELECT * FROM person WHERE person.id = /*=*/ -1 /**/;").query("person.id", 1).rows();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementOneParameterSpecialName1() {
        setup();
        Rows rows = statement("SELECT * FROM person WHERE id = /*id_a=*/ -1 /**/;").query("id_a", 1).rows();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementOneParameterSpecialName2() {
        setup();
        Rows rows = statement("SELECT * FROM person WHERE id = /*id_1=*/ -1 /**/;").query("id_1", 1).rows();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementOneParameterSpecialName3() {
        setup();
        Rows rows = statement("SELECT * FROM person WHERE id = /*person.id=*/ -1 /**/;").query("person.id", 1).rows();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementOneParameterSpecialName4() {
        setup();
        Rows rows = statement("SELECT * FROM person WHERE id = /* person.id = */ -1 /**/;").query("person.id", 1).rows();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementOneParameterMultipleOccurence() {
        setup();
        Rows rows = statement("SELECT * FROM person WHERE " +
                "id > /*val=*/ 99999 /**/\n" +
                "AND age > /*val=*/ 99999 /**/" +
                ";")
                .query("val", 3).rows();
        assertEquals(rows.size(), 2);
    }

    @Test
    public void statementTwoParameters() {
        setup();
        Rows rows = statement("SELECT * FROM person WHERE " +
                "id = /*id=*/ -1 /**/\n" +
                "AND age = /*age=*/ -1 /**/" +
                ";")
                .query(
                        "id", 2,
                        "age", 12
                ).rows();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementThreeParameters() {
        setup();
        Rows rows = statement("SELECT * FROM person WHERE " +
                "id = /*id=*/ -1 /**/\n" +
                "AND age = /*age=*/ -1 /**/\n" +
                "AND fullname = /*fullname=*/ 'xxx' /**/" +
                ";")
                .query(
                        "id", 3,
                        "age", 13,
                        "fullname", "c"
                ).rows();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementThreeParametersInOneLine() {
        setup();
        Rows rows = statement(
                "SELECT * FROM person WHERE " +
                        "id = /*id=*/ -1 /**/ AND age = /*age=*/ -1 /**/ AND fullname = /*fullname=*/ 'xxx' /**/;")
                .query(
                        "id", 3,
                        "age", 13,
                        "fullname", "c"
                ).rows();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementThreeParametersUnused() {
        setup();
        Rows rows = statement(
                "SELECT * FROM person WHERE " +
                        "id = /*id=*/ 1 /**/ AND age = /*age=*/ 11 /**/ AND fullname = /*fullname=*/ 'a' /**/;")
                .query().rows();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementThreeParametersUnnamed() {
        setup();
        Rows rows = statement("SELECT * FROM person WHERE " +
                "id = /*=*/ -1 /**/" +
                "AND age = /*=*/ -1 /**/" +
                "AND fullname = /*=*/ 'xxx' /**/" +
                ";")
                .query(
                        "id", 4,
                        "age", 14,
                        "fullname", "d"
                ).rows();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementThreeParametersUnnamedMissingSpace() {
        setup();
        Rows rows = statement("SELECT * FROM person WHERE " +
                "id = /*=*/ -1 /**/" +
                "AND age = /*=*/ -1 /**/" +
                "AND fullname= /*=*/ 'xxx' /**/" +
                ";")
                .query(
                        "id", 4,
                        "age", 14,
                        "fullname", "d"
                ).rows();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementThreeParametersMultipleOccurences() {
        setup();
        SqlStatement statement = statement("SELECT * FROM person WHERE " +
                "id > /*val=*/ 99999 /**/" +
                "AND age > /*val=*/ 99999 /**/" +
                "AND fullname= /*=*/ 'c' /**/" +
                ";");

        Rows rows = statement.query("val", 3, "fullname", "d").rows();
        assertEquals(rows.size(), 1);
        rows = statement.query("val", 4, "fullname", "e").rows();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementQueryParameter() {
        setup();
        SqlStatement statement = statement("SELECT * FROM person WHERE id = /*=*/ 99999 /**/;");

        Rows rows = statement.query("id", new QueryParameter() {
            @Override
            public void set(PreparedStatement ps, int index) throws SQLException {
                ps.setInt(index, 1);
            }
        }).rows();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementInAndOutConverter() {
        JavaBoolToSqlStringConverter converter = new JavaBoolToSqlStringConverter("positive", "negative");

        lSql.executeRawSql("CREATE TABLE T (id INT PRIMARY KEY, yesno VARCHAR(100));");
        Table t = lSql.table("T");
        t.column("yesno").setConverter(converter);
        t.newLinkedRow("id", 1, "yesno", true).insert();

        // Ensure that it was saved as a String
        Row row = lSql.executeRawQuery("SELECT * FROM T").rows().first().get();
        assertEquals(row.get("yesno"), "positive");
        assertNotEquals(row.get("yesno"), true);

        // Ensure that table converter is working
        LinkedRow linkedRow = t.load(1).get();
        assertEquals(linkedRow.get("yesno"), true);
        assertNotEquals(linkedRow.get("yesno"), "positive");

        // Use IN converter
        row = statement("SELECT * FROM T WHERE yesno = /*=*/ 'positive' /**/;")
                .addInConverter("yesno", converter)
                .query("yesno", true).rows().first().get();
        assertEquals(row.get("yesno"), "positive");

        // Pass-through OUT converter
        row = statement("SELECT * FROM T WHERE yesno = /*=*/ 'positive' /**/;")
                .addInConverter("yesno", converter)
                .addOutConverter("yesno", converter)
                .query("yesno", true).rows().first().get();
        assertEquals(row.get("yesno"), true);

        // Pass-through table column converters to converters API 1
        row = statement("SELECT * FROM T WHERE yesno = /*=*/ 'positive' /**/;")
                .setInConverters(t.getColumnConverters())
                .setOutConverters(t.getColumnConverters())
                .query("yesno", true).rows().first().get();
        assertEquals(row.get("yesno"), true);

        // Pass-through table column converters to converters API 2
        row = statement("SELECT * FROM T WHERE yesno = /*=*/ 'positive' /**/;")
                .setInAndOutConverters(t.getColumnConverters())
                .query("yesno", true).rows().first().get();
        assertEquals(row.get("yesno"), true);
    }

    @Test
    public void removeLine() {
        setup();
        Rows rows = statement("SELECT * FROM person \n" +
                "WHERE id = /*id=*/ -1 /**/ \n" +
                ";")
                .query("id", SqlStatement.RAW_REMOVE_LINE).rows();
        assertEquals(rows.size(), 5);
    }

    private void setup() {
        createTable();
        insert(1, 11, "a");
        insert(2, 12, "b");
        insert(3, 13, "c");
        insert(4, 14, "d");
        insert(5, 15, "e");
    }

    private void createTable() {
        lSql.executeRawSql("CREATE TABLE person (" +
                "id INT PRIMARY KEY," +
                "age INT," +
                "fullname VARCHAR(100)" +
                ")");
    }

    private void insert(int id, int age, String fullname) {
        Table person = lSql.table("person");
        person.insert(Row.fromKeyVals("id", id, "age", age, "fullname", fullname));
    }

    private SqlStatement statement(String sqlString) {
        return new SqlStatement(this.lSql, "testStatement", sqlString);
    }

}
