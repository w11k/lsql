package com.w11k.lsql.query;

import com.w11k.lsql.Row;

import static com.google.common.collect.Lists.newLinkedList;

public class RowEntityCreator extends EntityCreator {

    @Override
    public Object createEntity(Object parent, String fieldNameInParent, boolean isList) {
        Row row = new Row();
        if (parent == null) {
            return row;
        }

        // Store entity in parent
        Row parentRow = (Row) parent;
        Object fieldInParent = parentRow.get(fieldNameInParent);
        if (isList) {
            // 1:n
            if (fieldInParent == null) {
                parentRow.put(fieldNameInParent, newLinkedList());
            }
            parentRow.getAsListOf(Object.class, fieldNameInParent).add(row);
        } else {
            // 1:1
            assert fieldInParent == null;
            parentRow.put(fieldNameInParent, row);
        }


        return row;
    }

    @Override
    public void setValue(Object entity, String fieldName, Object value) {
        Row row = (Row) entity;
        row.put(fieldName, value);
    }
}
