package com.w11k.lsql.guice;

import com.google.inject.Inject;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.w11k.lsql.LSql;
import com.w11k.lsql.Table;
import com.w11k.lsql.sqlfile.SqlFile;

import java.lang.reflect.Field;

public class LSqlMemberListener implements TypeListener {

    @Inject
    private LSql lSql;

    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        for (final Field field : type.getRawType().getDeclaredFields()) {
            checkTable(encounter, field);
            checkSqlFile(encounter, field);
        }
    }

    private <I> void checkTable(final TypeEncounter<I> encounter, final Field field) {
        if (field.getType().isAssignableFrom(Table.class) &&
                field.isAnnotationPresent(InjectTable.class)) {

            InjectTable anno = field.getAnnotation(InjectTable.class);
            final String tableName = anno.value();

            encounter.register(new MembersInjector<I>() {

                @Override
                public void injectMembers(I instance) {
                    setFieldValue(instance, field, lSql.table(tableName));
                }
            });
        }
    }

    private <I> void checkSqlFile(final TypeEncounter<I> encounter, final Field field) {
        if (field.getType().isAssignableFrom(SqlFile.class) &&
                field.isAnnotationPresent(InjectSqlFile.class)) {

            encounter.register(new MembersInjector<I>() {

                @Override
                public void injectMembers(I instance) {
                    setFieldValue(instance, field, lSql.sqlFile(instance.getClass()));
                }
            });
        }
    }

    private void setFieldValue(Object instance, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
