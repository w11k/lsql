package com.w11k.lsql.guice;

import com.w11k.lsql.LSql;
import com.w11k.lsql.Query;
import com.w11k.lsql.sqlfile.SqlFile;

import java.util.Map;

public class LSqlDao {

    private LSql lSql;

    private SqlFile sqlFile;

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

    public SqlFile getSqlFile() {
        return sqlFile;
    }

    public void setSqlFile(SqlFile sqlFile) {
        this.sqlFile = sqlFile;
    }

    public Query methodQuery() {
        return getSqlFile().query(getCurrentMethodName());
    }

    public Query methodQuery(Object... keyVals) {
        return getSqlFile().query(getCurrentMethodName(), keyVals);
    }

    public Query methodQuery(Map<String, Object> queryParameters) {
        return getSqlFile().query(getCurrentMethodName(), queryParameters);
    }

    private String getCurrentMethodName() {
        return methodNameThreadLocal.get();
    }

}
