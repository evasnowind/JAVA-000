
## 分库分表后的订单表设计  

订单表结构不变，表的schema参见末尾。

### 分库分表方案  

订单中包含用户id、订单id，因此可以采用用户id（member_id）进行分库、使用订单id分表。

本次创建2个库、每个库16个订单。分库时使用用户id % 2取余，订单id % 16取余即可。


## 程序说明   

### 启动准备  

准备数据库、表，目前配置文件里配置的是：
- 两个库，分别是order_0, order_1
- 每个库16张表，分别是oms_order_0, oms_order_1, ......, oms_order_15
- 订单的主键id字段采用snowflake生成

批量创建库的脚本参见[batch_create_table_script.md](src/main/resources/script/batch_create_table_script.md)， 该脚本每次可以创建一个数据库、并在库中批量创建相同前缀的表。

也可以直接导入已生成好的SQL，参见`src/main/resources/script/order_0.sql` `src/main/resources/script/order_1.sql` 

### 测试  

准备好数据库后，修改配置文件中的数据库连接地址，然后启动ShardingDbTableReadWriteApplication.java。  
目前使用spring boot写了CRUD接口，方便起见，接口都采用了GET请求方式、且很多参数都直接写死了，毕竟我们的目的是联系sharding-jdbc分库分表的使用。

已完成的接口如下：

- 插入数据
    - 示例：http://localhost:8084/api/order/add?username=bbb&memberId=52&payment=100
- 查询所有订单
    - 示例：http://localhost:8084/api/order/list
- 查询某用户的所有订单（按用户id查询）
    - http://localhost:8084/api/order/listByMemberId?memberId=52
- 查询单个订单
    - http://localhost:8084/api/order/get?memberId=52&orderId=543033976919425024
- 更新某个订单
    - http://localhost:8084/api/order/update/amount?memberId=52&orderId=543033990689325057&amount=1023.11
- 删除某个订单
    - http://localhost:8084/api/order/delete?memberId=52&orderId=543033990689325057

## 问题  

遇到一个比较奇怪的问题，实在没时间深入看：
手工通过上面URL，测试单个增删查改操作时，没有问题，但通过sb连续压测（其实只是想连续多次执行），遇到这个帖子中的问题：
[ShardingSphere报Sharding value must implements Comparable.的解决过程](https://blog.csdn.net/qiaoqiyu6416/article/details/107044664)

希望有时间再深入研究下这个问题吧，本周作业先这样，本周实在太赶了，有时间的话我觉得还是先去复习比较好，先这样。

## 附录：订单表SCHEMA
```
CREATE TABLE `oms_order_XX` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键，记录唯一标识',
  `member_id` bigint(20) NOT NULL COMMENT '用户id',
  `coupon_id` bigint(20) DEFAULT NULL COMMENT '优惠券id',
  `order_sn` varchar(64) DEFAULT NULL COMMENT '订单编号',
  `create_time` datetime DEFAULT NULL COMMENT '提交时间',
  `member_username` varchar(64) DEFAULT NULL COMMENT '用户帐号',
  `total_amount` decimal(10,2) DEFAULT NULL COMMENT '订单总金额',
  `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '应付金额（实际支付金额）',
  `freight_amount` decimal(10,2) DEFAULT NULL COMMENT '运费金额',
  `promotion_amount` decimal(10,2) DEFAULT NULL COMMENT '促销优化金额（促销价、满减、阶梯价）',
  `integration_amount` decimal(10,2) DEFAULT NULL COMMENT '积分抵扣金额',
  `coupon_amount` decimal(10,2) DEFAULT NULL COMMENT '优惠券抵扣金额',
  `discount_amount` decimal(10,2) DEFAULT NULL COMMENT '管理员后台调整订单使用的折扣金额',
  `pay_type` int(1) DEFAULT NULL COMMENT '支付方式：0->未支付；1->支付宝；2->微信',
  `source_type` int(1) DEFAULT NULL COMMENT '订单来源：0->PC订单；1->app订单',
  `status` int(1) DEFAULT NULL COMMENT '订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单',
  `order_type` int(1) DEFAULT NULL COMMENT '订单类型：0->正常订单；1->秒杀订单',
  `delivery_company` varchar(64) DEFAULT NULL COMMENT '物流公司(配送方式)',
  `delivery_sn` varchar(64) DEFAULT NULL COMMENT '物流单号',
  `auto_confirm_day` int(11) DEFAULT NULL COMMENT '自动确认时间（天）',
  `integration` int(11) DEFAULT NULL COMMENT '可以获得的积分',
  `growth` int(11) DEFAULT NULL COMMENT '可以活动的成长值',
  `promotion_info` varchar(100) DEFAULT NULL COMMENT '活动信息',
  `bill_type` int(1) DEFAULT NULL COMMENT '发票类型：0->不开发票；1->电子发票；2->纸质发票',
  `bill_header` varchar(200) DEFAULT NULL COMMENT '发票抬头',
  `bill_content` varchar(200) DEFAULT NULL COMMENT '发票内容',
  `bill_receiver_phone` varchar(32) DEFAULT NULL COMMENT '收票人电话',
  `bill_receiver_email` varchar(64) DEFAULT NULL COMMENT '收票人邮箱',
  `receiver_name` varchar(100) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(32) NOT NULL COMMENT '收货人电话',
  `receiver_post_code` varchar(32) DEFAULT NULL COMMENT '收货人邮编',
  `receiver_province` varchar(32) DEFAULT NULL COMMENT '省份/直辖市',
  `receiver_city` varchar(32) DEFAULT NULL COMMENT '城市',
  `receiver_region` varchar(32) DEFAULT NULL COMMENT '区',
  `receiver_detail_address` varchar(200) DEFAULT NULL COMMENT '详细地址',
  `note` varchar(500) DEFAULT NULL COMMENT '订单备注',
  `confirm_status` int(1) DEFAULT NULL COMMENT '确认收货状态：0->未确认；1->已确认',
  `delete_status` int(1) NOT NULL DEFAULT '0' COMMENT '删除状态：0->未删除；1->已删除',
  `use_integration` int(11) DEFAULT NULL COMMENT '下单时使用的积分',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '确认收货时间',
  `comment_time` datetime DEFAULT NULL COMMENT '评价时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表'
```

## 附录2：我遇到的问题  

问题先记录下，只能有时间再捯饬了~.~

现象：
浏览器手工访问没问题：
http://localhost:8084/api/order/add?username=ccc&memberId=100&payment=100

windows 使用sb测试：
请求：http://localhost:8084/api/order/add?username=ccc&memberId=100&payment=100

将会出现如下异常信息：

```text
Creating a new SqlSession
SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6f53186e] was not registered for synchronization because synchronization is not active
JDBC Connection [org.apache.shardingsphere.shardingjdbc.jdbc.core.connection.ShardingConnection@1580b619] will not be managed by Spring
==>  Preparing: INSERT INTO oms_order ( member_id, coupon_id, order_sn, create_time, member_username, total_amount, pay_amount, freight_amount, promotion_amount, integration_amount, coupon_amount, discount_amount, receiver_name, receiver_phone ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) 
==> Parameters: null, 6(Long), 4a2f4dea-9790-4ddc-b5b1-033f14d069d4(String), 2020-12-08(Date), ccc(String), null, null, 0(BigDecimal), 0(BigDecimal), 0(BigDecimal), 0(BigDecimal), 0(BigDecimal), ccc(String), 15811111111(String)
Closing non transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6f53186e]
2020-12-08 15:31:49.348 ERROR 30420 --- [io-8084-exec-10] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is org.mybatis.spring.MyBatisSystemException: nested exception is org.apache.ibatis.exceptions.PersistenceException: 
### Error updating database.  Cause: java.lang.IllegalArgumentException: Sharding value must implements Comparable.
### The error may exist in file [D:\GitRepository\JAVA-000\Week_08\homework1-data-sharding\target\classes\mappers\OmsOrder.xml]
### The error may involve defaultParameterMap
### The error occurred while setting parameters
### SQL: INSERT INTO oms_order (            member_id,            coupon_id,            order_sn,            create_time,            member_username,            total_amount,            pay_amount,            freight_amount,            promotion_amount,            integration_amount,            coupon_amount,            discount_amount,            receiver_name,            receiver_phone         )         VALUES (         ?,         ?,         ?,         ?,         ?,         ?,         ?,         ?,         ?,         ?,         ?,         ?,         ?,         ?         )
### Cause: java.lang.IllegalArgumentException: Sharding value must implements Comparable.] with root cause

java.lang.IllegalArgumentException: Sharding value must implements Comparable.
	at com.google.common.base.Preconditions.checkArgument(Preconditions.java:122) ~[guava-18.0.jar:na]
	at org.apache.shardingsphere.sharding.route.engine.condition.engine.InsertClauseShardingConditionEngine.getRouteValue(InsertClauseShardingConditionEngine.java:112) ~[sharding-core-route-4.1.1.jar:4.1.1]
	at org.apache.shardingsphere.sharding.route.engine.condition.engine.InsertClauseShardingConditionEngine.createShardingCondition(InsertClauseShardingConditionEngine.java:94) ~[sharding-core-route-4.1.1.jar:4.1.1]
	at org.apache.shardingsphere.sharding.route.engine.condition.engine.InsertClauseShardingConditionEngine.createShardingConditions(InsertClauseShardingConditionEngine.java:65) ~[sharding-core-route-4.1.1.jar:4.1.1]
	at org.apache.shardingsphere.sharding.route.engine.ShardingRouteDecorator.getShardingConditions(ShardingRouteDecorator.java:80) ~[sharding-core-route-4.1.1.jar:4.1.1]
	at org.apache.shardingsphere.sharding.route.engine.ShardingRouteDecorator.decorate(ShardingRouteDecorator.java:62) ~[sharding-core-route-4.1.1.jar:4.1.1]
	at org.apache.shardingsphere.sharding.route.engine.ShardingRouteDecorator.decorate(ShardingRouteDecorator.java:53) ~[sharding-core-route-4.1.1.jar:4.1.1]
	at org.apache.shardingsphere.underlying.route.DataNodeRouter.executeRoute(DataNodeRouter.java:91) ~[shardingsphere-route-4.1.1.jar:4.1.1]
	at org.apache.shardingsphere.underlying.route.DataNodeRouter.route(DataNodeRouter.java:76) ~[shardingsphere-route-4.1.1.jar:4.1.1]
	at org.apache.shardingsphere.underlying.pluggble.prepare.PreparedQueryPrepareEngine.route(PreparedQueryPrepareEngine.java:54) ~[shardingsphere-pluggable-4.1.1.jar:4.1.1]
	at org.apache.shardingsphere.underlying.pluggble.prepare.BasePrepareEngine.executeRoute(BasePrepareEngine.java:96) ~[shardingsphere-pluggable-4.1.1.jar:4.1.1]
	at org.apache.shardingsphere.underlying.pluggble.prepare.BasePrepareEngine.prepare(BasePrepareEngine.java:83) ~[shardingsphere-pluggable-4.1.1.jar:4.1.1]
	at org.apache.shardingsphere.shardingjdbc.jdbc.core.statement.ShardingPreparedStatement.prepare(ShardingPreparedStatement.java:183) ~[sharding-jdbc-core-4.1.1.jar:4.1.1]
	at org.apache.shardingsphere.shardingjdbc.jdbc.core.statement.ShardingPreparedStatement.execute(ShardingPreparedStatement.java:143) ~[sharding-jdbc-core-4.1.1.jar:4.1.1]
	at sun.reflect.GeneratedMethodAccessor53.invoke(Unknown Source) ~[na:na]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_161]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[na:1.8.0_161]
	at org.apache.ibatis.logging.jdbc.PreparedStatementLogger.invoke(PreparedStatementLogger.java:59) ~[mybatis-3.5.1.jar:3.5.1]
	at com.sun.proxy.$Proxy75.execute(Unknown Source) ~[na:na]
	at org.apache.ibatis.executor.statement.PreparedStatementHandler.update(PreparedStatementHandler.java:47) ~[mybatis-3.5.1.jar:3.5.1]
	at org.apache.ibatis.executor.statement.RoutingStatementHandler.update(RoutingStatementHandler.java:74) ~[mybatis-3.5.1.jar:3.5.1]
	at org.apache.ibatis.executor.SimpleExecutor.doUpdate(SimpleExecutor.java:50) ~[mybatis-3.5.1.jar:3.5.1]
	at org.apache.ibatis.executor.BaseExecutor.update(BaseExecutor.java:117) ~[mybatis-3.5.1.jar:3.5.1]
	at org.apache.ibatis.executor.CachingExecutor.update(CachingExecutor.java:76) ~[mybatis-3.5.1.jar:3.5.1]
	at org.apache.ibatis.session.defaults.DefaultSqlSession.update(DefaultSqlSession.java:197) ~[mybatis-3.5.1.jar:3.5.1]
	at org.apache.ibatis.session.defaults.DefaultSqlSession.insert(DefaultSqlSession.java:184) ~[mybatis-3.5.1.jar:3.5.1]
	at sun.reflect.GeneratedMethodAccessor36.invoke(Unknown Source) ~[na:na]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_161]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[na:1.8.0_161]
	at org.mybatis.spring.SqlSessionTemplate$SqlSessionInterceptor.invoke(SqlSessionTemplate.java:433) ~[mybatis-spring-2.0.1.jar:2.0.1]
	at com.sun.proxy.$Proxy68.insert(Unknown Source) ~[na:na]
	at org.mybatis.spring.SqlSessionTemplate.insert(SqlSessionTemplate.java:278) ~[mybatis-spring-2.0.1.jar:2.0.1]
	at org.apache.ibatis.binding.MapperMethod.execute(MapperMethod.java:62) ~[mybatis-3.5.1.jar:3.5.1]
	at org.apache.ibatis.binding.MapperProxy.invoke(MapperProxy.java:58) ~[mybatis-3.5.1.jar:3.5.1]
	at com.sun.proxy.$Proxy69.insertOrder(Unknown Source) ~[na:na]
	at com.prayerlaputa.sharding.service.OrderService.insertOrder(OrderService.java:23) ~[classes/:na]
	at com.prayerlaputa.sharding.controller.OrderController.insertOrder(OrderController.java:50) ~[classes/:na]
	at sun.reflect.GeneratedMethodAccessor35.invoke(Unknown Source) ~[na:na]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_161]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[na:1.8.0_161]
	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:190) ~[spring-web-5.2.5.RELEASE.jar:5.2.5.RELEASE]
	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:138) ~[spring-web-5.2.5.RELEASE.jar:5.2.5.RELEASE]
	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:105) ~[spring-webmvc-5.2.5.RELEASE.jar:5.2.5.RELEASE]
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:879) ~[spring-webmvc-5.2.5.RELEASE.jar:5.2.5.RELEASE]
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:793) ~[spring-webmvc-5.2.5.RELEASE.jar:5.2.5.RELEASE]
	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87) ~[spring-webmvc-5.2.5.RELEASE.jar:5.2.5.RELEASE]
	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1040) ~[spring-webmvc-5.2.5.RELEASE.jar:5.2.5.RELEASE]
	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:943) ~[spring-webmvc-5.2.5.RELEASE.jar:5.2.5.RELEASE]
	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1006) ~[spring-webmvc-5.2.5.RELEASE.jar:5.2.5.RELEASE]
	at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:898) ~[spring-webmvc-5.2.5.RELEASE.jar:5.2.5.RELEASE]
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:634) ~[tomcat-embed-core-9.0.33.jar:9.0.33]
	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:883) ~[spring-webmvc-5.2.5.RELEASE.jar:5.2.5.RELEASE]
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:741) ~[tomcat-embed-core-9.0.33.jar:9.0.33]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:231) ~[tomcat-embed-core-9.0.33.jar:9.0.33]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) ~[tomcat-embed-core-9.0.33.jar:9.0.33]
	at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53) ~[tomcat-embed-websocket-9.0.33.jar:9.0.33]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) ~[tomcat-embed-core-9.0.33.jar:9.0.33]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) ~[tomcat-embed-core-9.0.33.jar:9.0.33]
	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100) ~[spring-web-5.2.5.RELEASE.jar:5.2.5.RELEASE]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119) ~[spring-web-5.2.5.RELEASE.jar:5.2.5.RELEASE]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) ~[tomcat-embed-core-9.0.33.jar:9.0.33]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) ~[tomcat-embed-core-9.0.33.jar:9.0.33]
	at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93) ~[spring-web-5.2.5.RELEASE.jar:5.2.5.RELEASE]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119) ~[spring-web-5.2.5.RELEASE.jar:5.2.5.RELEASE]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) ~[tomcat-embed-core-9.0.33.jar:9.0.33]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) ~[tomcat-embed-core-9.0.33.jar:9.0.33]
	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201) ~[spring-web-5.2.5.RELEASE.jar:5.2.5.RELEASE]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119) ~[spring-web-5.2.5.RELEASE.jar:5.2.5.RELEASE]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) ~[tomcat-embed-core-9.0.33.jar:9.0.33]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) ~[tomcat-embed-core-9.0.33.jar:9.0.33]
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:202) ~[tomcat-embed-core-9.0.33.jar:9.0.33]
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:96) [tomcat-embed-core-9.0.33.jar:9.0.33]
	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:541) [tomcat-embed-core-9.0.33.jar:9.0.33]
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:139) [tomcat-embed-core-9.0.33.jar:9.0.33]
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92) [tomcat-embed-core-9.0.33.jar:9.0.33]
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74) [tomcat-embed-core-9.0.33.jar:9.0.33]
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:343) [tomcat-embed-core-9.0.33.jar:9.0.33]
	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:373) [tomcat-embed-core-9.0.33.jar:9.0.33]
	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:65) [tomcat-embed-core-9.0.33.jar:9.0.33]
	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:868) [tomcat-embed-core-9.0.33.jar:9.0.33]
	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1594) [tomcat-embed-core-9.0.33.jar:9.0.33]
	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49) [tomcat-embed-core-9.0.33.jar:9.0.33]
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149) [na:1.8.0_161]
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624) [na:1.8.0_161]
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61) [tomcat-embed-core-9.0.33.jar:9.0.33]
```