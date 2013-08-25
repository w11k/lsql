package com.w11k.lsql.tests;

import com.beust.jcommander.internal.Lists;
import org.apache.commons.dbcp.BasicDataSource;
import org.postgresql.Driver;
import org.testng.annotations.Factory;

import java.util.List;

public class TestsFactory {

    private List<Class<? extends AbstractLSqlTest>> testClasses = Lists.newLinkedList();

    public static BasicDataSource createPostgresDataSource() {
        BasicDataSource postgresDataSource = new BasicDataSource();
        postgresDataSource.setDriverClassName(Driver.class.getName());
        postgresDataSource.setUrl("jdbc:postgresql://localhost/lsqltests?user=lsqltestsuser&password=lsqltestspass");
        postgresDataSource.setDefaultAutoCommit(false);
        return postgresDataSource;
    }

    public static BasicDataSource createH2DataSource() {
        BasicDataSource h2DataSource = new BasicDataSource();
        h2DataSource.setDriverClassName(org.h2.Driver.class.getName());
        h2DataSource.setUrl("jdbc:h2:mem:testdb_;MODE=PostgreSQL");
        return h2DataSource;
    }

    public TestsFactory() {
        /*
        testClasses.add(LSqlTest.class);
        testClasses.add(ConverterTest.class);
        testClasses.add(ConverterTypeTest.class);
        testClasses.add(QueryTest.class);
        testClasses.add(RowTest.class);
        testClasses.add(SqlFileReaderTest.class);
        */
        testClasses.add(TableTest.class);
    }

    @Factory
    public Object[] createInstances() throws Exception {
        List<Object> tests = Lists.newLinkedList();

        // H2
        /*
        BasicDataSource h2DataSource = createH2DataSource();
        for (Class<? extends AbstractLSqlTest> testClass : testClasses) {
            AbstractLSqlTest test = testClass.getConstructor().newInstance();
            test.setDataSource(h2DataSource);
            tests.add(test);
        }
        */

        // PostgreSQL
        BasicDataSource postgresDataSource = createPostgresDataSource();
        for (Class<? extends AbstractLSqlTest> testClass : testClasses) {
            AbstractLSqlTest test = testClass.getConstructor().newInstance();
            test.setDataSource(postgresDataSource);
            tests.add(test);
        }

        return tests.toArray();
    }

}
