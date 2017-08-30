package com.w11k.lsql.cli_tests.tests.test01;

import com.w11k.lsql.cli.tests.test01.schema_public.Person1;
import com.w11k.lsql.cli.tests.test01.schema_public.Person2;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public final class CliGenTest {

    @Test
    public void gen() {
        Person1 p1 = new Person1();
        p1 = p1
                .withId(1)
                .withFirstName("Max");

        assertEquals(p1.getId(), new Integer(1));
        assertEquals(p1.getFirstName(), "Max");

        Person2 p2 = new Person2()
                .withId(2)
                .withFirstName("Max2")
                .withAge(50);

        p2 = p1.assignNew(p2);

    }


}
