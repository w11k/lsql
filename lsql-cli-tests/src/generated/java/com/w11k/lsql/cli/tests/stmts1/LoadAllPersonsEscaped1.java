package com.w11k.lsql.cli.tests.stmts1;

import com.w11k.lsql.cli.tests.structural_fields.*;
import java.util.*;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public final class LoadAllPersonsEscaped1 implements com.w11k.lsql.TableRow, Theid_Integer {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Theid_Integer> LoadAllPersonsEscaped1 from(T source) {
        Object target = new LoadAllPersonsEscaped1();
        target = ((Theid_Integer) target).withTheid(source.getTheid());
        return (LoadAllPersonsEscaped1) target;
    }

    // constructors ----------

    @SuppressWarnings("ConstantConditions")
    public LoadAllPersonsEscaped1() {
        this.theid = null;
    }

    @SuppressWarnings("NullableProblems")
    private LoadAllPersonsEscaped1(
            java.lang.Integer theid) {
        this.theid = theid;
    }

    @SuppressWarnings("unused")
    public LoadAllPersonsEscaped1(java.util.Map<String, Object> from) {
        this.theid = (java.lang.Integer) from.get("theid");
    }

    // fields ----------

    @SuppressWarnings("unused")
    public static final String FIELD_theid = "theid";

    @javax.annotation.Nonnull public final java.lang.Integer theid;

    @javax.annotation.Nonnull public java.lang.Integer getTheid() {
        return this.theid;
    }

    public LoadAllPersonsEscaped1 withTheid(@javax.annotation.Nonnull java.lang.Integer theid) {
        return new LoadAllPersonsEscaped1(theid);
    }

    // class methods ----------

    @SuppressWarnings("unchecked")
    public <T extends 
            Theid_Integer> T as(T targetStart) {
        Object target = targetStart;
        target = ((Theid_Integer) target).withTheid(this.getTheid());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            Theid_Integer> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("theid", this.theid);
        return map;
    }

    // Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoadAllPersonsEscaped1 that = (LoadAllPersonsEscaped1) o;
        return     Objects.equals(theid, that.theid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theid);
    }

    @Override
    public String toString() {
        return "LoadAllPersonsEscaped1{" + "theid=" + theid + "}";
    }

}
