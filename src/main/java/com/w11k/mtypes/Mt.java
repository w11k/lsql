package com.w11k.mtypes;

import com.google.common.base.Optional;

import java.util.Map;

public class Mt {

    private TypesConverter typesConverter = new TypesConverter();

    public TypesConverter getTypesConverter() {
        return typesConverter;
    }

    public void setTypesConverter(TypesConverter typesConverter) {
        this.typesConverter = typesConverter;
    }

    public <A> A convertTo(Class<A> type, Object value) {
        return typesConverter.convert(type, value).get();
    }

    public MtMap newMap() {
        return new MtMap(this, Optional.<String>absent());
    }

    public MtMap newMap(String typeName) {
        return new MtMap(this, Optional.of(typeName));
    }

    public MtMap newMap(Map<String, Object> fromMap) {
        return new MtMap(this, Optional.<String>absent(), fromMap);
    }

    public MtMap newMap(String typeName, Map<String, Object> fromMap) {
        return new MtMap(this, Optional.of(typeName), fromMap);
    }

}
