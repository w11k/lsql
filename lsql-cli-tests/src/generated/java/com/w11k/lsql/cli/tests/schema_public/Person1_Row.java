package com.w11k.lsql.cli.tests.schema_public;

import com.w11k.lsql.cli.tests.structural_fields.*;

public final class Person1_Row implements com.w11k.lsql.TableRow, Id_Integer, First_Name_String {

    @SuppressWarnings("unchecked")
    public static <T extends 
            Id_Integer
            & First_Name_String> Person1_Row from(T source) {
        Object target = new Person1_Row();
        target = ((Id_Integer) target).withId(source.getId());
        target = ((First_Name_String) target).withFirstName(source.getFirstName());
        return (Person1_Row) target;
    }

    public Person1_Row() {
        
        this.id = null;
        this.firstName = null;
    }

    private Person1_Row(
            java.lang.Integer id,
            java.lang.String firstName) {
        
        this.id = id;
        this.firstName = firstName;
    }

    public Person1_Row(java.util.Map<String, Object> from) {
        
        this.id = (java.lang.Integer) from.get("id");
        this.firstName = (java.lang.String) from.get("first_name");
    }

    public static final String COL_ID = "id";

    public final java.lang.Integer id;

    public java.lang.Integer getId() {
        return this.id;
    }

    public Person1_Row withId(java.lang.Integer id) {
        return new Person1_Row(id,firstName);
    }
    public static final String COL_FIRST_NAME = "first_name";

    public final java.lang.String firstName;

    public java.lang.String getFirstName() {
        return this.firstName;
    }

    public Person1_Row withFirstName(java.lang.String firstName) {
        return new Person1_Row(id,firstName);
    }

    // class methods ----------

    @SuppressWarnings("unchecked")
    public <T extends 
            Id_Integer
            & First_Name_String> T as(T targetStart) {
        Object target = targetStart;
        target = ((Id_Integer) target).withId(this.getId());
        target = ((First_Name_String) target).withFirstName(this.getFirstName());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            Id_Integer
            & First_Name_String> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
        map.put("id", this.id);
        map.put("first_name", this.firstName);
        return map;
    }

}
