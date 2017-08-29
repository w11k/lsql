package com.w11k.lsql.example;

import com.w11k.lsql.LSql;
import com.w11k.lsql.LinkedRow;
import com.w11k.lsql.Row;
import com.w11k.lsql.Table;
import com.w11k.lsql.dialects.H2Config;
import com.w11k.lsql.jdbc.ConnectionProviders;
import org.joda.time.DateTime;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;


public class CrudExamples {

    private LSql lSql;

    @BeforeMethod
    public void beforeMethod() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:lsqlexamples;mode=postgresql");
        lSql = new LSql(H2Config.class, ConnectionProviders.fromInstance(conn));

        lSql.executeRawSql("CREATE TABLE person (" +
                "id SERIAL PRIMARY KEY, " +
                "name TEXT, " +
                "birthday TIMESTAMP)");

        lSql.executeRawSql("CREATE TABLE address (" +
                "id SERIAL PRIMARY KEY, " +
                "person_id INT REFERENCES person(id), " +
                "city TEXT)");
    }

    @AfterMethod
    public void afterMethod() {
        lSql.executeRawSql("DROP TABLE address;");
        lSql.executeRawSql("DROP TABLE person;");
    }


    @Test
    public void insert() throws Exception {
        /*
        [[[
        fdsfjdsfdsfdsfds fdsjfk jds lfdsf

        - fsfdsfs
        - fjdfdslfs

        ```
        */
        Table personTable = lSql.table("person");

        // Option 1: java.util.Map
        Map<String, Object> person1 = new HashMap<String, Object>();
        person1.put("name", "John");
        person1.put("birthday", DateTime.parse("1980-10-1"));
        personTable.insert(new Row(person1));

        // Option 2: Use a Row class
        Row person2 = Row.fromKeyVals(
                "name", "Linus",
                "birthday", DateTime.parse("1970-10-1")
        );
        personTable.insert(person2);

        // Option 3: LinkedRow
        LinkedRow person3 = personTable.newLinkedRow(
                "name", "Linus",
                "birthday", DateTime.parse("1970-10-1")
        );
        person3.save();
        /*
        ```
        ]]]
         */

        // Row and LinkedRow are subclasses of java.util.Map
    }

    @Test
    public void get() throws Exception {
        insert();

        Table personTable = lSql.table("person");
        LinkedRow linkedRow = personTable.load(1).get();
        assert linkedRow.getString("name").equals("John");
    }



}
