package com.w11k.lsql.tests;

import com.w11k.lsql.SqlFileReader;
import org.testng.annotations.Test;

public class SqlFileReaderTest {

    @Test
    public void readSqlFile() {
        SqlFileReader.relativeToClass(getClass(), "file1.sql");
    }

}
