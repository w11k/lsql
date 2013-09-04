/*
 * Created by IntelliJ IDEA.
 * User: roman
 * Date: 31/08/13
 * Time: 13:57
 */
package com.w11k.lsql.guice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface QueryMethod {
}
