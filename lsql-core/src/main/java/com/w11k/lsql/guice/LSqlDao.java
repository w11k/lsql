package com.w11k.lsql.guice;

import com.w11k.lsql.LSql;
import com.w11k.lsql.Query;
import com.w11k.lsql.sqlfile.LSqlFile;

import java.util.Map;

public class LSqlDao {

    private LSql lSql;

    private LSqlFile lSqlFile;

    private ThreadLocal<String> methodNameThreadLocal = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            throw new IllegalStateException(
                    "Thread's method name is undefined. Make sure that a LSqlDaoProvider was " +
                            "used to create the Dao and that the query method is annotated with QueryMethod.");
        }
    };

    public ThreadLocal<String> getMethodNameThreadLocal() {
        return methodNameThreadLocal;
    }

    public LSql getlSql() {
        return lSql;
    }

    public void setlSql(LSql lSql) {
        this.lSql = lSql;
    }

    public LSqlFile getlSqlFile() {
        return lSqlFile;
    }

    public void setlSqlFile(LSqlFile lSqlFile) {
        this.lSqlFile = lSqlFile;
    }

    public Query methodQuery() {
        return getlSqlFile().query(getCurrentMethodName());
    }

    public Query methodQuery(Object... keyVals) {
        return getlSqlFile().query(getCurrentMethodName(), keyVals);
    }

    public Query methodQuery(Map<String, Object> queryParameters) {
        return getlSqlFile().query(getCurrentMethodName(), queryParameters);
    }

    private String getCurrentMethodName() {
        return methodNameThreadLocal.get();
    }

}
