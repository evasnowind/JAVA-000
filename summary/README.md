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

