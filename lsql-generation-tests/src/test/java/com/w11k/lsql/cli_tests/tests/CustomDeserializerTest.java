package com.w11k.lsql.cli_tests.tests;

import com.google.common.io.ByteStreams;
import com.w11k.lsql.Blob;
import com.w11k.lsql.LSql;
import com.w11k.lsql.Row;
import com.w11k.lsql.RowDeserializer;
import com.w11k.lsql.cli.schema_public.Custom_Deserializer_Row;
import com.w11k.lsql.cli.schema_public.Custom_Deserializer_Table;
import com.w11k.lsql.converter.Converter;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

public final class CustomDeserializerTest extends AbstractTestCliTest {

    @Test
    public void getBinaryStream() {
        Custom_Deserializer_Table table = new Custom_Deserializer_Table(this.lSql);
        table.insert(new Custom_Deserializer_Row().withId(1).withData(new Blob("abc".getBytes())));

        List<byte[]> result = new LinkedList<>();
        Custom_Deserializer_Row row = table.load(1, new RowDeserializer.Deserializer() {
            @Override
            public void deserializeField(LSql lSql,
                                         Row row,
                                         Converter converter,
                                         String internalSqlColumnName,
                                         ResultSet resultSet,
                                         int resultSetColumnPosition) throws Exception {

                if (internalSqlColumnName.equals(Custom_Deserializer_Row.INTERNAL_FIELD_DATA)) {
                    result.add(ByteStreams.toByteArray(resultSet.getBinaryStream(resultSetColumnPosition)));
                } else {
                    super.deserializeField(lSql, row, converter, internalSqlColumnName, resultSet, resultSetColumnPosition);
                }
            }
        }).get();

        Assert.assertEquals(row.id, Integer.valueOf(1));
        Assert.assertEquals(result.get(0), "abc".getBytes());
    }

}
