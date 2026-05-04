package com.liuxuanhui.aicodehelper.exam.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Inherited
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {

    String prefix() default "";

    String suffix() default "";

    long ttl() default 300;

    int randomTime() default 5;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    boolean resetCache() default false;
}
