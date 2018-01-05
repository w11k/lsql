package com.w11k.lsql.cli.tests.cli_tests_tests;

import com.w11k.lsql.cli.tests.*;

public class LoadAllPersonsRow implements com.w11k.lsql.TableRow,FirstNameString,IdInteger {

    @SuppressWarnings("unchecked")
    public static <T extends 
            FirstNameString
            & IdInteger> LoadAllPersonsRow from(T source) {
        Object target = new LoadAllPersonsRow();
        target = ((FirstNameString) target).withFirstName(source.getFirstName());
        target = ((IdInteger) target).withId(source.getId());
        return (LoadAllPersonsRow) target;
    }

    public LoadAllPersonsRow() {
        this.firstName = null;
        this.id = null;
    }

    private LoadAllPersonsRow(
            java.lang.String firstName,
            java.lang.Integer id) {
        this.firstName = firstName;
        this.id = id;
    }

    public LoadAllPersonsRow(java.util.Map<String, Object> from) {
        this.firstName = (java.lang.String) from.get("firstName");
        this.id = (java.lang.Integer) from.get("id");
    }

    // ------------------------------------------------------------

    public static final String COL_FIRST_NAME = "firstName";

    public final java.lang.String firstName;

    public java.lang.String getFirstName() {
        return this.firstName;
    }

    public LoadAllPersonsRow withFirstName(java.lang.String firstName) {
        return new LoadAllPersonsRow(firstName,id);
    }

    // ------------------------------------------------------------

    public static final String COL_ID = "id";

    public final java.lang.Integer id;

    public java.lang.Integer getId() {
        return this.id;
    }

    public LoadAllPersonsRow withId(java.lang.Integer id) {
        return new LoadAllPersonsRow(firstName,id);
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
