package com.w11k.lsql;

import com.google.common.base.Optional;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class RowPojo extends Row {

    public RowPojo() {
    }

    @Override
    public Object put(String key, Object value) {
        Optional<Method> setter = getMethod(toPropertyName("set", key));
        if (setter.isPresent()) {
            return invoke(setter.get(), value);
        } else {
            return super.put(key, value);
        }
    }

    @Override
    public Object get(@Nullable Object key) {
        Optional<Method> getter = getMethod(toPropertyName("get", key));
        if (getter.isPresent()) {
            return invoke(getter.get());
        } else {
            return super.get(key);
        }
    }

    @Override
    public <A> A getAs(Class<A> type, String key) {
        Optional<Method> getter = getMethod(toPropertyName("get", key));
        if (getter.isPresent()) {
            return type.cast(invoke(getter.get()));
        } else {
            return super.getAs(type, key);
        }
    }

    @Override
    public Set<String> keySet() {
        updateDelegateWithProperties();
        return super.keySet();
    }

    @Override
    public int size() {
        updateDelegateWithProperties();
        return super.size();
    }

    @Override
    public boolean isEmpty() {
        updateDelegateWithProperties();
        return super.isEmpty();
    }

    @Override
    public boolean containsKey(@Nullable Object key) {
        updateDelegateWithProperties();
        return super.containsKey(key);
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        updateDelegateWithProperties();
        return super.containsValue(value);
    }

    @Override
    public Object remove(Object object) {
        put(object.toString(), null);
        return super.remove(object);
    }

    @Override
    public Collection<Object> values() {
        updateDelegateWithProperties();
        return super.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        updateDelegateWithProperties();
        return super.entrySet();
    }

    @Override
    public int hashCode() {
        updateDelegateWithProperties();
        return super.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object object) {
        updateDelegateWithProperties();
        return super.equals(object);
    }

    @Override
    public String toString() {
        updateDelegateWithProperties();
        return super.toString();
    }

    @Override
    public void setDelegate(Map<String, Object> data) {
        super.setDelegate(data);

        // Push values into properties
        for (String key : data.keySet()) {
            put(key, data.get(key));
        }
    }

    private String toPropertyName(String prefix, Object name) {
        String propName = name.toString();
        return prefix + propName.substring(0, 1).toUpperCase() + propName.substring(1);
    }

    private Optional<Method> getMethod(String methodName) {
        Method[] methods = getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return of(method);
            }
        }
        return absent();
    }

    private Object invoke(Method method, Object... values) {
        try {
            return method.invoke(this, values);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateDelegateWithProperties() {
        for (Method method : getClass().getMethods()) {
            if (method.getName().startsWith("get")) {
                String property = method.getName().substring(3);
                Optional<Method> setter = getMethod("set" + property);
                if (setter.isPresent()) {
                    // Getter & Setter found
                    String mapPropertyName = property.substring(0, 1).toLowerCase() + property.substring(1);
                    delegate().put(mapPropertyName, invoke(method));
                }
            }
        }
    }

}
