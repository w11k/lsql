package com.w11k.lsql.tests.pojo.no_case_conversion;

class ContinentFact {
    private int id;

    private int continent_id;

    private String fact_name;

    private String fact_value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContinent_id() {
        return continent_id;
    }

    public void setContinent_id(int continent_id) {
        this.continent_id = continent_id;
    }

    public String getFact_name() {
        return fact_name;
    }

    public void setFact_name(String fact_name) {
        this.fact_name = fact_name;
    }

    public String getFact_value() {
        return fact_value;
    }

    public void setFact_value(String fact_value) {
        this.fact_value = fact_value;
    }
}
