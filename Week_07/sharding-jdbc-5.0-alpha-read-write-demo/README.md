# sharding-jdbc-5.0-alpha 分库分表

配置文件里参考官方例子基本都配了，但目前启动时遇到下面问题，感觉是本地编译出的包有问题？还是我哪里配置问题呢？：
```text

2020-12-02 15:58:44.193  INFO 16516 --- [           main] .p.s.ShardingDbTableReadWriteApplication : Starting ShardingDbTableReadWriteApplication on BR-IT-A00639 with PID 16516 (D:\GitRepository\JAVA-000\Week_07\sharding-jdbc-5.0-alpha-read-write-demo\target\classes started by chenglong.yu in D:\GitRepository\JAVA-000\Week_07)
2020-12-02 15:58:44.200  INFO 16516 --- [           main] .p.s.ShardingDbTableReadWriteApplication : No active profile set, falling back to default profiles: default
2020-12-02 15:58:46.129 ERROR 16516 --- [           main] o.s.boot.SpringApplication               : Application run failed

java.lang.reflect.InvocationTargetException: null
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_161]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_161]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_161]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[na:1.8.0_161]
	at org.apache.shardingsphere.spring.boot.util.PropertyUtil.v2(PropertyUtil.java:111) ~[shardingsphere-jdbc-spring-boot-starter-infra-5.0.0-alpha.jar:5.0.0-alpha]
	at org.apache.shardingsphere.spring.boot.util.PropertyUtil.handle(PropertyUtil.java:75) ~[shardingsphere-jdbc-spring-boot-starter-infra-5.0.0-alpha.jar:5.0.0-alpha]
	at org.apache.shardingsphere.spring.boot.registry.AbstractAlgorithmProvidedBeanRegistry.registerBean(AbstractAlgorithmProvidedBeanRegistry.java:50) ~[shardingsphere-jdbc-spring-boot-starter-infra-5.0.0-alpha.jar:5.0.0-alpha]
	at org.apache.shardingsphere.sharding.spring.boot.algorithm.ShardingAlgorithmProvidedBeanRegistry.postProcessBeanDefinitionRegistry(ShardingAlgorithmProvidedBeanRegistry.java:38) ~[shardingsphere-sharding-spring-boot-starter-5.0.0-alpha.jar:5.0.0-alpha]
	at org.springframework.context.support.PostProcessorRegistrationDelegate.invokeBeanDefinitionRegistryPostProcessors(PostProcessorRegistrationDelegate.java:275) ~[spring-context-5.2.5.RELEASE.jar:5.2.5.RELEASE]
	at org.springframework.context.support.PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(PostProcessorRegistrationDelegate.java:125) ~[spring-context-5.2.5.RELEASE.jar:5.2.5.RELEASE]
	at org.springframework.context.support.AbstractApplicationContext.invokeBeanFactoryPostProcessors(AbstractApplicationContext.java:706) ~[spring-context-5.2.5.RELEASE.jar:5.2.5.RELEASE]
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:532) ~[spring-context-5.2.5.RELEASE.jar:5.2.5.RELEASE]
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:141) ~[spring-boot-2.2.6.RELEASE.jar:2.2.6.RELEASE]
	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:747) [spring-boot-2.2.6.RELEASE.jar:2.2.6.RELEASE]
	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:397) [spring-boot-2.2.6.RELEASE.jar:2.2.6.RELEASE]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:315) [spring-boot-2.2.6.RELEASE.jar:2.2.6.RELEASE]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1226) [spring-boot-2.2.6.RELEASE.jar:2.2.6.RELEASE]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1215) [spring-boot-2.2.6.RELEASE.jar:2.2.6.RELEASE]
	at com.prayerlaputa.sharding.ShardingDbTableReadWriteApplication.main(ShardingDbTableReadWriteApplication.java:16) [classes/:na]
Caused by: java.util.NoSuchElementException: No value bound
	at org.springframework.boot.context.properties.bind.BindResult.get(BindResult.java:56) ~[spring-boot-2.2.6.RELEASE.jar:2.2.6.RELEASE]
	... 19 common frames omitted

```