package com.w11k.lsql.cli.tests.stmts1;

import com.w11k.lsql.cli.tests.structural_fields.*;

public final class LoadAllPersonsEscaped1 implements com.w11k.lsql.TableRow, Theid_Integer {

    @SuppressWarnings("unchecked")
    public static <T extends 
            Theid_Integer> LoadAllPersonsEscaped1 from(T source) {
        Object target = new LoadAllPersonsEscaped1();
        target = ((Theid_Integer) target).withTheid(source.getTheid());
        return (LoadAllPersonsEscaped1) target;
    }

    public LoadAllPersonsEscaped1() {
        
        this.theid = null;
    }

    private LoadAllPersonsEscaped1(
            java.lang.Integer theid) {
        
        this.theid = theid;
    }

    public LoadAllPersonsEscaped1(java.util.Map<String, Object> from) {
        
        this.theid = (java.lang.Integer) from.get("theid");
    }

    public static final String COL_THEID = "theid";

    public final java.lang.Integer theid;

    public java.lang.Integer getTheid() {
        return this.theid;
    }

    public LoadAllPersonsEscaped1 withTheid(java.lang.Integer theid) {
        return new LoadAllPersonsEscaped1(theid);
    }

    // class methods ----------

    @SuppressWarnings("unchecked")
    public <T extends 
            Theid_Integer> T as(T targetStart) {
        Object target = targetStart;
        target = ((Theid_Integer) target).withTheid(this.getTheid());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            Theid_Integer> T as(Class<? extends T> targetClass) {
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
