package com.w11k.lsql.cli.tests.schema_public;

import com.w11k.lsql.cli.tests.structural_fields.*;
import java.util.*;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public final class Checks_Row implements com.w11k.lsql.TableRow, Yesno_Boolean {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Yesno_Boolean> Checks_Row from(T source) {
        Object target = new Checks_Row();
        target = ((Yesno_Boolean) target).withYesno(source.isYesno());
        return (Checks_Row) target;
    }

    // constructors ----------

    @SuppressWarnings("ConstantConditions")
    public Checks_Row() {
        this.yesno = null;
    }

    @SuppressWarnings("NullableProblems")
    private Checks_Row(
            java.lang.Boolean yesno) {
        this.yesno = yesno;
    }

    @SuppressWarnings("unused")
    public Checks_Row(java.util.Map<String, Object> from) {
        this.yesno = (java.lang.Boolean) from.get("yesno");
    }

    // fields ----------

    @SuppressWarnings("unused")
    public static final String FIELD_yesno = "yesno";

    @javax.annotation.Nonnull public final java.lang.Boolean yesno;

    @javax.annotation.Nonnull public java.lang.Boolean isYesno() {
        return this.yesno;
    }

    public Checks_Row withYesno(@javax.annotation.Nonnull java.lang.Boolean yesno) {
        return new Checks_Row(yesno);
    }

    // class methods ----------

    @SuppressWarnings("unchecked")
    public <T extends 
            Yesno_Boolean> T as(T targetStart) {
        Object target = targetStart;
        target = ((Yesno_Boolean) target).withYesno(this.isYesno());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            Yesno_Boolean> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("yesno", this.yesno);
        return map;
    }

    // Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Checks_Row that = (Checks_Row) o;
        return     Objects.equals(yesno, that.yesno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(yesno);
    }

    @Override
    public String toString() {
        return "Checks_Row{" + "yesno=" + yesno + "}";
    }

}
