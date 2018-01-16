package com.w11k.lsql.cli.tests.schema_public;

import com.w11k.lsql.cli.tests.structural_fields.*;
import java.util.*;

public final class A_Table_Row implements com.w11k.lsql.TableRow, Id_Integer {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Id_Integer> A_Table_Row from(T source) {
        Object target = new A_Table_Row();
        target = ((Id_Integer) target).withId(source.getId());
        return (A_Table_Row) target;
    }

    // constructors ----------

    public A_Table_Row() {
        
        this.id = null;
    }

    private A_Table_Row(
            java.lang.Integer id) {
        
        this.id = id;
    }

    public A_Table_Row(java.util.Map<String, Object> from) {
        
        this.id = (java.lang.Integer) from.get("id");
    }

    // fields ----------

    public static final String FIELD_id = "id";

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

    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
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
