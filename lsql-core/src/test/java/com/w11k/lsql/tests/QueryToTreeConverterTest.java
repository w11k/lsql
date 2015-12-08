package com.w11k.lsql.tests;

import com.w11k.lsql.Query;
import com.w11k.lsql.Row;
import com.w11k.lsql.SqlStatement;
import com.w11k.lsql.sqlfile.LSqlFile;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;

import static org.testng.Assert.assertEquals;

public class QueryToTreeConverterTest extends AbstractLSqlTest {

    private LSqlFile lSqlFile;

    @SuppressWarnings("SqlResolve")
    @BeforeMethod
    public void beforeMethod() {
        super.beforeMethod();
        this.lSqlFile = lSql.readSqlFile(getClass());

        createTable("CREATE TABLE table1 (id INT, name1 TEXT)");
        lSql.executeRawSql("INSERT INTO table1 VALUES (1, 'table1-a')");
        lSql.executeRawSql("INSERT INTO table1 VALUES (2, 'table1-b')");

        createTable("CREATE TABLE table2 (id INT, table1_id INT, name2 TEXT)");
        lSql.executeRawSql("INSERT INTO table2 VALUES (1, 1, 'table2-a')");
        lSql.executeRawSql("INSERT INTO table2 VALUES (2, 1, 'table2-b')");
        lSql.executeRawSql("INSERT INTO table2 VALUES (3, 2, 'table2-c')");
        lSql.executeRawSql("INSERT INTO table2 VALUES (4, 2, 'table2-d')");

        createTable("CREATE TABLE table2b (id INT, table2_id INT, name2b TEXT)");
        lSql.executeRawSql("INSERT INTO table2b VALUES (1, 1, 'table2b-a')");
        lSql.executeRawSql("INSERT INTO table2b VALUES (2, 1, 'table2b-b')");
        lSql.executeRawSql("INSERT INTO table2b VALUES (3, 3, 'table2b-c')");
        lSql.executeRawSql("INSERT INTO table2b VALUES (4, 3, 'table2b-d')");

        createTable("CREATE TABLE table3 (id INT, table1_id INT, name3 TEXT)");
        lSql.executeRawSql("INSERT INTO table3 VALUES (1, 1, 'table3-a')");
        lSql.executeRawSql("INSERT INTO table3 VALUES (2, 1, 'table3-b')");
        lSql.executeRawSql("INSERT INTO table3 VALUES (3, 2, 'table3-c')");
        lSql.executeRawSql("INSERT INTO table3 VALUES (4, 2, 'table3-d')");
    }

    private SqlStatement statement(String name) {
        return lSqlFile.statement(name);
    }

    @Test
    public void toTree1() {
        Query query = statement("tree1").query();
        LinkedHashMap<Number, Row> tree = query.toTree();
        assertEquals(tree.size(), 2);
        assertEquals(tree.get(1), Row.fromKeyVals("id", 1, "name1", "table1-a"));
        assertEquals(tree.get(2), Row.fromKeyVals("id", 2, "name1", "table1-b"));
    }

    @Test
    public void toTree2() {
        Query query = statement("tree2").query();
        LinkedHashMap<Number, Row> tree = query.toTree();

        assertEquals(tree.size(), 2);

        // Table1
        assertEquals(tree.get(1).getInt("id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getString("name1"), "table1-a");
        assertEquals(tree.get(2).getInt("id"), Integer.valueOf(2));
        assertEquals(tree.get(2).getString("name1"), "table1-b");

        // Table2
        assertEquals(tree.get(1).getTree("table2").get(1).size(), 3);
        assertEquals(tree.get(1).getTree("table2").get(1).getInt("id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("table2").get(1).getInt("table1_id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("table2").get(1).getString("name2"), "table2-a");

        assertEquals(tree.get(1).getTree("table2").get(2).size(), 3);
        assertEquals(tree.get(1).getTree("table2").get(2).getInt("id"), Integer.valueOf(2));
        assertEquals(tree.get(1).getTree("table2").get(2).getInt("table1_id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("table2").get(2).getString("name2"), "table2-b");

        assertEquals(tree.get(2).getTree("table2").get(3).size(), 3);
        assertEquals(tree.get(2).getTree("table2").get(3).getInt("id"), Integer.valueOf(3));
        assertEquals(tree.get(2).getTree("table2").get(3).getInt("table1_id"), Integer.valueOf(2));
        assertEquals(tree.get(2).getTree("table2").get(3).getString("name2"), "table2-c");

        assertEquals(tree.get(2).getTree("table2").get(4).size(), 3);
        assertEquals(tree.get(2).getTree("table2").get(4).getInt("id"), Integer.valueOf(4));
        assertEquals(tree.get(2).getTree("table2").get(4).getInt("table1_id"), Integer.valueOf(2));
        assertEquals(tree.get(2).getTree("table2").get(4).getString("name2"), "table2-d");
    }

    @Test
    public void toTree3() {
        Query query = statement("tree2and3").query();
        LinkedHashMap<Number, Row> tree = query.toTree();

        assertEquals(tree.size(), 2);

        // Table1
        assertEquals(tree.get(1).getInt("id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getString("name1"), "table1-a");
        assertEquals(tree.get(2).getInt("id"), Integer.valueOf(2));
        assertEquals(tree.get(2).getString("name1"), "table1-b");

        // Table2
        assertEquals(tree.get(1).getTree("table2").get(1).size(), 3);
        assertEquals(tree.get(1).getTree("table2").get(1).getInt("id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("table2").get(1).getInt("table1_id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("table2").get(1).getString("name2"), "table2-a");

        assertEquals(tree.get(1).getTree("table2").get(2).size(), 3);
        assertEquals(tree.get(1).getTree("table2").get(2).getInt("id"), Integer.valueOf(2));
        assertEquals(tree.get(1).getTree("table2").get(2).getInt("table1_id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("table2").get(2).getString("name2"), "table2-b");

        assertEquals(tree.get(2).getTree("table2").get(3).size(), 3);
        assertEquals(tree.get(2).getTree("table2").get(3).getInt("id"), Integer.valueOf(3));
        assertEquals(tree.get(2).getTree("table2").get(3).getInt("table1_id"), Integer.valueOf(2));
        assertEquals(tree.get(2).getTree("table2").get(3).getString("name2"), "table2-c");

        assertEquals(tree.get(2).getTree("table2").get(4).size(), 3);
        assertEquals(tree.get(2).getTree("table2").get(4).getInt("id"), Integer.valueOf(4));
        assertEquals(tree.get(2).getTree("table2").get(4).getInt("table1_id"), Integer.valueOf(2));
        assertEquals(tree.get(2).getTree("table2").get(4).getString("name2"), "table2-d");

        // Table3
        assertEquals(tree.get(1).getTree("table2").get(1).size(), 3);
        assertEquals(tree.get(1).getTree("table3").get(1).getInt("id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("table3").get(1).getInt("table1_id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("table3").get(1).getString("name3"), "table3-a");

        assertEquals(tree.get(1).getTree("table2").get(2).size(), 3);
        assertEquals(tree.get(1).getTree("table3").get(2).getInt("id"), Integer.valueOf(2));
        assertEquals(tree.get(1).getTree("table3").get(2).getInt("table1_id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("table3").get(2).getString("name3"), "table3-b");

        assertEquals(tree.get(2).getTree("table2").get(3).size(), 3);
        assertEquals(tree.get(2).getTree("table3").get(3).getInt("id"), Integer.valueOf(3));
        assertEquals(tree.get(2).getTree("table3").get(3).getInt("table1_id"), Integer.valueOf(2));
        assertEquals(tree.get(2).getTree("table3").get(3).getString("name3"), "table3-c");

        assertEquals(tree.get(2).getTree("table2").get(4).size(), 3);
        assertEquals(tree.get(2).getTree("table3").get(4).getInt("id"), Integer.valueOf(4));
        assertEquals(tree.get(2).getTree("table3").get(4).getInt("table1_id"), Integer.valueOf(2));
        assertEquals(tree.get(2).getTree("table3").get(4).getString("name3"), "table3-d");
    }

    @Test
    public void tree2Nested2bAnd3() {
        Query query = statement("tree2Nested2bAnd3").query();
        internalTestTree2Nested2bAnd3(query);
    }

    @Test
    public void syntax1() {
        Query query = statement("syntax1").query();
        internalTestTree2Nested2bAnd3(query);
    }

    private void internalTestTree2Nested2bAnd3(Query query) {
        LinkedHashMap<Number, Row> tree = query.toTree();
        assertEquals(tree.size(), 2);
        assertEquals(tree.get(1).getTree("table2").get(1).size(), 4);
        assertEquals(tree.get(1).getTree("table2").get(1).getTree("table2b").size(), 2);
        assertEquals(tree.get(2).getTree("table2").get(3).getTree("table2b").size(), 2);

        assertEquals(tree.get(1).getTree("table2").get(1).getTree("table2b").get(1).getInt("id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("table2").get(1).getTree("table2b").get(1).getInt("table2_id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("table2").get(1).getTree("table2b").get(1).getString("name2b"), "table2b-a");

        assertEquals(tree.get(1).getTree("table2").get(1).getTree("table2b").get(2).getInt("id"), Integer.valueOf(2));
        assertEquals(tree.get(1).getTree("table2").get(1).getTree("table2b").get(2).getInt("table2_id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("table2").get(1).getTree("table2b").get(2).getString("name2b"), "table2b-b");

        assertEquals(tree.get(2).getTree("table2").get(3).getTree("table2b").get(3).getInt("id"), Integer.valueOf(3));
        assertEquals(tree.get(2).getTree("table2").get(3).getTree("table2b").get(3).getInt("table2_id"), Integer.valueOf(3));
        assertEquals(tree.get(2).getTree("table2").get(3).getTree("table2b").get(3).getString("name2b"), "table2b-c");

        assertEquals(tree.get(2).getTree("table2").get(3).getTree("table2b").get(4).getInt("id"), Integer.valueOf(4));
        assertEquals(tree.get(2).getTree("table2").get(3).getTree("table2b").get(4).getInt("table2_id"), Integer.valueOf(3));
        assertEquals(tree.get(2).getTree("table2").get(3).getTree("table2b").get(4).getString("name2b"), "table2b-d");
    }


}
