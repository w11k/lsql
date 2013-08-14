package com.w11k.lsql_example;

import com.w11k.lsql.ConnectionFactories;
import com.w11k.lsql.LSql;
import com.w11k.lsql.Row;
import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;

public class LSqlExample {

    public static void main(String[] args) throws Exception {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:testdb;MODE=PostgreSQL");
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(true);

        LSql lSql = new LSql(ConnectionFactories.fromInstance(connection));

        lSql.executeRawSql("create table persons (name text, age int);");
        Row person1 = new Row(lSql, "persons").addKeyVals("name", "Joe", "age", 10);
        Row person2 = new Row(lSql, "persons").addKeyVals("name", "John", "age", 20);
        lSql.executeInsert(person1);
        lSql.executeInsert(person2);

        int sum = 0;
        for (Row row : lSql.executeRawQuery("select * from persons")) {
            sum += row.getInt("age");
        }
        System.out.println("SUM = " + sum);
    }

}
