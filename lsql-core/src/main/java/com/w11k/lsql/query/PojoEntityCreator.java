package com.w11k.lsql.query;

import com.google.common.collect.Lists;
import com.w11k.lsql.PojoMapper;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class PojoEntityCreator<T> extends EntityCreator {

    private final Class<T> topLevelClass;

    public PojoEntityCreator(Class<T> topLevelClass) {
        this.topLevelClass = topLevelClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object createEntity(Object parent, String fieldNameInParent, boolean isList) {
        if (parent == null) {
            return PojoMapper.getFor(this.topLevelClass).newInstance();
        }

        PojoMapper<Object> parentPojoMapper = (PojoMapper<Object>) PojoMapper.getFor(parent.getClass());
        Class<?> fieldTypeInParent = parentPojoMapper.getTypeOfField(fieldNameInParent);
        PojoMapper<?> pojoMapper = PojoMapper.getFor(getTypeOfField(parent, fieldTypeInParent, fieldNameInParent, isList));
        Object obj = pojoMapper.newInstance();

        // Store entity in parent
        Object fieldInParent = parentPojoMapper.getValue(parent, fieldNameInParent);
        if (isList) {
            // 1:n
            Collection<Object> coll;
            if (fieldInParent == null) {
                coll = createCollectionInstance(fieldTypeInParent);
                parentPojoMapper.setValue(parent, fieldNameInParent, coll);
            } else {
                coll = (Collection<Object>) fieldInParent;
            }
            coll.add(obj);
        } else {
            // 1:1
            // TODO
//            assert fieldInParent == null;
//            parentRow.put(fieldNameInParent, row);
        }

        return obj;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(Object entity, String fieldName, Object value) {
        PojoMapper.getFor((Class<Object>) entity.getClass()).setValue(entity, fieldName, value);
    }

    private Collection<Object> createCollectionInstance(Class<?> fieldTypeInParent) {
        if (fieldTypeInParent.equals(Collection.class)) {
            return Lists.newLinkedList();
        } else if (fieldTypeInParent.equals(List.class)) {
            return Lists.newLinkedList();
        } else if (fieldTypeInParent.equals(LinkedList.class)) {
            return Lists.newLinkedList();
        } else if (fieldTypeInParent.equals(ArrayList.class)) {
            return Lists.newArrayList();
        }

        throw new RuntimeException("unsupported Collection field type: " + fieldTypeInParent.getCanonicalName());
    }

    @SuppressWarnings("unchecked")
    private Class<?> getTypeOfField(Object parent, Class<?> fieldTypeInParent, String fieldNameInParent, boolean fieldExpectedToBeList) {
        if (fieldExpectedToBeList && !Collection.class.isAssignableFrom(fieldTypeInParent)) {
            throw new RuntimeException("field '" + fieldNameInParent + "' must be a subtype of " + Collection.class.getCanonicalName());
        }

        // direct field
        if (!fieldExpectedToBeList) {
            return fieldTypeInParent;
        }

        // List
        try {
            Field stringListField = parent.getClass().getDeclaredField(fieldNameInParent);
            ParameterizedType listParamType = (ParameterizedType) stringListField.getGenericType();
            return (Class<?>) listParamType.getActualTypeArguments()[0];
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

}
