package com.w11k.lsql.tests.pojo.no_case_conversion;

import java.util.Map;

class QueryContinent extends Continent {

    private Map<Integer, ContinentFact> facts;

    private Map<Integer, QueryCountry> countries;

    public Map<Integer, ContinentFact> getFacts() {
        return facts;
    }

    public void setFacts(Map<Integer, ContinentFact> facts) {
        this.facts = facts;
    }

    public Map<Integer, QueryCountry> getCountries() {
        return countries;
    }

    public void setCountries(Map<Integer, QueryCountry> countries) {
        this.countries = countries;
    }
}
