package com.w11k.lsql.cli.tests;

import com.w11k.lsql.cli.tests.structural_fields.*;
import java.util.*;

public final class DummyDto implements com.w11k.lsql.TableRow, Field_AString {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Field_AString> DummyDto from(T source) {
        Object target = new DummyDto();
        target = ((Field_AString) target).withFieldA(source.getFieldA());
        return (DummyDto) target;
    }

    // constructors ----------

    public DummyDto() {
        
        this.fieldA = null;
    }

    private DummyDto(
            java.lang.String fieldA) {
        
        this.fieldA = fieldA;
    }

    public DummyDto(java.util.Map<String, Object> from) {
        
        this.fieldA = (java.lang.String) from.get("fieldA");
    }

    // fields ----------

    public static final String FIELD_fieldA = "fieldA";

    public final java.lang.String fieldA;

    public java.lang.String getFieldA() {
        return this.fieldA;
    }

    public DummyDto withFieldA(java.lang.String fieldA) {
        return new DummyDto(fieldA);
    }

    // class methods ----------

    @SuppressWarnings("unchecked")
    public <T extends 
            Field_AString> T as(T targetStart) {
        Object target = targetStart;
        target = ((Field_AString) target).withFieldA(this.getFieldA());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            Field_AString> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
        map.put("fieldA", this.fieldA);
        return map;
    }

    // Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DummyDto that = (DummyDto) o;
        return     Objects.equals(fieldA, that.fieldA);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldA);
    }

    @Override
    public String toString() {
        return "DummyDto{" + "fieldA=" + fieldA + "}";
    }

}
