package com.w11k.lsql.tests.testdata;

public class PersonSubclass extends Person {

    private String data;

    public PersonSubclass(int id, String firstName, int age) {
        super(id, firstName, age);
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
