package com.springboot.admin.annotation;

import java.lang.annotation.*;

/**
 * 标记需要记录日志的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Loggable {

    /**
     * 是否打印入参
     */
    boolean logRequest() default true;

    /**
     * 是否打印返回值
     */
    boolean logResponse() default true;

    /**
     * 是否保存审计日志（如入库 / MQ）
     */
    boolean saveLog() default false;

    /**
     * 是否使用安全序列化（防止循环引用 / 超大对象）
     */
    boolean safeSerialize() default true;

    /**
     * 慢方法阈值（毫秒）
     * <=0 表示使用全局默认
     */
    long slowThresholdMs() default -1;

    /** 日志级别 */
    Level level() default Level.INFO;

    enum Level {
        DEBUG, INFO
    }
}

