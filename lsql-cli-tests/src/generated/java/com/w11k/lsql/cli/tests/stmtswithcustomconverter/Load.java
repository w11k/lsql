package com.w11k.lsql.cli.tests.stmtswithcustomconverter;

import com.w11k.lsql.cli.tests.structural_fields.*;
import java.util.*;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public final class Load implements com.w11k.lsql.TableRow, Field_Integer {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Field_Integer> Load from(T source) {
        Object target = new Load();
        target = ((Field_Integer) target).withField(source.getField());
        return (Load) target;
    }

    @SuppressWarnings("unused")
    public static Load fromInternalMap(java.util.Map<String, Object> internalMap) {
        return new Load((java.lang.Integer) internalMap.get("field"));
    }

    @SuppressWarnings("unused")
    public static Load fromMap(java.util.Map<String, Object> map) {
        return new Load((java.lang.Integer) map.get("field"));
    }

    // constructors ----------

    @SuppressWarnings("ConstantConditions")
    public Load() {
        this.field = null;
    }

    @SuppressWarnings("NullableProblems")
    private Load(
            java.lang.Integer field) {
        this.field = field;
    }

    // fields ----------

    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_FIELD = "field";

    @SuppressWarnings("unused")
    public static final String FIELD_FIELD = "field";

    @javax.annotation.Nullable public final java.lang.Integer field;

    @javax.annotation.Nullable public java.lang.Integer getField() {
        return this.field;
    }

    public Load withField(@javax.annotation.Nullable java.lang.Integer field) {
        return new Load(field);
    }

    // class methods ----------

    @SuppressWarnings("unchecked")
    public <T extends 
            Field_Integer> T as(T targetStart) {
        Object target = targetStart;
        target = ((Field_Integer) target).withField(this.getField());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            Field_Integer> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toInternalMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("field", this.field);
        return map;
    }

    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("field", this.field);
        return map;
    }

    // Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Load that = (Load) o;
        return     Objects.equals(field, that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field);
    }

    @Override
    public String toString() {
        return "Load{" + "field=" + field + "}";
    }

}
