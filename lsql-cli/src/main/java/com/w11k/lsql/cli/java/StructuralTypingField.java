package com.w11k.lsql.cli.java;

import com.google.common.base.CaseFormat;

import static com.w11k.lsql.cli.CodeGenUtils.createSaveNameForClass;
import static com.w11k.lsql.cli.CodeGenUtils.firstCharUpperCase;

public final class StructuralTypingField {

    private final String interfaceName;

    private final String uppercaseName;

    private final Class<?> fieldType;

    public StructuralTypingField(String name, Class<?> fieldType) {
        this.uppercaseName = firstCharUpperCase(name);
        this.fieldType = fieldType;

        String canonicalNameWithUnderscores = fieldType.getCanonicalName().replace('.', '_').toLowerCase();
        String typeName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, canonicalNameWithUnderscores);

        if (typeName.startsWith("JavaLang")) {
            typeName = typeName.substring("JavaLang".length());
        }

        this.interfaceName = createSaveNameForClass(name + typeName);
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getUppercaseName() {
        return uppercaseName;
    }

    public Class<?> getFieldType() {
        return fieldType;
    }

    public String getGetterMethodName() {
        if (fieldType.isAssignableFrom(Boolean.class)) {
            return "is" + uppercaseName;
        } else {
            return "get" + uppercaseName;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StructuralTypingField that = (StructuralTypingField) o;
        return interfaceName.equals(that.interfaceName);
    }

    @Override
    public int hashCode() {
        return interfaceName.hashCode() * 31;
    }

    @Override
    public String toString() {
        return "StructuralTypingField{" +
                "interfaceName='" + interfaceName + '\'' +
                '}';
    }
}
