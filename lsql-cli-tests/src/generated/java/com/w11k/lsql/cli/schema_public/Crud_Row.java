package com.w11k.lsql.cli.schema_public;

import com.w11k.lsql.cli.structural_fields.*;
import java.util.*;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public final class Crud_Row implements com.w11k.lsql.TableRow, Field_AInteger, Field_BString, Id_Integer {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Field_AInteger
            & Field_BString
            & Id_Integer> Crud_Row from(T source) {
        Object target = new Crud_Row();
        target = ((Field_AInteger) target).withFieldA(source.getFieldA());
        target = ((Field_BString) target).withFieldB(source.getFieldB());
        target = ((Id_Integer) target).withId(source.getId());
        return (Crud_Row) target;
    }

    @SuppressWarnings("unused")
    public static Crud_Row fromInternalMap(java.util.Map<String, Object> internalMap) {
        return new Crud_Row((java.lang.Integer) internalMap.get("field_a"), (java.lang.String) internalMap.get("field_b"), (java.lang.Integer) internalMap.get("id"));
    }

    @SuppressWarnings("unused")
    public static Crud_Row fromRow(java.util.Map<String, Object> map) {
        return new Crud_Row((java.lang.Integer) map.get("fieldA"), (java.lang.String) map.get("fieldB"), (java.lang.Integer) map.get("id"));
    }

    // constructors ----------

    @SuppressWarnings("ConstantConditions")
    public Crud_Row() {
        this.fieldA = null;
        this.fieldB = null;
        this.id = null;
    }

    @SuppressWarnings("NullableProblems")
    private Crud_Row(
            java.lang.Integer fieldA,
            java.lang.String fieldB,
            java.lang.Integer id) {
        this.fieldA = fieldA;
        this.fieldB = fieldB;
        this.id = id;
    }

    // fields ----------

    @SuppressWarnings("unused")
    public static final String FIELD_FIELD_A = "field_a";

    @SuppressWarnings("unused")
    public static final String ROW_KEY_FIELD_A = "fieldA";

    @javax.annotation.Nullable public final java.lang.Integer fieldA;

    @javax.annotation.Nullable public java.lang.Integer getFieldA() {
        return this.fieldA;
    }

    public Crud_Row withFieldA(@javax.annotation.Nullable java.lang.Integer fieldA) {
        return new Crud_Row(fieldA,fieldB,id);
    }
    @SuppressWarnings("unused")
    public static final String FIELD_FIELD_B = "field_b";

    @SuppressWarnings("unused")
    public static final String ROW_KEY_FIELD_B = "fieldB";

    @javax.annotation.Nullable public final java.lang.String fieldB;

    @javax.annotation.Nullable public java.lang.String getFieldB() {
        return this.fieldB;
    }

    public Crud_Row withFieldB(@javax.annotation.Nullable java.lang.String fieldB) {
        return new Crud_Row(fieldA,fieldB,id);
    }
    @SuppressWarnings("unused")
    public static final String FIELD_ID = "id";

    @SuppressWarnings("unused")
    public static final String ROW_KEY_ID = "id";

    @javax.annotation.Nonnull public final java.lang.Integer id;

    @javax.annotation.Nonnull public java.lang.Integer getId() {
        return this.id;
    }

    public Crud_Row withId(@javax.annotation.Nonnull java.lang.Integer id) {
        return new Crud_Row(fieldA,fieldB,id);
    }

    // class methods ----------

    @SuppressWarnings("unchecked")
    public <T extends 
            Field_AInteger
            & Field_BString
            & Id_Integer> T as(T targetStart) {
        Object target = targetStart;
        target = ((Field_AInteger) target).withFieldA(this.getFieldA());
        target = ((Field_BString) target).withFieldB(this.getFieldB());
        target = ((Id_Integer) target).withId(this.getId());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            Field_AInteger
            & Field_BString
            & Id_Integer> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toInternalMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("field_a", this.fieldA);
        map.put("field_b", this.fieldB);
        map.put("id", this.id);
        return map;
    }

    public java.util.Map<String, Object> toRow() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("fieldA", this.fieldA);
        map.put("fieldB", this.fieldB);
        map.put("id", this.id);
        return map;
    }

    // Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Crud_Row that = (Crud_Row) o;
        return     Objects.equals(fieldA, that.fieldA) && 
            Objects.equals(fieldB, that.fieldB) && 
            Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldA, fieldB, id);
    }

    @Override
    public String toString() {
        return "Crud_Row{" + "fieldA=" + fieldA
            + ", " + "fieldB=" + fieldB
            + ", " + "id=" + id + "}";
    }

}
