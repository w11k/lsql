package com.w11k.lsql.tests.dialects;

import com.beust.jcommander.internal.Lists;
import org.testng.annotations.Factory;

import java.util.List;

public class DialectsFactory {

    @Factory
    public Object[] createDialectTests() {
        List<Object> tests = Lists.newLinkedList();

        tests.add(new H2Test());
        tests.add(new PostgresqlTest());


        return tests.toArray();
    }

}
