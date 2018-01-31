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

        assertEquals(getJavaCodeName(
                "a",
                false,
                true),
                "A");

        assertEquals(getJavaCodeName(
                "abc_def",
                false,
                false),
                "abcDef");

        assertEquals(getJavaCodeName(
                "abc_def",
                false,
                true),
                "AbcDef");

        assertEquals(getJavaCodeName(
                "abc_def",
                true,
                true),
                "Abc_Def");

        assertEquals(getJavaCodeName(
                "abc___def",
                false,
                false),
                "abcDef");

        assertEquals(getJavaCodeName(
                "abc___def",
                true,
                false),
                "abc___Def");

        assertEquals(getJavaCodeName(
                "idInteger",
                true,
                true),
                "Id_Integer");
    }

}
