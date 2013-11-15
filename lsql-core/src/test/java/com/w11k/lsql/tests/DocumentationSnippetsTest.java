package com.w11k.lsql.tests;

import com.google.common.base.Optional;
import com.googlecode.flyway.core.Flyway;
import com.w11k.lsql.*;
import com.w11k.lsql.dialects.H2Dialect;
import org.apache.commons.dbcp.BasicDataSource;
import org.h2.Driver;
import org.testng.annotations.Test;

import java.sql.SQLException;

public class DocumentationSnippetsTest extends AbstractLSqlTest {

    @Test
    public void quickExample() throws ClassNotFoundException, SQLException {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(Driver.class.getName());
        dataSource.setUrl("jdbc:h2:mem:testdb;mode=postgresql");
        dataSource.setDefaultAutoCommit(true);

        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.clean();

        LSql lsql = new LSql(new H2Dialect(), dataSource);

        lsql.executeRawSql("CREATE TABLE persons (id SERIAL PRIMARY KEY, name TEXT, age INT)");

        // Create a new person
        Row john = new Row();
        john.put("name", "John");
        john.put("age", 20);

        // Insert the new person
        Table persons = lsql.table("persons");
        persons.insert(john);

        // The generated ID is automatically put into the row object
        Object newId = john.get("id");

        // Use the ID to load the row, returns com.google.common.base.Optional
        Optional<LinkedRow> queried = persons.get(newId);
        LinkedRow queriedJohn = queried.get();

        assert queriedJohn.getString("name").equals("John");
        assert queriedJohn.getInt("age") == 20;
    }

}
