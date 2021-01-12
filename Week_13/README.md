# week 13 消息中间件

## 作业1

（必做）搭建ActiveMQ服务，基于JMS，写代码分别实现对于queue和topic的消息生产和消费，代码提交到github。

代码参见本文件中message-queue 项目中的jms-activemq-demo模块。



## 遇到的问题

### 1、activemq启动失败，报错

报错信息如下：

```java
Caused by: java.lang.IllegalStateException: BeanFactory not initialized or already closed - call 'refresh' before accessing beans via the ApplicationContext
at org.springframework.context.support.AbstractRefreshableApplicationContext.getBeanFactory(AbstractRefreshableApplicationContext.java:171)
at org.springframework.context.support.AbstractApplicationContext.destroyBeans(AbstractApplicationContext.java:1090)
at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:487)
at org.apache.xbean.spring.context.ResourceXmlApplicationContext.<init>(ResourceXmlApplicationContext.java:64)
at org.apache.xbean.spring.context.ResourceXmlApplicationContext.<init>(ResourceXmlApplicationContext.java:52)
at org.apache.activemq.xbean.XBeanBrokerFactory$1.<init>(XBeanBrokerFactory.java:104)
at org.apache.activemq.xbean.XBeanBrokerFactory.createApplicationContext(XBeanBrokerFactory.java:104)
at org.apache.activemq.xbean.XBeanBrokerFactory.createBroker(XBeanBrokerFactory.java:67)
at org.apache.activemq.broker.BrokerFactory.createBroker(BrokerFactory.java:71)
at org.apache.activemq.broker.BrokerFactory.createBroker(BrokerFactory.java:54)
at org.apache.activemq.console.command.StartCommand.runTask(StartCommand.java:87)
... 10 more
```

#### 解决

- 1.确认计算机主机名名称没有下划线
  - centos修改主机名可以参考这篇 https://www.cnblogs.com/mingyue5826/p/11528121.html
- 2.如果是win7，停止ICS(运行-->services.msc找到Internet Connection Sharing (ICS)服务,改成手动启动或禁用)



### 2、部署在VPS或是虚拟机上，也关闭了防火墙，但无法访问web页面xx.xx.xx.xx:8161/admin

修改`conf/jetty.xml`，注释掉host属性这行：

```xml
<bean id="jettyPort" class="org.apache.activemq.web.WebConsolePort" init-method="start">
           <!--默认是127.0.0.1， 注释掉这一行-->
        <!-- <property name="host" value="127.0.0.1"/> -->
        <property name="port" value="8161"/>
</bean>
```

重新启动activemq即可



## 参考资料

- [ActiveMQ常见错误一: BeanFactory not initialized or already closed - call 'refresh' before acces](https://blog.csdn.net/vtopqx/article/details/51787888)

- [centos修改主机名称的方式](https://www.cnblogs.com/mingyue5826/p/11528121.html)

- [activeMq不能被主机访问的问题](https://www.cnblogs.com/ytmm/p/14198680.html)





## 作业2

1、（必做）搭建一个3节点Kafka集群，测试功能和性能；实现spring kafka下对kafka集群的操作，将代码提交到github。

kafka操作的代码参见本文件夹message-queue 项目中的kafka-demo模块。



kafka集群的搭建：官方文档 https://kafka.apache.org/quickstart#quickstart_startserver

### 功能测试

略。

### 性能测试

环境：腾讯云 1核2G 网络1Mbps 高性能云硬盘（腾讯云最低配置。。。）

操作系统：centos 7.5

zookeeper: standalone部署

kafka堆内存配置: KAFKA_HEAP_OPTS="-Xmx256m -Xms128m"

**——内存配置很低，因为服务器太渣了。。。**

测试命令：

```shell
bin/kafka-producer-perf-test.sh --topic test --num-records 100000 --record-size 1000 --throughput 2000 --producer-props bootstrap.servers=localhost:9092
```

测试结果：

#### 单个节点测试

测试结果：

```
[root@centos7 kafka_2.12-2.5.0]# bin/kafka-producer-perf-test.sh --topic test --num-records 100000 --record-size 1000 --throughput 2000 --producer-props bootstrap.servers=localhost:9092
9990 records sent, 1996.4 records/sec (1.90 MB/sec), 392.4 ms avg latency, 948.0 ms max latency.
10004 records sent, 1998.8 records/sec (1.91 MB/sec), 4.5 ms avg latency, 81.0 ms max latency.
10034 records sent, 2006.4 records/sec (1.91 MB/sec), 7.4 ms avg latency, 78.0 ms max latency.
9964 records sent, 1989.6 records/sec (1.90 MB/sec), 0.7 ms avg latency, 40.0 ms max latency.
10046 records sent, 2004.4 records/sec (1.91 MB/sec), 3.6 ms avg latency, 118.0 ms max latency.
9976 records sent, 1995.2 records/sec (1.90 MB/sec), 0.5 ms avg latency, 27.0 ms max latency.
10056 records sent, 2010.8 records/sec (1.92 MB/sec), 1.2 ms avg latency, 53.0 ms max latency.
10002 records sent, 2000.0 records/sec (1.91 MB/sec), 0.8 ms avg latency, 44.0 ms max latency.
10004 records sent, 2000.0 records/sec (1.91 MB/sec), 0.4 ms avg latency, 42.0 ms max latency.
100000 records sent, 1999.600080 records/sec (1.91 MB/sec), 41.13 ms avg latency, 948.00 ms max latency, 0 ms 50th, 441 ms 95th, 783 ms 99th, 838 ms 99.9th.
```



#### 多个节点测试

```text
[root@centos7 kafka_2.12-2.5.0]# bin/kafka-producer-perf-test.sh --topic test --num-records 100000 --record-size 1000 --throughput 2000 --producer-props bootstrap.servers=localhost:9092
10000 records sent, 2000.0 records/sec (1.91 MB/sec), 109.8 ms avg latency, 1036.0 ms max latency.
10008 records sent, 2001.6 records/sec (1.91 MB/sec), 4.3 ms avg latency, 126.0 ms max latency.
10002 records sent, 2000.0 records/sec (1.91 MB/sec), 0.9 ms avg latency, 41.0 ms max latency.
10004 records sent, 2000.4 records/sec (1.91 MB/sec), 1.6 ms avg latency, 40.0 ms max latency.
10000 records sent, 1994.8 records/sec (1.90 MB/sec), 0.6 ms avg latency, 35.0 ms max latency.
10042 records sent, 2007.6 records/sec (1.91 MB/sec), 0.3 ms avg latency, 24.0 ms max latency.
10002 records sent, 2000.4 records/sec (1.91 MB/sec), 0.3 ms avg latency, 26.0 ms max latency.
10002 records sent, 2000.4 records/sec (1.91 MB/sec), 0.3 ms avg latency, 26.0 ms max latency.
10002 records sent, 2000.4 records/sec (1.91 MB/sec), 0.2 ms avg latency, 8.0 ms max latency.
100000 records sent, 1997.722596 records/sec (1.91 MB/sec), 11.86 ms avg latency, 1036.00 ms max latency, 0 ms 50th, 18 ms 95th, 399 ms 99th, 465 ms 99.9th.
```





### 遇到的问题

#### 1、内存不足，kafka-server-start.sh 启动失败

kafka启动脚本中默认的堆内存大小为1G，我自己买的VPS只有1G内存，于是修改启动脚本中的kafka堆内存配置：

```shell
if [ "x$KAFKA_HEAP_OPTS" = "x" ]; then
	# 本想都设置成128m，结果启动时会报OOM，128m对于kafka太小了。。。
    export KAFKA_HEAP_OPTS="-Xmx256m -Xms128m"
fi
```

#### 2、kafka启动报cluster id

异常信息如下

```java
[2021-01-12 11:28:46,233] INFO [ZooKeeperClient Kafka server] Connected. (kafka.zookeeper.ZooKeeperClient)
[2021-01-12 11:28:46,718] INFO Cluster ID = p_aZvpTvRdugTVT1C_sbPQ (kafka.server.KafkaServer)
[2021-01-12 11:28:46,772] ERROR Fatal error during KafkaServer startup. Prepare to shutdown (kafka.server.KafkaServer)
kafka.common.InconsistentClusterIdException: The Cluster ID p_aZvpTvRdugTVT1C_sbPQ doesn't match stored clusterId Some(AhesdLlbQAa7YgqFAmd8bQ) in meta.properties. The broker is trying to join the wrong cluster. Configured zookeeper.connect may be wrong.
        at kafka.server.KafkaServer.startup(KafkaServer.scala:223)
        at kafka.server.KafkaServerStartable.startup(KafkaServerStartable.scala:44)
        at kafka.Kafka$.main(Kafka.scala:82)
        at kafka.Kafka.main(Kafka.scala)
[2021-01-12 11:28:46,792] INFO shutting down (kafka.server.KafkaServer)
[2021-01-12 11:28:46,794] INFO [ZooKeeperClient Kafka server] Closing. (kafka.zookeeper.ZooKeeperClient)
[2021-01-12 11:28:46,913] INFO Session: 0x1004d284a250016 closed (org.apache.zookeeper.ZooKeeper)
[2021-01-12 11:28:46,915] INFO [ZooKeeperClient Kafka server] Closed. (kafka.zookeeper.ZooKeeperClient)
[2021-01-12 11:28:46,917] INFO EventThread shut down for session: 0x1004d284a250016 (org.apache.zookeeper.ClientCnxn)
[2021-01-12 11:28:46,918] INFO shut down completed (kafka.server.KafkaServer)
[2021-01-12 11:28:46,918] ERROR Exiting Kafka. (kafka.server.KafkaServerStartable)
[2021-01-12 11:28:46,939] INFO shutting down (kafka.server.KafkaServer)
```

原因：我这里是因为做试验、清空了zk里的数据，然后重启kafka时，cluster id重新生成，与原本配置文件中记录的不一致，导致启动失败

解决：找到kafka日志目录中的meta.properties文件，将日志中显示的新cluster id写入即可：

比如我上面日志显示旧的id是`AhesdLlbQAa7YgqFAmd8bQ`，在`meta.properties`中将其替换成`p_aZvpTvRdugTVT1C_sbPQ`，保存，重启kafka即可：

```properties
#Tue Jan 12 10:55:54 CST 2021
# 此处进行替换
cluster.id=p_aZvpTvRdugTVT1C_sbPQ
version=0
broker.id=3
```





#### 

## 参考资料

- [kafka记录（4）Kafka在zookeeper中的存储](https://www.cnblogs.com/kpsmile/p/10480386.html)
- [修改Kafka的启动内存](https://www.jianshu.com/p/944d7f129abb)
- [kafka启动报Cluster ID错误](https://blog.csdn.net/wangzhicheng1983/article/details/109136866)
- [docker——kafka集群安装](https://blog.csdn.net/wild46cat/article/details/78081051)



