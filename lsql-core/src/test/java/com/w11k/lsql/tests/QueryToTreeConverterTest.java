package com.w11k.lsql.tests;

import com.w11k.lsql.Query;
import com.w11k.lsql.Row;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;

import static org.testng.Assert.assertEquals;

public class QueryToTreeConverterTest extends AbstractLSqlTest {

    private TreeTestData treeTestData;

    @SuppressWarnings("SqlResolve")
    @BeforeMethod
    public void beforeMethod() {
        super.beforeMethod();
        this.treeTestData = new TreeTestData(this.lSql);
        this.treeTestData.insert();
    }

    @Test
    public void continents() {
        Query query = this.treeTestData.continents();
        LinkedHashMap<Number, Row> tree = query.toTree();
        assertEquals(tree.size(), 2);
        assertEquals(tree.get(1), Row.fromKeyVals("id", 1, "name", "Europe"));
        assertEquals(tree.get(2), Row.fromKeyVals("id", 2, "name", "North America"));
    }

    @Test
    public void continentsWithFacts() {
        Query query = this.treeTestData.continentsWithFacts();
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
        assertEquals(tree.get(1).getTree("facts").get(1).getInt("continentId"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("facts").get(1).getString("factName"), "Area");
        assertEquals(tree.get(1).getTree("facts").get(1).getString("factValue"), "10,180,000 km2");

        assertEquals(tree.get(1).getTree("facts").get(2).size(), 4);
        assertEquals(tree.get(1).getTree("facts").get(2).getInt("id"), Integer.valueOf(2));
        assertEquals(tree.get(1).getTree("facts").get(2).getInt("continentId"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("facts").get(2).getString("factName"), "Population");
        assertEquals(tree.get(1).getTree("facts").get(2).getString("factValue"), "742,452,000");

        assertEquals(tree.get(2).getTree("facts").get(3).size(), 4);
        assertEquals(tree.get(2).getTree("facts").get(3).getInt("id"), Integer.valueOf(3));
        assertEquals(tree.get(2).getTree("facts").get(3).getInt("continentId"), Integer.valueOf(2));
        assertEquals(tree.get(2).getTree("facts").get(3).getString("factName"), "Area");
        assertEquals(tree.get(2).getTree("facts").get(3).getString("factValue"), "24,709,000 km2");

        assertEquals(tree.get(2).getTree("facts").get(4).size(), 4);
        assertEquals(tree.get(2).getTree("facts").get(4).getInt("id"), Integer.valueOf(4));
        assertEquals(tree.get(2).getTree("facts").get(4).getInt("continentId"), Integer.valueOf(2));
        assertEquals(tree.get(2).getTree("facts").get(4).getString("factName"), "Largest city");
        assertEquals(tree.get(2).getTree("facts").get(4).getString("factValue"), "Mexico City");
    }

    @Test
    public void continentsWithFactsAndCountries() {
        Query query = this.treeTestData.continentsWithFactsAndCountries();
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
        assertEquals(tree.get(1).getTree("facts").get(1).getInt("continentId"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("facts").get(1).getString("factName"), "Area");
        assertEquals(tree.get(1).getTree("facts").get(1).getString("factValue"), "10,180,000 km2");

        assertEquals(tree.get(1).getTree("facts").get(2).size(), 4);
        assertEquals(tree.get(1).getTree("facts").get(2).getInt("id"), Integer.valueOf(2));
        assertEquals(tree.get(1).getTree("facts").get(2).getInt("continentId"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("facts").get(2).getString("factName"), "Population");
        assertEquals(tree.get(1).getTree("facts").get(2).getString("factValue"), "742,452,000");

        assertEquals(tree.get(2).getTree("facts").get(3).size(), 4);
        assertEquals(tree.get(2).getTree("facts").get(3).getInt("id"), Integer.valueOf(3));
        assertEquals(tree.get(2).getTree("facts").get(3).getInt("continentId"), Integer.valueOf(2));
        assertEquals(tree.get(2).getTree("facts").get(3).getString("factName"), "Area");
        assertEquals(tree.get(2).getTree("facts").get(3).getString("factValue"), "24,709,000 km2");

        assertEquals(tree.get(2).getTree("facts").get(4).size(), 4);
        assertEquals(tree.get(2).getTree("facts").get(4).getInt("id"), Integer.valueOf(4));
        assertEquals(tree.get(2).getTree("facts").get(4).getInt("continentId"), Integer.valueOf(2));
        assertEquals(tree.get(2).getTree("facts").get(4).getString("factName"), "Largest city");
        assertEquals(tree.get(2).getTree("facts").get(4).getString("factValue"), "Mexico City");

        // Counry
        assertEquals(tree.get(1).getTree("countries").get(1).size(), 3);
        assertEquals(tree.get(1).getTree("countries").get(1).getInt("id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("countries").get(1).getInt("continentId"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("countries").get(1).getString("name"), "Germany");

        assertEquals(tree.get(1).getTree("countries").get(2).size(), 3);
        assertEquals(tree.get(1).getTree("countries").get(2).getInt("id"), Integer.valueOf(2));
        assertEquals(tree.get(1).getTree("countries").get(2).getInt("continentId"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("countries").get(2).getString("name"), "Netherlands");

        assertEquals(tree.get(2).getTree("countries").get(3).size(), 3);
        assertEquals(tree.get(2).getTree("countries").get(3).getInt("id"), Integer.valueOf(3));
        assertEquals(tree.get(2).getTree("countries").get(3).getInt("continentId"), Integer.valueOf(2));
        assertEquals(tree.get(2).getTree("countries").get(3).getString("name"), "USA");

        assertEquals(tree.get(2).getTree("countries").get(4).size(), 3);
        assertEquals(tree.get(2).getTree("countries").get(4).getInt("id"), Integer.valueOf(4));
        assertEquals(tree.get(2).getTree("countries").get(4).getInt("continentId"), Integer.valueOf(2));
        assertEquals(tree.get(2).getTree("countries").get(4).getString("name"), "Canada");
    }

    @Test
    public void continentsWithFactsAndCountriesAndCities_1() {
        Query query = this.treeTestData.continentsWithFactsAndCountriesAndCities();
        internalContinentsWithFactsAndCountriesAndCities(query);
    }

    @Test
    public void continentsWithFactsAndCountriesAndCities_2() {
        Query query = this.treeTestData.continentsWithFactsAndCountriesAndCities_2();
        internalContinentsWithFactsAndCountriesAndCities(query);
    }

    private void internalContinentsWithFactsAndCountriesAndCities(Query query) {
        LinkedHashMap<Number, Row> tree = query.toTree();
        assertEquals(tree.size(), 2);
        assertEquals(tree.get(1).getTree("countries").get(1).size(), 4);
        assertEquals(tree.get(1).getTree("countries").get(1).getTree("cities").size(), 2);
        assertEquals(tree.get(2).getTree("countries").get(3).getTree("cities").size(), 2);
        assertEquals(tree.get(1).getTree("countries").get(1).getTree("cities").get(1).getInt("id"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("countries").get(1).getTree("cities").get(1).getInt("countryId"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("countries").get(1).getTree("cities").get(1).getString("name"), "Esslingen");
        assertEquals(tree.get(1).getTree("countries").get(1).getTree("cities").get(2).getInt("id"), Integer.valueOf(2));
        assertEquals(tree.get(1).getTree("countries").get(1).getTree("cities").get(2).getInt("countryId"), Integer.valueOf(1));
        assertEquals(tree.get(1).getTree("countries").get(1).getTree("cities").get(2).getString("name"), "Bonn");
        assertEquals(tree.get(2).getTree("countries").get(3).getTree("cities").get(3).getInt("id"), Integer.valueOf(3));
        assertEquals(tree.get(2).getTree("countries").get(3).getTree("cities").get(3).getInt("countryId"), Integer.valueOf(3));
        assertEquals(tree.get(2).getTree("countries").get(3).getTree("cities").get(3).getString("name"), "Boston");
        assertEquals(tree.get(2).getTree("countries").get(3).getTree("cities").get(4).getInt("id"), Integer.valueOf(4));
        assertEquals(tree.get(2).getTree("countries").get(3).getTree("cities").get(4).getInt("countryId"), Integer.valueOf(3));
        assertEquals(tree.get(2).getTree("countries").get(3).getTree("cities").get(4).getString("name"), "New York");
    }


}
