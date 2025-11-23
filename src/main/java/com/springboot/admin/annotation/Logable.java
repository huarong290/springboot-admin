package com.springboot.admin.annotation;

import java.lang.annotation.*;

/**
 * 标记需要记录日志的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Logable {
    /**
     * 是否打印返回值
     */
    boolean logResponse() default true;

    /**
     * 是否打印入参
     */
    boolean logRequest() default true;
}
