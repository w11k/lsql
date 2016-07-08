package com.w11k.lsql.tests.tree.pojo;

import java.util.List;

public class ContinentWithFactsCountriesCities {

    private int id;

    private String name;

    private List<ContinentFact> facts;

    private List<CountryWithCities> countries;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ContinentFact> getFacts() {
        return facts;
    }

    public void setFacts(List<ContinentFact> facts) {
        this.facts = facts;
    }

    public List<CountryWithCities> getCountries() {
        return countries;
    }

    public void setCountries(List<CountryWithCities> countries) {
        this.countries = countries;
    }
}
