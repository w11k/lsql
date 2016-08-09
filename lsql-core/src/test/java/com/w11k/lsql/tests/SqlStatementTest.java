package com.w11k.lsql.tests;

import com.google.common.collect.Lists;
import com.w11k.lsql.*;
import com.w11k.lsql.dialects.PostgresDialect;
import com.w11k.lsql.exceptions.QueryException;
import com.w11k.lsql.query.RowQuery;
import com.w11k.lsql.statement.AbstractSqlStatement;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class SqlStatementTest extends AbstractLSqlTest {

    @Test
    public void statementNoParameter() {
        setup();
        List<Row> rows = lSql.executeQuery("SELECT * FROM person").query().toList();
        assertEquals(rows.size(), 5);
    }

    @Test(expectedExceptions = QueryException.class)
    public void statementUnusedParameter() {
        setup();
        List<Row> rows = lSql.executeQuery("SELECT * FROM person WHERE id = /*xxxxx=*/ -1 /**/;").query(
                "id", 1
        ).toList();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementOneParameter() {
        setup();
        List<Row> rows = lSql.executeQuery("SELECT * FROM person WHERE id = /*id=*/ -1 /**/;").query("id", 1).toList();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementOneUnnamed() {
        setup();
        List<Row> rows = lSql.executeQuery("SELECT * FROM person WHERE id = /*=*/ -1 /**/;").query("id", 1).toList();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementOneUnnamed2() {
        setup();
        List<Row> rows = lSql.executeQuery("SELECT * FROM person WHERE id != /*=*/ -1 /**/;").query("id", 1).toList();
        assertEquals(rows.size(), 4);
    }

    @Test
    public void statementOneUnnamed3() {
        setup();
        List<Row> rows = lSql.executeQuery("SELECT * FROM person WHERE id!= /*=*/ -1 /**/;").query("id", 1).toList();
        assertEquals(rows.size(), 4);
    }

    @Test
    public void statementOneUnnamedWithTableNameInQuery() {
        setup();
        List<Row> rows = lSql.executeQuery("SELECT * FROM person WHERE person.id = /*=*/ -1 /**/;").query("person.id", 1).toList();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementOneParameterSpecialName1() {
        setup();
        List<Row> rows = lSql.executeQuery("SELECT * FROM person WHERE id = /*id_a=*/ -1 /**/;").query("id_a", 1).toList();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementOneParameterSpecialName2() {
        setup();
        List<Row> rows = lSql.executeQuery("SELECT * FROM person WHERE id = /*id_1=*/ -1 /**/;").query("id_1", 1).toList();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementOneParameterSpecialName3() {
        setup();
        List<Row> rows = lSql.executeQuery("SELECT * FROM person WHERE id = /*person.id=*/ -1 /**/;").query("person.id", 1).toList();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementOneParameterSpecialName4() {
        setup();
        List<Row> rows = lSql.executeQuery("SELECT * FROM person WHERE id = /* person.id = */ -1 /**/;").query("person.id", 1).toList();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementOneParameterMultipleOccurence() {
        setup();
        List<Row> rows = lSql.executeQuery("SELECT * FROM person WHERE " +
                "id > /*val=*/ 99999 /**/\n" +
                "AND age > /*val=*/ 99999 /**/" +
                ";")
                .query("val", 3).toList();
        assertEquals(rows.size(), 2);
    }

    @Test
    public void statementTwoParameters() {
        setup();
        List<Row> rows = lSql.executeQuery("SELECT * FROM person WHERE " +
                "id = /*id=*/ -1 /**/\n" +
                "AND age = /*age=*/ -1 /**/" +
                ";")
                .query(
                        "id", 2,
                        "age", 12
                ).toList();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementThreeParameters() {
        setup();
        List<Row> rows = lSql.executeQuery("SELECT * FROM person WHERE " +
                "id = /*id=*/ -1 /**/\n" +
                "AND age = /*age=*/ -1 /**/\n" +
                "AND fullname = /*fullname=*/ 'xxx' /**/" +
                ";")
                .query(
                        "id", 3,
                        "age", 13,
                        "fullname", "c"
                ).toList();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementThreeParametersInOneLine() {
        setup();
        List<Row> rows = lSql.executeQuery(
                "SELECT * FROM person WHERE " +
                        "id = /*id=*/ -1 /**/ AND age = /*age=*/ -1 /**/ AND fullname = /*fullname=*/ 'xxx' /**/;")
                .query(
                        "id", 3,
                        "age", 13,
                        "fullname", "c"
                ).toList();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementThreeParametersUnused() {
        setup();
        List<Row> rows = lSql.executeQuery(
                "SELECT * FROM person WHERE " +
                        "id = /*id=*/ 1 /**/ AND age = /*age=*/ 11 /**/ AND fullname = /*fullname=*/ 'a' /**/;")
                .query().toList();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementThreeParametersUnnamed() {
        setup();
        List<Row> rows = lSql.executeQuery("SELECT * FROM person WHERE " +
                "id = /*=*/ -1 /**/" +
                "AND age = /*=*/ -1 /**/" +
                "AND fullname = /*=*/ 'xxx' /**/" +
                ";")
                .query(
                        "id", 4,
                        "age", 14,
                        "fullname", "d"
                ).toList();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementThreeParametersUnnamedMissingSpace() {
        setup();
        List<Row> rows = lSql.executeQuery("SELECT * FROM person WHERE " +
                "id = /*=*/ -1 /**/" +
                "AND age = /*=*/ -1 /**/" +
                "AND fullname= /*=*/ 'xxx' /**/" +
                ";")
                .query(
                        "id", 4,
                        "age", 14,
                        "fullname", "d"
                ).toList();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementThreeParametersMultipleOccurences() {
        setup();
        AbstractSqlStatement<RowQuery> statement = lSql.executeQuery("SELECT * FROM person WHERE " +
                "id > /*val=*/ 99999 /**/" +
                "AND age > /*val=*/ 99999 /**/" +
                "AND fullname= /*=*/ 'c' /**/" +
                ";");

        List<Row> rows = statement.query("val", 3, "fullname", "d").toList();
        assertEquals(rows.size(), 1);
        rows = statement.query("val", 4, "fullname", "e").toList();
        assertEquals(rows.size(), 1);
    }

    @Test
    public void statementQueryParameter() {
        setup();
        AbstractSqlStatement<RowQuery> statement = lSql.executeQuery("SELECT * FROM person WHERE id = /*=*/ 99999 /**/;");

        List<Row> rows = statement.query("id", new QueryParameter() {
            @Override
            public void set(PreparedStatement ps, int index) throws SQLException {
                ps.setInt(index, 1);
            }
        }).toList();
        assertEquals(rows.size(), 1);
    }

/*    @Test
    public void statementInAndOutConverter() {
        JavaBoolToSqlStringConverter converter = new JavaBoolToSqlStringConverter("positive", "negative");

        lSql.executeRawSql("CREATE TABLE T (id INT PRIMARY KEY, yesno VARCHAR(100));");
        Table t = lSql.table("T");
        t.column("yesno").setConverter(converter);
        t.newLinkedRow("id", 1, "yesno", true).insert();

        // Ensure that it was saved as a String
        Row row = lSql.executeRawQuery("SELECT * FROM T").first().get();
        assertEquals(row.get("yesno"), "positive");
        assertNotEquals(row.get("yesno"), true);

        // Ensure that table converter is working
        LinkedRow linkedRow = t.load(1).get();
        assertEquals(linkedRow.get("yesno"), true);
        assertNotEquals(linkedRow.get("yesno"), "positive");

        // Use IN converter
        row = statement("SELECT * FROM T WHERE yesno = *//*=*//* 'positive' *//**//*;")
                .addInConverter("yesno", converter)
                .query("yesno", true).first().get();
        assertEquals(row.get("yesno"), "positive");

        // Pass-through OUT converter
        row = statement("SELECT * FROM T WHERE yesno = *//*=*//* 'positive' *//**//*;")
                .addInConverter("yesno", converter)
                .addOutConverter("yesno", converter)
                .query("yesno", true).first().get();
        assertEquals(row.get("yesno"), true);

        // Pass-through table column converters to converters API 1
        row = statement("SELECT * FROM T WHERE yesno = *//*=*//* 'positive' *//**//*;")
                .setInConverters(t.getColumnConverters())
                .setOutConverters(t.getColumnConverters())
                .query("yesno", true).first().get();
        assertEquals(row.get("yesno"), true);

        // Pass-through table column converters to converters API 2
        row = statement("SELECT * FROM T WHERE yesno = *//*=*//* 'positive' *//**//*;")
                .setInAndOutConverters(t.getColumnConverters())
                .query("yesno", true).first().get();
        assertEquals(row.get("yesno"), true);
    }*/

    @Test
    public void literalQueryParameter() {
        setup();
        List<Row> rows;

        AbstractSqlStatement<RowQuery> statement = this.lSql.executeQuery("select * from person where " +
                "age in (/*ages=*/ 11, 12, 13 /**/) " +
                "and 1 = /*param=*/ 1 /**/;");

        final int[] ages = new int[]{11, 12};

        // Manual String concat
        rows = statement.query(
                "ages", new LiteralQueryParameter() {
                    @Override
                    public String getSqlString() {
                        return "11, 12";
                    }

                    @Override
                    public int getNumberOfQueryParameters() {
                        return 0;
                    }

                    @Override
                    public void set(PreparedStatement ps, int preparedStatementIndex, int localIndex) throws SQLException {
                    }
                },
                "param", 1
        ).toList();
        assertEquals(rows.size(), 2);

        // Use parameters
        rows = statement.query(
                "ages", new LiteralQueryParameter() {
                    @Override
                    public String getSqlString() {
                        return "?, ?";
                    }

                    @Override
                    public int getNumberOfQueryParameters() {
                        return 2;
                    }

                    @Override
                    public void set(PreparedStatement ps, int preparedStatementIndex, int localIndex) throws SQLException {
                        ps.setInt(preparedStatementIndex, ages[localIndex]);
                    }
                },
                "param", 1
        ).toList();
        assertEquals(rows.size(), 2);
    }

    @Test
    public void listLiteralQueryParameter() {
        setup();
        List<Row> rows;

        AbstractSqlStatement<RowQuery> statement = lSql.executeQuery("select * from person where" +
                " age in (/*ages=*/ 11, 12, 13 /**/) " +
                "and 1 = /*param=*/ 1 /**/;");

        // API Version 1
        List<Integer> ages = Lists.newArrayList(11, 12);
        rows = statement.query(
                "ages", ListLiteralQueryParameter.of(ages),
                "param", 1
        ).toList();
        assertEquals(rows.size(), 2);

        // API Version 2
        rows = statement.query(
                "ages", ListLiteralQueryParameter.of(11, 12),
                "param", 1
        ).toList();
        assertEquals(rows.size(), 2);
    }

    @Test()
    public void listLiteralQueryParameterEmptyArray() {
        boolean skipTest = lSql.getDialect() instanceof PostgresDialect;
        if (skipTest) {
            throw new SkipException("empty list literal not support");
        }

        setup();
        List<Row> rows;

        AbstractSqlStatement<RowQuery> statement = lSql.executeQuery("select * from person where" +
                " age in (/*ages=*/ 11, 12, 13 /**/) " +
                "and 1 = /*param=*/ 1 /**/;");

        // API Version 2, empty
        rows = statement.query(
                "ages", ListLiteralQueryParameter.of(),
                "param", 1
        ).toList();
        assertEquals(rows.size(), 0);
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

//    private AbstractSqlStatement<RowQuery> statement(String sqlString) {
//        SqlStatementToPreparedStatement stmtToPs = new SqlStatementToPreparedStatement(this.lSql, "testStatement", sqlString);
//        return new AbstractSqlStatement<RowQuery>(stmtToPs) {
//            @Override
//            protected RowQuery createQueryInstance(LSql lSql, PreparedStatement ps) {
//                return new RowQuery(lSql, ps);
//            }
//        };
//    }

}
