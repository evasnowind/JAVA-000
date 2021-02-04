# Java训练营-知识体系总结

[TOC]



## 说明

- 知识体系总结说明
  - 每个主题都可以写很多，知识100字太少。我打算先在本文档中按照Java训练营毕业要求写一个速览、列出关键点，如果有需要再针对某些主题详细写总结。
- 思维导图说明
  - 按照个人理解，把所有课程的课件重新串了一遍，思维导图细分了一下，主要分如下模块，为方便阅览已转成PNG图片，思维导图原文件在文件夹xmind_files中：
    - JVM
    - java工具与GC
    - NIO与Netty
    - API网关
    - Java并发编程
    - Java相关框架
      - 包括Sping, Spring Boot, Java 8(lambda, stream), Lombok, Google Guava等
    - SQL优化
    - 数据库拆分与分库分表
    - 分布式事务
    - 分布式服务
      - 包括微服务架构，Spring Cloud, Dubbo等
    - 分布式缓存
    - 分布式缓存-redis
    - 分布式消息 
    - 分布式消息-kafka消息中间件
    - 分布式系统架构

## 知识体系速览

### 1、JVM

#### 1.1 Java字节码

- 分类
- 典型问题：
  - 泛型的实现？
    - 类型擦除：编译器擦除类型，checkcast 字节码指令
  - JDK 7 新增invokedynamic  --> Java 8 lambda表达式的基础

#### 1.2 Java类加载器

- 有哪些类加载器？
- 双亲委托模型
  - 如何描述该模型？
  - 源码实现？
  - 如何打破？
    - 1、Java SPI机制，如JDBC
    - 2、OSGi
    - 3、JDK 1.2之前

#### 1.3 类的生命周期

#### 1.4 JVM内存模型JMM

- JMM构成，每个部分的作用
- 并发相关
  - JSR-133 规范， happen-before
  - happen-before, as-if-serial
- ![](images/jmm.png)

#### 1.5 JVM启动参数

- 标准参数、非标参数、非稳定参数
- 场景：GC调优，详见GC章节

#### 1.6 Java agent, Instrument

- 应用场景：APM，如skywalking, pinpoint

### 2、java工具与GC

#### 2.1 GC基本理论

- 如何判断对象是垃圾？
  - 引用计数
    - 无法解决循环引用计数问题
  - GC Roots
    - 静态字段，活动线程，当前正执行的方法里的局部变量和输入参数，JNI引用
- 垃圾回收算法
  - 其实就3种：
    - 标记-清理mark-sweep
    - 标记-整理mark-sweep-compact
    - 复制算法mark-copy
- 分代回收思想
  - eden, survivor
  - 对象头中有4位2进制保存年代数，所以最大也就到15

#### 2.2 常见GC

##### 2.2.1 Serial + Serial Old

- 有STW
- 单线程
- **年轻代：复制算法，老年代：标记-整理**
- 低延迟GC

##### 2.2.2 Parallel Scavenge + Parallel Old

- 并发标记、并发回收，只不过将Serial + Serial Old 中的单线程变成多线程
- **年轻代：复制算法，老年代：标记-整理**
- **高吞吐量GC**

##### 2.2.3 ParNew + CMS

- ParNew其实是Parallel Scavenge的改进，只是为了能支持CMS
- 初始标记 -> 并发标记 -> 重新标记 -> 并发清理
- 初始标记、重新标记会STW
- **年轻代：复制算法，老年代：标记-清除算法**
- **低延迟GC**
- CMS存在浮动垃圾问题，因为并发清理阶段相当于边回收、边创造垃圾。如果回收不过来，将会调用Serial Old、单线程回收。

##### 2.2.4 G1

- 将堆内存划分成多个region， region分为：eden, survivor, old, humongous 这4种。
- 每次GC不再手机整个堆，而是只处理一部分
- 理论上不应该触发full gc
- 有一个mixed gc,  过程与CMS基本一致

##### 2.2.5 ZGC和Shenandoah

- 低延迟GC，最大停顿时间不超过10ms

#### 2.3 如何选择GC

##### 2.3.1 一般性原则

- 若希望系统吞吐优先：Parallel Scavenge + Parallel Old
- 若希望系统低延迟优先，每次GC时间尽量短：CMS
- 若系统内存堆较大，且希望平均GC时间可控：G1

##### 2.3.2 从内存看

- 一般4G以上，G1性价比比较高
- 超过8G，非常推荐G1

#### 2.4 java工具

##### 2.4.1 命令行工具

- jstack
- jstat
- jmap
- jhat
- jcmd

##### 2.4.2 图形化工具

- jconsole
- jvisualvm
- visualgc
- java mission control

## 3、NIO与Netty

### 3.1 java socket

### 3.2 NIO模型与相关概念

##### 3.2.1 基本概念

- 阻塞、非阻塞是线程处理模式
- 同步、异步是通信模式

##### 3.2.2 常见IO模型

- 同步阻塞IO(BIO)
  - 典型：平时我们手写的java socket
- 同步非阻塞IO
  - 由于非阻塞，不管是否拿到结果都会立即返回，需要不断轮询直到拿到数据
- IO多路复用(NIO)
  - 仍是同步、阻塞IO
  - 如果没有拿到数据，当前线程会一直阻塞，由操作系统内核去监听、拿到数据后返回
    - linux的select/poll/epoll的区别
      - select、poll都需要轮询，select有文件描述符fd上限的问题（可以通过改变宏定义或是重新编译内核，但太麻烦），并且每次都会对所有socket做一次线性扫描，浪费了一定开销。时间复杂度O(n)
      - poll与select基本一样，但不会有文件描述符fd上限的问题，基于链表，但还是需要遍历所有fd、时间复杂度O(n)
      - epoll适合于处理大量的fd ，且活跃fd不是很多的情况。只告知那些就绪的文件描述符。时间复杂度O(1)
- 信号驱动IO(SIGIO)
- 异步IO（AIO）

### 3.3 Netty

- 异步、事件驱动、基于NIO
- 为何快？
  - 基于NIO
  - 零拷贝：注意，与Kafka那个不一样，Kafka零拷贝是操作系统层面上的，减少了用户态与内核态之间来回拷贝数据；Netty是在用户态，更多的是数据操作的优化。
  - Reactor模型
- 基本概念
  - Channel & ChannelPipeline
  - ChannelFuture
  - Event & Handler
  - Encoder & Decoder
  - EventLoop
  - Bootstrap
- 常见问题
  - 粘包与拆包
    - 解决：自定义协议，指定报文长度（与HTTP报文设计思想是一样的）

## 4、API网关

### 4.1 网关职能

- 请求接入
- 业务聚合
- 中介策略
  - 安全、验证、路由、过滤、流控等
- 统一管理

### 4.2 网关分类

- 流量网关
  - 关注稳定与安全
    - 流控
    - 日志统计
    - 安全（防SQL注入、web工具）
    - IP黑白名单
    - 加解密
- 业务网关
  - 提供更好的服务
    - 服务级别流控
    - 服务降级与熔断
    - 路由与负载均衡、灰度策略
    - 服务过滤、聚合与发现
    - 业务规则与参数校验
    - 多级缓存策略
    - 权限校验与用户等级策略

### 4.3 常见网关技术选型

- 流量网关
  - OpenResty
  - Kong
- 业务网关
  - Zuul 2
  - Spring Cloud Gateway

## 5、Java并发编程

### 5.1  基础知识

- 如何创建线程
  - Thread, Runnable接口，线程池
    - 只实现Callable
- 线程的状态变化
- 常见问题
  - Thread.sleep与Object.wait的区别
    - sleep让出CPU时间片，但没有释放锁，wait会释放锁
- 线程安全
  - 临界区、竞态条件
  - 并发相关的性质：
    - 可见性
      - volatile使用及其原理
        - 保证可见性，但没保证原子性——CPU缓存一致性协议，IA32体系下是MESI协议
    - 原子性
      - 比如：i++不是原子的
    - 有序性
      - JSR 133 happen-before
      - as-if-serial: 单线程程序的执行结果不会被改变（CPU可以乱序、但结果一致就行）
  - sychronized
    - 使用：同步方法，同步块
    - 原理
      - synchronized作用域不同，底层实现原理也略有差别：
        - synchronized代码块是通过monitorenter和monitorexit来实现其语义的
          -  每一个对象都会和一个监视器monitor关联。监视器被占用时会被锁住，其他线程无法来获取该monitor。
             当JVM执行某个线程的某个方法内部的monitorenter时，它会尝试去获取当前对象对应的monitor的所有权。
        - synchronized方法是通过ACC_SYNCRHONIZED来实现其语义的
          - 当JVM执行引擎执行某一个方法时，其会从方法区中获取该方法的access_flags，检查其是否有ACC_SYNCRHONIZED标识符，若是有该标识符，则说明当前方法是同步方法，需要先获取当前对象的monitor，再来执行方法。
        - 上述是JVM字节码层面。无论是synchronized代码块还是synchronized方法区，其线程安全的语义实现最终依赖一个叫monitor的东西（JVM内部实现）。
    - 锁膨胀过程
      - 无锁
      - 偏向锁JDK 1.6开始
        - 偏向锁、轻量级锁、重量级锁的加锁与java对象的对象头有关
          - JOL：
            - 对象头
              - markword+Klass指针+数组长度（如果是数组）
            - 数据
            - 填充
      - 轻量级锁
      - 重量级锁
  - volatile
    - 使用：保证可见性
    - 原理：
      - 字节码层面上：当字段被volatile修饰时，字段表中对应字段的访问标志将会加上`ACC_VOLATILE`。
      - JVM内部：内存屏障
      - HotSpot源码实现：缓存一致性实现
    - 示例：DCL单例
  - final

### 5.2 重点

#### 5.2.1 线程池ThreadPoolExecutor

- 线程池参数
  - 核心线程数
  - 最大线程数
  - 空闲线程存活时间
  - 时间单位
  - 阻塞队列
  - 拒绝策略
    - AbortPolicy 丢弃任务并抛出异常RejectedExecutionException
    - DiscardPolicy 丢弃任务，不抛出异常
    - DiscardOldestPolicy 丢弃最老的任务，然后重新提交被拒绝的任务
    - CallerRunsPolicy 由调用线程处理该任务
  - 线程工厂
    - 应用：给线程命名，以便打日志时区分
- 线程池源码相关
  - JDK默认线程池由于在达到核心线程数之后、会先将后续任务放到阻塞队列中，而不是创建新的线程，因而比较适合CPU密集型、不适合IO密集型任务（比如数据库查询、RPC请求调用等），
  - 因此Tomcat、Dubbo都有修改线程池，将其行为变成：达到核心线程数之后，继续创建线程，直到达到最大线程数。

#### 5.2.2 线程池相关

- 经典问题
  - execute和submit的区别
    - submit有返回值，用Future封装，可以在主线程中抛出异常
    - execute没有返回值，主线程中捕获不到异常
  - 如何创建线程？
    - 不推荐使用Executors的工具方法，建议自己手工指定ThreadPoolExecutor的7个参数。原因：
      - newSingleThreadPool, newFixedThreadPool的阻塞队列是Integer.MAX_VALUE，可能导致堆积大量请求，进而引发OOM
      - newCachedThreadPool 容许创建的线程数量为Integer.MAX_VALUE, 可能会创建大量线程，进而引发OOM
  - Callable接口与Runable接口的区别
  - Future接口

## 6、Java并发包JUC

### 6.1 锁

- ReentrantLock与Condition
- ReentrantReadWriteLock
- LockSupport

### 6.2 并发原子类

- 并发原子类AtomicXXX
  - 无锁Unsafe API
- 其他
  - LongAdder
    - 分治思想

### 6.3 并发工具类

- **AQS**
  - CLH队列
- Semaphore信号量
- CountdownLatch
- CyclicBarrier
- Future/FutureTask/CompletableFuture

### 6.4 常用线程安全类型

- ConcurrentHashMap
  - 原理：
    - JDK 7
    - JDK 8
- CopyOnWriteArrayList
  - 原理
  - 适用场景
- ThreadLocal
  - 原理
  - 注意
    - 及时清理
- 并行stream
  - 原理
    - 底层利用ForkJoinPool
      - 工作窃取算法



## 7、性能与SQL优化

### 7.1 SQL基础

- 理论基础
  - 关系代数
- 规范
  - 1NF, 2NF, 3NF, BCNF, 4NF, 5NF
- 常见数据库
  - 关系数据库
    - 开源：MySQL, PostgreSQL
    - 商业：Oracle, DB2, SQL Server
  - 内存数据库
    - redis, VoltDB
  - 图数据库
    - Neo4J, Nebula
  - 时序数据库
    - InfluxDB
    - openTSDB
  - NoSQL
    - MongDB, Hbase, Cassandra, CouDB
  - NewSQL/分布式数据库
    - TiDB, CockroachDB, ......

### 7.2 MySQL

#### 7.2.1 历史发展

- 目前
  - 分裂为MariaDB, MySQL
- 版本
  - 4.0 支持事务
  - 5.6 历史最多使用的版本
  - 5.7 近期使用最多的版本
  - 8.0 最新、功能完善的版本
- 不同版本的差异
  - 5.6和5.7：
    - 5.7支持：
      - 多主一从
      - MGR
        - MySQL Group Replication, 基于Paxos，保证数据一致性
        - 原先的异步复制、半同步复制
      - .......
  - 5.7和8：
    - 8支持：
      - .......
      - **不再对group by进行隐式排序！**

#### 7.2.2 数据库原理

- mysql结构

  - server层+执行引擎层
  - 重点：
    - 各种log
      - binlog
      - redo log
      - undo log

- 索引原理

  - B树与B+树的区别？为何选用B+树？
  - InnoDB 聚簇索引、非聚簇索引
    - 聚簇索引：把数据和索引保存在一起
      - 聚簇索引不等价于主键，有主键的时候会作为聚簇索引，没有主键时自动选择一个唯一字段，如果没有唯一字段，则自动维护一个唯一字段作为聚簇索引
      - 非叶子节点存主键，叶子节点存实际数据
    - 什么是回表？
    - 为何一般单表数据要求不能超过2kw？

  

#### 7.2.3 参数优化

- 连接请求相关
- 缓冲区相关
  - 其他

#### 7.2.4 数据库表设计最佳实践



### 7.3 mysql事务与锁

#### 7.3.1 基础

- ACID
- mysql的锁
  - 表级锁
    - 意向锁：
      - 上锁前先加意向锁！
        - 分类：共享意向锁，排他意向锁
    - 自增锁
  - 行级锁
    - InnoDB才支持行级锁
      - 分类
        - 记录锁
        - 间隙锁：操作范围
        - 临键锁：记录锁+间隙锁
        - 谓词锁
- 死锁
  - 死锁条件？
  - 如何打破死锁？
    - 超时机制
    - 强行杀掉一个，破坏死锁条件



#### 7.3.2 事务相关

- **隔离级别**
  - 读未提交 read uncommitted
  - 读已提交 read committed
  - 可重复读 repeatable read
    - InnoDB默认隔离级别
    - 实现原理：
      - MVCC 多版本控制协议
  - 序列化 serializable
- 隔离级别解决的问题
  - 脏读
  - 不可重复读
  - 幻读
- mysql日志
  - undo log
    - 使用：事务回滚，一致性读，崩溃回复
  - redo log
    - 确保事务的持久化
    - WAL
  - 其他日志
    - relay log
      - 主从复制：IO线程将binlog拉取到本地，然后SQL线程重放binlog
    - binlog
      - server层的日志
    - 慢查询日志
    - 错误日志
- **MVCC**
  - 实现机制

### 7.4 SQL优化

- 数据类型的选择
- 存储引擎的选择
- 隐式转换
- 注意事项
  - 索引字段的选择：字段选择性 f = distinct(col) / count(*)
- 常见问题
  - 为何主键要单调递增？
    - 页分裂问题
  - 为何不推荐使用UUID作为主键？
    - 长度过长？
    - 不递增
  - 为何索引不使用hash、而是使用B+树结构？
    - hash对于等值查询效率很高，但不支持范围查询，并且hash索引不能排序、不支持like语法，不支持多列联合索引的最左匹配规则
  - 聚簇索引与非聚簇索引
  - 大批量写入的优化
    - PreparedStatement
    - Batch
    - Load Data直接导入
    - 大量写入时注意索引和约束
      - 推荐做法：批量导入导出前先删掉索引、约束，等导入结束后再重新加回来，这样效率最高
      - mysqldump就是这么干的
    - 间隙锁
  - 索引失效的情况
    - 使用函数
    - not in, is not null, is null
    - in会走索引！
    - 以%开头的like模糊查询
  - 如何实现主键ID？
    - 自增
    - UUID
    - sequence
    - 时间戳/随机数
    - snowflake
  - 如何高效的分页？
    - 分页插件的实现原理：limit语句，但limit本身并不是跳过前多少页数据，而是说从头开始、扫描到想要的数据后，将前边无用的数据丢弃。这导致分页插件（比如PageHelper）在大数据量分页时性能很差。
      - 改进1：反向查询
      - 改进2：带上id查询
      - 改进3：非精确的查询，比如可以算出某一页的id范围是多少，然后用这些id去查询。但由于业务操作、这些id并不一定是连续的，所以查出的结果不一定精确
  - 乐观锁与悲观锁

## 8、数据库拆分与分库分表

### 8.1 mysql主从复制

#### 8.1.1 基础

- 原理
  - 主库写binlog，从库IO线程将主binlog写入到relay log， 从库SQL线程读取relay log、同步数据
- binlog格式
  - statement
  - row
  - mixed

#### 8.1.2 同步方式

- 传统的主从复制
  - 完全异步进行，可能数据不一致
- 半同步复制
  - 需要启动插件
  - 至少一个从库与主库一致
- MGR
  - MySQL Group Replication 组复制
  - 引入了Paxos

**局限性：**

- 主从复制有延迟
- 应用侧需要配合读写分离框架
- 没有解决高可用问题
  - 主从复制没有解决高可用，但MGR除了主从、还包括高可用



### 8.2 读写分离

- 方案
  - 客户端方案
    - 如shardingsphere-jdbc
  - proxy方案
    - 如MyCAT, shardingsphere proxy
- 目前行业情况
  - 使用客户端方案居多，大厂可能会选用proxy方案

### 8.3 mysql高可用

#### 8.3.1 基础

- 高可用的定义
  - 3个9，4个9， 5个9
  - 4个9比较常见
  - 公有云大概能做到99.95%
- 为何要做高可用
  - 读写分离，提高读的能力；故障转移failover
- 常见策略
  - 多个实例不在同一个机架
  - 跨机房
  - 两地三中心

#### 8.3.2 mysql高可用方案

- 方案1：主从手动切换
  - LVS + keepalive, 配置VIP
- 方案2：MHA
  - 全自动、高可用
- 方案3：MGR
  - 主节点挂掉，自动选择从节点
  - 适合场景：弹性复制，高可用分片
- 方案4：MySQL Cluster
- 方案5：orchestrator

### 8.4 为何要做数据库拆分

- 扩展立方体
- 数据库拆分
  - 垂直拆分
    - 按业务拆角度拆
    - 分为拆库、拆表
  - 水平拆分
    - 分库分表

### 8.5 数据库垂直拆分

### 8.6 数据库水平拆分

- 示例：比如对于电商场景，用户订单，可以按用户id分库，按订单id分表

### 8.7 数据的分类管理

- 热数据
  - 放数据库和内存
- 温数据
  - 放数据库，提供正常查询操作
- 冷数据
  - 看情况
- 冰数据
  - 看情况

### 8.8 数据库中间件选型

- 客户端方案
  - shardingsphere-jdbc
  - TDDL 目前已不开源
- proxy方案
  - shardingsphere-proxy
  - MyCAT
  - DRDS(商业闭源)

## 9、分布式事务

### 9.1 基础

- 什么是分布式事务
- 为何需要分布式事务
  - 跨系统（比如多个不同公司的系统交互，典型的如支付宝与银行）
- 实现思路
  - 强一致性
  - 弱一致性
    - 业务侧冲正、补偿
    - 柔性事务

### 9.2 XA分布式事务

#### 9.2.1 基本概念

- 角色
  - AP：application
  - RM: 资源管理器， resource manager, 数据库，文件系统
  - TM：事务管理器，transaction manager, 分配事务唯一id、监控执行、回滚等

#### 9.2.2 执行

- XA事务执行过程
- 事务失败怎么办？
  - 业务SQL执行时 ，某个RM崩溃？
    - 没有prepare，TC会要求所有RM回滚
  - 全部prepare后，某个RM崩溃？
    - mysql 5.6 有问题、可能找不回来，5.7还能找回来
- commit时，某个RM崩溃？
  - 人工干预

#### 9.2.3 主流框架

- ATOMIKOS
- Narayana
- seata
  - seatad XA不是标准实现，不推荐用

#### 9.2.4 XA的问题

- 同步阻塞
- 性能损耗大
- 单点故障
- 数据不一致

### 9.3 BASE柔性事务

#### 9.3.1 基础

- 概念：基本可用，柔性状态，最终一致性
- 比较：本地事务  -> XA(2PC) -> BASE
- 常见模式
  - TCC通过手工补偿处理
  - AT自动补偿处理
- 使用
  - 性能上，柔性事务的并发较高，XA则不高

### 9.4 TCC事务

#### 9.4.1 基础

- TCC的概念
  - Try 完成所有业务检查，预留必须的业务资源
  - Confirm 对应于mysql中的prepare+commit
  - Cancel 撤回
- 对业务的侵入性较大
- 需要注意的问题
  - 防止空回滚
    - try没成功，则没必要cancel
    - cancel等操作需要做成幂等
  - 防悬挂控制
    - 因为网络抖动等原因，可能cancel先到、执行，然后try才到
  - 幂等设计

### 9.5 SAGA事务

- 理解：类似TCC，但没有try、直接confirm或是cancel

### 9.6 AT事务

- 

## 10、RPC与分布式服务

### 10.1 基础

- 基本原理
  - 代理：spring AOP, 动态代理
  - 序列化与反序列：文本（JSON, XML），二进制（kyro, avro, fst, protobuf）
  - 网络传输：TCP/SSL, HTTP/HTTPS
  - 服务的注册与发现
- 常见问题
  - 多个实例的管理
  - 服务的注册发现机制
  - 负载均衡、路由
  - 熔断、限流
  - 重试策略
  - 高可用、监控、性能

### 10.2 常见RPC技术

- 二进制协议：gRPC, Protocol Buffer, hession, Thrift
- WebService(Axis2, CXF)

### 10.3 如何自己设计一个RPC

- 代理
- 序列化
- 网络
- 服务注册与发现

### 10.4 Dubbo

#### 10.4.1 基础

- 核心功能：RPC调用，负载均衡，服务自动注册与发现，可扩展，流量调度

#### 10.4.2 架构

- 组成：
  - 业务层：
    - business
  - RPC层
    - config：配置层
    - proxy：服务代理层，生成stub和skeleton
    - regiestry：注册与发现
    - cluster：集群相关功能
      - directory：列出所有机器
      - router：找出一个范围（还是有多个）
      - load balance：负载均衡找出一个
    - monitor：监控层
    - protocol：协议层
  - 网络层remoting
    - exchange：信息交换层
    - transport：网络传输层
    - serialize：序列化层

#### 10.4.3 高级主题

- 泛化调用
- 隐式传参

### 10.5 微服务架构

- 常见概念
  - SOA: 一种思想，具体落地方式有分布式服务化、ESB两种
- 分布式服务化的常见主题
  - 配置中心、注册中心、元数据中心
    - zookeeper, etcd, nacos, Apollo
  - 服务注册与发现
  - 服务的集群与路由
  - 服务的过滤与流控
    - 限流、降级、过载保护

### 10.6 微服务发展历程

- 单体 -> 垂直架构 -> SOA架构 -> 微服务架构
- 响应式微服务
- 云原生
  - DevOps, 微服务，容器化，持续交付
- 服务网格
- 单元化架构

### 10.7 微服务架构最佳时间

- 遗留系统改造
- 恰当粒度拆分
- 扩展立方体
  - 水平复制
  - 功能解耦
  - 数据分区
- 自动化管理
  - 自动化测试
  - 自动化部署
  - 自动化运维
- 分布式事务
  - 尽量少用！
  - 常见模式：TCC, AT, XA
- 完善监控体系

### 10.8 微服务其他主题

- APM
- 监控
  - ELK
- 可观测性
  - logging
  - tracing
  - metrics
- 权限控制
- 数据处理
- 网关与通信

## 11、分布式缓存

### 11.1 基础

- 加载时机
- 如何评估缓存的有效性？
  - 读写比
  - 命中率
- 缓存使用不当引发的问题
  - 系统预热导致启动慢
  - 系统内存资源耗尽
- 分类
  - 本地缓存
    - guava
    - caffine
  - 分布式缓存
    - memcached
    - redis
  - 内存网格
    - Hazelcast
    - Ignite, Gemfire
    - redisson可以看做弱化版的Hazelcast

### 11.2 创建问题

- 缓存穿透
- 缓存击穿
- 缓存雪崩

### 11.3 Redis

#### 11.3.1 数据类型

- string
- list
- map
- set
- zset
- 高级
  - bitmap
  - Hyperloglogs
  - GEO

#### 11.3.2 应用场景

- 分布式缓存
- 业务数据处理
- 全局一致计数
- 高效统计计数
- 发布订阅与stream
- 分布式锁

#### 11.3.3 redis集群与高可用

- redis主从赋值
- 高可用：redis sentinel
- 数据分片：redis cluster

#### 11.3.4 redis常见问题

- 过期策略
- RDB与AOF

## 12、分布式消息

### 12.1 消息模式与协议

#### 12.1.1 消息模式

- 点对点PTP
  - 对应queue
- 发布/订阅 publish/subscribe
  - 对应topic

#### 12.1.2 协议

- STOMP
- JMS
- AMQP
- MQTT
- XMPP
- Open Messaging

#### 12.1.3 开源消息中间件分类

- 第一代
  - ActiveMQ, RabbitMQ
  - 主要靠内存，不支持堆积
- 第二代
  - Kafka, RocketMQ
  - 基于磁盘和堆积，默认不删消息，WAL
- 第三代
  - Pulsar
  - 计算和存储分离

### 12.2 kafka

#### 12.2.1 概念

- broker
- topic
  - 逻辑概念
- partition
  - 物理概念
- producer
- consumer
- consumer group
- ISR

#### 12.2.2 高级特性

- 副本机制
- 发送：同步、异步
- 顺序保证
- 可靠性传递
- 为何快？
  - 零拷贝
  - 顺序写文件



## 13、秒杀系统设计

- 丢弃订单
- 优化吞吐量
- 异步队列
- 内存分配
- 拆分扩展
- 服务降级

