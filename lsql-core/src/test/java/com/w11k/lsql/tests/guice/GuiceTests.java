package com.w11k.lsql.tests.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.w11k.lsql.Row;
import com.w11k.lsql.tests.AbstractLSqlTest;
import com.w11k.lsql.tests.LSqlProvider;
import org.testng.annotations.Test;

public class GuiceTests extends AbstractLSqlTest {

    @Test(dataProvider = "lSqlProvider_h2")
    public void daoProvider(LSqlProvider provider) {
        provider.init(this);

        createTable("CREATE TABLE table1 (afield int)");
        lSql.table("table1").insert(Row.fromKeyVals("afield", 1));

        Injector inj = Guice.createInjector(new TestModule(lSql));
        TestDao dao = inj.getInstance(TestDao.class);
        dao.query1();

    }

}
