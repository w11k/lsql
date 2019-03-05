package com.w11k.lsql.testdata;

import java.util.concurrent.atomic.AtomicInteger;

public class PersonWithAtomicIntegerAge {

    private int idPk;

    private String firstName;

    private AtomicInteger age;

    private String title;

    public PersonWithAtomicIntegerAge() {
    }

    public PersonWithAtomicIntegerAge(int idPk, String firstName, AtomicInteger age) {
        this.idPk = idPk;
        this.firstName = firstName;
        this.age = age;
    }

    public int getIdPk() {
        return this.idPk;
    }

    public void setIdPk(int idPk) {
        this.idPk = idPk;
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

        return this.idPk == that.idPk
                && (this.firstName != null ? this.firstName.equals(that.firstName) : that.firstName == null
                && (this.age != null ? this.age.equals(that.age) : that.age == null));

    }

    @Override
    public int hashCode() {
        int result = this.idPk;
        result = 31 * result + (this.firstName != null ? this.firstName.hashCode() : 0);
        result = 31 * result + (this.age != null ? this.age.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PersonWithAtomicIntegerAge{" +
                "id=" + this.idPk +
                ", firstName='" + this.firstName + '\'' +
                ", age=" + this.age +
                '}';
    }
}
