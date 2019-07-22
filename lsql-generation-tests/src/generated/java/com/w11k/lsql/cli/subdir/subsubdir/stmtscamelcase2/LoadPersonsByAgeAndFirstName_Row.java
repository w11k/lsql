package com.w11k.lsql.cli.subdir.subsubdir.stmtscamelcase2;

import com.w11k.lsql.cli.structural_fields.*;
import java.util.*;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public final class LoadPersonsByAgeAndFirstName_Row implements com.w11k.lsql.TableRow, Id_Integer, First_Name_String, Age_Integer {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Id_Integer
            & First_Name_String
            & Age_Integer> LoadPersonsByAgeAndFirstName_Row from(T source) {
        Object target = new LoadPersonsByAgeAndFirstName_Row();
        target = ((Id_Integer) target).withId(source.getId());
        target = ((First_Name_String) target).withFirstName(source.getFirstName());
        target = ((Age_Integer) target).withAge(source.getAge());
        return (LoadPersonsByAgeAndFirstName_Row) target;
    }

    @SuppressWarnings("unused")
    public static LoadPersonsByAgeAndFirstName_Row fromInternalMap(java.util.Map<String, Object> internalMap) {
        return new LoadPersonsByAgeAndFirstName_Row((java.lang.Integer) internalMap.get("id"), (java.lang.String) internalMap.get("first_name"), (java.lang.Integer) internalMap.get("age"));
    }

    @SuppressWarnings("unused")
    public static LoadPersonsByAgeAndFirstName_Row fromRow(java.util.Map<String, Object> map) {
        return new LoadPersonsByAgeAndFirstName_Row((java.lang.Integer) map.get("id"), (java.lang.String) map.get("firstName"), (java.lang.Integer) map.get("age"));
    }

    // constructors ----------

    @SuppressWarnings("ConstantConditions")
    public LoadPersonsByAgeAndFirstName_Row() {
        this.id = null;
        this.firstName = null;
        this.age = null;
    }

    @SuppressWarnings("NullableProblems")
    private LoadPersonsByAgeAndFirstName_Row(
            java.lang.Integer id,
            java.lang.String firstName,
            java.lang.Integer age) {
        this.id = id;
        this.firstName = firstName;
        this.age = age;
    }

    // fields ----------

    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_ID = "id";

    @SuppressWarnings("unused")
    public static final String ROW_KEY_ID = "id";

    @javax.annotation.Nonnull public final java.lang.Integer id;

    @javax.annotation.Nonnull public java.lang.Integer getId() {
        return this.id;
    }

    public LoadPersonsByAgeAndFirstName_Row withId(@javax.annotation.Nonnull java.lang.Integer id) {
        return new LoadPersonsByAgeAndFirstName_Row(id,firstName,age);
    }
    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_FIRST_NAME = "first_name";

    @SuppressWarnings("unused")
    public static final String ROW_KEY_FIRST_NAME = "firstName";

    @javax.annotation.Nullable public final java.lang.String firstName;

    @javax.annotation.Nullable public java.lang.String getFirstName() {
        return this.firstName;
    }

    public LoadPersonsByAgeAndFirstName_Row withFirstName(@javax.annotation.Nullable java.lang.String firstName) {
        return new LoadPersonsByAgeAndFirstName_Row(id,firstName,age);
    }
    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_AGE = "age";

    @SuppressWarnings("unused")
    public static final String ROW_KEY_AGE = "age";

    @javax.annotation.Nullable public final java.lang.Integer age;

    @javax.annotation.Nullable public java.lang.Integer getAge() {
        return this.age;
    }

    public LoadPersonsByAgeAndFirstName_Row withAge(@javax.annotation.Nullable java.lang.Integer age) {
        return new LoadPersonsByAgeAndFirstName_Row(id,firstName,age);
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

    public java.util.Map<String, Object> toInternalMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", this.id);
        map.put("first_name", this.firstName);
        map.put("age", this.age);
        return map;
    }

    public java.util.Map<String, Object> toRowMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", this.id);
        map.put("firstName", this.firstName);
        map.put("age", this.age);
        return map;
    }

    // Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoadPersonsByAgeAndFirstName_Row that = (LoadPersonsByAgeAndFirstName_Row) o;
        return     Objects.equals(id, that.id) && 
            Objects.equals(firstName, that.firstName) && 
            Objects.equals(age, that.age);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, age);
    }

    @Override
    public String toString() {
        return "LoadPersonsByAgeAndFirstName_Row{" + "id=" + id
            + ", " + "firstName=" + firstName
            + ", " + "age=" + age + "}";
    }

}