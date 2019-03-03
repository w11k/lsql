package com.w11k.lsql.cli_tests.tests;

import com.w11k.lsql.cli.tests.schema_public.Person2_Row;
import com.w11k.lsql.cli.tests.schema_public.Person2_Table;
import com.w11k.lsql.cli.tests.subdir.subsubdir.StmtsCamelCase2;
import com.w11k.lsql.cli.tests.subdir.subsubdir.stmtscamelcase2.LoadPersonsByAgeAndFirstName;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

public final class StmtCaseConversionsTest extends AbstractTestCliTest {

    @Test
    public void statementSelect() {
        Person2_Table person2Table = new Person2_Table(lSql);
        person2Table.insert(new Person2_Row()
                .withId(1)
                .withFirstName("a")
                .withAge(50));

        StmtsCamelCase2 statement = new StmtsCamelCase2(lSql);
        List<LoadPersonsByAgeAndFirstName> list = statement.loadPersonsByAgeAndFirstName()
                .withFirstName("a")
                .withAge(50)
                .toList();

        assertEquals(list.size(), 1);

        LoadPersonsByAgeAndFirstName row = list.get(0);
        assertEquals(row.getId(), new Integer(1));
        assertEquals(row.id, new Integer(1));
        assertEquals(row.getFirstName(), "a");
        assertEquals(row.firstName, "a");
        assertEquals(row.getAge(), new Integer(50));
    }

}
