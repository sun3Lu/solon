package feature.controller;

import feature.controller.service.*;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XContext;
import org.noear.solon.extend.uapi.Result;
import org.noear.solon.extend.uapi.UapiCode;
import org.noear.solon.extend.uapi.UapiGateway;

@XController
@XMapping("/api/*")
public class ApiGateway extends UapiGateway {
    @Override
    public void register() {
        Aop.beanSubscribe("@api", (bw) -> {
            add(bw);
        });
    }

    @Override
    public void render(Object obj, XContext c) throws Throwable {
        if (obj instanceof UapiCode) {
            c.render(Result.failure((UapiCode) obj));
        } else if (obj instanceof Throwable) {
            c.render(Result.failure(new UapiCode((Throwable) obj)));
        } else {
            c.render(Result.succeed(obj));
        }
    }
}
