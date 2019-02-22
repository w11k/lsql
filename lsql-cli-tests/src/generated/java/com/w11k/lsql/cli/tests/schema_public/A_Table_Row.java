package com.w11k.lsql.cli.tests.schema_public;

import com.w11k.lsql.cli.tests.structural_fields.*;
import java.util.*;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public final class A_Table_Row implements com.w11k.lsql.TableRow, Id_Pk_Integer {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Id_Pk_Integer> A_Table_Row from(T source) {
        Object target = new A_Table_Row();
        target = ((Id_Pk_Integer) target).withIdPk(source.getIdPk());
        return (A_Table_Row) target;
    }

    @SuppressWarnings("unused")
    public static A_Table_Row fromInternalMap(java.util.Map<String, Object> internalMap) {
        return new A_Table_Row((java.lang.Integer) internalMap.get("id_pk"));
    }

    @SuppressWarnings("unused")
    public static A_Table_Row fromRow(java.util.Map<String, Object> map) {
        return new A_Table_Row((java.lang.Integer) map.get("idPk"));
    }

    // constructors ----------

    @SuppressWarnings("ConstantConditions")
    public A_Table_Row() {
        this.idPk = null;
    }

    @SuppressWarnings("NullableProblems")
    private A_Table_Row(
            java.lang.Integer idPk) {
        this.idPk = idPk;
    }

    // fields ----------

    @SuppressWarnings("unused")
    public static final String FIELD_ID_PK = "id_pk";

    @SuppressWarnings("unused")
    public static final String ROW_KEY_ID_PK = "idPk";

    @javax.annotation.Nonnull public final java.lang.Integer idPk;

    @javax.annotation.Nonnull public java.lang.Integer getIdPk() {
        return this.idPk;
    }

    public A_Table_Row withIdPk(@javax.annotation.Nonnull java.lang.Integer idPk) {
        return new A_Table_Row(idPk);
    }

    // class methods ----------

    @SuppressWarnings("unchecked")
    public <T extends 
            Id_Pk_Integer> T as(T targetStart) {
        Object target = targetStart;
        target = ((Id_Pk_Integer) target).withIdPk(this.getIdPk());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            Id_Pk_Integer> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toInternalMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id_pk", this.idPk);
        return map;
    }

    public java.util.Map<String, Object> toRow() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("idPk", this.idPk);
        return map;
    }

    // Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        A_Table_Row that = (A_Table_Row) o;
        return     Objects.equals(idPk, that.idPk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPk);
    }

    @Override
    public String toString() {
        return "A_Table_Row{" + "idPk=" + idPk + "}";
    }

}
