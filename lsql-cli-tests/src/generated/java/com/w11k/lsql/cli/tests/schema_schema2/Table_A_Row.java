package com.w11k.lsql.cli.tests.schema_schema2;

import com.w11k.lsql.cli.tests.structural_fields.*;
import java.util.*;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public final class Table_A_Row implements com.w11k.lsql.TableRow, Id_Integer, Col1String {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Id_Integer
            & Col1String> Table_A_Row from(T source) {
        Object target = new Table_A_Row();
        target = ((Id_Integer) target).withId(source.getId());
        target = ((Col1String) target).withCol1(source.getCol1());
        return (Table_A_Row) target;
    }

    @SuppressWarnings("unused")
    public static Table_A_Row fromInternalMap(java.util.Map<String, Object> internalMap) {
        return new Table_A_Row((java.lang.Integer) internalMap.get("id"), (java.lang.String) internalMap.get("col1"));
    }

    @SuppressWarnings("unused")
    public static Table_A_Row fromRow(java.util.Map<String, Object> map) {
        return new Table_A_Row((java.lang.Integer) map.get("id"), (java.lang.String) map.get("col1"));
    }

    // constructors ----------

    @SuppressWarnings("ConstantConditions")
    public Table_A_Row() {
        this.id = null;
        this.col1 = null;
    }

    @SuppressWarnings("NullableProblems")
    private Table_A_Row(
            java.lang.Integer id,
            java.lang.String col1) {
        this.id = id;
        this.col1 = col1;
    }

    // fields ----------

    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_ID = "id";

    @SuppressWarnings("unused")
    public static final String FIELD_ID = "id";

    @javax.annotation.Nonnull public final java.lang.Integer id;

    @javax.annotation.Nonnull public java.lang.Integer getId() {
        return this.id;
    }

    public Table_A_Row withId(@javax.annotation.Nonnull java.lang.Integer id) {
        return new Table_A_Row(id,col1);
    }
    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_COL1 = "col1";

    @SuppressWarnings("unused")
    public static final String FIELD_COL1 = "col1";

    @javax.annotation.Nullable public final java.lang.String col1;

    @javax.annotation.Nullable public java.lang.String getCol1() {
        return this.col1;
    }

    public Table_A_Row withCol1(@javax.annotation.Nullable java.lang.String col1) {
        return new Table_A_Row(id,col1);
    }

    // class methods ----------

    @SuppressWarnings("unchecked")
    public <T extends 
            Id_Integer
            & Col1String> T as(T targetStart) {
        Object target = targetStart;
        target = ((Id_Integer) target).withId(this.getId());
        target = ((Col1String) target).withCol1(this.getCol1());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            Id_Integer
            & Col1String> T as(Class<? extends T> targetClass) {
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
        Table_A_Row that = (Table_A_Row) o;
        return     Objects.equals(id, that.id) && 
            Objects.equals(col1, that.col1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, col1);
    }

    @Override
    public String toString() {
        return "Table_A_Row{" + "id=" + id
            + ", " + "col1=" + col1 + "}";
    }

}
