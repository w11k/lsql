package com.w11k.lsql.guice;

import com.google.inject.Inject;
import com.w11k.lsql.LSql;
import com.w11k.lsql.Table;
import com.w11k.lsql.query.PojoQuery;
import com.w11k.lsql.query.RowQuery;
import com.w11k.lsql.sqlfile.LSqlFile;
import com.w11k.lsql.statement.AbstractSqlStatement;

public class LSqlDao {

    @Inject
    private LSql lSql;

    private LSqlFile lSqlFile;

    public LSql getlSql() {
        return lSql;
    }

    public void setlSql(LSql lSql) {
        this.lSql = lSql;
    }

    public synchronized LSqlFile getlSqlFile() {
        if (lSqlFile == null) {
            this.lSqlFile = lSql.readSqlFile(getClass());
        }
        return lSqlFile;
    }

    public void setlSqlFile(LSqlFile lSqlFile) {
        this.lSqlFile = lSqlFile;
    }

    public AbstractSqlStatement<RowQuery> statement(String name) {
        return getlSqlFile().statement(name);
    }

    public <T> AbstractSqlStatement<PojoQuery<T>> statement(String name, Class<T> pojoClass) {
        return getlSqlFile().statement(name, pojoClass);
    }

    private Table table(String tableName) {
        return getlSql().table(tableName);
    }

}
