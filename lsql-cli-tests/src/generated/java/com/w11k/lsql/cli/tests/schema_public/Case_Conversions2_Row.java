package com.w11k.lsql.cli.tests.schema_public;

import com.w11k.lsql.cli.tests.structural_fields.*;
import java.util.*;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public final class Case_Conversions2_Row implements com.w11k.lsql.TableRow, Id_Integer, Col1Integer {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Id_Integer
            & Col1Integer> Case_Conversions2_Row from(T source) {
        Object target = new Case_Conversions2_Row();
        target = ((Id_Integer) target).withId(source.getId());
        target = ((Col1Integer) target).withCol1(source.getCol1());
        return (Case_Conversions2_Row) target;
    }

    @SuppressWarnings("unused")
    public static Case_Conversions2_Row fromInternalMap(java.util.Map<String, Object> internalMap) {
        return new Case_Conversions2_Row((java.lang.Integer) internalMap.get("id"), (java.lang.Integer) internalMap.get("col1"));
    }

    @SuppressWarnings("unused")
    public static Case_Conversions2_Row fromRow(java.util.Map<String, Object> map) {
        return new Case_Conversions2_Row((java.lang.Integer) map.get("id"), (java.lang.Integer) map.get("col1"));
    }

    // constructors ----------

    @SuppressWarnings("ConstantConditions")
    public Case_Conversions2_Row() {
        this.id = null;
        this.col1 = null;
    }

    @SuppressWarnings("NullableProblems")
    private Case_Conversions2_Row(
            java.lang.Integer id,
            java.lang.Integer col1) {
        this.id = id;
        this.col1 = col1;
    }

    // fields ----------

    @SuppressWarnings("unused")
    public static final String FIELD_ID = "id";

    @SuppressWarnings("unused")
    public static final String ROW_KEY_ID = "id";

    @javax.annotation.Nonnull public final java.lang.Integer id;

    @javax.annotation.Nonnull public java.lang.Integer getId() {
        return this.id;
    }

    public Case_Conversions2_Row withId(@javax.annotation.Nonnull java.lang.Integer id) {
        return new Case_Conversions2_Row(id,col1);
    }
    @SuppressWarnings("unused")
    public static final String FIELD_COL1 = "col1";

    @SuppressWarnings("unused")
    public static final String ROW_KEY_COL1 = "col1";

    @javax.annotation.Nullable public final java.lang.Integer col1;

    @javax.annotation.Nullable public java.lang.Integer getCol1() {
        return this.col1;
    }

    public Case_Conversions2_Row withCol1(@javax.annotation.Nullable java.lang.Integer col1) {
        return new Case_Conversions2_Row(id,col1);
    }

    // class methods ----------

    @SuppressWarnings("unchecked")
    public <T extends 
            Id_Integer
            & Col1Integer> T as(T targetStart) {
        Object target = targetStart;
        target = ((Id_Integer) target).withId(this.getId());
        target = ((Col1Integer) target).withCol1(this.getCol1());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            Id_Integer
            & Col1Integer> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toInternalMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", this.id);
        map.put("col1", this.col1);
        return map;
    }

    public java.util.Map<String, Object> toRow() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", this.id);
        map.put("col1", this.col1);
        return map;
    }

    // Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Case_Conversions2_Row that = (Case_Conversions2_Row) o;
        return     Objects.equals(id, that.id) && 
            Objects.equals(col1, that.col1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, col1);
    }

    @Override
    public String toString() {
        return "Case_Conversions2_Row{" + "id=" + id
            + ", " + "col1=" + col1 + "}";
    }

}
