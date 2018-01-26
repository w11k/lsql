package com.w11k.lsql.cli.tests;

import org.testng.annotations.Test;

import static com.w11k.lsql.cli.CodeGenUtils.getJavaCodeName;
import static org.testng.Assert.assertEquals;

public final class CodeGenUtilsTest {

    @Test
    public void getJavaCodeNameTests() {
        assertEquals(getJavaCodeName(
                "a",
                false,
                false),
                "a");
    }

}
