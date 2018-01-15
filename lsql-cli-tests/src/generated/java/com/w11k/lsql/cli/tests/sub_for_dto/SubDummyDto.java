package com.w11k.lsql.cli.tests.sub_for_dto;

import com.w11k.lsql.cli.tests.structural_fields.*;
import java.util.*;

public final class SubDummyDto implements com.w11k.lsql.TableRow, Field_AString, Field_BNumber {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Field_AString
            & Field_BNumber> SubDummyDto from(T source) {
        Object target = new SubDummyDto();
        target = ((Field_AString) target).withFieldA(source.getFieldA());
        target = ((Field_BNumber) target).withFieldB(source.getFieldB());
        return (SubDummyDto) target;
    }

    // constructors ----------

    public SubDummyDto() {
        
        this.fieldA = null;
        this.fieldB = null;
    }

    private SubDummyDto(
            java.lang.String fieldA,
            java.lang.Number fieldB) {
        
        this.fieldA = fieldA;
        this.fieldB = fieldB;
    }

    public SubDummyDto(java.util.Map<String, Object> from) {
        
        this.fieldA = (java.lang.String) from.get("fieldA");
        this.fieldB = (java.lang.Number) from.get("fieldB");
    }

    // fields ----------

    public static final String FIELD_fieldA = "fieldA";

    public final java.lang.String fieldA;

    public java.lang.String getFieldA() {
        return this.fieldA;
    }

    public SubDummyDto withFieldA(java.lang.String fieldA) {
        return new SubDummyDto(fieldA,fieldB);
    }
    public static final String FIELD_fieldB = "fieldB";

    public final java.lang.Number fieldB;

    public java.lang.Number getFieldB() {
        return this.fieldB;
    }

    public SubDummyDto withFieldB(java.lang.Number fieldB) {
        return new SubDummyDto(fieldA,fieldB);
    }

    // class methods ----------

    @SuppressWarnings("unchecked")
    public <T extends 
            Field_AString
            & Field_BNumber> T as(T targetStart) {
        Object target = targetStart;
        target = ((Field_AString) target).withFieldA(this.getFieldA());
        target = ((Field_BNumber) target).withFieldB(this.getFieldB());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            Field_AString
            & Field_BNumber> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
        map.put("fieldA", this.fieldA);
        map.put("fieldB", this.fieldB);
        return map;
    }

    // Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubDummyDto that = (SubDummyDto) o;
        return     Objects.equals(fieldA, that.fieldA) && 
            Objects.equals(fieldB, that.fieldB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldA, fieldB);
    }

    @Override
    public String toString() {
        return "SubDummyDto{" + "fieldA=" + fieldA
            + ", " + "fieldB=" + fieldB + "}";
    }

}
