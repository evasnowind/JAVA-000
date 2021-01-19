# 目标  

在版本1 MQ的基础上，去掉内存Queue，设计自定义Queue，实现消息确认和消费offset
- 1）自定义内存Message数组模拟Queue。  
- 2）使用指针记录当前消息写入位置。  
- 3）对于每个命名消费者，用指针记录消费位置   

## 实现思路  

### 1. 基本思路  

创建一个数组，并保存如下变量：
- header  
    - 下一个待消费消息的保存位置
- tail   
    - 下一个新入消息的保存位置
- capacity  
    - 队列最大能保存的消息数量
- counter  
    - 当前已保存的消息数量
    - 为了保证线程安全，需要使用AtomicInteger

消息被消费后，需要在数组中将其清除，清理出可用空间，为此，可以采用环形队列的思想，用一维数组实现环形队列，但要注意队列判空、判满的条件，可以参考这篇文章：https://blog.csdn.net/huangkq1989/article/details/5719529

### 2. consumer如何实现`poll(time, timeUnit)`方法的超时机制  

可以参考JDK阻塞队列`LinkedBlockingQueue`的实现。  

加入一个`ReentrantLock`，在调用`poll(time, timeUnit)`方法、且队列为空时加上一个`awaitNanos(time)`，等待直至超时。

### 3. 并发安全的保证   

- `offer`的并发保证  
    - 创建一个`putLock`可重入锁，每次将新数据写入到先上锁
    - 当调用`offer`方法时：
    - ```text
      1. putLock上锁
      2. 判断队列是否已满，若已满则阻塞
          2.1 如果是带超时的offer方法，此处还需要加上只等待一定时间、超时自动唤醒的逻辑  
      3. 新数据入队
      4. 解锁
      ```
- `poll`的并发安全保证  
    - 创建一个'takeLock'可重入锁，每次从队列取数据都先上锁  
    - 当调用`poll`方法时：
    - ```text
      1. takeLock上锁
      2. 判断队列是否为空，若为空则阻塞
        2.1 如果是带超时的poll方法，此处还需要加上只等待一定时间、超时自动唤醒的逻辑  
      3. 数据出队
      4. 解锁
      ```



### 4. 实际操作

代码的变化：

- 将原本kmq项目拷贝过来后，稍微改了改类名字： Kmq -> Pmq
- 自定义PrayerQueue接口，并定义ArrayPrayerQueue实现了该接口
- 将Pmq类中queue成员变量替换为PrayerQueue类型，并用ArrayPrayerQueue实例化

### 优缺点分析

数据一致保存在内存中，虽然可以保证操作很快，但无法接收大量数据（会将内存撑爆。。。）。

## 进一步    

1、内存队列，可以使用disruptor
