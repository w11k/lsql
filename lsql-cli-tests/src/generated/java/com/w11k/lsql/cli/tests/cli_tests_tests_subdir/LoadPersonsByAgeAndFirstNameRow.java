package com.w11k.lsql.cli.tests.cli_tests_tests_subdir;

import com.w11k.lsql.cli.tests.*;

public class LoadPersonsByAgeAndFirstNameRow implements com.w11k.lsql.TableRow,AgeInteger,FirstNameString,IdInteger {

    @SuppressWarnings("unchecked")
    public static <T extends 
            AgeInteger
            & FirstNameString
            & IdInteger> LoadPersonsByAgeAndFirstNameRow from(T source) {
        Object target = new LoadPersonsByAgeAndFirstNameRow();
        target = ((AgeInteger) target).withAge(source.getAge());
        target = ((FirstNameString) target).withFirstName(source.getFirstName());
        target = ((IdInteger) target).withId(source.getId());
        return (LoadPersonsByAgeAndFirstNameRow) target;
    }

    public LoadPersonsByAgeAndFirstNameRow() {
        this.age = null;
        this.firstName = null;
        this.id = null;
    }

    private LoadPersonsByAgeAndFirstNameRow(
            java.lang.Integer age,
            java.lang.String firstName,
            java.lang.Integer id) {
        this.age = age;
        this.firstName = firstName;
        this.id = id;
    }

    public LoadPersonsByAgeAndFirstNameRow(java.util.Map<String, Object> from) {
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

    public LoadPersonsByAgeAndFirstNameRow withAge(java.lang.Integer age) {
        return new LoadPersonsByAgeAndFirstNameRow(age,firstName,id);
    }

    // ------------------------------------------------------------

    public static final String COL_FIRST_NAME = "firstName";

    public final java.lang.String firstName;

    public java.lang.String getFirstName() {
        return this.firstName;
    }

    public LoadPersonsByAgeAndFirstNameRow withFirstName(java.lang.String firstName) {
        return new LoadPersonsByAgeAndFirstNameRow(age,firstName,id);
    }

    // ------------------------------------------------------------

    public static final String COL_ID = "id";

    public final java.lang.Integer id;

    public java.lang.Integer getId() {
        return this.id;
    }

    public LoadPersonsByAgeAndFirstNameRow withId(java.lang.Integer id) {
        return new LoadPersonsByAgeAndFirstNameRow(age,firstName,id);
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
