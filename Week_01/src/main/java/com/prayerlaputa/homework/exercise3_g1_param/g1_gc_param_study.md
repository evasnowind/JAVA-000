# week1 第2课实践

## 题目：本机使用G1 GC启动一个程序，仿照课上案例分析一下JVM情况

### 1. 程序启动参数

使用`jps -mlvV`查看程序启动
```text
14 /opt/SpringCloud/app/account-system/lib/account-api.jar --server.tomcat.max-threads=1000 -Xmx4g -Xms4g -Xss512k -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=512m -XX:+UseG1GC -XX:InitiatingHeapOccupancyPercent=35 -XX:G1HeapRegionSize=8m -XX:ParallelGCThreads=12 -XX:ConcGCThreads=4 -XX:MaxDirectMemorySize=256m -XX:ReservedCodeCacheSize=256m -XX:G1ReservePercent=15 -XX:+ParallelRefProcEnabled -Dspeed.env=pre -Xbootclasspath/a:/opt/SpringCloud/app/account-system/config/pre
```
启动时指定的VM参数：
- 指定最大堆内存、最小堆内存均为4G
- 一个线程所用的栈指定为256K
- 元空间、最大元空间指定为512M
- 使用G1进行GC，所以不能指定年轻代、不用配-Xmn，G1自己会动态调整
    - G1 region大小指定为8M
    - G1 并发收集线程为12个，并发标记线程为4个
    - code cache配置为256M,
    - 设置G1ReservePercent为15（默认是10），略微调高了G1保存空间的比例
    - -XX:+ParallelRefProcEnabled 并行的处理Reference对象，如WeakReference，除非在GC log里出现Reference处理时间较长的日志，否则效果不会很明显。
    


使用`jcmd pid VM.flags`，获得启动参数：
```
[root@account-system-747cdb7679-hdq5d SpringCloud]# jcmd 14 VM.flags
14:
-XX:CICompilerCount=12 -XX:CompressedClassSpaceSize=528482304 -XX:ConcGCThreads=4 -XX:G1HeapRegionSize=8388608 -XX:G1ReservePercent=15 -XX:InitialHeapSize=4294967296 -XX:InitiatingHeapOccupancyPercent=35 -XX:MarkStackSize=4194304 -XX:MaxDirectMemorySize=268435456 -XX:MaxHeapSize=4294967296 -XX:MaxMetaspaceSize=536870912 -XX:MaxNewSize=2575302656 -XX:MetaspaceSize=536870912 -XX:MinHeapDeltaBytes=8388608 -XX:ParallelGCThreads=12 -XX:+ParallelRefProcEnabled -XX:ReservedCodeCacheSize=268435456 -XX:ThreadStackSize=512 -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseFastUnorderedTimeStamps -XX:+UseG1GC 
```

### 2. GC回收情况
#### 使用`jstat -gc 14 1000 1000`查看GC情况

```text
[root@account-system-747cdb7679-hdq5d SpringCloud]# jstat -gc 14 1000 1000
 S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT     GCT   
 0.0   8192.0  0.0   8192.0 2637824.0 2293760.0 1548288.0   298567.9  151040.0 142235.6 16640.0 15097.8   2274   50.711   0      0.000   50.711
 0.0   8192.0  0.0   8192.0 2637824.0 2301952.0 1548288.0   298567.9  151040.0 142235.6 16640.0 15097.8   2274   50.711   0      0.000   50.711
 0.0   8192.0  0.0   8192.0 2637824.0 2310144.0 1548288.0   298567.9  151040.0 142235.6 16640.0 15097.8   2274   50.711   0      0.000   50.711
 0.0   8192.0  0.0   8192.0 2637824.0 2318336.0 1548288.0   298567.9  151040.0 142235.6 16640.0 15097.8   2274   50.711   0      0.000   50.711
 0.0   8192.0  0.0   8192.0 2637824.0 2318336.0 1548288.0   298567.9  151040.0 142235.6 16640.0 15097.8   2274   50.711   0      0.000   50.711
 0.0   8192.0  0.0   8192.0 2637824.0 2334720.0 1548288.0   298567.9  151040.0 142235.6 16640.0 15097.8   2274   50.711   0      0.000   50.711
 0.0   8192.0  0.0   8192.0 2637824.0 2334720.0 1548288.0   298567.9  151040.0 142235.6 16640.0 15097.8   2274   50.711   0      0.000   50.711
 0.0   8192.0  0.0   8192.0 2637824.0 2342912.0 1548288.0   298567.9  151040.0 142235.6 16640.0 15097.8   2274   50.711   0      0.000   50.711
 0.0   8192.0  0.0   8192.0 2637824.0 2359296.0 1548288.0   298567.9  151040.0 142235.6 16640.0 15097.8   2274   50.711   0      0.000   50.711
```

#### 使用`jstat -gcutil 14 1000 1000`查看GC

```text
[root@account-system-747cdb7679-hdq5d SpringCloud]# jstat -gcutil 14 1000 1000
  S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT   
  0.00 100.00  30.75  19.07  94.17  90.73   2275   50.721     0    0.000   50.721
  0.00 100.00  31.06  19.07  94.17  90.73   2275   50.721     0    0.000   50.721
  0.00 100.00  31.06  19.07  94.17  90.73   2275   50.721     0    0.000   50.721
  0.00 100.00  31.37  19.07  94.17  90.73   2275   50.721     0    0.000   50.721
  0.00 100.00  31.68  19.07  94.17  90.73   2275   50.721     0    0.000   50.721
  0.00 100.00  31.99  19.07  94.17  90.73   2275   50.721     0    0.000   50.721
  0.00 100.00  31.99  19.07  94.17  90.73   2275   50.721     0    0.000   50.721
  0.00 100.00  32.30  19.07  94.17  90.73   2275   50.721     0    0.000   50.721
  0.00 100.00  32.30  19.07  94.17  90.73   2275   50.721     0    0.000   50.721
  0.00 100.00  32.61  19.07  94.17  90.73   2275   50.721     0    0.000   50.721
```

由于采集信息时，系统基本没人使用，看上去没做任何垃圾回收。
G1比较关键的FGC目前是0次，看上去正常（一般是要求G1的FGC为0次）。

