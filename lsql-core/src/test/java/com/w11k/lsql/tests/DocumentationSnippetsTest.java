package com.w11k.lsql.tests;

import com.google.common.base.Optional;
import com.googlecode.flyway.core.Flyway;
import com.w11k.lsql.LSql;
import com.w11k.lsql.dialects.H2Dialect;
import com.w11k.lsql.relational.QueriedRow;
import com.w11k.lsql.relational.Row;
import com.w11k.lsql.relational.Table;
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

        lsql.executeRawSql("CREATE TABLE person (id SERIAL PRIMARY KEY, name TEXT, age INT)");

        // Create a new person row
        Row newJohn = new Row();
        newJohn.put("name", "John");
        newJohn.put("age", 20);

        // Insert the new person
        Table tPerson = lsql.table("person");
        tPerson.insert(newJohn);

        // The generated ID is automatically put into the row object
        Object generatedId = newJohn.get("id");

        // Use the ID to load the row
        Optional<QueriedRow> queriedJohn = tPerson.get(generatedId);
        assert queriedJohn.get().getString("name").equals("John");
        assert queriedJohn.get().getInt("age") == 20;
    }

}
