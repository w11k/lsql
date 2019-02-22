package com.w11k.lsql.cli.tests.schema_public;

import com.w11k.lsql.cli.tests.structural_fields.*;
import java.util.*;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public final class Custom_Converter_Row implements com.w11k.lsql.TableRow, Field_Integer {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Field_Integer> Custom_Converter_Row from(T source) {
        Object target = new Custom_Converter_Row();
        target = ((Field_Integer) target).withField(source.getField());
        return (Custom_Converter_Row) target;
    }

    @SuppressWarnings("unused")
    public static Custom_Converter_Row fromInternalMap(java.util.Map<String, Object> internalMap) {
        return new Custom_Converter_Row((java.lang.Integer) internalMap.get("field"));
    }

    @SuppressWarnings("unused")
    public static Custom_Converter_Row fromRow(java.util.Map<String, Object> map) {
        return new Custom_Converter_Row((java.lang.Integer) map.get("field"));
    }

    // constructors ----------

    @SuppressWarnings("ConstantConditions")
    public Custom_Converter_Row() {
        this.field = null;
    }

    @SuppressWarnings("NullableProblems")
    private Custom_Converter_Row(
            java.lang.Integer field) {
        this.field = field;
    }

    // fields ----------

    @SuppressWarnings("unused")
    public static final String FIELD_FIELD = "field";

    @SuppressWarnings("unused")
    public static final String ROW_KEY_FIELD = "field";

    @javax.annotation.Nullable public final java.lang.Integer field;

    @javax.annotation.Nullable public java.lang.Integer getField() {
        return this.field;
    }

    public Custom_Converter_Row withField(@javax.annotation.Nullable java.lang.Integer field) {
        return new Custom_Converter_Row(field);
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

    public java.util.Map<String, Object> toRow() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("field", this.field);
        return map;
    }

    // Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Custom_Converter_Row that = (Custom_Converter_Row) o;
        return     Objects.equals(field, that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field);
    }

    @Override
    public String toString() {
        return "Custom_Converter_Row{" + "field=" + field + "}";
    }

}
