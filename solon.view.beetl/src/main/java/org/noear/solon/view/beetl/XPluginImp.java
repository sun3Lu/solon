package org.noear.solon.view.beetl;

import org.beetl.core.tag.Tag;
import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {

        BeetlRender render = new BeetlRender();

        Aop.beanOnloaded(() -> {
            Aop.beanForeach((k, v) -> {
                if (k.startsWith("btl:")) {
                    if(Tag.class.isAssignableFrom(v.clz())) {
                        render.registerTag(k.split(":")[1], v.clz());
                    }else{
                        render.setSharedVariable(k.split(":")[1], v.raw());
                    }
                }
            });
        });

        app.renderSet(render);
    }
}