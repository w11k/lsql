package com.w11k.lsql;

import com.w11k.lsql.sqlfile.LSqlFile;
import com.w11k.lsql.tests.AbstractLSqlTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class QueryToTreeConverterTest extends AbstractLSqlTest {

    private LSqlFile lSqlFile;

    @BeforeMethod
    public void beforeMethod() {
        super.beforeMethod();
        this.lSqlFile = lSql.readSqlFile(getClass(), "tree.sql");

        createTable("CREATE TABLE table1 (id INT, name1 TEXT)");
        lSql.executeRawSql("INSERT INTO table1 VALUES (1, 'a')");
        lSql.executeRawSql("INSERT INTO table1 VALUES (2, 'b')");

        createTable("CREATE TABLE table2 (id INT, table1_id INT, name2 TEXT)");
        lSql.executeRawSql("INSERT INTO table2 VALUES (1, 1, 'a-2-a')");
        lSql.executeRawSql("INSERT INTO table2 VALUES (2, 1, 'a-2-b')");
        lSql.executeRawSql("INSERT INTO table2 VALUES (3, 2, 'b-2-a')");
        lSql.executeRawSql("INSERT INTO table2 VALUES (4, 2, 'b-2-b')");

        createTable("CREATE TABLE table3 (id INT, table1_id INT, name3 TEXT)");
        lSql.executeRawSql("INSERT INTO table3 VALUES (1, 1, 'a-3-a')");
        lSql.executeRawSql("INSERT INTO table3 VALUES (2, 1, 'a-3-b')");
        lSql.executeRawSql("INSERT INTO table3 VALUES (1, 2, 'b-3-a')");
        lSql.executeRawSql("INSERT INTO table3 VALUES (2, 2, 'b-3-b')");

    }

    private SqlStatement statement(String name) {
        return lSqlFile.statement(name);
    }

    @Test
    public void groupBy() {
        Query query = statement("tree").query();
        List<Row> tree = query.toList();
        for (Row row : tree) {
            System.out.println(row  );
        }

        System.out.println("----------------------------------------------------");

        query.toTree();

    }


}
