package com.w11k.lsql.tests.pojo;

import com.w11k.lsql.tests.AbstractLSqlTest;
import com.w11k.lsql.tests.testdata.*;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

public class PojoQueryTest extends AbstractLSqlTest {

    @Test
    public void executeRawQuery() {
        PersonTestData.init(this.lSql, true);

        List<Person> rows = this.lSql.executeRawQuery(
                PersonTestData.SELECT_ALL_ORDER_BY_ID,
                Person.class).toList();

        assertEquals(rows.size(), 2);
        assertEquals(rows.get(0).getFirstName(), "Adam");
        assertEquals(rows.get(1).getFirstName(), "Eve");
    }


    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = ".*missing field.*title.*String.*"
    )
    public void errorMessageOnMissingField() {
        PersonTestData.init(this.lSql, true);
        this.lSql.executeRawQuery(
                PersonTestData.SELECT_ALL_ORDER_BY_ID,
                PersonMissingTitle.class
        ).toList();
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = ".*title.*wrong type.*Boolean.*String.*"
    )
    public void errorMessageOnWrongFieldType() {
        PersonTestData.init(this.lSql, true);
        this.lSql.executeRawQuery(
                PersonTestData.SELECT_ALL_ORDER_BY_ID,
                PersonWrongTitleType.class
        ).toList();
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = ".*superfluous field.*oops.*"
    )
    public void errorMessageOnSuperfluousType() {
        PersonTestData.init(this.lSql, true);
        this.lSql.executeRawQuery(
                PersonTestData.SELECT_ALL_ORDER_BY_ID,
                PersonWithSuperfluousField.class
        ).toList();
    }


}
