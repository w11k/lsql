package com.w11k.lsql.tests.pojo.no_case_conversion;

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
    public void continentsToPojo() {
        Query query = this.treeTestData.continents();
        List<Continent> continents = query.toPojo(Continent.class);
        assertEquals(continents.size(), 2);
        assertEquals(continents.get(0).getId(), 1);
        assertEquals(continents.get(0).getName(), "Europe");
        assertEquals(continents.get(1).getId(), 2);
        assertEquals(continents.get(1).getName(), "North America");
    }

    @Test
    public void continentsWithFactsToPojo() {
        Query query = this.treeTestData.continentsWithFacts();
        List<ContinentWithFacts> tree = query.toPojo(ContinentWithFacts.class);

        assertEquals(tree.size(), 2);

        // Continent
        assertEquals(tree.get(1).getId(), 1);
        assertEquals(tree.get(1).getName(), "Europe");
        assertEquals(tree.get(2).getId(), 2);
        assertEquals(tree.get(2).getName(), "North America");

        // Continent Facts
//        assertEquals(tree.get(1).getTree("facts").get(1).size(), 4);
//        assertEquals(tree.get(1).getTree("facts").get(1).getInt("id"), Integer.valueOf(1));
//        assertEquals(tree.get(1).getTree("facts").get(1).getInt("continent_id"), Integer.valueOf(1));
//        assertEquals(tree.get(1).getTree("facts").get(1).getString("fact_name"), "Area");
//        assertEquals(tree.get(1).getTree("facts").get(1).getString("fact_value"), "10,180,000 km2");
//
//        assertEquals(tree.get(1).getTree("facts").get(2).size(), 4);
//        assertEquals(tree.get(1).getTree("facts").get(2).getInt("id"), Integer.valueOf(2));
//        assertEquals(tree.get(1).getTree("facts").get(2).getInt("continent_id"), Integer.valueOf(1));
//        assertEquals(tree.get(1).getTree("facts").get(2).getString("fact_name"), "Population");
//        assertEquals(tree.get(1).getTree("facts").get(2).getString("fact_value"), "742,452,000");
//
//        assertEquals(tree.get(2).getTree("facts").get(3).size(), 4);
//        assertEquals(tree.get(2).getTree("facts").get(3).getInt("id"), Integer.valueOf(3));
//        assertEquals(tree.get(2).getTree("facts").get(3).getInt("continent_id"), Integer.valueOf(2));
//        assertEquals(tree.get(2).getTree("facts").get(3).getString("fact_name"), "Area");
//        assertEquals(tree.get(2).getTree("facts").get(3).getString("fact_value"), "24,709,000 km2");
//
//        assertEquals(tree.get(2).getTree("facts").get(4).size(), 4);
//        assertEquals(tree.get(2).getTree("facts").get(4).getInt("id"), Integer.valueOf(4));
//        assertEquals(tree.get(2).getTree("facts").get(4).getInt("continent_id"), Integer.valueOf(2));
//        assertEquals(tree.get(2).getTree("facts").get(4).getString("fact_name"), "Largest city");
//        assertEquals(tree.get(2).getTree("facts").get(4).getString("fact_value"), "Mexico City");
    }

}
