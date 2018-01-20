package setup;

import com.w11k.lsql.LSql;

public final class Db {

    public static void createTables(LSql lSql) {
        lSql.executeRawSql("create table person (id integer primary key, first_name text)");
    }

}
