package com.w11k.lsql.cli.tests;

import com.w11k.lsql.cli.tests.*;

public class LoadAllPersonsEscaped2Row implements com.w11k.lsql.TableRow,TheidInteger {

    @SuppressWarnings("unchecked")
    public static <T extends 
            TheidInteger> LoadAllPersonsEscaped2Row from(T source) {
        Object target = new LoadAllPersonsEscaped2Row();
        target = ((TheidInteger) target).withTheid(source.getTheid());
        return (LoadAllPersonsEscaped2Row) target;
    }

    public LoadAllPersonsEscaped2Row() {
        this.theid = null;
    }

    private LoadAllPersonsEscaped2Row(
            java.lang.Integer theid) {
        this.theid = theid;
    }

    public LoadAllPersonsEscaped2Row(java.util.Map<String, Object> from) {
        this.theid = (java.lang.Integer) from.get("theid");
    }

    // ------------------------------------------------------------

    public static final String COL_THEID = "theid";

    public final java.lang.Integer theid;

    public java.lang.Integer getTheid() {
        return this.theid;
    }

    public LoadAllPersonsEscaped2Row withTheid(java.lang.Integer theid) {
        return new LoadAllPersonsEscaped2Row(theid);
    }

    // ------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public <T extends 
            TheidInteger> T as(T targetStart) {
        Object target = targetStart;
        target = ((TheidInteger) target).withTheid(this.getTheid());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            TheidInteger> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
        map.put("theid", this.theid);
        return map;
    }

}
