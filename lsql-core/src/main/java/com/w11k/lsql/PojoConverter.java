package com.w11k.lsql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.google.common.base.CaseFormat;
import com.google.common.base.Function;

public class PojoConverter {

    private ObjectMapper mapper;

    public PojoConverter(LSql lSql) {
        ObjectMapper copy = lSql.getPlainObjectMapper().copy();

        final Function<String, String> propertyNameConverter = new Function<String, String>() {
            @Override
            public String apply(String input) {
                return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, input);
            }
        };
        copy.setPropertyNamingStrategy(new PropertyNamingStrategy() {
            @Override
            public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
                return propertyNameConverter.apply(defaultName);
            }

            @Override
            public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
                return propertyNameConverter.apply(defaultName);
            }

            @Override
            public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
                return propertyNameConverter.apply(defaultName);
            }

            @Override
            public String nameForConstructorParameter(MapperConfig<?> config, AnnotatedParameter ctorParam, String defaultName) {
                return propertyNameConverter.apply(defaultName);
            }
        });

        this.mapper = copy;
    }

    public <T> T convert(Object fromValue, Class<T> toValueType) {
        return mapper.convertValue(fromValue, toValueType);
    }
}
