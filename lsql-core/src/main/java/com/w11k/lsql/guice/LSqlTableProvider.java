package com.w11k.lsql.guice;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Names;
import com.w11k.lsql.LSql;
import com.w11k.lsql.Table;

public class LSqlTableProvider implements Provider<Table> {

    private final String tableName;

    @Inject
    private LSql lSql;

    public static void bind(Binder binder, String tableName) {
        binder.bind(Table.class).annotatedWith(Names.named(tableName))
                .toProvider(new LSqlTableProvider(tableName)).asEagerSingleton();
    }

    public LSqlTableProvider(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public Table get() {
        return lSql.table(tableName);
    }

}
