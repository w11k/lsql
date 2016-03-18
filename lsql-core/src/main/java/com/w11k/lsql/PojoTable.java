package com.w11k.lsql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.base.Optional;

public class PojoTable<T> {

    private final LSql lSql;

    private final String tableName;

    private final Class<T> pojoClass;

    private final Table table;

    private ObjectMapper mapper;

    public PojoTable(LSql lSql, String tableName, Class<T> pojoClass, Function<String, String> propertyNameConverter) {
        this.lSql = lSql;
        this.tableName = tableName;
        this.pojoClass = pojoClass;
        this.table = new Table(lSql, tableName);

        if (propertyNameConverter == null) {
            propertyNameConverter = new Function<String, String>() {
                @Override
                public String apply(String input) {
                    return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, input);
                }
            };
        }
        createObjectMapper(propertyNameConverter);
    }

    public T insert(T pojo) {
        Row row = mapper.convertValue(pojo, Row.class);
        Optional<Object> id = table.insert(row);
        if (!id.isPresent()) {
            return null;
        }
        return load(id.get()).get();
    }

    public Optional<T> load(Object id) {
        Optional<LinkedRow> row = table.load(id);
        if (!row.isPresent()) {
            return Optional.absent();
        }

        T t = mapper.convertValue(row.get(), pojoClass);


        return Optional.of(t);
    }

    private void createObjectMapper(final Function<String, String> propertyNameConverter) {
        this.mapper = LSql.OBJECT_MAPPER.copy();
        this.mapper.setPropertyNamingStrategy(new PropertyNamingStrategy() {
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
    }

}
