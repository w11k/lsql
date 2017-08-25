package com.w11k.lsql;

public class Config {

    private boolean useColumnTypeForConverterLookupInQueries = false;

    public boolean isUseColumnTypeForConverterLookupInQueries() {
        return useColumnTypeForConverterLookupInQueries;
    }

    public void setUseColumnTypeForConverterLookupInQueries(boolean useColumnTypeForConverterLookupInQueries) {
        this.useColumnTypeForConverterLookupInQueries = useColumnTypeForConverterLookupInQueries;
    }

}
