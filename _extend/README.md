
### 主框架与插件：

###### 主框架

| 组件 | 说明 |
| --- | --- |
| org.noear:solon-parent | 框架版本管理 |
| org.noear:solon | 主框架 |

###### 快速集成开发包

| 组件 | 说明 |
| --- | --- |
| org.noear:solon-web | 可进行http api,mvc,rpc开发的快速集成包 |

###### 插件

| boot插件 | 说明 |
| --- | --- |
| org.noear:solon.boot.jlhttp* | boot插件,对`jlhttp`适配,提供`http`服务（不自带session state） |
| org.noear:solon.boot.jetty* | boot插件,对`jetty`适配,提供`http`服务（网友@khb提供） |
| org.noear:solon.boot.undertow* | boot插件,对`undertow`适配,提供`http`服务（网友@tyk提供） |
| org.noear:solon.boot.websocket | boot插件,对`java-websocket`适配，提供`websocket`服务 |
| org.noear:solon.extend.jetty.jsp | 扩展插件,为`jetty`添加`jsp`支持（不建议使用jsp）（网友@khb提供） |
| org.noear:solon.extend.undertow.jsp | 扩展插件,为`undertow`添加`jsp`支持（不建议使用jsp）（网友@tyk提供） |

| 静态文件支持插件 | 说明 |
| --- | --- |
| org.noear:solon.extend.staticfiles | 扩展插件,添加静态文件支持（监视 resources/static 文件夹） |

| Yaml配置支持插件 | 说明 |
| --- | --- |
| org.noear:solon.extend.properties.yaml | 扩展插件,添加yml配置文件支持 |

| Session插件 | 说明（可将boot插件的session state服务，自动换掉） |
| --- | --- |
| org.noear:solon.extend.sessionstate.local | 扩展插件,本地`session` |
| org.noear:solon.extend.sessionstate.redis | 扩展插件,分布式`session`（其于`redis`构建） |

| 序列化插件 | 说明 |
| --- | --- |
| org.noear:solon.serialization.fastjson* | 序列化插件，对 `fastjson` 适配，提供`json`视图输出 或 序列化输出 |
| org.noear:solon.serialization.snack3* | 序列化插件，对 `snack3` 适配，提供`json`视图输出 或 序列化输出 |
| org.noear:solon.serialization.hession* | 序列化插件，对 `hession` 适配，提供 `hession` 序列化输出 |
| org.noear:solon.serialization.jackson | 序列化插件，对 `jackson` 适配，提供`json`视图输出 或 序列化输出 |

| 视图插件 | 说明（可置多个视图插件） |
| --- | --- |
| org.noear:solon.view.freemarker* | 视图插件，对 `freemarker` 适配，提供`html`视图输出 |
| org.noear:solon.view.jsp | 视图插件，对 `jsp` 适配，提供`html`视图输出 |
| org.noear:solon.view.velocity | 视图插件，对 `velocity` 适配，提供`html`视图输出 |
| org.noear:solon.view.thymeleaf | 视图插件，对 `thymeleaf` 适配，提供`html`视图输出 |
| org.noear:solon.view.beetl | 视图插件，对 `beetl` 适配，提供`html`视图输出 |
| org.noear:solon.view.enjoy | 视图插件，对 `enjoy` 适配，提供`html`视图输出 |

| rpc client | 说明 |
| --- | --- |
| org.noear:solonclient | solon rpc client 与solon 的 rpc service 配对 |

| 外部框架适配 | 说明 |
| --- | --- |
| org.noear:cron4j-solon-plugin | cron4j 适配插件 |
| org.noear:dubbo-solon-plugin | dubbo 适配插件|
| org.noear:mybatis-solon-plugin | mybatis 适配插件|