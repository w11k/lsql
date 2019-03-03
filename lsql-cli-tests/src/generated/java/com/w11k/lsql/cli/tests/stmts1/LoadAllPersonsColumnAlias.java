package com.w11k.lsql.cli.tests.stmts1;

import com.w11k.lsql.cli.tests.structural_fields.*;
import java.util.*;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public final class LoadAllPersonsColumnAlias implements com.w11k.lsql.TableRow, Pid_Integer {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Pid_Integer> LoadAllPersonsColumnAlias from(T source) {
        Object target = new LoadAllPersonsColumnAlias();
        target = ((Pid_Integer) target).withPid(source.getPid());
        return (LoadAllPersonsColumnAlias) target;
    }

    @SuppressWarnings("unused")
    public static LoadAllPersonsColumnAlias fromInternalMap(java.util.Map<String, Object> internalMap) {
        return new LoadAllPersonsColumnAlias((java.lang.Integer) internalMap.get("pid"));
    }

    @SuppressWarnings("unused")
    public static LoadAllPersonsColumnAlias fromRow(java.util.Map<String, Object> map) {
        return new LoadAllPersonsColumnAlias((java.lang.Integer) map.get("pid"));
    }

    // constructors ----------

    @SuppressWarnings("ConstantConditions")
    public LoadAllPersonsColumnAlias() {
        this.pid = null;
    }

    @SuppressWarnings("NullableProblems")
    private LoadAllPersonsColumnAlias(
            java.lang.Integer pid) {
        this.pid = pid;
    }

    // fields ----------

    @SuppressWarnings("unused")
    public static final String FIELD_PID = "pid";

    @SuppressWarnings("unused")
    public static final String ROW_KEY_PID = "pid";

    @javax.annotation.Nonnull public final java.lang.Integer pid;

    @javax.annotation.Nonnull public java.lang.Integer getPid() {
        return this.pid;
    }

    public LoadAllPersonsColumnAlias withPid(@javax.annotation.Nonnull java.lang.Integer pid) {
        return new LoadAllPersonsColumnAlias(pid);
    }

    // class methods ----------

    @SuppressWarnings("unchecked")
    public <T extends 
            Pid_Integer> T as(T targetStart) {
        Object target = targetStart;
        target = ((Pid_Integer) target).withPid(this.getPid());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            Pid_Integer> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toInternalMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("pid", this.pid);
        return map;
    }

    public java.util.Map<String, Object> toRow() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("pid", this.pid);
        return map;
    }

    // Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoadAllPersonsColumnAlias that = (LoadAllPersonsColumnAlias) o;
        return     Objects.equals(pid, that.pid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pid);
    }

    @Override
    public String toString() {
        return "LoadAllPersonsColumnAlias{" + "pid=" + pid + "}";
    }

}
