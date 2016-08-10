package com.w11k.lsql;

public class Config {

    boolean useColumnTypeForConverterLookupInQueries = false;

    public boolean isUseColumnTypeForConverterLookupInQueries() {
        return useColumnTypeForConverterLookupInQueries;
    }

    public void setUseColumnTypeForConverterLookupInQueries(boolean useColumnTypeForConverterLookupInQueries) {
        this.useColumnTypeForConverterLookupInQueries = useColumnTypeForConverterLookupInQueries;
    }

}
