package com.w11k.lsql.cli.tests.schema_public;

import com.w11k.lsql.cli.tests.*;

public class ChecksRow implements com.w11k.lsql.TableRow,YesnoBoolean {

    @SuppressWarnings("unchecked")
    public static <T extends 
            YesnoBoolean> ChecksRow from(T source) {
        Object target = new ChecksRow();
        target = ((YesnoBoolean) target).withYesno(source.isYesno());
        return (ChecksRow) target;
    }

    public ChecksRow() {
        this.yesno = null;
    }

    private ChecksRow(
            java.lang.Boolean yesno) {
        this.yesno = yesno;
    }

    public ChecksRow(java.util.Map<String, Object> from) {
        this.yesno = (java.lang.Boolean) from.get("yesno");
    }

    // ------------------------------------------------------------

    public static final String COL_YESNO = "yesno";

    public final java.lang.Boolean yesno;

    public java.lang.Boolean isYesno() {
        return this.yesno;
    }

    public ChecksRow withYesno(java.lang.Boolean yesno) {
        return new ChecksRow(yesno);
    }

    // ------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public <T extends 
            YesnoBoolean> T as(T targetStart) {
        Object target = targetStart;
        target = ((YesnoBoolean) target).withYesno(this.isYesno());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            YesnoBoolean> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
        map.put("yesno", this.yesno);
        return map;
    }

}
