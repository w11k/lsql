package com.w11k.lsql;

import com.google.common.collect.Maps;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.dialects.GenericDialect;

import java.util.Map;

public class Config {

    private Map<String, Map<String, Converter>> converters = Maps.newHashMap();

    private GenericDialect dialect = new GenericDialect();

    private String generatedPackageName = null;

    public Config() {
    }

    private boolean useColumnTypeForConverterLookupInQueries = false;

    public boolean isUseColumnTypeForConverterLookupInQueries() {
        return useColumnTypeForConverterLookupInQueries;
    }

    public void setUseColumnTypeForConverterLookupInQueries(boolean useColumnTypeForConverterLookupInQueries) {
        this.useColumnTypeForConverterLookupInQueries = useColumnTypeForConverterLookupInQueries;
    }

    public String getGeneratedPackageName() {
        return generatedPackageName;
    }

    public void setGeneratedPackageName(String generatedPackageName) {
        this.generatedPackageName = generatedPackageName;
    }

    public GenericDialect getDialect() {
        return dialect;
    }

    public void setDialect(GenericDialect dialect) {
        this.dialect = dialect;
    }

    public Map<String, Map<String, Converter>> getConverters() {
        return converters;
    }

    protected void setConverters(Map<String, Map<String, Converter>> converters) {
        this.converters = converters;
    }

    protected void setConverter(String tableName, String columnName, Class<?> classForConverterLookup) {
        this.setConverter(
                tableName,
                columnName,
                this.getDialect().getConverterRegistry().getConverterForJavaType(classForConverterLookup));
    }

    protected void setConverter(String tableName, String columnName, Converter converter) {
        if (!this.converters.containsKey(tableName)) {
            this.converters.put(tableName, Maps.<String, Converter>newHashMap());
        }

        Map<String, Converter> columnClassMap = this.converters.get(tableName);
        columnClassMap.put(columnName, converter);
    }

}
