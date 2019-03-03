package com.w11k.lsql.cli.tests.stmtstypeannotations;

import com.w11k.lsql.cli.tests.structural_fields.*;
import java.util.*;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public final class ColumnTypeAnnotation implements com.w11k.lsql.TableRow, Id_Integer {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Id_Integer> ColumnTypeAnnotation from(T source) {
        Object target = new ColumnTypeAnnotation();
        target = ((Id_Integer) target).withId(source.getId());
        return (ColumnTypeAnnotation) target;
    }

    @SuppressWarnings("unused")
    public static ColumnTypeAnnotation fromInternalMap(java.util.Map<String, Object> internalMap) {
        return new ColumnTypeAnnotation((java.lang.Integer) internalMap.get("id"));
    }

    @SuppressWarnings("unused")
    public static ColumnTypeAnnotation fromRow(java.util.Map<String, Object> map) {
        return new ColumnTypeAnnotation((java.lang.Integer) map.get("id"));
    }

    // constructors ----------

    @SuppressWarnings("ConstantConditions")
    public ColumnTypeAnnotation() {
        this.id = null;
    }

    @SuppressWarnings("NullableProblems")
    private ColumnTypeAnnotation(
            java.lang.Integer id) {
        this.id = id;
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

    public ColumnTypeAnnotation withId(@javax.annotation.Nonnull java.lang.Integer id) {
        return new ColumnTypeAnnotation(id);
    }

    // class methods ----------

    @SuppressWarnings("unchecked")
    public <T extends 
            Id_Integer> T as(T targetStart) {
        Object target = targetStart;
        target = ((Id_Integer) target).withId(this.getId());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            Id_Integer> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toInternalMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", this.id);
        return map;
    }

    public java.util.Map<String, Object> toRow() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", this.id);
        return map;
    }

    // Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnTypeAnnotation that = (ColumnTypeAnnotation) o;
        return     Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ColumnTypeAnnotation{" + "id=" + id + "}";
    }

}
