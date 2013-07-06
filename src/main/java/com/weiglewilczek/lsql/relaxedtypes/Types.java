package com.weiglewilczek.lsql.relaxedtypes;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.List;

public class Types {

    private Converter converter;

    public Types() {
        converter = new Converter();
    }

    public Types(Converter converter) {
        this.converter = converter;
    }

    public <A> A convert(Class<A> targetType, Object value) {
        return converter.convert(value.getClass(), targetType, value).get();
    }

    public int convertToInt(Object value) {
        return convert(Integer.class, value);
    }

    public String convertToString(Object value) {
        return convert(String.class, value);
    }

    public List<?> asList(Object value) {
        return convert(List.class, value);
    }

    public <A> List<? extends A> asListOf(final Class<A> targetType, final Object list) {
        List<?> objects = asList(list);
        return Lists.transform(objects, new Function<Object, A>() {
            @Nullable
            @Override
            public A apply(@Nullable Object input) {
                return convert(targetType, list);
            }
        });
    }

    // TODO
    /*
    @SuppressWarnings("unchecked")
    public MapNode<Object, Object> asMap() {

        return null;
    }
    */



}
