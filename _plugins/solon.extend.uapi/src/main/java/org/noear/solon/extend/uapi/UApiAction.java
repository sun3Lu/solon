package org.noear.solon.extend.uapi;

import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.*;

import java.lang.reflect.Method;

/**
 * 将结果，转到ctx.attr[result]
 * */
public class UApiAction extends XAction implements UApi {
    private String _name;

    public UApiAction(BeanWrap bw, Method m, XMapping mp, String path) {
        super(bw, m, mp, path);

        if (mp == null) {
            _name = m.getName();
        } else {
            _name = mp.value();
        }
    }

    /**
     * 接口名称
     * */
    @Override
    public String name() {
        return _name;
    }

    /**
     * 取消原有的渲染；改为属性传递
     * */
    @Override
    protected void renderDo(XContext c, Object result) throws Throwable {
        if (result == null) {
            return;
        }

        //
        // 结果写入 c.attr["result"]
        //
        c.attrSet("result", result);
    }
}