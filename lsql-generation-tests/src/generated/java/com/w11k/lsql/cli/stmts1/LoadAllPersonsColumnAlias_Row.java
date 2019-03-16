package com.w11k.lsql.cli.stmts1;

import com.w11k.lsql.cli.structural_fields.*;
import java.util.*;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public final class LoadAllPersonsColumnAlias_Row implements com.w11k.lsql.TableRow, Pid_Integer {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Pid_Integer> LoadAllPersonsColumnAlias_Row from(T source) {
        Object target = new LoadAllPersonsColumnAlias_Row();
        target = ((Pid_Integer) target).withPid(source.getPid());
        return (LoadAllPersonsColumnAlias_Row) target;
    }

    @SuppressWarnings("unused")
    public static LoadAllPersonsColumnAlias_Row fromInternalMap(java.util.Map<String, Object> internalMap) {
        return new LoadAllPersonsColumnAlias_Row((java.lang.Integer) internalMap.get("pid"));
    }

    @SuppressWarnings("unused")
    public static LoadAllPersonsColumnAlias_Row fromRow(java.util.Map<String, Object> map) {
        return new LoadAllPersonsColumnAlias_Row((java.lang.Integer) map.get("pid"));
    }

    // constructors ----------

    @SuppressWarnings("ConstantConditions")
    public LoadAllPersonsColumnAlias_Row() {
        this.pid = null;
    }

    @SuppressWarnings("NullableProblems")
    private LoadAllPersonsColumnAlias_Row(
            java.lang.Integer pid) {
        this.pid = pid;
    }

    // fields ----------

    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_PID = "pid";

    @SuppressWarnings("unused")
    public static final String ROW_KEY_PID = "pid";

    @javax.annotation.Nonnull public final java.lang.Integer pid;

    @javax.annotation.Nonnull public java.lang.Integer getPid() {
        return this.pid;
    }

    public LoadAllPersonsColumnAlias_Row withPid(@javax.annotation.Nonnull java.lang.Integer pid) {
        return new LoadAllPersonsColumnAlias_Row(pid);
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

    public java.util.Map<String, Object> toRowMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("pid", this.pid);
        return map;
    }

    // Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoadAllPersonsColumnAlias_Row that = (LoadAllPersonsColumnAlias_Row) o;
        return     Objects.equals(pid, that.pid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pid);
    }

    @Override
    public String toString() {
        return "LoadAllPersonsColumnAlias_Row{" + "pid=" + pid + "}";
    }

}
