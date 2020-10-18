学习笔记
## 本周总结  

- JVM基础
    - 基本结构，各个部分作用，java程序如何运行
- Java字节码
    - 字节码文件结构，如何基于JVM执行
- JMM
    - 内存结构、作用，重要区域的一些默认值
    - 比如一个线程的线程栈默认是1M大小
- JVM类加载机制
    - 双亲加载机制
    - 面试题：如何破坏双亲加载机制（参见深入理解JVM，JDBC、OSGi等）
- GC
    - 面试考察重点，也是难点
    - 常见GC、不同GC的组合，JDK不同版本里默认GC是？
        - Serial + Serial Old
        - Parallel Scavenge + Parallel Old
            JDK 8以及之前的默认GC
        - ParNew + CMS
            CMS只是实验性GC，没有任何一个JDK将其作为默认
        - G1
    - 不同GC的适用场景，如何选择，常见参数
    - G1原理，使用
