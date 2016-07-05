package com.w11k.lsql.tests.testdata;

import java.util.concurrent.atomic.AtomicInteger;

public class PersonWithAtomicIntegerAge {

    private int id;

    private String firstName;

    private AtomicInteger age;

    private String title;

    public PersonWithAtomicIntegerAge() {
    }

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

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonWithAtomicIntegerAge that = (PersonWithAtomicIntegerAge) o;

        return this.id == that.id
                && (this.firstName != null ? this.firstName.equals(that.firstName) : that.firstName == null
                && (this.age != null ? this.age.equals(that.age) : that.age == null));

    }

    @Override
    public int hashCode() {
        int result = this.id;
        result = 31 * result + (this.firstName != null ? this.firstName.hashCode() : 0);
        result = 31 * result + (this.age != null ? this.age.hashCode() : 0);
        return result;
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
