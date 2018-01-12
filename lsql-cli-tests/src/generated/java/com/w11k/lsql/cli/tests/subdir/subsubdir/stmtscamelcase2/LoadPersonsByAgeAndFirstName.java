package com.w11k.lsql.cli.tests.subdir.subsubdir.stmtscamelcase2;

import com.w11k.lsql.cli.tests.structural_fields.*;

public final class LoadPersonsByAgeAndFirstName implements com.w11k.lsql.TableRow, Id_Integer, First_Name_String, Age_Integer {

    @SuppressWarnings("unchecked")
    public static <T extends 
            Id_Integer
            & First_Name_String
            & Age_Integer> LoadPersonsByAgeAndFirstName from(T source) {
        Object target = new LoadPersonsByAgeAndFirstName();
        target = ((Id_Integer) target).withId(source.getId());
        target = ((First_Name_String) target).withFirstName(source.getFirstName());
        target = ((Age_Integer) target).withAge(source.getAge());
        return (LoadPersonsByAgeAndFirstName) target;
    }

    public LoadPersonsByAgeAndFirstName() {
        
        this.id = null;
        this.firstName = null;
        this.age = null;
    }

    private LoadPersonsByAgeAndFirstName(
            java.lang.Integer id,
            java.lang.String firstName,
            java.lang.Integer age) {
        
        this.id = id;
        this.firstName = firstName;
        this.age = age;
    }

    public LoadPersonsByAgeAndFirstName(java.util.Map<String, Object> from) {
        
        this.id = (java.lang.Integer) from.get("id");
        this.firstName = (java.lang.String) from.get("firstName");
        this.age = (java.lang.Integer) from.get("age");
    }

    public static final String COL_ID = "id";

    public final java.lang.Integer id;

    public java.lang.Integer getId() {
        return this.id;
    }

    public LoadPersonsByAgeAndFirstName withId(java.lang.Integer id) {
        return new LoadPersonsByAgeAndFirstName(id,firstName,age);
    }
    public static final String COL_FIRST_NAME = "firstName";

    public final java.lang.String firstName;

    public java.lang.String getFirstName() {
        return this.firstName;
    }

    public LoadPersonsByAgeAndFirstName withFirstName(java.lang.String firstName) {
        return new LoadPersonsByAgeAndFirstName(id,firstName,age);
    }
    public static final String COL_AGE = "age";

    public final java.lang.Integer age;

    public java.lang.Integer getAge() {
        return this.age;
    }

    public LoadPersonsByAgeAndFirstName withAge(java.lang.Integer age) {
        return new LoadPersonsByAgeAndFirstName(id,firstName,age);
    }

    // class methods ----------

    @SuppressWarnings("unchecked")
    public <T extends 
            Id_Integer
            & First_Name_String
            & Age_Integer> T as(T targetStart) {
        Object target = targetStart;
        target = ((Id_Integer) target).withId(this.getId());
        target = ((First_Name_String) target).withFirstName(this.getFirstName());
        target = ((Age_Integer) target).withAge(this.getAge());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            Id_Integer
            & First_Name_String
            & Age_Integer> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
        map.put("id", this.id);
        map.put("firstName", this.firstName);
        map.put("age", this.age);
        return map;
    }

}
