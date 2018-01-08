package com.w11k.lsql.cli.tests;

import com.w11k.lsql.cli.tests.*;

public class LoadAllPersonsEscaped1Row implements com.w11k.lsql.TableRow,TheidInteger {

    @SuppressWarnings("unchecked")
    public static <T extends 
            TheidInteger> LoadAllPersonsEscaped1Row from(T source) {
        Object target = new LoadAllPersonsEscaped1Row();
        target = ((TheidInteger) target).withTheid(source.getTheid());
        return (LoadAllPersonsEscaped1Row) target;
    }

    public LoadAllPersonsEscaped1Row() {
        this.theid = null;
    }

    private LoadAllPersonsEscaped1Row(
            java.lang.Integer theid) {
        this.theid = theid;
    }

    public LoadAllPersonsEscaped1Row(java.util.Map<String, Object> from) {
        this.theid = (java.lang.Integer) from.get("theid");
    }

    // ------------------------------------------------------------

    public static final String COL_THEID = "theid";

    public final java.lang.Integer theid;

    public java.lang.Integer getTheid() {
        return this.theid;
    }

    public LoadAllPersonsEscaped1Row withTheid(java.lang.Integer theid) {
        return new LoadAllPersonsEscaped1Row(theid);
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
