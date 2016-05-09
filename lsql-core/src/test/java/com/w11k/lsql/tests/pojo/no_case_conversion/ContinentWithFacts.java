package com.w11k.lsql.tests.pojo.no_case_conversion;

import java.util.Map;

class ContinentWithFacts extends Continent {

    private Map<Integer, ContinentFact> facts;

    public Map<Integer, ContinentFact> getFacts() {
        return facts;
    }

    public void setFacts(Map<Integer, ContinentFact> facts) {
        this.facts = facts;
    }
}
