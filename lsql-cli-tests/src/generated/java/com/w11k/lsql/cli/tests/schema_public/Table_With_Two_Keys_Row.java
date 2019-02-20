package com.w11k.lsql.cli.tests.schema_public;

import com.w11k.lsql.cli.tests.structural_fields.*;
import java.util.*;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public final class Table_With_Two_Keys_Row implements com.w11k.lsql.TableRow, Key1Integer, Key2Integer {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Key1Integer
            & Key2Integer> Table_With_Two_Keys_Row from(T source) {
        Object target = new Table_With_Two_Keys_Row();
        target = ((Key1Integer) target).withKey1(source.getKey1());
        target = ((Key2Integer) target).withKey2(source.getKey2());
        return (Table_With_Two_Keys_Row) target;
    }

    @SuppressWarnings("unused")
    public static Table_With_Two_Keys_Row fromInternalMap(java.util.Map<String, Object> internalMap) {
        return new Table_With_Two_Keys_Row((java.lang.Integer) internalMap.get("key1"), (java.lang.Integer) internalMap.get("key2"));
    }

    @SuppressWarnings("unused")
    public static Table_With_Two_Keys_Row fromRow(java.util.Map<String, Object> map) {
        return new Table_With_Two_Keys_Row((java.lang.Integer) map.get("key1"), (java.lang.Integer) map.get("key2"));
    }

    // constructors ----------

    @SuppressWarnings("ConstantConditions")
    public Table_With_Two_Keys_Row() {
        this.key1 = null;
        this.key2 = null;
    }

    @SuppressWarnings("NullableProblems")
    private Table_With_Two_Keys_Row(
            java.lang.Integer key1,
            java.lang.Integer key2) {
        this.key1 = key1;
        this.key2 = key2;
    }

    // fields ----------

    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_KEY1 = "key1";

    @SuppressWarnings("unused")
    public static final String FIELD_KEY1 = "key1";

    @javax.annotation.Nonnull public final java.lang.Integer key1;

    @javax.annotation.Nonnull public java.lang.Integer getKey1() {
        return this.key1;
    }

    public Table_With_Two_Keys_Row withKey1(@javax.annotation.Nonnull java.lang.Integer key1) {
        return new Table_With_Two_Keys_Row(key1,key2);
    }
    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_KEY2 = "key2";

    @SuppressWarnings("unused")
    public static final String FIELD_KEY2 = "key2";

    @javax.annotation.Nonnull public final java.lang.Integer key2;

    @javax.annotation.Nonnull public java.lang.Integer getKey2() {
        return this.key2;
    }

    public Table_With_Two_Keys_Row withKey2(@javax.annotation.Nonnull java.lang.Integer key2) {
        return new Table_With_Two_Keys_Row(key1,key2);
    }

    // class methods ----------

    @SuppressWarnings("unchecked")
    public <T extends 
            Key1Integer
            & Key2Integer> T as(T targetStart) {
        Object target = targetStart;
        target = ((Key1Integer) target).withKey1(this.getKey1());
        target = ((Key2Integer) target).withKey2(this.getKey2());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            Key1Integer
            & Key2Integer> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toInternalMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("key1", this.key1);
        map.put("key2", this.key2);
        return map;
    }

    public java.util.Map<String, Object> toRow() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("key1", this.key1);
        map.put("key2", this.key2);
        return map;
    }

    // Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table_With_Two_Keys_Row that = (Table_With_Two_Keys_Row) o;
        return     Objects.equals(key1, that.key1) && 
            Objects.equals(key2, that.key2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key1, key2);
    }

    @Override
    public String toString() {
        return "Table_With_Two_Keys_Row{" + "key1=" + key1
            + ", " + "key2=" + key2 + "}";
    }

}
