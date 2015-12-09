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

        createTable("CREATE TABLE continent (id INT, name TEXT)");
        lSql.executeRawSql("INSERT INTO continent VALUES (1, 'Europe')");
        lSql.executeRawSql("INSERT INTO continent VALUES (2, 'North America')");

        createTable("CREATE TABLE continent_fact (id INT, continent_id INT, fact_name TEXT, fact_value TEXT)");
        lSql.executeRawSql("INSERT INTO continent_fact VALUES (1, 1, 'Area', '10,180,000 km2')");
        lSql.executeRawSql("INSERT INTO continent_fact VALUES (2, 1, 'Population', '742,452,000')");
        lSql.executeRawSql("INSERT INTO continent_fact VALUES (3, 2, 'Area', '24,709,000 km2')");
        lSql.executeRawSql("INSERT INTO continent_fact VALUES (4, 2, 'Largest city', 'Mexico City')");

        createTable("CREATE TABLE country (id INT, continent_id INT, name TEXT)");
        lSql.executeRawSql("INSERT INTO country VALUES (1, 1, 'Germany')");
        lSql.executeRawSql("INSERT INTO country VALUES (2, 1, 'Netherlands')");
        lSql.executeRawSql("INSERT INTO country VALUES (3, 2, 'USA')");
        lSql.executeRawSql("INSERT INTO country VALUES (4, 2, 'Canada')");

        createTable("CREATE TABLE city (id INT, country_id INT, name TEXT)");
        lSql.executeRawSql("INSERT INTO city VALUES (1, 1, 'Esslingen')");
        lSql.executeRawSql("INSERT INTO city VALUES (2, 1, 'Bonn')");
        lSql.executeRawSql("INSERT INTO city VALUES (3, 3, 'Boston')");
        lSql.executeRawSql("INSERT INTO city VALUES (4, 3, 'New York')");

    }

    private SqlStatement statement(String name) {
        return lSqlFile.statement(name);
    }

    @Test
    public void continents() {
        Query query = statement("continents").query();
        LinkedHashMap<Number, Row> tree = query.toTree();
        assertEquals(tree.size(), 2);
        assertEquals(tree.get(1), Row.fromKeyVals("id", 1, "name", "Europe"));
        assertEquals(tree.get(2), Row.fromKeyVals("id", 2, "name", "North America"));
    }

    @Test
    public void continentsWithFacts() {
        Query query = statement("continentsWithFacts").query();
        LinkedHashMap<Number, Row> tree = query.toTree();

        assertEquals(tree.size(), 2);

        // Continent
        assertEquals(tree.get(1).getInt("id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getString("name"), "Europe");
        assertEquals(tree.get(2).getInt("id"), Integer.valueOf(2));
        assertEquals(tree.get(2).getString("name"), "North America");

        // Continent Facts
        assertEquals(tree.get(1).getTree("facts").get(1).size(), 4);
        assertEquals(tree.get(1).getTree("facts").get(1).getInt("id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("facts").get(1).getInt("continent_id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("facts").get(1).getString("fact_name"), "Area");
        assertEquals(tree.get(1).getTree("facts").get(1).getString("fact_value"), "10,180,000 km2");

        assertEquals(tree.get(1).getTree("facts").get(2).size(), 4);
        assertEquals(tree.get(1).getTree("facts").get(2).getInt("id"), Integer.valueOf(2));
        assertEquals(tree.get(1).getTree("facts").get(2).getInt("continent_id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("facts").get(2).getString("fact_name"), "Population");
        assertEquals(tree.get(1).getTree("facts").get(2).getString("fact_value"), "742,452,000");

        assertEquals(tree.get(2).getTree("facts").get(3).size(), 4);
        assertEquals(tree.get(2).getTree("facts").get(3).getInt("id"), Integer.valueOf(3));
        assertEquals(tree.get(2).getTree("facts").get(3).getInt("continent_id"), Integer.valueOf(2));
        assertEquals(tree.get(2).getTree("facts").get(3).getString("fact_name"), "Area");
        assertEquals(tree.get(2).getTree("facts").get(3).getString("fact_value"), "24,709,000 km2");

        assertEquals(tree.get(2).getTree("facts").get(4).size(), 4);
        assertEquals(tree.get(2).getTree("facts").get(4).getInt("id"), Integer.valueOf(4));
        assertEquals(tree.get(2).getTree("facts").get(4).getInt("continent_id"), Integer.valueOf(2));
        assertEquals(tree.get(2).getTree("facts").get(4).getString("fact_name"), "Largest city");
        assertEquals(tree.get(2).getTree("facts").get(4).getString("fact_value"), "Mexico City");
    }

    @Test
    public void continentsWithFactsAndCountries() {
        Query query = statement("continentsWithFactsAndCountries").query();
        LinkedHashMap<Number, Row> tree = query.toTree();

        assertEquals(tree.size(), 2);

        // Continent
        assertEquals(tree.get(1).getInt("id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getString("name"), "Europe");
        assertEquals(tree.get(2).getInt("id"), Integer.valueOf(2));
        assertEquals(tree.get(2).getString("name"), "North America");

        // Continent Facts
        assertEquals(tree.get(1).getTree("facts").get(1).size(), 4);
        assertEquals(tree.get(1).getTree("facts").get(1).getInt("id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("facts").get(1).getInt("continent_id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("facts").get(1).getString("fact_name"), "Area");
        assertEquals(tree.get(1).getTree("facts").get(1).getString("fact_value"), "10,180,000 km2");

        assertEquals(tree.get(1).getTree("facts").get(2).size(), 4);
        assertEquals(tree.get(1).getTree("facts").get(2).getInt("id"), Integer.valueOf(2));
        assertEquals(tree.get(1).getTree("facts").get(2).getInt("continent_id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("facts").get(2).getString("fact_name"), "Population");
        assertEquals(tree.get(1).getTree("facts").get(2).getString("fact_value"), "742,452,000");

        assertEquals(tree.get(2).getTree("facts").get(3).size(), 4);
        assertEquals(tree.get(2).getTree("facts").get(3).getInt("id"), Integer.valueOf(3));
        assertEquals(tree.get(2).getTree("facts").get(3).getInt("continent_id"), Integer.valueOf(2));
        assertEquals(tree.get(2).getTree("facts").get(3).getString("fact_name"), "Area");
        assertEquals(tree.get(2).getTree("facts").get(3).getString("fact_value"), "24,709,000 km2");

        assertEquals(tree.get(2).getTree("facts").get(4).size(), 4);
        assertEquals(tree.get(2).getTree("facts").get(4).getInt("id"), Integer.valueOf(4));
        assertEquals(tree.get(2).getTree("facts").get(4).getInt("continent_id"), Integer.valueOf(2));
        assertEquals(tree.get(2).getTree("facts").get(4).getString("fact_name"), "Largest city");
        assertEquals(tree.get(2).getTree("facts").get(4).getString("fact_value"), "Mexico City");

        // Counry
        assertEquals(tree.get(1).getTree("countries").get(1).size(), 3);
        assertEquals(tree.get(1).getTree("countries").get(1).getInt("id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("countries").get(1).getInt("continent_id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("countries").get(1).getString("name"), "Germany");

        assertEquals(tree.get(1).getTree("countries").get(2).size(), 3);
        assertEquals(tree.get(1).getTree("countries").get(2).getInt("id"), Integer.valueOf(2));
        assertEquals(tree.get(1).getTree("countries").get(2).getInt("continent_id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("countries").get(2).getString("name"), "Netherlands");

        assertEquals(tree.get(2).getTree("countries").get(3).size(), 3);
        assertEquals(tree.get(2).getTree("countries").get(3).getInt("id"), Integer.valueOf(3));
        assertEquals(tree.get(2).getTree("countries").get(3).getInt("continent_id"), Integer.valueOf(2));
        assertEquals(tree.get(2).getTree("countries").get(3).getString("name"), "USA");

        assertEquals(tree.get(2).getTree("countries").get(4).size(), 3);
        assertEquals(tree.get(2).getTree("countries").get(4).getInt("id"), Integer.valueOf(4));
        assertEquals(tree.get(2).getTree("countries").get(4).getInt("continent_id"), Integer.valueOf(2));
        assertEquals(tree.get(2).getTree("countries").get(4).getString("name"), "Canada");
    }

    @Test
    public void continentsWithFactsAndCountriesAndCities_1() {
        Query query = statement("continentsWithFactsAndCountriesAndCities_1").query();
        internalContinentsWithFactsAndCountriesAndCities(query);
    }

    @Test
    public void continentsWithFactsAndCountriesAndCities_2() {
        Query query = statement("continentsWithFactsAndCountriesAndCities_2").query();
        internalContinentsWithFactsAndCountriesAndCities(query);
    }

    private void internalContinentsWithFactsAndCountriesAndCities(Query query) {
        LinkedHashMap<Number, Row> tree = query.toTree();
        assertEquals(tree.size(), 2);
        assertEquals(tree.get(1).getTree("countries").get(1).size(), 4);
        assertEquals(tree.get(1).getTree("countries").get(1).getTree("cities").size(), 2);
        assertEquals(tree.get(2).getTree("countries").get(3).getTree("cities").size(), 2);
        assertEquals(tree.get(1).getTree("countries").get(1).getTree("cities").get(1).getInt("id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("countries").get(1).getTree("cities").get(1).getInt("country_id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("countries").get(1).getTree("cities").get(1).getString("name"), "Esslingen");
        assertEquals(tree.get(1).getTree("countries").get(1).getTree("cities").get(2).getInt("id"), Integer.valueOf(2));
        assertEquals(tree.get(1).getTree("countries").get(1).getTree("cities").get(2).getInt("country_id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("countries").get(1).getTree("cities").get(2).getString("name"), "Bonn");
        assertEquals(tree.get(2).getTree("countries").get(3).getTree("cities").get(3).getInt("id"), Integer.valueOf(3));
        assertEquals(tree.get(2).getTree("countries").get(3).getTree("cities").get(3).getInt("country_id"), Integer.valueOf(3));
        assertEquals(tree.get(2).getTree("countries").get(3).getTree("cities").get(3).getString("name"), "Boston");
        assertEquals(tree.get(2).getTree("countries").get(3).getTree("cities").get(4).getInt("id"), Integer.valueOf(4));
        assertEquals(tree.get(2).getTree("countries").get(3).getTree("cities").get(4).getInt("country_id"), Integer.valueOf(3));
        assertEquals(tree.get(2).getTree("countries").get(3).getTree("cities").get(4).getString("name"), "New York");
    }


}
