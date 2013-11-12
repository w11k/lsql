package com.w11k.lsql.guice;

import com.google.inject.Inject;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.w11k.lsql.LSql;
import com.w11k.lsql.sqlfile.LSqlFile;

public class LSqlDaoListener implements TypeListener {

    @Inject
    private LSql lSql;

    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        if (LSqlDao.class.isAssignableFrom(type.getRawType())) {
            encounter.register(new MembersInjector<I>() {

                @Override
                public void injectMembers(I instance) {
                    LSqlDao dao = (LSqlDao) instance;
                    dao.setlSql(lSql);
                    LSqlFile lSqlFile = lSql.readSqlFile(instance.getClass());
                    dao.setlSqlFile(lSqlFile);
                }
            });
        }
    }

}
