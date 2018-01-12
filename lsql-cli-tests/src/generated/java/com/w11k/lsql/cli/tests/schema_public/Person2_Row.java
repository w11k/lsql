package com.w11k.lsql.cli.tests.schema_public;

import com.w11k.lsql.cli.tests.structural_fields.*;

public final class Person2_Row implements com.w11k.lsql.TableRow, First_Name_String, Id_Integer, Age_Integer {

    @SuppressWarnings("unchecked")
    public static <T extends 
            First_Name_String
            & Id_Integer
            & Age_Integer> Person2_Row from(T source) {
        Object target = new Person2_Row();
        target = ((First_Name_String) target).withFirstName(source.getFirstName());
        target = ((Id_Integer) target).withId(source.getId());
        target = ((Age_Integer) target).withAge(source.getAge());
        return (Person2_Row) target;
    }

    public Person2_Row() {
        
        this.firstName = null;
        this.id = null;
        this.age = null;
    }

    private Person2_Row(
            java.lang.String firstName,
            java.lang.Integer id,
            java.lang.Integer age) {
        
        this.firstName = firstName;
        this.id = id;
        this.age = age;
    }

    public Person2_Row(java.util.Map<String, Object> from) {
        
        this.firstName = (java.lang.String) from.get("firstName");
        this.id = (java.lang.Integer) from.get("id");
        this.age = (java.lang.Integer) from.get("age");
    }

    public static final String COL_FIRST_NAME = "firstName";

    public final java.lang.String firstName;

    public java.lang.String getFirstName() {
        return this.firstName;
    }

    public Person2_Row withFirstName(java.lang.String firstName) {
        return new Person2_Row(firstName,id,age);
    }
    public static final String COL_ID = "id";

    public final java.lang.Integer id;

    public java.lang.Integer getId() {
        return this.id;
    }

    public Person2_Row withId(java.lang.Integer id) {
        return new Person2_Row(firstName,id,age);
    }
    public static final String COL_AGE = "age";

    public final java.lang.Integer age;

    public java.lang.Integer getAge() {
        return this.age;
    }

    public Person2_Row withAge(java.lang.Integer age) {
        return new Person2_Row(firstName,id,age);
    }

    // class methods ----------

    @SuppressWarnings("unchecked")
    public <T extends 
            First_Name_String
            & Id_Integer
            & Age_Integer> T as(T targetStart) {
        Object target = targetStart;
        target = ((First_Name_String) target).withFirstName(this.getFirstName());
        target = ((Id_Integer) target).withId(this.getId());
        target = ((Age_Integer) target).withAge(this.getAge());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            First_Name_String
            & Id_Integer
            & Age_Integer> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
        map.put("firstName", this.firstName);
        map.put("id", this.id);
        map.put("age", this.age);
        return map;
    }

}
