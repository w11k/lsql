package com.w11k.lsql.cli;

import com.google.common.base.CaseFormat;

public final class StructuralTypingField {

    private final String uppercaseName;

    private final Class<?> fieldClass;

    private final String typeName;

    public StructuralTypingField(String name, Class<?> fieldClass) {
        this.uppercaseName = name.substring(0, 1).toUpperCase() +  name.substring(1);
        this.fieldClass = fieldClass;

        String canonicalNameWithUnderscores = fieldClass.getCanonicalName().replace('.', '_').toLowerCase();
        String typeName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, canonicalNameWithUnderscores);

        if (typeName.startsWith("JavaLang")) {
            typeName = typeName.substring("JavaLang".length());
        }
        this.typeName = typeName;
    }

    public String getUppercaseName() {
        return uppercaseName;
    }

    public Class<?> getFieldClass() {
        return fieldClass;
    }

    public String getInterfaceName() {
        return this.uppercaseName + this.typeName;
    }

    public String getGetterMethodName() {
        if (getFieldClass().isAssignableFrom(Boolean.class)) {
            return "is" + getUppercaseName();
        } else {
            return "get" + getUppercaseName();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StructuralTypingField that = (StructuralTypingField) o;
        return uppercaseName.equals(that.uppercaseName) && typeName.equals(that.typeName);
    }

    @Override
    public int hashCode() {
        int result = uppercaseName.hashCode();
        result = 31 * result + typeName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "StructuralTypingField{" +
                "name='" + uppercaseName + '\'' +
                ", typeName='" + typeName + '\'' +
                '}';
    }
}
