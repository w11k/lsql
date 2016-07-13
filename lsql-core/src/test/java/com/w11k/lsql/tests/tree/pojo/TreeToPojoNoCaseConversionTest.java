package com.w11k.lsql.tests.tree.pojo;

import com.w11k.lsql.tests.AbstractLSqlTest;
import com.w11k.lsql.tests.tree.TreeTestData;
import com.w11k.lsql.utils.DebugUtils;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

public class TreeToPojoNoCaseConversionTest extends AbstractLSqlTest {

    private TreeTestData treeTestData;

    @Override
    public void beforeMethodHook() {
        this.treeTestData = new TreeTestData(this.lSql);
        this.treeTestData.insert();
    }

    @Test
    public void continentsWithFactsAndCountriesAndCities() {
        List<ContinentWithFactsCountriesCities> tree = this.treeTestData.getlSqlFile()
                .statement("continentsWithFactsAndCountriesAndCities_2", ContinentWithFactsCountriesCities.class).query().toTree();

        DebugUtils.prettyPrintJson(tree);

        assertEquals(tree.size(), 2);

        // Continent
        assertEquals(tree.get(0).getId(), 1);
        assertEquals(tree.get(0).getName(), "Europe");
        assertEquals(tree.get(1).getId(), 2);
        assertEquals(tree.get(1).getName(), "North America");

        // Facts
        assertEquals(tree.get(0).getFacts().size(), 2);

        assertEquals(tree.get(0).getFacts().get(0).getId(), 1);
        assertEquals(tree.get(0).getFacts().get(0).getContinentId(), 1);
        assertEquals(tree.get(0).getFacts().get(0).getFactName(), "Area");
        assertEquals(tree.get(0).getFacts().get(0).getFactValue(), "10,180,000 km2");

        assertEquals(tree.get(0).getFacts().get(1).getId(), 2);
        assertEquals(tree.get(0).getFacts().get(1).getContinentId(), 1);
        assertEquals(tree.get(0).getFacts().get(1).getFactName(), "Population");
        assertEquals(tree.get(0).getFacts().get(1).getFactValue(), "742,452,000");

        assertEquals(tree.get(1).getFacts().get(0).getId(), 3);
        assertEquals(tree.get(1).getFacts().get(0).getContinentId(), 2);
        assertEquals(tree.get(1).getFacts().get(0).getFactName(), "Area");
        assertEquals(tree.get(1).getFacts().get(0).getFactValue(), "24,709,000 km2");

        assertEquals(tree.get(1).getFacts().get(1).getId(), 4);
        assertEquals(tree.get(1).getFacts().get(1).getContinentId(), 2);
        assertEquals(tree.get(1).getFacts().get(1).getFactName(), "Largest city");
        assertEquals(tree.get(1).getFacts().get(1).getFactValue(), "Mexico City");

        // Countries
        assertEquals(tree.get(0).getCountries().size(), 2);

        assertEquals(tree.get(0).getCountries().get(0).getId(), 1);
        assertEquals(tree.get(0).getCountries().get(0).getContinentId(), 1);
        assertEquals(tree.get(0).getCountries().get(0).getName(), "Germany");

        assertEquals(tree.get(0).getCountries().get(1).getId(), 2);
        assertEquals(tree.get(0).getCountries().get(1).getContinentId(), 1);
        assertEquals(tree.get(0).getCountries().get(1).getName(), "Netherlands");

        assertEquals(tree.get(1).getCountries().get(0).getId(), 3);
        assertEquals(tree.get(1).getCountries().get(0).getContinentId(), 2);
        assertEquals(tree.get(1).getCountries().get(0).getName(), "USA");

        assertEquals(tree.get(1).getCountries().get(1).getId(), 4);
        assertEquals(tree.get(1).getCountries().get(1).getContinentId(), 2);
        assertEquals(tree.get(1).getCountries().get(1).getName(), "Canada");

        // Cities
        assertEquals(tree.get(0).getCountries().get(0).getCities().get(0).getId(), 1);
        assertEquals(tree.get(0).getCountries().get(0).getCities().get(0).getCountryId(), 1);
        assertEquals(tree.get(0).getCountries().get(0).getCities().get(0).getName(), "Esslingen");

        assertEquals(tree.get(0).getCountries().get(0).getCities().get(1).getId(), 2);
        assertEquals(tree.get(0).getCountries().get(0).getCities().get(1).getCountryId(), 1);
        assertEquals(tree.get(0).getCountries().get(0).getCities().get(1).getName(), "Bonn");

        assertEquals(tree.get(1).getCountries().get(0).getCities().get(0).getId(), 3);
        assertEquals(tree.get(1).getCountries().get(0).getCities().get(0).getCountryId(), 3);
        assertEquals(tree.get(1).getCountries().get(0).getCities().get(0).getName(), "Boston");

        assertEquals(tree.get(1).getCountries().get(0).getCities().get(1).getId(), 4);
        assertEquals(tree.get(1).getCountries().get(0).getCities().get(1).getCountryId(), 3);
        assertEquals(tree.get(1).getCountries().get(0).getCities().get(1).getName(), "New York");
    }


}
