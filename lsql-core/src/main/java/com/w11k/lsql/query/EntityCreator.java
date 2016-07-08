package com.w11k.lsql.query;

public abstract class EntityCreator {

    public abstract Object createEntity(Object parent, String fieldNameInParent, boolean isList);

    public abstract void setValue(Object entity, String fieldName, Object value);



}
