package com.w11k.lsql.guice;

import com.w11k.lsql.LSql;
import com.w11k.lsql.Query;
import com.w11k.lsql.sqlfile.SqlFile;

import java.util.Map;

abstract public class AbstractLSqlDao {

    private LSql lSql;

    private SqlFile sqlFile;

    private ThreadLocal<String> methodNameThreadLocal = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            throw new IllegalStateException("Did you bind a LSqlDaoProvider for this class?");
        }
    };

    private String getCurrentMethodName() {
        return methodNameThreadLocal.get();
    }

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

}
