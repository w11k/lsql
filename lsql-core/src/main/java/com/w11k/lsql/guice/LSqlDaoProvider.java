package com.w11k.lsql.guice;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.binder.ScopedBindingBuilder;
import com.w11k.lsql.LSql;
import com.w11k.lsql.Table;
import com.w11k.lsql.sqlfile.LSqlFile;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class LSqlDaoProvider<T extends LSqlDao> implements Provider<T> {

    private final Class<T> targetClass;

    @Inject
    private Injector injector;

    @Inject
    private LSql lSql;

    public static <A extends LSqlDao> ScopedBindingBuilder bind(Binder binder, Class<A> dao) {
        return binder.bind(dao).toProvider(new LSqlDaoProvider<A>(dao));
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
                        try {
                            return proceed.invoke(self, args);
                        } finally {
                            target.getMethodNameThreadLocal().remove();
                        }
                    }
                };
        T dao = null;
        try {
            dao = targetClass.cast(c.newInstance());
            dao.setlSql(lSql);
            LSqlFile lSqlFile = lSql.readSqlFile(targetClass);
            dao.setlSqlFile(lSqlFile);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        ((Proxy) dao).setHandler(mi);
        injector.injectMembers(dao);
        checkTableInjection(targetClass, dao);
        return dao;
    }

    private void checkTableInjection(Class<T> targetClass, T dao) {
        // TODO should be getFields instead of getDeclaredFields but that return an empty array
        Field[] fields = targetClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().isAssignableFrom(Table.class) &&
                    field.isAnnotationPresent(InjectTable.class)) {
                try {
                    field.setAccessible(true);
                    String tableName = field.getAnnotation(InjectTable.class).value();
                    field.set(dao, lSql.table(tableName));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
