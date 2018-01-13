package com.w11k.lsql.cli.tests.stmts1;

import com.w11k.lsql.cli.tests.structural_fields.*;

public final class KeepUnderscoreForCamelCase implements com.w11k.lsql.TableRow, A_Field_Integer, Afield_String {

    @SuppressWarnings("unchecked")
    public static <T extends 
            A_Field_Integer
            & Afield_String> KeepUnderscoreForCamelCase from(T source) {
        Object target = new KeepUnderscoreForCamelCase();
        target = ((A_Field_Integer) target).withAField(source.getAField());
        target = ((Afield_String) target).withAfield(source.getAfield());
        return (KeepUnderscoreForCamelCase) target;
    }

    public KeepUnderscoreForCamelCase() {
        
        this.aField = null;
        this.afield = null;
    }

    private KeepUnderscoreForCamelCase(
            java.lang.Integer aField,
            java.lang.String afield) {
        
        this.aField = aField;
        this.afield = afield;
    }

    public KeepUnderscoreForCamelCase(java.util.Map<String, Object> from) {
        
        this.aField = (java.lang.Integer) from.get("a_field");
        this.afield = (java.lang.String) from.get("afield");
    }

    public static final String COL_A_FIELD = "a_field";

    public final java.lang.Integer aField;

    public java.lang.Integer getAField() {
        return this.aField;
    }

    public KeepUnderscoreForCamelCase withAField(java.lang.Integer aField) {
        return new KeepUnderscoreForCamelCase(aField,afield);
    }
    public static final String COL_AFIELD = "afield";

    public final java.lang.String afield;

    public java.lang.String getAfield() {
        return this.afield;
    }

    public KeepUnderscoreForCamelCase withAfield(java.lang.String afield) {
        return new KeepUnderscoreForCamelCase(aField,afield);
    }

    // class methods ----------

    @SuppressWarnings("unchecked")
    public <T extends 
            A_Field_Integer
            & Afield_String> T as(T targetStart) {
        Object target = targetStart;
        target = ((A_Field_Integer) target).withAField(this.getAField());
        target = ((Afield_String) target).withAfield(this.getAfield());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            A_Field_Integer
            & Afield_String> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
        map.put("a_field", this.aField);
        map.put("afield", this.afield);
        return map;
    }

}
