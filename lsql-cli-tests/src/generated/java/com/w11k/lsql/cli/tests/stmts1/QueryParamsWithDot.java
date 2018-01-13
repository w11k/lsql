package com.w11k.lsql.cli.tests.stmts1;

import com.w11k.lsql.cli.tests.structural_fields.*;

public final class QueryParamsWithDot implements com.w11k.lsql.TableRow, Id_Integer, First_Name_String {

    @SuppressWarnings("unchecked")
    public static <T extends 
            Id_Integer
            & First_Name_String> QueryParamsWithDot from(T source) {
        Object target = new QueryParamsWithDot();
        target = ((Id_Integer) target).withId(source.getId());
        target = ((First_Name_String) target).withFirstName(source.getFirstName());
        return (QueryParamsWithDot) target;
    }

    public QueryParamsWithDot() {
        
        this.id = null;
        this.firstName = null;
    }

    private QueryParamsWithDot(
            java.lang.Integer id,
            java.lang.String firstName) {
        
        this.id = id;
        this.firstName = firstName;
    }

    public QueryParamsWithDot(java.util.Map<String, Object> from) {
        
        this.id = (java.lang.Integer) from.get("id");
        this.firstName = (java.lang.String) from.get("first_name");
    }

    public static final String COL_ID = "id";

    public final java.lang.Integer id;

    public java.lang.Integer getId() {
        return this.id;
    }

    public QueryParamsWithDot withId(java.lang.Integer id) {
        return new QueryParamsWithDot(id,firstName);
    }
    public static final String COL_FIRST_NAME = "first_name";

    public final java.lang.String firstName;

    public java.lang.String getFirstName() {
        return this.firstName;
    }

    public QueryParamsWithDot withFirstName(java.lang.String firstName) {
        return new QueryParamsWithDot(id,firstName);
    }

    // class methods ----------

    @SuppressWarnings("unchecked")
    public <T extends 
            Id_Integer
            & First_Name_String> T as(T targetStart) {
        Object target = targetStart;
        target = ((Id_Integer) target).withId(this.getId());
        target = ((First_Name_String) target).withFirstName(this.getFirstName());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            Id_Integer
            & First_Name_String> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
        map.put("id", this.id);
        map.put("first_name", this.firstName);
        return map;
    }

}
