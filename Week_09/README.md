## Week 9 作业

- homework 1 : 

  - （必做）改造自定义RPC的程序，提交到github： 

    1）尝试将服务端写死查找接口实现类变成泛型和反射

    2）尝试将客户端动态代理改成AOP，添加异常处理

    3）尝试使用Netty+HTTP作为client端传输方式

  参见：文件夹内rpcfx， 详细说明参见 [rpcfx作业说明](rpcfx/README.md)

- homework 2 : 

  - 3、（必做）结合dubbo+hmily，实现一个TCC外汇交易处理，代码提交到github： 

    1）用户A的美元账户和人民币账户都在A库，使用1美元兑换7人民币； 

    2）用户B的美元账户和人民币账户都在B库，使用7人民币兑换1美元； 

    3）设计账户表，冻结资产表，实现上述两个本地事务的分布式事务

  - 没加dubbo版本：[simple-hmily-tcc-demo](simple-hmily-tcc-demo)
  
  - dubbo+hmily版本：[dubbo-spring-boot-demo](dubbo-spring-boot-demo)
  
    - 使用zk做注册中心时遇到问题，尝试了多种方式，死活过不去，最后采用了nacos作为dubbo的服务注册中心
  
  
  
  
  
  ### 备注
  
  使用zk作为服务注册中心时，总是报如下错误，就连老师提供的dubbo demo（rpcfx02）都报这个错误，基本确定是本地操作系统不对劲，先留下疑问后续再研究吧，否则这个dubbo+hmily作业就耗费两天，有点得不偿失。。。
  
  ```text
  
  2020-12-18 17:57:07.945  INFO 35768 --- [34.80.253:2181)] org.apache.zookeeper.ClientCnxn          : Opening socket connection to server 62.234.80.253/<unresolved>:2181. Will not attempt to authenticate using SASL (unknown error)
  2020-12-18 17:57:07.945  WARN 35768 --- [34.80.253:2181)] org.apache.zookeeper.ClientCnxn          : Session 0x0 for server 62.234.80.253/<unresolved>:2181, unexpected error, closing socket connection and attempting reconnect
  
  java.nio.channels.UnresolvedAddressException: null
  	at java.base/sun.nio.ch.Net.checkAddress(Net.java:139) ~[na:na]
  	at java.base/sun.nio.ch.SocketChannelImpl.checkRemote(SocketChannelImpl.java:727) ~[na:na]
  	at java.base/sun.nio.ch.SocketChannelImpl.connect(SocketChannelImpl.java:741) ~[na:na]
  	at org.apache.zookeeper.ClientCnxnSocketNIO.registerAndConnect(ClientCnxnSocketNIO.java:277) ~[zookeeper-3.4.13.jar:3.4.13-2d71af4dbe22557fda74f9a9b4309b15a7487f03]
  	at org.apache.zookeeper.ClientCnxnSocketNIO.connect(ClientCnxnSocketNIO.java:287) ~[zookeeper-3.4.13.jar:3.4.13-2d71af4dbe22557fda74f9a9b4309b15a7487f03]
  	at org.apache.zookeeper.ClientCnxn$SendThread.startConnect(ClientCnxn.java:1021) ~[zookeeper-3.4.13.jar:3.4.13-2d71af4dbe22557fda74f9a9b4309b15a7487f03]
  	at org.apache.zookeeper.ClientCnxn$SendThread.run(ClientCnxn.java:1064) ~[zookeeper-3.4.13.jar:3.4.13-2d71af4dbe22557fda74f9a9b4309b15a7487f03]
  
  2020-12-18 17:57:09.046  INFO 35768 --- [34.80.253:2181)] org.apache.zookeeper.ClientCnxn          : Opening socket connection to server 62.234.80.253/<unresolved>:2181. Will not attempt to authenticate using SASL (unknown error)
  2020-12-18 17:57:09.046  WARN 35768 --- [34.80.253:2181)] org.apache.zookeeper.ClientCnxn          : Session 0x0 for server 62.234.80.253/<unresolved>:2181, unexpected error, closing socket connection and attempting reconnect
  
  java.nio.channels.UnresolvedAddressException: null
  	at java.base/sun.nio.ch.Net.checkAddress(Net.java:139) ~[na:na]
  	at java.base/sun.nio.ch.SocketChannelImpl.checkRemote(SocketChannelImpl.java:727) ~[na:na]
  	at java.base/sun.nio.ch.SocketChannelImpl.connect(SocketChannelImpl.java:741) ~[na:na]
  	at org.apache.zookeeper.ClientCnxnSocketNIO.registerAndConnect(ClientCnxnSocketNIO.java:277) ~[zookeeper-3.4.13.jar:3.4.13-2d71af4dbe22557fda74f9a9b4309b15a7487f03]
  	at org.apache.zookeeper.ClientCnxnSocketNIO.connect(ClientCnxnSocketNIO.java:287) ~[zookeeper-3.4.13.jar:3.4.13-2d71af4dbe22557fda74f9a9b4309b15a7487f03]
  	at org.apache.zookeeper.ClientCnxn$SendThread.startConnect(ClientCnxn.java:1021) ~[zookeeper-3.4.13.jar:3.4.13-2d71af4dbe22557fda74f9a9b4309b15a7487f03]
  	at org.apache.zookeeper.ClientCnxn$SendThread.run(ClientCnxn.java:1064) ~[zookeeper-3.4.13.jar:3.4.13-2d71af4dbe22557fda74f9a9b4309b15a7487f03]
  ......
  ```
  
  

