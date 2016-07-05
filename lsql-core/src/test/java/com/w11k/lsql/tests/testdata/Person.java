package com.w11k.lsql.tests.testdata;

public class Person {

    private int id;

    private String firstName;

    private int age;

    private String title;

    public Person() {
    }

    public Person(int id, String firstName, int age) {
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

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
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

        Person person = (Person) o;

        return this.id == person.id && this.age == person.age
                && (this.firstName != null ? this.firstName.equals(person.firstName) : person.firstName == null
                && (this.title != null ? this.title.equals(person.title) : person.title == null));

    }

    @Override
    public int hashCode() {
        int result = this.id;
        result = 31 * result + (this.firstName != null ? this.firstName.hashCode() : 0);
        result = 31 * result + this.age;
        result = 31 * result + (this.title != null ? this.title.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + this.id +
                ", firstName='" + this.firstName + '\'' +
                ", age=" + this.age +
                '}';
    }
}
