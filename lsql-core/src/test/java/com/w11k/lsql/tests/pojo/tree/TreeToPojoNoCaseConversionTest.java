package com.w11k.lsql.tests.pojo.tree;

import com.w11k.lsql.Query;
import com.w11k.lsql.tests.AbstractLSqlTest;
import com.w11k.lsql.tests.TreeTestData;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

public class TreeToPojoNoCaseConversionTest extends AbstractLSqlTest {

    private TreeTestData treeTestData;

    @SuppressWarnings("SqlResolve")
    @BeforeMethod
    public void beforeMethod() {
        super.beforeMethod();
        this.treeTestData = new TreeTestData(this.lSql);
        this.treeTestData.insert();
    }

    @Test
    public void continentsWithFactsAndCountriesAndCities() {
        Query query = this.treeTestData.continentsWithFactsAndCountriesAndCities();
        List<QueryContinent> continent = query.toPojo(QueryContinent.class);

        assertEquals(continent.size(), 2);

        // Continent
        assertEquals(continent.get(0).getId(), 1);
        assertEquals(continent.get(0).getName(), "Europe");
        assertEquals(continent.get(1).getId(), 2);
        assertEquals(continent.get(1).getName(), "North America");

        // Facts
        assertEquals(continent.get(0).getFacts().size(), 2);

        assertEquals(continent.get(0).getFacts().get(1).getId(), 1);
        assertEquals(continent.get(0).getFacts().get(1).getContinentId(), 1);
        assertEquals(continent.get(0).getFacts().get(1).getFactName(), "Area");
        assertEquals(continent.get(0).getFacts().get(1).getFactValue(), "10,180,000 km2");

        assertEquals(continent.get(0).getFacts().get(2).getId(), 2);
        assertEquals(continent.get(0).getFacts().get(2).getContinentId(), 1);
        assertEquals(continent.get(0).getFacts().get(2).getFactName(), "Population");
        assertEquals(continent.get(0).getFacts().get(2).getFactValue(), "742,452,000");

        assertEquals(continent.get(1).getFacts().get(3).getId(), 3);
        assertEquals(continent.get(1).getFacts().get(3).getContinentId(), 2);
        assertEquals(continent.get(1).getFacts().get(3).getFactName(), "Area");
        assertEquals(continent.get(1).getFacts().get(3).getFactValue(), "24,709,000 km2");

        assertEquals(continent.get(1).getFacts().get(4).getId(), 4);
        assertEquals(continent.get(1).getFacts().get(4).getContinentId(), 2);
        assertEquals(continent.get(1).getFacts().get(4).getFactName(), "Largest city");
        assertEquals(continent.get(1).getFacts().get(4).getFactValue(), "Mexico City");

        // Countries
        assertEquals(continent.get(0).getCountries().size(), 2);

        assertEquals(continent.get(0).getCountries().get(1).getId(), 1);
        assertEquals(continent.get(0).getCountries().get(1).getContinentId(), 1);
        assertEquals(continent.get(0).getCountries().get(1).getName(), "Germany");

        assertEquals(continent.get(0).getCountries().get(2).getId(), 2);
        assertEquals(continent.get(0).getCountries().get(2).getContinentId(), 1);
        assertEquals(continent.get(0).getCountries().get(2).getName(), "Netherlands");

        assertEquals(continent.get(1).getCountries().get(3).getId(), 3);
        assertEquals(continent.get(1).getCountries().get(3).getContinentId(), 2);
        assertEquals(continent.get(1).getCountries().get(3).getName(), "USA");

        assertEquals(continent.get(1).getCountries().get(4).getId(), 4);
        assertEquals(continent.get(1).getCountries().get(4).getContinentId(), 2);
        assertEquals(continent.get(1).getCountries().get(4).getName(), "Canada");

        // Cities
        assertEquals(continent.get(0).getCountries().get(1).getCities().get(1).getId(), 1);
        assertEquals(continent.get(0).getCountries().get(1).getCities().get(1).getCountryId(), 1);
        assertEquals(continent.get(0).getCountries().get(1).getCities().get(1).getName(), "Esslingen");

        assertEquals(continent.get(0).getCountries().get(1).getCities().get(2).getId(), 2);
        assertEquals(continent.get(0).getCountries().get(1).getCities().get(2).getCountryId(), 1);
        assertEquals(continent.get(0).getCountries().get(1).getCities().get(2).getName(), "Bonn");

        assertEquals(continent.get(1).getCountries().get(3).getCities().get(3).getId(), 3);
        assertEquals(continent.get(1).getCountries().get(3).getCities().get(3).getCountryId(), 3);
        assertEquals(continent.get(1).getCountries().get(3).getCities().get(3).getName(), "Boston");

        assertEquals(continent.get(1).getCountries().get(3).getCities().get(4).getId(), 4);
        assertEquals(continent.get(1).getCountries().get(3).getCities().get(4).getCountryId(), 3);
        assertEquals(continent.get(1).getCountries().get(3).getCities().get(4).getName(), "New York");
    }

}
