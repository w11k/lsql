package com.w11k.lsql.cli;

import com.google.common.base.CaseFormat;

public final class StructuralTypingField {

    private final String name;

    private final Class<?> fieldClass;

    private final String typeName;

    public StructuralTypingField(String name, Class<?> fieldClass) {
        this.name = name.substring(0, 1).toUpperCase() +  name.substring(1);
        this.fieldClass = fieldClass;

        String canonicalNameWithUnderscores = fieldClass.getCanonicalName().replace('.', '_').toLowerCase();
        this.typeName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, canonicalNameWithUnderscores);
    }

    public String getName() {
        return name;
    }

    public Class<?> getFieldClass() {
        return fieldClass;
    }

    public String getInterfaceName() {
        return this.name + this.typeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StructuralTypingField that = (StructuralTypingField) o;
        return name.equals(that.name) && typeName.equals(that.typeName);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + typeName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "StructuralTypingField{" +
                "name='" + name + '\'' +
                ", typeName='" + typeName + '\'' +
                '}';
    }
}
