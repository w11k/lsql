package com.w11k.lsql.tests;

import com.w11k.lsql.LSql;
import com.w11k.lsql.Query;
import com.w11k.lsql.sqlfile.LSqlFile;

public class TreeTestData {

    private final LSql lSql;

    private LSqlFile lSqlFile;

    public TreeTestData(LSql lSql) {
        this.lSql = lSql;
        this.lSqlFile = lSql.readSqlFile(getClass());
    }

    public void insert() {
        lSql.executeRawSql("CREATE TABLE continent (id INT, name TEXT)");
        lSql.executeRawSql("INSERT INTO continent VALUES (1, 'Europe')");
        lSql.executeRawSql("INSERT INTO continent VALUES (2, 'North America')");

        lSql.executeRawSql("CREATE TABLE continent_fact (id INT, continent_id INT, fact_name TEXT, fact_value TEXT)");
        lSql.executeRawSql("INSERT INTO continent_fact VALUES (1, 1, 'Area', '10,180,000 km2')");
        lSql.executeRawSql("INSERT INTO continent_fact VALUES (2, 1, 'Population', '742,452,000')");
        lSql.executeRawSql("INSERT INTO continent_fact VALUES (3, 2, 'Area', '24,709,000 km2')");
        lSql.executeRawSql("INSERT INTO continent_fact VALUES (4, 2, 'Largest city', 'Mexico City')");

        lSql.executeRawSql("CREATE TABLE country (id INT, continent_id INT, name TEXT)");
        lSql.executeRawSql("INSERT INTO country VALUES (1, 1, 'Germany')");
        lSql.executeRawSql("INSERT INTO country VALUES (2, 1, 'Netherlands')");
        lSql.executeRawSql("INSERT INTO country VALUES (3, 2, 'USA')");
        lSql.executeRawSql("INSERT INTO country VALUES (4, 2, 'Canada')");

        lSql.executeRawSql("CREATE TABLE city (id INT, country_id INT, name TEXT)");
        lSql.executeRawSql("INSERT INTO city VALUES (1, 1, 'Esslingen')");
        lSql.executeRawSql("INSERT INTO city VALUES (2, 1, 'Bonn')");
        lSql.executeRawSql("INSERT INTO city VALUES (3, 3, 'Boston')");
        lSql.executeRawSql("INSERT INTO city VALUES (4, 3, 'New York')");
    }

    public Query continents() {
        return lSqlFile.statement("continents").query();
    }

    public Query continentsWithFacts() {
        return lSqlFile.statement("continentsWithFacts").query();
    }

    public Query continentsWithFactsAndCountries() {
        return lSqlFile.statement("continentsWithFactsAndCountries").query();
    }

    public Query continentsWithFactsAndCountriesAndCities() {
        return lSqlFile.statement("continentsWithFactsAndCountriesAndCities_1").query();
    }

    public Query continentsWithFactsAndCountriesAndCities_2() {
        return lSqlFile.statement("continentsWithFactsAndCountriesAndCities_2").query();
    }

}
