package org.noear.solon.extend.cron4j;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Job {
    String cron4x();
    boolean enable() default true;
}

