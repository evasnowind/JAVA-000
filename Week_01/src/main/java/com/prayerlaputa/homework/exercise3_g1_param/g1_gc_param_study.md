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

### 3. 查看堆使用情况
使用`jmap -heap 14`，分析参见下面注释：

```text
Attaching to process ID 14, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.162-b12

using thread-local object allocation.
Garbage-First (G1) GC with 12 thread(s)

Heap Configuration:
   MinHeapFreeRatio         = 40 #  jvm heap 在使用率小于 40的情况下 ,heap 进行收缩 ,Xmx==Xms 的情况下无效
   MaxHeapFreeRatio         = 70
   MaxHeapSize              = 4294967296 (4096.0MB)  # 堆大小4G
   NewSize                  = 1363144 (1.2999954223632812MB) # 新生代初始内存的大小将近 1.3M
   MaxNewSize               = 2575302656 (2456.0MB)
   OldSize                  = 5452592 (5.1999969482421875MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 536870912 (512.0MB) # 元空间512M
   CompressedClassSpaceSize = 528482304 (504.0MB) # 压缩类空间504M
   MaxMetaspaceSize         = 536870912 (512.0MB) 
   G1HeapRegionSize         = 8388608 (8.0MB) # G1 region大小为8M

Heap Usage:
G1 Heap:
   regions  = 512  # 由于启动时已经设置了region大小为8M，整个堆也被指定为4G，因此region数量4096/8=512
   capacity = 4294967296 (4096.0MB)
   used     = 1535838304 (1464.6895446777344MB)
   free     = 2759128992 (2631.3104553222656MB)
   35.75902208685875% used
G1 Young Generation:
Eden Space:
   regions  = 146
   capacity = 2701131776 (2576.0MB)
   used     = 1224736768 (1168.0MB)
   free     = 1476395008 (1408.0MB)
   45.3416149068323% used
Survivor Space:
   regions  = 1
   capacity = 8388608 (8.0MB)
   used     = 8388608 (8.0MB)
   free     = 0 (0.0MB)
   100.0% used
G1 Old Generation:
   regions  = 37
   capacity = 1585446912 (1512.0MB)
   used     = 302712928 (288.6895446777344MB)
   free     = 1282733984 (1223.3104553222656MB)
   19.09322385434751% used

76747 interned Strings occupying 8466592 bytes.
```


