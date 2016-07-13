package com.w11k.lsql.tests.tree;

import com.w11k.lsql.Row;
import com.w11k.lsql.query.RowQuery;
import com.w11k.lsql.tests.AbstractLSqlTest;
import com.w11k.lsql.utils.DebugUtils;
import org.testng.annotations.Test;

import java.util.List;

import static java.lang.Integer.valueOf;
import static org.testng.Assert.assertEquals;

public class RowQueryToTreeConverterTest extends AbstractLSqlTest {

    private TreeTestData treeTestData;

    @Override
    public void beforeMethodHook() {
        this.treeTestData = new TreeTestData(this.lSql);
        this.treeTestData.insert();
    }

    @Test
    public void continents() {
        RowQuery query = this.treeTestData.getlSqlFile().statement("continents").query();
        List<Row> tree = query.toTree();
        assertContinents(tree);
    }

    @Test
    public void continentsWithFacts() {
        RowQuery query = this.treeTestData.getlSqlFile().statement("continentsWithFacts").query();
        List<Row> tree = query.toTree();
        assertContinents(tree);
        assertFacts(tree);
    }

    @Test
    public void continentsWithFactsAndCountries() {
        RowQuery query = this.treeTestData.getlSqlFile().statement("continentsWithFactsAndCountries").query();
        List<Row> tree = query.toTree();
        assertContinents(tree);
        assertFacts(tree);
        assertCountries(tree);
    }


    @Test
    public void continentsWithFactsAndCountriesAndCities_1() {
        RowQuery query = this.treeTestData.getlSqlFile().statement("continentsWithFactsAndCountriesAndCities_1").query();
        List<Row> tree = query.toTree();
        assertContinents(tree);
        assertFacts(tree);
        assertCountries(tree);
        assertCities(tree);
    }

    @Test
    public void continentsWithFactsAndCountriesAndCities_2() {
        RowQuery query = this.treeTestData.getlSqlFile().statement("continentsWithFactsAndCountriesAndCities_2").query();
        List<Row> tree = query.toTree();
        DebugUtils.prettyPrintJson(tree);

        assertContinents(tree);
        assertFacts(tree);
        assertCountries(tree);
        assertCities(tree);
    }

    private void assertContinents(List<Row> tree) {
        assertEquals(tree.size(), 2);
        assertEquals(tree.get(0).getInt("id"), valueOf(1));
        assertEquals(tree.get(0).getString("name"), "Europe");
        assertEquals(tree.get(1).getInt("id"), valueOf(2));
        assertEquals(tree.get(1).getString("name"), "North America");
    }

    private void assertFacts(List<Row> tree) {
        assertEquals(tree.get(0).getAsListOf(Row.class, "facts").get(0).size(), 4);
        assertEquals(tree.get(0).getAsListOf(Row.class, "facts").get(0).getInt("id"), valueOf(1));
        assertEquals(tree.get(0).getAsListOf(Row.class, "facts").get(0).getInt("continentId"), valueOf(1));
        assertEquals(tree.get(0).getAsListOf(Row.class, "facts").get(0).getString("factName"), "Area");
        assertEquals(tree.get(0).getAsListOf(Row.class, "facts").get(0).getString("factValue"), "10,180,000 km2");

        assertEquals(tree.get(0).getAsListOf(Row.class, "facts").get(1).size(), 4);
        assertEquals(tree.get(0).getAsListOf(Row.class, "facts").get(1).getInt("id"), valueOf(2));
        assertEquals(tree.get(0).getAsListOf(Row.class, "facts").get(1).getInt("continentId"), valueOf(1));
        assertEquals(tree.get(0).getAsListOf(Row.class, "facts").get(1).getString("factName"), "Population");
        assertEquals(tree.get(0).getAsListOf(Row.class, "facts").get(1).getString("factValue"), "742,452,000");

        assertEquals(tree.get(1).getAsListOf(Row.class, "facts").get(0).size(), 4);
        assertEquals(tree.get(1).getAsListOf(Row.class, "facts").get(0).getInt("id"), valueOf(3));
        assertEquals(tree.get(1).getAsListOf(Row.class, "facts").get(0).getInt("continentId"), valueOf(2));
        assertEquals(tree.get(1).getAsListOf(Row.class, "facts").get(0).getString("factName"), "Area");
        assertEquals(tree.get(1).getAsListOf(Row.class, "facts").get(0).getString("factValue"), "24,709,000 km2");

        assertEquals(tree.get(1).getAsListOf(Row.class, "facts").get(1).size(), 4);
        assertEquals(tree.get(1).getAsListOf(Row.class, "facts").get(1).getInt("id"), valueOf(4));
        assertEquals(tree.get(1).getAsListOf(Row.class, "facts").get(1).getInt("continentId"), valueOf(2));
        assertEquals(tree.get(1).getAsListOf(Row.class, "facts").get(1).getString("factName"), "Largest city");
        assertEquals(tree.get(1).getAsListOf(Row.class, "facts").get(1).getString("factValue"), "Mexico City");
    }

    private void assertCountries(List<Row> tree) {
        // use delta of 1.0, since the country row might contain the cities array

        assertEquals(tree.get(0).getAsListOf(Row.class, "countries").get(0).size(), 3.0, 1.0);
        assertEquals(tree.get(0).getAsListOf(Row.class, "countries").get(0).getInt("id"), valueOf(1));
        assertEquals(tree.get(0).getAsListOf(Row.class, "countries").get(0).getInt("continentId"), valueOf(1));
        assertEquals(tree.get(0).getAsListOf(Row.class, "countries").get(0).getString("name"), "Germany");

        assertEquals(tree.get(0).getAsListOf(Row.class, "countries").get(1).size(), 3.0, 1.0);
        assertEquals(tree.get(0).getAsListOf(Row.class, "countries").get(1).getInt("id"), valueOf(2));
        assertEquals(tree.get(0).getAsListOf(Row.class, "countries").get(1).getInt("continentId"), valueOf(1));
        assertEquals(tree.get(0).getAsListOf(Row.class, "countries").get(1).getString("name"), "Netherlands");

        assertEquals(tree.get(1).getAsListOf(Row.class, "countries").get(0).size(), 3.0, 1.0);
        assertEquals(tree.get(1).getAsListOf(Row.class, "countries").get(0).getInt("id"), valueOf(3));
        assertEquals(tree.get(1).getAsListOf(Row.class, "countries").get(0).getInt("continentId"), valueOf(2));
        assertEquals(tree.get(1).getAsListOf(Row.class, "countries").get(0).getString("name"), "USA");

        assertEquals(tree.get(1).getAsListOf(Row.class, "countries").get(1).size(), 3.0, 1.0);
        assertEquals(tree.get(1).getAsListOf(Row.class, "countries").get(1).getInt("id"), valueOf(4));
        assertEquals(tree.get(1).getAsListOf(Row.class, "countries").get(1).getInt("continentId"), valueOf(2));
        assertEquals(tree.get(1).getAsListOf(Row.class, "countries").get(1).getString("name"), "Canada");
    }

    private void assertCities(List<Row> tree) {
        assertEquals(tree.get(0).getAsListOf(Row.class, "countries").get(0).getAsListOf(Row.class, "cities").size(), 2);

        assertEquals(tree.get(0).getAsListOf(Row.class, "countries").get(0).getAsListOf(Row.class, "cities").get(0).getInt("id"), Integer.valueOf(1));
        assertEquals(tree.get(0).getAsListOf(Row.class, "countries").get(0).getAsListOf(Row.class, "cities").get(0).getInt("countryId"), Integer.valueOf(1));
        assertEquals(tree.get(0).getAsListOf(Row.class, "countries").get(0).getAsListOf(Row.class, "cities").get(0).getString("name"), "Esslingen");

        assertEquals(tree.get(0).getAsListOf(Row.class, "countries").get(0).getAsListOf(Row.class, "cities").get(1).getInt("id"), Integer.valueOf(2));
        assertEquals(tree.get(0).getAsListOf(Row.class, "countries").get(0).getAsListOf(Row.class, "cities").get(1).getInt("countryId"), Integer.valueOf(1));
        assertEquals(tree.get(0).getAsListOf(Row.class, "countries").get(0).getAsListOf(Row.class, "cities").get(1).getString("name"), "Bonn");

        assertEquals(tree.get(1).getAsListOf(Row.class, "countries").get(0).getAsListOf(Row.class, "cities").size(), 2);

        assertEquals(tree.get(1).getAsListOf(Row.class, "countries").get(0).getAsListOf(Row.class, "cities").get(0).getInt("id"), Integer.valueOf(3));
        assertEquals(tree.get(1).getAsListOf(Row.class, "countries").get(0).getAsListOf(Row.class, "cities").get(0).getInt("countryId"), Integer.valueOf(3));
        assertEquals(tree.get(1).getAsListOf(Row.class, "countries").get(0).getAsListOf(Row.class, "cities").get(0).getString("name"), "Boston");

        assertEquals(tree.get(1).getAsListOf(Row.class, "countries").get(0).getAsListOf(Row.class, "cities").get(1).getInt("id"), Integer.valueOf(4));
        assertEquals(tree.get(1).getAsListOf(Row.class, "countries").get(0).getAsListOf(Row.class, "cities").get(1).getInt("countryId"), Integer.valueOf(3));
        assertEquals(tree.get(1).getAsListOf(Row.class, "countries").get(0).getAsListOf(Row.class, "cities").get(1).getString("name"), "New York");
    }

}
