package com.w11k.lsql_benchmark.db.schema_public;

import com.w11k.lsql_benchmark.db.structural_fields.*;
import java.util.*;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public final class Table1_Row implements com.w11k.lsql.TableRow, Field1Integer, Id_Integer {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Field1Integer
            & Id_Integer> Table1_Row from(T source) {
        Object target = new Table1_Row();
        target = ((Field1Integer) target).withField1(source.getField1());
        target = ((Id_Integer) target).withId(source.getId());
        return (Table1_Row) target;
    }

    @SuppressWarnings("unused")
    public static Table1_Row fromInternalMap(java.util.Map<String, Object> internalMap) {
        return new Table1_Row((java.lang.Integer) internalMap.get("field1"), (java.lang.Integer) internalMap.get("id"));
    }

    @SuppressWarnings("unused")
    public static Table1_Row fromRow(java.util.Map<String, Object> map) {
        return new Table1_Row((java.lang.Integer) map.get("field1"), (java.lang.Integer) map.get("id"));
    }

    // constructors ----------

    @SuppressWarnings("ConstantConditions")
    public Table1_Row() {
        this.field1 = null;
        this.id = null;
    }

    @SuppressWarnings("NullableProblems")
    private Table1_Row(
            java.lang.Integer field1,
            java.lang.Integer id) {
        this.field1 = field1;
        this.id = id;
    }

    // fields ----------

    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_FIELD1 = "field1";

    @SuppressWarnings("unused")
    public static final String FIELD_FIELD1 = "field1";

    @javax.annotation.Nullable public final java.lang.Integer field1;

    @javax.annotation.Nullable public java.lang.Integer getField1() {
        return this.field1;
    }

    public Table1_Row withField1(@javax.annotation.Nullable java.lang.Integer field1) {
        return new Table1_Row(field1,id);
    }
    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_ID = "id";

    @SuppressWarnings("unused")
    public static final String FIELD_ID = "id";

    @javax.annotation.Nonnull public final java.lang.Integer id;

    @javax.annotation.Nonnull public java.lang.Integer getId() {
        return this.id;
    }

    public Table1_Row withId(@javax.annotation.Nonnull java.lang.Integer id) {
        return new Table1_Row(field1,id);
    }

    // class methods ----------

    @SuppressWarnings("unchecked")
    public <T extends 
            Field1Integer
            & Id_Integer> T as(T targetStart) {
        Object target = targetStart;
        target = ((Field1Integer) target).withField1(this.getField1());
        target = ((Id_Integer) target).withId(this.getId());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            Field1Integer
            & Id_Integer> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toInternalMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("field1", this.field1);
        map.put("id", this.id);
        return map;
    }

    public java.util.Map<String, Object> toRow() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("field1", this.field1);
        map.put("id", this.id);
        return map;
    }

    // Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table1_Row that = (Table1_Row) o;
        return     Objects.equals(field1, that.field1) && 
            Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field1, id);
    }

    @Override
    public String toString() {
        return "Table1_Row{" + "field1=" + field1
            + ", " + "id=" + id + "}";
    }

}
