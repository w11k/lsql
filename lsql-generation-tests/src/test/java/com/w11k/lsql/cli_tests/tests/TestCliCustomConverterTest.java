package com.w11k.lsql.cli_tests.tests;

import com.w11k.lsql.cli.StmtsWithCustomConverter;
import com.w11k.lsql.cli.TestCliConfig;
import com.w11k.lsql.cli.schema_public.Custom_Converter_Row;
import com.w11k.lsql.cli.schema_public.Custom_Converter_Table;
import com.w11k.lsql.cli.schema_public.Person1_Row;
import com.w11k.lsql.cli.schema_public.Person1_Table;
import com.w11k.lsql.cli.stmtswithcustomconverter.Load_Row;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class TestCliCustomConverterTest extends AbstractTestCliTest {

    @Test
    public void statementDelete() {
        Custom_Converter_Table cct = new Custom_Converter_Table(this.lSql);
        cct.insert(new Custom_Converter_Row()
                .withField(1));

        StmtsWithCustomConverter stmt = new StmtsWithCustomConverter(this.lSql);
        Load_Row loaded = stmt.load()
                .withField(new TestCliConfig.CustomType(1))
                .first()
                .get();

        Assert.assertEquals(loaded.field, new Integer(1));
    }

    @Test
    public void testQueryParameter() {
        Person1_Table person1Table = new Person1_Table(this.lSql);
        person1Table.insert(new Person1_Row()
                .withId(1)
                .withFirstName("Max"));

        StmtsWithCustomConverter stmt = new StmtsWithCustomConverter(this.lSql);
//        stmt.testQueryParamter()
//                .withId("");
    }

}
