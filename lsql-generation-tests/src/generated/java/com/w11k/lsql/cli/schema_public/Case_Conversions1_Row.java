package com.w11k.lsql.cli.schema_public;

import com.w11k.lsql.cli.structural_fields.*;
import java.util.*;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public final class Case_Conversions1_Row implements com.w11k.lsql.TableRow, Aaa_Bbb_Integer, Id_Integer, Aaabbb_Integer {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Aaa_Bbb_Integer
            & Id_Integer
            & Aaabbb_Integer> Case_Conversions1_Row from(T source) {
        Object target = new Case_Conversions1_Row();
        target = ((Aaa_Bbb_Integer) target).withAaaBbb(source.getAaaBbb());
        target = ((Id_Integer) target).withId(source.getId());
        target = ((Aaabbb_Integer) target).withAaabbb(source.getAaabbb());
        return (Case_Conversions1_Row) target;
    }

    @SuppressWarnings("unused")
    public static Case_Conversions1_Row fromInternalMap(java.util.Map<String, Object> internalMap) {
        return new Case_Conversions1_Row((java.lang.Integer) internalMap.get("aaa_bbb"), (java.lang.Integer) internalMap.get("id"), (java.lang.Integer) internalMap.get("aaabbb"));
    }

    @SuppressWarnings("unused")
    public static Case_Conversions1_Row fromRow(java.util.Map<String, Object> map) {
        return new Case_Conversions1_Row((java.lang.Integer) map.get("aaaBbb"), (java.lang.Integer) map.get("id"), (java.lang.Integer) map.get("aaabbb"));
    }

    // constructors ----------

    @SuppressWarnings("ConstantConditions")
    public Case_Conversions1_Row() {
        this.aaaBbb = null;
        this.id = null;
        this.aaabbb = null;
    }

    @SuppressWarnings("NullableProblems")
    private Case_Conversions1_Row(
            java.lang.Integer aaaBbb,
            java.lang.Integer id,
            java.lang.Integer aaabbb) {
        this.aaaBbb = aaaBbb;
        this.id = id;
        this.aaabbb = aaabbb;
    }

    // fields ----------

    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_AAA_BBB = "aaa_bbb";

    @SuppressWarnings("unused")
    public static final String ROW_KEY_AAA_BBB = "aaaBbb";

    @javax.annotation.Nullable public final java.lang.Integer aaaBbb;

    @javax.annotation.Nullable public java.lang.Integer getAaaBbb() {
        return this.aaaBbb;
    }

    public Case_Conversions1_Row withAaaBbb(@javax.annotation.Nullable java.lang.Integer aaaBbb) {
        return new Case_Conversions1_Row(aaaBbb,id,aaabbb);
    }
    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_ID = "id";

    @SuppressWarnings("unused")
    public static final String ROW_KEY_ID = "id";

    @javax.annotation.Nonnull public final java.lang.Integer id;

    @javax.annotation.Nonnull public java.lang.Integer getId() {
        return this.id;
    }

    public Case_Conversions1_Row withId(@javax.annotation.Nonnull java.lang.Integer id) {
        return new Case_Conversions1_Row(aaaBbb,id,aaabbb);
    }
    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_AAABBB = "aaabbb";

    @SuppressWarnings("unused")
    public static final String ROW_KEY_AAABBB = "aaabbb";

    @javax.annotation.Nullable public final java.lang.Integer aaabbb;

    @javax.annotation.Nullable public java.lang.Integer getAaabbb() {
        return this.aaabbb;
    }

    public Case_Conversions1_Row withAaabbb(@javax.annotation.Nullable java.lang.Integer aaabbb) {
        return new Case_Conversions1_Row(aaaBbb,id,aaabbb);
    }

    // class methods ----------

    @SuppressWarnings("unchecked")
    public <T extends 
            Aaa_Bbb_Integer
            & Id_Integer
            & Aaabbb_Integer> T as(T targetStart) {
        Object target = targetStart;
        target = ((Aaa_Bbb_Integer) target).withAaaBbb(this.getAaaBbb());
        target = ((Id_Integer) target).withId(this.getId());
        target = ((Aaabbb_Integer) target).withAaabbb(this.getAaabbb());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            Aaa_Bbb_Integer
            & Id_Integer
            & Aaabbb_Integer> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toInternalMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("aaa_bbb", this.aaaBbb);
        map.put("id", this.id);
        map.put("aaabbb", this.aaabbb);
        return map;
    }

    public java.util.Map<String, Object> toRowMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("aaaBbb", this.aaaBbb);
        map.put("id", this.id);
        map.put("aaabbb", this.aaabbb);
        return map;
    }

    // Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Case_Conversions1_Row that = (Case_Conversions1_Row) o;
        return     Objects.equals(aaaBbb, that.aaaBbb) && 
            Objects.equals(id, that.id) && 
            Objects.equals(aaabbb, that.aaabbb);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aaaBbb, id, aaabbb);
    }

    @Override
    public String toString() {
        return "Case_Conversions1_Row{" + "aaaBbb=" + aaaBbb
            + ", " + "id=" + id
            + ", " + "aaabbb=" + aaabbb + "}";
    }

}
