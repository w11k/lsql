package com.w11k.lsql.tests;

public class ValidationTest extends AbstractLSqlTest {

//    @Test
//    public void wrongColumnName() {
//        createTable("CREATE TABLE table1 (age INT)");
//        Table table1 = lSql.table("table1");
//        Optional<? extends AbstractValidationError> validation = table1.validate("wrong", 1);
//        assertTrue(validation.isPresent());
//        assertEquals(validation.get().getClass(), KeyError.class);
//    }
//
//    @Test
//    public void wrongColumnValueType() {
//        createTable("CREATE TABLE table1 (age INT)");
//        Table table1 = lSql.table("table1");
//        Column age = table1.column("age");
//        Optional<? extends AbstractValidationError> validation = age.validateValue("1");
//        assertTrue(validation.isPresent());
//        assertEquals(validation.get().getClass(), TypeError.class);
//    }
//
//    @Test
//    public void stringValueIsTooLong() {
//        createTable("CREATE TABLE table1 (name VARCHAR(5))");
//        Table table1 = lSql.table("table1");
//        Column age = table1.column("name");
//        Optional<? extends AbstractValidationError> validation = age.validateValue("12345");
//        assertFalse(validation.isPresent());
//        validation = age.validateValue("123456");
//        assertTrue(validation.isPresent());
//        assertEquals(validation.get().getClass(), StringTooLongError.class);
//    }

}
