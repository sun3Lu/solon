package org.noear.solon.extend.properties.yaml;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.XPlugin;
import org.noear.solon.core.XPropertiesLoader;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        if (XPropertiesLoader.global instanceof PropertiesLoader) {
            return;
        } else {
            //切换配置加载器
            XPropertiesLoader.global = PropertiesLoader.g;

            //尝试.yml的配置加载
            app.prop().load(XUtil.getResource("application.yml"));
        }
    }
}
