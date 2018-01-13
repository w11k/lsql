package com.w11k.lsql.cli.tests.schema_public;

import com.w11k.lsql.cli.tests.structural_fields.*;
import java.util.*;

public final class A_table_Row implements com.w11k.lsql.TableRow, Id_Integer {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Id_Integer> A_table_Row from(T source) {
        Object target = new A_table_Row();
        target = ((Id_Integer) target).withId(source.getId());
        return (A_table_Row) target;
    }

    // constructors ----------

    public A_table_Row() {
        
        this.id = null;
    }

    private A_table_Row(
            java.lang.Integer id) {
        
        this.id = id;
    }

    public A_table_Row(java.util.Map<String, Object> from) {
        
        this.id = (java.lang.Integer) from.get("id");
    }

    // fields ----------

    public static final String COL_ID = "id";

    public final java.lang.Integer id;

    public java.lang.Integer getId() {
        return this.id;
    }

    public A_table_Row withId(java.lang.Integer id) {
        return new A_table_Row(id);
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
        A_table_Row that = (A_table_Row) o;
        return     Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "A_table_Row{" + "id=" + id + "}";
    }

}