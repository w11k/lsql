package com.w11k.lsql.cli_tests.tests;

import com.w11k.lsql.cli.tests.StmtsWithCustomConverter;
import com.w11k.lsql.cli.tests.TestCliConfig;
import com.w11k.lsql.cli.tests.schema_public.Custom_Converter_Row;
import com.w11k.lsql.cli.tests.schema_public.Custom_Converter_Table;
import com.w11k.lsql.cli.tests.stmtswithcustomconverter.Load;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class TestCliCustomConverterTest extends AbstractTestCliTest {

    @Test
    public void statementDelete() {
        Custom_Converter_Table cct = new Custom_Converter_Table(this.lSql);
        cct.insert(new Custom_Converter_Row()
                .withField(1));

        StmtsWithCustomConverter stmt = new StmtsWithCustomConverter(this.lSql);
        Load loaded = stmt.load()
                .withField(new TestCliConfig.CustomType(1))
                .first()
                .get();

        Assert.assertEquals(loaded.field, new Integer(1));
    }


}
