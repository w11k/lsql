package com.w11k.lsql.tests.testdata;

public class PersonWithSuperfluousField extends Person {

    private String oops;

    public String getOops() {
        return this.oops;
    }

    public void setOops(String oops) {
        this.oops = oops;
    }
}
