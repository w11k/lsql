package com.weiglewilczek.lsql;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TypesConverter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    // from type -> to type -> Function(from -> to)
    private final Map<Class<?>, Map<Class<?>, Function<Object, Object>>> converters = Maps.newHashMap();

    public TypesConverter() {
        addDefaultConverters();
    }

    public void addConverter(Class<?> fromType, Class<?> toType, Function<Object, Object> converter) {
        if (!converters.containsKey(fromType)) {
            Map<Class<?>, Function<Object, Object>> map = Maps.newHashMap();
            converters.put(fromType, map);
        }
        if (converters.get(fromType).containsKey(toType)) {
            throw new IllegalArgumentException("A converter for " + fromType + " -> " + toType + " is already registered.");
        }
        converters.get(fromType).put(toType, converter);
    }

    public <B> Optional<B> convert(Class<B> toType, Object value) {
        return convert(value.getClass(), toType, value);
    }

    public <A, B> Optional<B> convert(Class <A> fromType, Class<B> toType, Object value) {
        logger.info("Converting value {} from type {} to type {}.", value, fromType.getName(), toType.getName());

        if (fromType.equals(toType)) {
            // Nothing to do
            return Optional.of(toType.cast(value));
        }

        Map<Class<?>, Function<Object, Object>> toConverters = converters.get(fromType);
        if (toConverters == null) {
            // No direct converters found
            if (!fromType.getClass().equals(Object.class)) {
                // check if we can find a converter for a superclass or interface of fromType
                List<Class<?>> superTypeAndInterfaces = Lists.newLinkedList();

                // get all supertypes and interfaces ...
                if (!fromType.isInterface()) {
                    superTypeAndInterfaces.add(fromType.getSuperclass());
                }
                superTypeAndInterfaces.addAll(Arrays.asList(fromType.getInterfaces()));

                // ... and try to convert with them
                for (Class<?> superTypeOrInterface : superTypeAndInterfaces) {
                    Optional<B> withSuper = convert(superTypeOrInterface, toType, value);
                    if (withSuper.isPresent()) {
                        return withSuper;
                    }
                }
                // no converter for a superclass or interface found
                return Optional.absent();
            } else {
                return Optional.absent();
            }
        }

        Function<Object, Object> converter = toConverters.get(toType);
        if (converter == null) {
            // We have a fromType match but couldn't find a toType match.
            // Check if a converter for a subtype of toType was registered.
            for (Class<?> aClass : toConverters.keySet()) {
                if (toType.isAssignableFrom(aClass)) {
                    converter = toConverters.get(aClass);
                    break;
                }
            }
        }

        if (converter == null) {
            if (fromType.equals(Object.class)) {
                return Optional.absent();
            } else {
                // We had a fromType match but couldn't find a converter for a subtype of toType.
                // Start again with the superclass of fromType.
                return convert(fromType.getSuperclass(), toType, value);
            }
        }

        return Optional.of(toType.cast(converter.apply(value)));
    }

    private void addDefaultConverters() {
        addConverter(Number.class, Integer.class, new Function<Object, Object>() {
            public Object apply(Object input) {
                return ((Number) input).intValue();
            }
        });
        addConverter(Number.class, Float.class, new Function<Object, Object>() {
            public Object apply(Object input) {
                return ((Number) input).floatValue();
            }
        });
        addConverter(Number.class, Double.class, new Function<Object, Object>() {
            public Object apply(Object input) {
                return ((Number) input).doubleValue();
            }
        });
        addConverter(String.class, Integer.class, new Function<Object, Object>() {
            public Object apply(Object input) {
                return Integer.parseInt((String) input);
            }
        });
        addConverter(String.class, Long.class, new Function<Object, Object>() {
            public Object apply(Object input) {
                return Long.parseLong((String) input);
            }
        });
        addConverter(String.class, Float.class, new Function<Object, Object>() {
            public Object apply(Object input) {
                return Float.parseFloat((String) input);
            }
        });
        addConverter(String.class, Double.class, new Function<Object, Object>() {
            public Object apply(Object input) {
                return Double.parseDouble((String) input);
            }
        });
        addConverter(Object.class, String.class, new Function<Object, Object>() {
            public Object apply(Object input) {
                return String.valueOf(input);
            }
        });
    }

}
