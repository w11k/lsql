package com.w11k.lsql.tests;

import com.google.common.base.Optional;
import com.w11k.lsql.Column;
import com.w11k.lsql.Table;
import com.w11k.lsql.validation.AbstractValidationError;
import com.w11k.lsql.validation.KeyError;
import com.w11k.lsql.validation.StringTooLongError;
import com.w11k.lsql.validation.TypeError;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ValidationTest extends AbstractLSqlTest {

    @Test
    public void wrongColumnName() {
        createTable("CREATE TABLE table1 (age INT)");
        Table<?> table1 = lSql.table("table1");
        Optional<? extends AbstractValidationError> validation = table1.validate("wrong", 1);
        assertTrue(validation.isPresent());
        assertEquals(validation.get().getClass(), KeyError.class);
    }

    @Test
    public void wrongColumnValueType() {
        createTable("CREATE TABLE table1 (age INT)");
        Table<?> table1 = lSql.table("table1");
        Column age = table1.column("age");
        Optional<? extends AbstractValidationError> validation = age.validateValue("1");
        assertTrue(validation.isPresent());
        assertEquals(validation.get().getClass(), TypeError.class);
    }

    @Test
    public void stringValueIsTooLong() {
        createTable("CREATE TABLE table1 (name VARCHAR(5))");
        Table<?> table1 = lSql.table("table1");
        Column age = table1.column("name");
        Optional<? extends AbstractValidationError> validation = age.validateValue("12345");
        assertFalse(validation.isPresent());
        validation = age.validateValue("123456");
        assertTrue(validation.isPresent());
        assertEquals(validation.get().getClass(), StringTooLongError.class);
    }

}
