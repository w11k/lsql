package com.w11k.lsql.cli.schema_public;

import com.w11k.lsql.cli.structural_fields.*;
import java.util.*;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public final class Api1_Row implements com.w11k.lsql.TableRow, Id_Integer, First_Name_String {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Id_Integer
            & First_Name_String> Api1_Row from(T source) {
        Object target = new Api1_Row();
        target = ((Id_Integer) target).withId(source.getId());
        target = ((First_Name_String) target).withFirstName(source.getFirstName());
        return (Api1_Row) target;
    }

    @SuppressWarnings("unused")
    public static Api1_Row fromInternalMap(java.util.Map<String, Object> internalMap) {
        return new Api1_Row((java.lang.Integer) internalMap.get("id"), (java.lang.String) internalMap.get("first_name"));
    }

    @SuppressWarnings("unused")
    public static Api1_Row fromRow(java.util.Map<String, Object> map) {
        return new Api1_Row((java.lang.Integer) map.get("id"), (java.lang.String) map.get("firstName"));
    }

    // constructors ----------

    @SuppressWarnings("ConstantConditions")
    public Api1_Row() {
        this.id = null;
        this.firstName = null;
    }

    @SuppressWarnings("NullableProblems")
    private Api1_Row(
            java.lang.Integer id,
            java.lang.String firstName) {
        this.id = id;
        this.firstName = firstName;
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

    public Api1_Row withId(@javax.annotation.Nonnull java.lang.Integer id) {
        return new Api1_Row(id,firstName);
    }
    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_FIRST_NAME = "first_name";

    @SuppressWarnings("unused")
    public static final String ROW_KEY_FIRST_NAME = "firstName";

    @javax.annotation.Nullable public final java.lang.String firstName;

    @javax.annotation.Nullable public java.lang.String getFirstName() {
        return this.firstName;
    }

    public Api1_Row withFirstName(@javax.annotation.Nullable java.lang.String firstName) {
        return new Api1_Row(id,firstName);
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

    public java.util.Map<String, Object> toInternalMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", this.id);
        map.put("first_name", this.firstName);
        return map;
    }

    public java.util.Map<String, Object> toRowMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", this.id);
        map.put("firstName", this.firstName);
        return map;
    }

    // Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Api1_Row that = (Api1_Row) o;
        return     Objects.equals(id, that.id) && 
            Objects.equals(firstName, that.firstName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName);
    }

    @Override
    public String toString() {
        return "Api1_Row{" + "id=" + id
            + ", " + "firstName=" + firstName + "}";
    }

}