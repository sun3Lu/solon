package org.noear.solon.annotation;

import org.noear.solon.core.XHandler;
import java.lang.annotation.*;

/**
 * 触发器：前置处理（仅争对 XController 和 XAction 的拦截器）
 * */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XBefore {
    Class<? extends XHandler>[] value();
}
