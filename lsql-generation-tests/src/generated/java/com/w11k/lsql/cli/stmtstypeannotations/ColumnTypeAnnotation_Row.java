package com.w11k.lsql.cli.stmtstypeannotations;

import com.w11k.lsql.cli.structural_fields.*;
import java.util.*;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public final class ColumnTypeAnnotation_Row implements com.w11k.lsql.TableRow, Id_Integer {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Id_Integer> ColumnTypeAnnotation_Row from(T source) {
        Object target = new ColumnTypeAnnotation_Row();
        target = ((Id_Integer) target).withId(source.getId());
        return (ColumnTypeAnnotation_Row) target;
    }

    @SuppressWarnings("unused")
    public static ColumnTypeAnnotation_Row fromInternalMap(java.util.Map<String, Object> internalMap) {
        return new ColumnTypeAnnotation_Row((java.lang.Integer) internalMap.get("id"));
    }

    @SuppressWarnings("unused")
    public static ColumnTypeAnnotation_Row fromRow(java.util.Map<String, Object> map) {
        return new ColumnTypeAnnotation_Row((java.lang.Integer) map.get("id"));
    }

    // constructors ----------

    @SuppressWarnings("ConstantConditions")
    public ColumnTypeAnnotation_Row() {
        this.id = null;
    }

    @SuppressWarnings("NullableProblems")
    private ColumnTypeAnnotation_Row(
            java.lang.Integer id) {
        this.id = id;
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

    public ColumnTypeAnnotation_Row withId(@javax.annotation.Nonnull java.lang.Integer id) {
        return new ColumnTypeAnnotation_Row(id);
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

    public java.util.Map<String, Object> toRowMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", this.id);
        return map;
    }

    // Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnTypeAnnotation_Row that = (ColumnTypeAnnotation_Row) o;
        return     Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ColumnTypeAnnotation_Row{" + "id=" + id + "}";
    }

}
