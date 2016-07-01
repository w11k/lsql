package com.w11k.lsql.tests.pojo.tree;

import java.util.Map;

public class QueryCountry extends Country {

    private Map<Integer, City> cities;

    public Map<Integer, City> getCities() {
        return cities;
    }

    public void setCities(Map<Integer, City> cities) {
        this.cities = cities;
    }
}
