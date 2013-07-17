package com.w11k.lsql_example;

import com.google.common.base.Function;
import com.w11k.relda.ConnectionFactories;
import com.w11k.relda.LMap;
import com.w11k.relda.LSql;
import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;
import java.util.List;

public class LSqlExample {

    public static void main(String[] args) throws Exception {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:testdb;MODE=PostgreSQL");
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(true);

        LSql lSql = new LSql(ConnectionFactories.fromInstance(connection));

        lSql.execute("create table persons (name text, age int);");
        LMap person1 = LMap.fromKeyVals("name", "Joe", "age", 10);
        LMap person2 = LMap.fromKeyVals("name", "John", "age", 20);
        lSql.executeInsert("persons", person1);
        lSql.executeInsert("persons", person2);

        List<Integer> ages = lSql.executeQuery("select * from persons")
                .map(new Function<LMap, Integer>() {
                    public Integer apply(LMap input) {
                        return input.getInt("age");
                    }
                });

        int sum = 0;
        for (int age : ages) {
            sum += age;
        }
        System.out.println("SUM = " + sum);
    }

}
