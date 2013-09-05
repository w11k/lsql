package com.w11k.lsql.guice;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.w11k.lsql.LSql;
import com.w11k.lsql.sqlfile.SqlFile;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

import java.lang.reflect.Method;

public class LSqlDaoProvider<T extends LSqlDao> implements Provider<T> {

    @Inject
    private LSql lSql;

    private Class<T> targetClass;

    public static <A extends LSqlDao> void bind(Binder binder, Class<A> dao) {
        binder.bind(dao).toProvider(new LSqlDaoProvider<A>(dao)).asEagerSingleton();
    }

    public LSqlDaoProvider(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public T get() {
        ProxyFactory f = new ProxyFactory();
        f.setSuperclass(targetClass);
        f.setFilter(new MethodFilter() {
            public boolean isHandled(Method m) {
                return m.getAnnotation(QueryMethod.class) != null;
            }
        });
        Class c = f.createClass();
        MethodHandler mi = new
                MethodHandler() {
                    public Object invoke(Object self, Method m, Method proceed,
                                         Object[] args) throws Throwable {
                        T target = targetClass.cast(self);
                        target.getMethodNameThreadLocal().set(m.getName());
                        return proceed.invoke(self, args);
                    }
                };
        T dao = null;
        try {
            dao = targetClass.cast(c.newInstance());
            dao.setlSql(lSql);
            SqlFile sqlFile = lSql.sqlFile(targetClass);
            dao.setSqlFile(sqlFile);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        ((Proxy) dao).setHandler(mi);
        return dao;
    }
}
