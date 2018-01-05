package com.w11k.lsql.cli.tests.schema_public;

import com.w11k.lsql.cli.tests.*;

public class Person1Row implements com.w11k.lsql.TableRow,FirstNameString,IdInteger {

    @SuppressWarnings("unchecked")
    public static <T extends 
            FirstNameString
            & IdInteger> Person1Row from(T source) {
        Object target = new Person1Row();
        target = ((FirstNameString) target).withFirstName(source.getFirstName());
        target = ((IdInteger) target).withId(source.getId());
        return (Person1Row) target;
    }

    public Person1Row() {
        this.firstName = null;
        this.id = null;
    }

    private Person1Row(
            java.lang.String firstName,
            java.lang.Integer id) {
        this.firstName = firstName;
        this.id = id;
    }

    public Person1Row(java.util.Map<String, Object> from) {
        this.firstName = (java.lang.String) from.get("firstName");
        this.id = (java.lang.Integer) from.get("id");
    }

    // ------------------------------------------------------------

    public static final String COL_FIRST_NAME = "firstName";

    public final java.lang.String firstName;

    public java.lang.String getFirstName() {
        return this.firstName;
    }

    public Person1Row withFirstName(java.lang.String firstName) {
        return new Person1Row(firstName,id);
    }

    // ------------------------------------------------------------

    public static final String COL_ID = "id";

    public final java.lang.Integer id;

    public java.lang.Integer getId() {
        return this.id;
    }

    public Person1Row withId(java.lang.Integer id) {
        return new Person1Row(firstName,id);
    }

    // ------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public <T extends 
            FirstNameString
            & IdInteger> T as(T targetStart) {
        Object target = targetStart;
        target = ((FirstNameString) target).withFirstName(this.getFirstName());
        target = ((IdInteger) target).withId(this.getId());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            FirstNameString
            & IdInteger> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
        map.put("firstName", this.firstName);
        map.put("id", this.id);
        return map;
    }

}
