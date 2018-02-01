package com.w11k.lsql.cli.tests.schema_public;

import com.w11k.lsql.cli.tests.structural_fields.*;
import java.util.*;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public final class A_Table_Row implements com.w11k.lsql.TableRow, Id_Integer {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Id_Integer> A_Table_Row from(T source) {
        Object target = new A_Table_Row();
        target = ((Id_Integer) target).withId(source.getId());
        return (A_Table_Row) target;
    }

    @SuppressWarnings("unused")
    public static A_Table_Row fromInternalMap(java.util.Map<String, Object> internalMap) {
        return new A_Table_Row((java.lang.Integer) internalMap.get("id"));
    }

    @SuppressWarnings("unused")
    public static A_Table_Row fromMap(java.util.Map<String, Object> map) {
        return new A_Table_Row((java.lang.Integer) map.get("id"));
    }

    // constructors ----------

    @SuppressWarnings("ConstantConditions")
    public A_Table_Row() {
        this.id = null;
    }

    @SuppressWarnings("NullableProblems")
    private A_Table_Row(
            java.lang.Integer id) {
        this.id = id;
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

    public A_Table_Row withId(@javax.annotation.Nonnull java.lang.Integer id) {
        return new A_Table_Row(id);
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

    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", this.id);
        return map;
    }

    // Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        A_Table_Row that = (A_Table_Row) o;
        return     Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "A_Table_Row{" + "id=" + id + "}";
    }

}
