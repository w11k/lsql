package com.w11k.lsql.tests;

import com.google.common.collect.ImmutableMap;
import com.w11k.lsql.Row;
import com.w11k.lsql.Rows;
import com.w11k.lsql.SqlStatement;
import com.w11k.lsql.Table;
import com.w11k.lsql.exceptions.QueryException;
import com.w11k.lsql.sqlfile.LSqlFile;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class SqlFileReaderTest extends AbstractLSqlTest {

    @Test
    public void readSqlFileForClass() {
        LSqlFile lSqlFile = lSql.readSqlFile(getClass(), "twoStatements.sql");
        ImmutableMap<String, SqlStatement> stmts = lSqlFile.getStatements();
        assertEquals(stmts.size(), 2, "wrong number of SQL statements");
    }

    @Test
    public void execute() {
        statement("create table table1 (val int);").execute();
        Table table1 = lSql.table("table1");
        table1.insert(Row.fromKeyVals("val", 1));
        table1.insert(Row.fromKeyVals("val", 2));
        table1.insert(Row.fromKeyVals("val", 3));
        Rows rows = lSql.executeRawQuery("SELECT * FROM table1").rows();
        assertEquals(rows.size(), 3);
    }

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

//    @Test(expectedExceptions = IllegalArgumentException.class)
//    public void exceptionOnWrongStatementName() {
//        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "file1.sql");
//        lSqlFile.statement("ERRO");
//    }

//    @Test
//    public void executeSqlStatementWithParameters() {
//        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "file1.sql");
//        lSqlFile.statement("create1").execute();
//        Table table1 = lSql.table("table1");
//        table1.insert(Row.fromKeyVals("age", 1, "content", "text1")).get();
//        table1.insert(Row.fromKeyVals("age", 3, "content", "text2")).get();
//        table1.insert(Row.fromKeyVals("age", 6, "content", "text3")).get();
//
//        assertEquals(lSql.executeRawQuery("SELECT * FROM table1").asList().size(), 3);
//        SqlStatement deleteYoung = lSqlFile.statement("deleteYoung");
//        deleteYoung.execute("table1.age", 2);
//        assertEquals(lSql.executeRawQuery("SELECT * FROM table1").asList().size(), 2);
//    }

//    @Test
//    public void executeQueryWithoutChangingParameters() {
//        executeSqlStatement();
//        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "file1.sql");
//        SqlStatement qInt = lSqlFile.statement("queryRangeMarkers");
//        Query query = qInt.query();
//        assertEquals(query.asList().size(), 1);
//        String firstRow = query.firstRow().get().getString("content");
//        assertEquals(firstRow, "text1");
//    }

//    @Test(expectedExceptions = QueryException.class)
//    public void executeQueryWithUnusedParameter() {
//        executeSqlStatement();
//        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "file1.sql");
//        SqlStatement qInt = lSqlFile.statement("queryRangeMarkers");
//        qInt.query("WRONG", 1);
//    }

//    @Test
//    public void useNullValueInQuery() {
//        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "file1.sql");
//        lSqlFile.statement("create1").execute();
//        Table table1 = lSql.table("table1");
//        table1.insert(Row.fromKeyVals("age", null, "content", "text1"));
//        SqlStatement qInt = lSqlFile.statement("convertOperatorForNullValues");
//
//        Query query = qInt.query("age", null);
//        assertTrue(query.firstRow().isPresent());
//    }

//    @Test
//    public void parametersInQueryUseCustomColumnConverter() {
//        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "file1.sql");
//        lSqlFile.statement("create2").execute();
//
//        Table t2 = lSql.table("table2");
//        t2.column("number").setConverter(new Converter() {
//            public void setValue(LSql lSql, PreparedStatement ps, int index,
//                                 Object val) throws SQLException {
//                ps.setInt(index, ((IntWrapper) val).getI());
//            }
//
//            public Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException {
//                return new IntWrapper(rs.getInt(index));
//            }
//
//            public int getSqlTypeForNullValues() {
//                return Types.INTEGER;
//            }
//        });
//        Row r1 = Row.fromKeyVals("number", new IntWrapper(0));
//        t2.insert(r1);
//        Row r2 = Row.fromKeyVals("number", new IntWrapper(1));
//        t2.insert(r2);
//
//        QueriedRow row = lSqlFile.statement("queryColumnConverter").query().firstRow().get();
//        assertEquals(row.get("number"), new IntWrapper(0));
//
//        row = lSqlFile.statement("queryColumnConverter").query("table2.number", new IntWrapper(1))
//                .firstRow().get();
//        assertEquals(row, r1);
//    }

//    @Test
//    public void useFunctionCallbackAsParameter() {
//        executeSqlStatement();
//        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "file1.sql");
//        SqlStatement markers = lSqlFile.statement("queryFunctionCallback");
//        Query query = markers.query(
//                "age", new QueryParameter() {
//                    @Override
//                    public void set(PreparedStatement ps, int index) throws SQLException {
//                        double[] array = new double[]{10, 60};
//                        ps.setObject(index, array);
//                    }
//                }
//        );
//        List<QueriedRow> result = query.asList();
    // TODO does not work currently !!!
//        assertEquals(result.size(), 2);
//        assertEquals(result.load(0).getInt("age"), 10);
//        assertEquals(result.load(1).getInt("age"), 60);
//    }

//    @Test
//    public void fullnamedParameter() {
//        executeSqlStatement();
//        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "file1.sql");
//        SqlStatement markers = lSqlFile.statement("fullnamedParameter");
//        Query query = markers.query(
//                "foo", 60
//        );
//        List<QueriedRow> result = query.asList();
//        assertEquals(result.size(), 1);
//        assertEquals(result.get(0).getString("content"), "text3");
//    }


}
