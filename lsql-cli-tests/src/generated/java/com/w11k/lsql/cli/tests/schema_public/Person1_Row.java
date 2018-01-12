package com.w11k.lsql.cli.tests.schema_public;

import com.w11k.lsql.cli.tests.structural_fields.*;

public final class Person1_Row implements com.w11k.lsql.TableRow, First_Name_String, Id_Integer {

    @SuppressWarnings("unchecked")
    public static <T extends 
            First_Name_String
            & Id_Integer> Person1_Row from(T source) {
        Object target = new Person1_Row();
        target = ((First_Name_String) target).withFirstName(source.getFirstName());
        target = ((Id_Integer) target).withId(source.getId());
        return (Person1_Row) target;
    }

    public Person1_Row() {
        
        this.firstName = null;
        this.id = null;
    }

    private Person1_Row(
            java.lang.String firstName,
            java.lang.Integer id) {
        
        this.firstName = firstName;
        this.id = id;
    }

    public Person1_Row(java.util.Map<String, Object> from) {
        
        this.firstName = (java.lang.String) from.get("firstName");
        this.id = (java.lang.Integer) from.get("id");
    }

    public static final String COL_FIRST_NAME = "firstName";

    public final java.lang.String firstName;

    public java.lang.String getFirstName() {
        return this.firstName;
    }

    public Person1_Row withFirstName(java.lang.String firstName) {
        return new Person1_Row(firstName,id);
    }
    public static final String COL_ID = "id";

    public final java.lang.Integer id;

    public java.lang.Integer getId() {
        return this.id;
    }

    public Person1_Row withId(java.lang.Integer id) {
        return new Person1_Row(firstName,id);
    }

    // class methods ----------

    @SuppressWarnings("unchecked")
    public <T extends 
            First_Name_String
            & Id_Integer> T as(T targetStart) {
        Object target = targetStart;
        target = ((First_Name_String) target).withFirstName(this.getFirstName());
        target = ((Id_Integer) target).withId(this.getId());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            First_Name_String
            & Id_Integer> T as(Class<? extends T> targetClass) {
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
