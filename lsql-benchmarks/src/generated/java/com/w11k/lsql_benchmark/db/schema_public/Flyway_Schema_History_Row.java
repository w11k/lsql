package com.w11k.lsql_benchmark.db.schema_public;

import com.w11k.lsql_benchmark.db.structural_fields.*;
import java.util.*;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public final class Flyway_Schema_History_Row implements com.w11k.lsql.TableRow, Installed_By_String, Success_Boolean, Checksum_Integer, Description_String, Installed_On_Org_Joda_Time_Datetime, Type_String, Version_String, Script_String, Installed_Rank_Integer, Execution_Time_Integer {

    // static methods ----------

    @SuppressWarnings("unchecked")
    public static <T extends 
            Installed_By_String
            & Success_Boolean
            & Checksum_Integer
            & Description_String
            & Installed_On_Org_Joda_Time_Datetime
            & Type_String
            & Version_String
            & Script_String
            & Installed_Rank_Integer
            & Execution_Time_Integer> Flyway_Schema_History_Row from(T source) {
        Object target = new Flyway_Schema_History_Row();
        target = ((Installed_By_String) target).withInstalledBy(source.getInstalledBy());
        target = ((Success_Boolean) target).withSuccess(source.isSuccess());
        target = ((Checksum_Integer) target).withChecksum(source.getChecksum());
        target = ((Description_String) target).withDescription(source.getDescription());
        target = ((Installed_On_Org_Joda_Time_Datetime) target).withInstalledOn(source.getInstalledOn());
        target = ((Type_String) target).withType(source.getType());
        target = ((Version_String) target).withVersion(source.getVersion());
        target = ((Script_String) target).withScript(source.getScript());
        target = ((Installed_Rank_Integer) target).withInstalledRank(source.getInstalledRank());
        target = ((Execution_Time_Integer) target).withExecutionTime(source.getExecutionTime());
        return (Flyway_Schema_History_Row) target;
    }

    @SuppressWarnings("unused")
    public static Flyway_Schema_History_Row fromInternalMap(java.util.Map<String, Object> internalMap) {
        return new Flyway_Schema_History_Row((java.lang.String) internalMap.get("installed_by"), (java.lang.Boolean) internalMap.get("success"), (java.lang.Integer) internalMap.get("checksum"), (java.lang.String) internalMap.get("description"), (org.joda.time.DateTime) internalMap.get("installed_on"), (java.lang.String) internalMap.get("type"), (java.lang.String) internalMap.get("version"), (java.lang.String) internalMap.get("script"), (java.lang.Integer) internalMap.get("installed_rank"), (java.lang.Integer) internalMap.get("execution_time"));
    }

    @SuppressWarnings("unused")
    public static Flyway_Schema_History_Row fromRow(java.util.Map<String, Object> map) {
        return new Flyway_Schema_History_Row((java.lang.String) map.get("installed_by"), (java.lang.Boolean) map.get("success"), (java.lang.Integer) map.get("checksum"), (java.lang.String) map.get("description"), (org.joda.time.DateTime) map.get("installed_on"), (java.lang.String) map.get("type"), (java.lang.String) map.get("version"), (java.lang.String) map.get("script"), (java.lang.Integer) map.get("installed_rank"), (java.lang.Integer) map.get("execution_time"));
    }

    // constructors ----------

    @SuppressWarnings("ConstantConditions")
    public Flyway_Schema_History_Row() {
        this.installedBy = null;
        this.success = null;
        this.checksum = null;
        this.description = null;
        this.installedOn = null;
        this.type = null;
        this.version = null;
        this.script = null;
        this.installedRank = null;
        this.executionTime = null;
    }

    @SuppressWarnings("NullableProblems")
    private Flyway_Schema_History_Row(
            java.lang.String installedBy,
            java.lang.Boolean success,
            java.lang.Integer checksum,
            java.lang.String description,
            org.joda.time.DateTime installedOn,
            java.lang.String type,
            java.lang.String version,
            java.lang.String script,
            java.lang.Integer installedRank,
            java.lang.Integer executionTime) {
        this.installedBy = installedBy;
        this.success = success;
        this.checksum = checksum;
        this.description = description;
        this.installedOn = installedOn;
        this.type = type;
        this.version = version;
        this.script = script;
        this.installedRank = installedRank;
        this.executionTime = executionTime;
    }

    // fields ----------

    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_INSTALLED_BY = "installed_by";

    @SuppressWarnings("unused")
    public static final String FIELD_INSTALLED_BY = "installed_by";

    @javax.annotation.Nonnull public final java.lang.String installedBy;

    @javax.annotation.Nonnull public java.lang.String getInstalledBy() {
        return this.installedBy;
    }

    public Flyway_Schema_History_Row withInstalledBy(@javax.annotation.Nonnull java.lang.String installedBy) {
        return new Flyway_Schema_History_Row(installedBy,success,checksum,description,installedOn,type,version,script,installedRank,executionTime);
    }
    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_SUCCESS = "success";

    @SuppressWarnings("unused")
    public static final String FIELD_SUCCESS = "success";

    @javax.annotation.Nonnull public final java.lang.Boolean success;

    @javax.annotation.Nonnull public java.lang.Boolean isSuccess() {
        return this.success;
    }

    public Flyway_Schema_History_Row withSuccess(@javax.annotation.Nonnull java.lang.Boolean success) {
        return new Flyway_Schema_History_Row(installedBy,success,checksum,description,installedOn,type,version,script,installedRank,executionTime);
    }
    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_CHECKSUM = "checksum";

    @SuppressWarnings("unused")
    public static final String FIELD_CHECKSUM = "checksum";

    @javax.annotation.Nullable public final java.lang.Integer checksum;

    @javax.annotation.Nullable public java.lang.Integer getChecksum() {
        return this.checksum;
    }

    public Flyway_Schema_History_Row withChecksum(@javax.annotation.Nullable java.lang.Integer checksum) {
        return new Flyway_Schema_History_Row(installedBy,success,checksum,description,installedOn,type,version,script,installedRank,executionTime);
    }
    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_DESCRIPTION = "description";

    @SuppressWarnings("unused")
    public static final String FIELD_DESCRIPTION = "description";

    @javax.annotation.Nonnull public final java.lang.String description;

    @javax.annotation.Nonnull public java.lang.String getDescription() {
        return this.description;
    }

    public Flyway_Schema_History_Row withDescription(@javax.annotation.Nonnull java.lang.String description) {
        return new Flyway_Schema_History_Row(installedBy,success,checksum,description,installedOn,type,version,script,installedRank,executionTime);
    }
    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_INSTALLED_ON = "installed_on";

    @SuppressWarnings("unused")
    public static final String FIELD_INSTALLED_ON = "installed_on";

    @javax.annotation.Nonnull public final org.joda.time.DateTime installedOn;

    @javax.annotation.Nonnull public org.joda.time.DateTime getInstalledOn() {
        return this.installedOn;
    }

    public Flyway_Schema_History_Row withInstalledOn(@javax.annotation.Nonnull org.joda.time.DateTime installedOn) {
        return new Flyway_Schema_History_Row(installedBy,success,checksum,description,installedOn,type,version,script,installedRank,executionTime);
    }
    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_TYPE = "type";

    @SuppressWarnings("unused")
    public static final String FIELD_TYPE = "type";

    @javax.annotation.Nonnull public final java.lang.String type;

    @javax.annotation.Nonnull public java.lang.String getType() {
        return this.type;
    }

    public Flyway_Schema_History_Row withType(@javax.annotation.Nonnull java.lang.String type) {
        return new Flyway_Schema_History_Row(installedBy,success,checksum,description,installedOn,type,version,script,installedRank,executionTime);
    }
    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_VERSION = "version";

    @SuppressWarnings("unused")
    public static final String FIELD_VERSION = "version";

    @javax.annotation.Nullable public final java.lang.String version;

    @javax.annotation.Nullable public java.lang.String getVersion() {
        return this.version;
    }

    public Flyway_Schema_History_Row withVersion(@javax.annotation.Nullable java.lang.String version) {
        return new Flyway_Schema_History_Row(installedBy,success,checksum,description,installedOn,type,version,script,installedRank,executionTime);
    }
    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_SCRIPT = "script";

    @SuppressWarnings("unused")
    public static final String FIELD_SCRIPT = "script";

    @javax.annotation.Nonnull public final java.lang.String script;

    @javax.annotation.Nonnull public java.lang.String getScript() {
        return this.script;
    }

    public Flyway_Schema_History_Row withScript(@javax.annotation.Nonnull java.lang.String script) {
        return new Flyway_Schema_History_Row(installedBy,success,checksum,description,installedOn,type,version,script,installedRank,executionTime);
    }
    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_INSTALLED_RANK = "installed_rank";

    @SuppressWarnings("unused")
    public static final String FIELD_INSTALLED_RANK = "installed_rank";

    @javax.annotation.Nonnull public final java.lang.Integer installedRank;

    @javax.annotation.Nonnull public java.lang.Integer getInstalledRank() {
        return this.installedRank;
    }

    public Flyway_Schema_History_Row withInstalledRank(@javax.annotation.Nonnull java.lang.Integer installedRank) {
        return new Flyway_Schema_History_Row(installedBy,success,checksum,description,installedOn,type,version,script,installedRank,executionTime);
    }
    @SuppressWarnings("unused")
    public static final String INTERNAL_FIELD_EXECUTION_TIME = "execution_time";

    @SuppressWarnings("unused")
    public static final String FIELD_EXECUTION_TIME = "execution_time";

    @javax.annotation.Nonnull public final java.lang.Integer executionTime;

    @javax.annotation.Nonnull public java.lang.Integer getExecutionTime() {
        return this.executionTime;
    }

    public Flyway_Schema_History_Row withExecutionTime(@javax.annotation.Nonnull java.lang.Integer executionTime) {
        return new Flyway_Schema_History_Row(installedBy,success,checksum,description,installedOn,type,version,script,installedRank,executionTime);
    }

    // class methods ----------

    @SuppressWarnings("unchecked")
    public <T extends 
            Installed_By_String
            & Success_Boolean
            & Checksum_Integer
            & Description_String
            & Installed_On_Org_Joda_Time_Datetime
            & Type_String
            & Version_String
            & Script_String
            & Installed_Rank_Integer
            & Execution_Time_Integer> T as(T targetStart) {
        Object target = targetStart;
        target = ((Installed_By_String) target).withInstalledBy(this.getInstalledBy());
        target = ((Success_Boolean) target).withSuccess(this.isSuccess());
        target = ((Checksum_Integer) target).withChecksum(this.getChecksum());
        target = ((Description_String) target).withDescription(this.getDescription());
        target = ((Installed_On_Org_Joda_Time_Datetime) target).withInstalledOn(this.getInstalledOn());
        target = ((Type_String) target).withType(this.getType());
        target = ((Version_String) target).withVersion(this.getVersion());
        target = ((Script_String) target).withScript(this.getScript());
        target = ((Installed_Rank_Integer) target).withInstalledRank(this.getInstalledRank());
        target = ((Execution_Time_Integer) target).withExecutionTime(this.getExecutionTime());
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    public <T extends 
            Installed_By_String
            & Success_Boolean
            & Checksum_Integer
            & Description_String
            & Installed_On_Org_Joda_Time_Datetime
            & Type_String
            & Version_String
            & Script_String
            & Installed_Rank_Integer
            & Execution_Time_Integer> T as(Class<? extends T> targetClass) {
        try {
            Object target = targetClass.newInstance();
            return this.as((T) target);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public java.util.Map<String, Object> toInternalMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("installed_by", this.installedBy);
        map.put("success", this.success);
        map.put("checksum", this.checksum);
        map.put("description", this.description);
        map.put("installed_on", this.installedOn);
        map.put("type", this.type);
        map.put("version", this.version);
        map.put("script", this.script);
        map.put("installed_rank", this.installedRank);
        map.put("execution_time", this.executionTime);
        return map;
    }

    public java.util.Map<String, Object> toRow() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("installed_by", this.installedBy);
        map.put("success", this.success);
        map.put("checksum", this.checksum);
        map.put("description", this.description);
        map.put("installed_on", this.installedOn);
        map.put("type", this.type);
        map.put("version", this.version);
        map.put("script", this.script);
        map.put("installed_rank", this.installedRank);
        map.put("execution_time", this.executionTime);
        return map;
    }

    // Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flyway_Schema_History_Row that = (Flyway_Schema_History_Row) o;
        return     Objects.equals(installedBy, that.installedBy) && 
            Objects.equals(success, that.success) && 
            Objects.equals(checksum, that.checksum) && 
            Objects.equals(description, that.description) && 
            Objects.equals(installedOn, that.installedOn) && 
            Objects.equals(type, that.type) && 
            Objects.equals(version, that.version) && 
            Objects.equals(script, that.script) && 
            Objects.equals(installedRank, that.installedRank) && 
            Objects.equals(executionTime, that.executionTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(installedBy, success, checksum, description, installedOn, type, version, script, installedRank, executionTime);
    }

    @Override
    public String toString() {
        return "Flyway_Schema_History_Row{" + "installedBy=" + installedBy
            + ", " + "success=" + success
            + ", " + "checksum=" + checksum
            + ", " + "description=" + description
            + ", " + "installedOn=" + installedOn
            + ", " + "type=" + type
            + ", " + "version=" + version
            + ", " + "script=" + script
            + ", " + "installedRank=" + installedRank
            + ", " + "executionTime=" + executionTime + "}";
    }

}
