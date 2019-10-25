package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 通用组件
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XBean {
    String value() default "";//as bean.name
    boolean remoting() default false; //是否开始远程服务
}
