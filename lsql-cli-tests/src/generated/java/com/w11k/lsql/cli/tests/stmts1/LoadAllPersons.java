package com.w11k.lsql.cli.tests.stmts1;

import com.w11k.lsql.cli.tests.structural_fields.*;
import java.util.*;

public final class LoadAllPersons implements com.w11k.lsql.TableRow, Id_Integer, First_Name_String {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Id_Integer
            & First_Name_String> LoadAllPersons from(T source) {
        Object target = new LoadAllPersons();
        target = ((Id_Integer) target).withId(source.getId());
        target = ((First_Name_String) target).withFirstName(source.getFirstName());
        return (LoadAllPersons) target;
    }

    // constructors ----------

    public LoadAllPersons() {
        
        this.id = null;
        this.firstName = null;
    }

    private LoadAllPersons(
            java.lang.Integer id,
            java.lang.String firstName) {
        
        this.id = id;
        this.firstName = firstName;
    }

    public LoadAllPersons(java.util.Map<String, Object> from) {
        
        this.id = (java.lang.Integer) from.get("id");
        this.firstName = (java.lang.String) from.get("first_name");
    }

    // fields ----------

    public static final String FIELD_id = "id";

    @javax.annotation.Nullable public final java.lang.Integer id;

    @javax.annotation.Nullable public java.lang.Integer getId() {
        return this.id;
    }

    public LoadAllPersons withId(@javax.annotation.Nullable java.lang.Integer id) {
        return new LoadAllPersons(id,firstName);
    }
    public static final String FIELD_first_name = "first_name";

    @javax.annotation.Nullable public final java.lang.String firstName;

    @javax.annotation.Nullable public java.lang.String getFirstName() {
        return this.firstName;
    }

    public LoadAllPersons withFirstName(@javax.annotation.Nullable java.lang.String firstName) {
        return new LoadAllPersons(id,firstName);
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

    // Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoadAllPersons that = (LoadAllPersons) o;
        return     Objects.equals(id, that.id) && 
            Objects.equals(firstName, that.firstName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName);
    }

    @Override
    public String toString() {
        return "LoadAllPersons{" + "id=" + id
            + ", " + "firstName=" + firstName + "}";
    }

}
