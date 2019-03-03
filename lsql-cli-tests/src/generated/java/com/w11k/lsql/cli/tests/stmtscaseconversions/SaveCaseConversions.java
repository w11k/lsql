package com.w11k.lsql.cli.tests.stmtscaseconversions;

import com.w11k.lsql.cli.tests.structural_fields.*;
import java.util.*;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public final class SaveCaseConversions implements com.w11k.lsql.TableRow, Id_Integer, One_Two_Integer, Onetwo_Integer {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Id_Integer
            & One_Two_Integer
            & Onetwo_Integer> SaveCaseConversions from(T source) {
        Object target = new SaveCaseConversions();
        target = ((Id_Integer) target).withId(source.getId());
        target = ((One_Two_Integer) target).withOneTwo(source.getOneTwo());
        target = ((Onetwo_Integer) target).withOnetwo(source.getOnetwo());
        return (SaveCaseConversions) target;
    }

    @SuppressWarnings("unused")
    public static SaveCaseConversions fromInternalMap(java.util.Map<String, Object> internalMap) {
        return new SaveCaseConversions((java.lang.Integer) internalMap.get("id"), (java.lang.Integer) internalMap.get("one_two"), (java.lang.Integer) internalMap.get("onetwo"));
    }

    @SuppressWarnings("unused")
    public static SaveCaseConversions fromRow(java.util.Map<String, Object> map) {
        return new SaveCaseConversions((java.lang.Integer) map.get("id"), (java.lang.Integer) map.get("oneTwo"), (java.lang.Integer) map.get("onetwo"));
    }

    // constructors ----------

    @SuppressWarnings("ConstantConditions")
    public SaveCaseConversions() {
        this.id = null;
        this.oneTwo = null;
        this.onetwo = null;
    }

    @SuppressWarnings("NullableProblems")
    private SaveCaseConversions(
            java.lang.Integer id,
            java.lang.Integer oneTwo,
            java.lang.Integer onetwo) {
        this.id = id;
        this.oneTwo = oneTwo;
        this.onetwo = onetwo;
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

    public SaveCaseConversions withId(@javax.annotation.Nonnull java.lang.Integer id) {
        return new SaveCaseConversions(id,oneTwo,onetwo);
    }
    @SuppressWarnings("unused")
    public static final String FIELD_ONE_TWO = "one_two";

    @SuppressWarnings("unused")
    public static final String ROW_KEY_ONE_TWO = "oneTwo";

    @javax.annotation.Nullable public final java.lang.Integer oneTwo;

    @javax.annotation.Nullable public java.lang.Integer getOneTwo() {
        return this.oneTwo;
    }

    public SaveCaseConversions withOneTwo(@javax.annotation.Nullable java.lang.Integer oneTwo) {
        return new SaveCaseConversions(id,oneTwo,onetwo);
    }
    @SuppressWarnings("unused")
    public static final String FIELD_ONETWO = "onetwo";

    @SuppressWarnings("unused")
    public static final String ROW_KEY_ONETWO = "onetwo";

    @javax.annotation.Nullable public final java.lang.Integer onetwo;

    @javax.annotation.Nullable public java.lang.Integer getOnetwo() {
        return this.onetwo;
    }

    public SaveCaseConversions withOnetwo(@javax.annotation.Nullable java.lang.Integer onetwo) {
        return new SaveCaseConversions(id,oneTwo,onetwo);
    }

    // class methods ----------

    @SuppressWarnings("unchecked")
    public <T extends 
            Id_Integer
            & One_Two_Integer
            & Onetwo_Integer> T as(T targetStart) {
        Object target = targetStart;
        target = ((Id_Integer) target).withId(this.getId());
        target = ((One_Two_Integer) target).withOneTwo(this.getOneTwo());
        target = ((Onetwo_Integer) target).withOnetwo(this.getOnetwo());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            Id_Integer
            & One_Two_Integer
            & Onetwo_Integer> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toInternalMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", this.id);
        map.put("one_two", this.oneTwo);
        map.put("onetwo", this.onetwo);
        return map;
    }

    public java.util.Map<String, Object> toRow() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", this.id);
        map.put("oneTwo", this.oneTwo);
        map.put("onetwo", this.onetwo);
        return map;
    }

    // Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaveCaseConversions that = (SaveCaseConversions) o;
        return     Objects.equals(id, that.id) && 
            Objects.equals(oneTwo, that.oneTwo) && 
            Objects.equals(onetwo, that.onetwo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, oneTwo, onetwo);
    }

    @Override
    public String toString() {
        return "SaveCaseConversions{" + "id=" + id
            + ", " + "oneTwo=" + oneTwo
            + ", " + "onetwo=" + onetwo + "}";
    }

}
