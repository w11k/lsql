package com.w11k.lsql.cli.tests.schema_public;

import com.w11k.lsql.cli.tests.*;

public class Person2Row implements com.w11k.lsql.TableRow,AgeInteger,FirstNameString,IdInteger {

    @SuppressWarnings("unchecked")
    public static <T extends 
            AgeInteger
            & FirstNameString
            & IdInteger> Person2Row from(T source) {
        Object target = new Person2Row();
        target = ((AgeInteger) target).withAge(source.getAge());
        target = ((FirstNameString) target).withFirstName(source.getFirstName());
        target = ((IdInteger) target).withId(source.getId());
        return (Person2Row) target;
    }

    public Person2Row() {
        this.age = null;
        this.firstName = null;
        this.id = null;
    }

    private Person2Row(
            java.lang.Integer age,
            java.lang.String firstName,
            java.lang.Integer id) {
        this.age = age;
        this.firstName = firstName;
        this.id = id;
    }

    public Person2Row(java.util.Map<String, Object> from) {
        this.age = (java.lang.Integer) from.get("age");
        this.firstName = (java.lang.String) from.get("firstName");
        this.id = (java.lang.Integer) from.get("id");
    }

    // ------------------------------------------------------------

    public static final String COL_AGE = "age";

    public final java.lang.Integer age;

    public java.lang.Integer getAge() {
        return this.age;
    }

    public Person2Row withAge(java.lang.Integer age) {
        return new Person2Row(age,firstName,id);
    }

    // ------------------------------------------------------------

    public static final String COL_FIRST_NAME = "firstName";

    public final java.lang.String firstName;

    public java.lang.String getFirstName() {
        return this.firstName;
    }

    public Person2Row withFirstName(java.lang.String firstName) {
        return new Person2Row(age,firstName,id);
    }

    // ------------------------------------------------------------

    public static final String COL_ID = "id";

    public final java.lang.Integer id;

    public java.lang.Integer getId() {
        return this.id;
    }

    public Person2Row withId(java.lang.Integer id) {
        return new Person2Row(age,firstName,id);
    }

    // ------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public <T extends 
            AgeInteger
            & FirstNameString
            & IdInteger> T as(T targetStart) {
        Object target = targetStart;
        target = ((AgeInteger) target).withAge(this.getAge());
        target = ((FirstNameString) target).withFirstName(this.getFirstName());
        target = ((IdInteger) target).withId(this.getId());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            AgeInteger
            & FirstNameString
            & IdInteger> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
        map.put("age", this.age);
        map.put("firstName", this.firstName);
        map.put("id", this.id);
        return map;
    }

}
