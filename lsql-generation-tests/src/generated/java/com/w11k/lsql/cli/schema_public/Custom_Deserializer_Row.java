package com.w11k.lsql.cli.schema_public;

import com.w11k.lsql.cli.structural_fields.*;
import java.util.*;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public final class Custom_Deserializer_Row implements com.w11k.lsql.TableRow, Data_Com_W11k_Lsql_Blob, Id_Integer {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Data_Com_W11k_Lsql_Blob
            & Id_Integer> Custom_Deserializer_Row from(T source) {
        Object target = new Custom_Deserializer_Row();
        target = ((Data_Com_W11k_Lsql_Blob) target).withData(source.getData());
        target = ((Id_Integer) target).withId(source.getId());
        return (Custom_Deserializer_Row) target;
    }

    @SuppressWarnings("unused")
    public static Custom_Deserializer_Row fromInternalMap(java.util.Map<String, Object> internalMap) {
        return new Custom_Deserializer_Row((com.w11k.lsql.Blob) internalMap.get("data"), (java.lang.Integer) internalMap.get("id"));
    }

    @SuppressWarnings("unused")
    public static Custom_Deserializer_Row fromRow(java.util.Map<String, Object> map) {
        return new Custom_Deserializer_Row((com.w11k.lsql.Blob) map.get("data"), (java.lang.Integer) map.get("id"));
    }

    // constructors ----------

    @SuppressWarnings("ConstantConditions")
    public Custom_Deserializer_Row() {
        this.data = null;
        this.id = null;
    }

    @SuppressWarnings("NullableProblems")
    private Custom_Deserializer_Row(
            com.w11k.lsql.Blob data,
            java.lang.Integer id) {
        this.data = data;
        this.id = id;
    }

    // fields ----------

    @SuppressWarnings("unused")
    public static final String FIELD_DATA = "data";

    @SuppressWarnings("unused")
    public static final String ROW_KEY_DATA = "data";

    @javax.annotation.Nullable public final com.w11k.lsql.Blob data;

    @javax.annotation.Nullable public com.w11k.lsql.Blob getData() {
        return this.data;
    }

    public Custom_Deserializer_Row withData(@javax.annotation.Nullable com.w11k.lsql.Blob data) {
        return new Custom_Deserializer_Row(data,id);
    }
    @SuppressWarnings("unused")
    public static final String FIELD_ID = "id";

    @SuppressWarnings("unused")
    public static final String ROW_KEY_ID = "id";

    @javax.annotation.Nonnull public final java.lang.Integer id;

    @javax.annotation.Nonnull public java.lang.Integer getId() {
        return this.id;
    }

    public Custom_Deserializer_Row withId(@javax.annotation.Nonnull java.lang.Integer id) {
        return new Custom_Deserializer_Row(data,id);
    }

    // class methods ----------

    @SuppressWarnings("unchecked")
    public <T extends 
            Data_Com_W11k_Lsql_Blob
            & Id_Integer> T as(T targetStart) {
        Object target = targetStart;
        target = ((Data_Com_W11k_Lsql_Blob) target).withData(this.getData());
        target = ((Id_Integer) target).withId(this.getId());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            Data_Com_W11k_Lsql_Blob
            & Id_Integer> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toInternalMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("data", this.data);
        map.put("id", this.id);
        return map;
    }

    public java.util.Map<String, Object> toRow() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("data", this.data);
        map.put("id", this.id);
        return map;
    }

    // Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Custom_Deserializer_Row that = (Custom_Deserializer_Row) o;
        return     Objects.equals(data, that.data) && 
            Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, id);
    }

    @Override
    public String toString() {
        return "Custom_Deserializer_Row{" + "data=" + data
            + ", " + "id=" + id + "}";
    }

}
