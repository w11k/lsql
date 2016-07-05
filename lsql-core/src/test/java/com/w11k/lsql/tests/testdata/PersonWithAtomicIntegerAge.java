package com.w11k.lsql.tests.testdata;

import java.util.concurrent.atomic.AtomicInteger;

public class PersonWithAtomicIntegerAge {

    private int id;

    private String firstName;

    private AtomicInteger age;

    public PersonWithAtomicIntegerAge(int id, String firstName, AtomicInteger age) {
        this.id = id;
        this.firstName = firstName;
        this.age = age;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public AtomicInteger getAge() {
        return this.age;
    }

    public void setAge(AtomicInteger age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "PersonWithAtomicIntegerAge{" +
                "id=" + this.id +
                ", firstName='" + this.firstName + '\'' +
                ", age=" + this.age +
                '}';
    }
}
