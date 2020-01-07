## 大数据开发面试指南

### Java 基础篇

- 语言基础
- 锁
- 多线程
- 并发包中常用的并发容器（J.U.C）

##### 语言基础

- Java 的面向对象
- Java 语言的三大特征：封装、继承和多态
- Java 语言数据类型：

> 内置数据类型（byte、short、int、float、double、boolean、char） 引用数据类型：在 Java 中，引用类型的变量非常类似于 C/C++ 的指针，引用类型指向一个对象，指向对象的变量是引用变量，比如对象、数组

- Java 的自动类型转换，强制类型转换
- String 的不可变性，虚拟机的常量池，String.intern() 的底层原理
- Java 语言中的关键字：**final**、**static**、**transient**、**instanceof**、**volatile**、**synchronized**的底层原理
- Java 中常用的集合类的实现原理： ArrayList/LinkedList/Vector、SynchronizedList/Vector、HashMap/HashTable/ConcurrentHashMap 互相的区别以及底层实现原理
- 动态代理的实现方式

##### 锁

- CAS、乐观锁与悲观锁、数据库相关锁机制、分布式锁、偏向锁、轻量级锁、重量级锁、monitor
- 锁优化、锁消除、锁粗化、自旋锁、可重入锁、阻塞锁、死锁
- 死锁的原因
- 死锁的解决办法
- CountDownLatch、CyclicBarrier 和 Semaphore 三个类的使用和原理

##### 多线程

- 并发和并行的区别
- 线程与进程的区别
- 线程的实现、线程的状态、优先级、线程调度、创建线程的多种方式、守护线程
- 自己设计线程池、submit() 和 execute()、线程池原理
- 为什么不允许使用 Executors 创建线程池
- 死锁、死锁如何排查、线程安全和内存模型的关系
- ThreadLocal 变量
- Executor 创建线程池的几种方式：
  - newFixedThreadPool(int nThreads)
  - newCachedThreadPool()
  - newSingleThreadExecutor()
  - newScheduledThreadPool(int corePoolSize)
  - newSingleThreadExecutor()
- ThreadPoolExecutor 创建线程池、拒绝策略
- 线程池关闭的方式

##### 并发容器（J.U.C）

- JUC 包中 List 接口的实现类：CopyOnWriteArrayList
- JUC 包中 Set 接口的实现类：CopyOnWriteArraySet、ConcurrentSkipListSet
- JUC 包中 Map 接口的实现类：ConcurrentHashMap、ConcurrentSkipListMap
- JUC包中Queue接口的实现类：ConcurrentLinkedQueue、ConcurrentLinkedDeque、ArrayBlockingQueue、LinkedBlockingQueue、LinkedBlockingDeque

### Java 进阶篇

进阶篇部分是对 Java 基础篇的补充，这部分内容是我们熟读大数据框架的源码必备的技能，也是我们在面试高级职位的时候的面试重灾区。

##### JVM

JVM 内存结构

> class 文件格式、运行时数据区：堆、栈、方法区、直接内存、运行时常量池

堆和栈区别

> Java 中的对象一定在堆上分配吗？

Java 内存模型

> 计算机内存模型、缓存一致性、MESI 协议、可见性、原子性、顺序性、happens-before、内存屏障、synchronized、volatile、final、锁

垃圾回收

> GC 算法：标记清除、引用计数、复制、标记压缩、分代回收、增量式回收、GC 参数、对象存活的判定、垃圾收集器（CMS、G1、ZGC、Epsilon）

JVM 参数及调优

> -Xmx、-Xmn、-Xms、Xss、-XX:SurvivorRatio、-XX:PermSize、-XX:MaxPermSize、-XX:MaxTenuringThreshold

Java 对象模型

> oop-klass、对象头

HotSpot

> 即时编译器、编译优化

虚拟机性能监控与故障处理工具

> jps、jstack、jmap、jstat、jconsole、 jinfo、 jhat、javap、btrace、TProfiler、Arthas

类加载机制

> classLoader、类加载过程、双亲委派（破坏双亲委派）、模块化（jboss modules、osgi、jigsaw）

##### NIO

- 用户空间以及内核空间
- Linux 网络 I/O 模型：阻塞 I/O (Blocking I/O)、非阻塞 I/O (Non-Blocking I/O)、I/O 复用（I/O Multiplexing)、信号驱动的 I/O (Signal Driven I/O)、异步 I/O
- 灵拷贝（ZeroCopy）
- BIO 与 NIO 对比
- 缓冲区 Buffer
- 通道 Channel
- 反应堆
- 选择器
- AIO

##### RPC

- RPC 的原理编程模型
- 常用的 RPC 框架：Thrift、Dubbo、SpringCloud
- RPC 的应用场景和与消息队列的差别
- RPC 核心技术点：服务暴露、远程代理对象、通信、序列化

### Linux 基础

- 了解 Linux 的常用命令
- 远程登录
- 上传下载
- 系统目录
- 文件和目录操作
- Linux 下的权限体系
- 压缩和打包
- 用户和组
- Shell 脚本的编写
- 管道操作

### 分布式理论篇

- 分布式中的一些基本概念：集群（Cluster）、负载均衡（Load Balancer）等
- 分布式系统理论基础： 一致性、2PC 和 3PC
- 分布式系统理论基础：CAP
- 分布式系统理论基础：时间、时钟和事件顺序
- 分布式系统理论进阶：Paxos
- 分布式系统理论进阶：Raft、Zab
- 分布式系统理论进阶：选举、多数派和租约
- 分布式锁的解决方案
- 分布式事务的解决方案
- 分布式 ID 生成器解决方案

### 大数据框架网络通信基石——Netty

Netty 是当前最流行的 NIO 框架，Netty 在互联网领域、大数据分布式计算领域、游戏行业、通信行业等获得了广泛的应用，业界著名的开源组件只要涉及到网络通信，Netty 是最佳的选择。

关于 Netty 我们要掌握：

- Netty 三层网络架构：Reactor 通信调度层、职责链 PipeLine、业务逻辑处理层
- Netty 的线程调度模型
- 序列化方式
- 链路有效性检测
- 流量整形
- 优雅停机策略
- Netty 对 SSL/TLS 的支持
- Netty 的源码质量极高，推荐对部分的核心代码进行阅读：
- Netty 的 Buffer
- Netty 的 Reactor
- Netty 的 Pipeline
- Netty 的 Handler 综述
- Netty 的 ChannelHandler
- Netty 的 LoggingHandler
- Netty 的 TimeoutHandler
- Netty 的 CodecHandler
- Netty 的 MessageToByteEncoder

### 离线数据存储——Hadoop

Hadoop 体系是我们学习大数据框架的基石，尤其是 MapReduce、HDFS、Yarn 三驾马车基本垫定了整个数据方向的发展道路。也是后面我们学习其他框架的基础，关于 Hadoop 本身我们应该掌握哪些呢？

MapReduce：

- 掌握 MapReduce 的工作原理
- 能用 MapReduce 手写代码实现简单的 WordCount 或者 TopN 算法
- 掌握 MapReduce Combiner 和 Partitioner的作用
- 熟悉 Hadoop 集群的搭建过程，并且能解决常见的错误
- 熟悉 Hadoop 集群的扩容过程和常见的坑
- 如何解决 MapReduce 的数据倾斜
- Shuffle 原理和减少 Shuffle 的方法

HDFS：

- 十分熟悉 HDFS 的架构图和读写流程
- 十分熟悉 HDFS 的配置
- 熟悉 DataNode 和 NameNode 的作用
- NameNode 的 HA 搭建和配置，Fsimage 和 EditJournal 的作用的场景
- HDFS 操作文件的常用命令
- HDFS 的安全模式

Yarn：

- Yarn 的产生背景和架构
- Yarn 中的角色划分和各自的作用
- Yarn 的配置和常用的资源调度策略
- Yarn 进行一次任务资源调度的过程

### OLAP 引擎 Hive

Hive 是一个数据仓库基础工具，在 Hadoop 中用来处理结构化数据。它架构在 Hadoop 之上，总归为大数据，并使得查询和分析方便。Hive 是应用最广泛的 OLAP 框架。Hive SQL 也是我们进行 SQL 开发用的最多的框架。

关于 Hive 你必须掌握的知识点如下：

- HiveSQL 的原理：我们都知道 HiveSQL 会被翻译成 MapReduce 任务执行，那么一条 SQL 是如何翻译成 MapReduce 的？
- Hive 和普通关系型数据库有什么区别？
- Hive 支持哪些数据格式
- Hive 在底层是如何存储 NULL 的
- HiveSQL 支持的几种排序各代表什么意思（Sort By/Order By/Cluster By/Distrbute By）
- Hive 的动态分区
- HQL 和 SQL 有哪些常见的区别
- Hive 中的内部表和外部表的区别
- Hive 表进行关联查询如何解决长尾和数据倾斜问题
- HiveSQL 的优化（系统参数调整、SQL 语句优化）

### 列式数据库 Hbase

我们在提到列式数据库这个概念的时候，第一反应就是 Hbase。

HBase 本质上是一个数据模型，类似于谷歌的大表设计，可以提供快速随机访问海量结构化数据。它利用了 Hadoop 的文件系统（HDFS）提供的容错能力。

它是 Hadoop 的生态系统，提供对数据的随机实时读/写访问，是 Hadoop 文件系统的一部分。

我们可以直接或通过 HBase 的存储 HDFS 数据。使用 HBase 在 HDFS 读取消费/随机访问数据。 HBase 在 Hadoop 的文件系统之上，并提供了读写访问。

HBase 是一个面向列的数据库，在表中它由行排序。表模式定义只能列族，也就是键值对。一个表有多个列族以及每一个列族可以有任意数量的列。后续列的值连续地存储在磁盘上。表中的每个单元格值都具有时间戳。总之，在一个 HBase：表是行的集合、行是列族的集合、列族是列的集合、列是键值对的集合。

关于 Hbase 你需要掌握：

- Hbase 的架构和原理
- Hbase 的读写流程
- Hbase 有没有并发问题？Hbase 如何实现自己的 MVVC 的？
- Hbase 中几个重要的概念：HMaster、RegionServer、WAL 机制、MemStore
- Hbase 在进行表设计过程中如何进行列族和 RowKey 的设计
- Hbase 的数据热点问题发现和解决办法
- 提高 Hbase 的读写性能的通用做法
- HBase 中 RowFilter 和 BloomFilter 的原理
- Hbase API 中常见的比较器
- Hbase 的预分区
- Hbase 的 Compaction
- Hbase 集群中 HRegionServer 宕机如何解决

### 分布式消息队列 Kafka

Kafka 是最初由 Linkedin 公司开发，是一个分布式、支持分区的（partition）、多副本的（replica）的分布式消息系统，它的最大的特性就是可以实时的处理大量数据以满足各种需求场景：比如基于 Hadoop 的批处理系统、低延迟的实时系统、Spark 流式处理引擎，Nginx 日志、访问日志，消息服务等等，用 Scala 语言编写，Linkedin 于 2010 年贡献给了 Apache 基金会并成为顶级开源项目。

Kafka 或者类似 Kafka 各个公司自己造的消息'轮子'已经是大数据领域消息中间件的事实标准。目前 Kafka 已经更新到了 2.x 版本，支持了类似 KafkaSQL 等功能，Kafka 不满足单纯的消息中间件，也正朝着平台化的方向演进。

关于 Kafka 我们需要掌握：

- Kafka 的特性和使用场景
- Kafka 中的一些概念：Leader、Broker、Producer、Consumer、Topic、Group、Offset、Partition、ISR
- Kafka 的整体架构
- Kafka 选举策略
- Kafka 读取和写入消息过程中都发生了什么
- Kakfa 如何进行数据同步（ISR）
- Kafka 实现分区消息顺序性的原理
- 消费者和消费组的关系
- 消费 Kafka 消息的 Best Practice（最佳实践）是怎样的
- Kafka 如何保证消息投递的可靠性和幂等性
- Kafka 消息的事务性是如何实现的
- 如何管理 Kafka 消息的 Offset
- Kafka 的文件存储机制
- Kafka 是如何支持 Exactly-once 语义的
- 通常 Kafka 还会要求和 RocketMQ 等消息中间件进行比较

### Spark

Spark 是专门为大数据处理设计的通用计算引擎，是一个实现快速通用的集群计算平台。它是由加州大学伯克利分校 AMP 实验室开发的通用内存并行计算框架，用来构建大型的、低延迟的数据分析应用程序。它扩展了广泛使用的 MapReduce 计算模型。高效的支撑更多计算模式，包括交互式查询和流处理。Spark 的一个主要特点是能够在内存中进行计算，即使依赖磁盘进行复杂的运算，Spark 依然比 MapReduce 更加高效。

Spark 生态包含了：Spark Core、Spark Streaming、Spark SQL、Structured Streming 和机器学习相关的库等。

学习 Spark 我们应该掌握：

（1）Spark Core：

- Spark的集群搭建和集群架构（Spark 集群中的角色）

   https://blog.csdn.net/weixin_42685589/article/details/81030438

   ![img](https://images2015.cnblogs.com/blog/1122015/201703/1122015-20170324171609033-312322432.png)
   
   ![img](https://images2015.cnblogs.com/blog/1122015/201703/1122015-20170327114818967-165213638.png)  
   
   

- Spark Cluster 和 Client 模式的区别

   yarn：https://blog.csdn.net/wyqwilliam/article/details/81123191

   ​            https://www.cnblogs.com/Transkai/p/11366049.html

   https://blog.csdn.net/wyqwilliam/article/details/81123227



- Spark 的弹性分布式数据集 RDD

   RDD，全称Resilient Distributed Dataset，弹性分布式数据集，作为Spark中最基础的数据抽象，类似Java中对象的概念；

   它代表一个不可变（只读）、可分区、里面的元素可并行计算的集合，List、Set、Map都是RDD的常见形式。
   
   特点：只读、分区、血缘、缓存、checkpoint
   
   1）、RDD五大特性：(重点)
   
   ```java
       1. RDD是由一系列的Paratition组成的。（partition个数=split切片数 约等于 block数；Spark没有读文件的方法，依赖MR读文件的方法）
       2. RDD提供的每一个算子实际上是作用在每一个Paratition上的。
       3. RDD实际上是有一系列的依赖关系的，依赖于其他的RDD。（计算的容错性；体现了RDD的弹性；父RDD不一定知道子RDD是谁，子RDD一定知道父RDD是谁）
       4. 可选：分区器作用在内部计算逻辑的返回值是kv格式的RDD上。
       5. 可选：RDD会提供一系列的最佳计算位置。（计算找数据）
  ```
  
     2）、算子
  
  ```java
      1. taransformation类算子
          map（一对一）、flatMap（一对多）、filter（一对N（0、1））、join、leftouterJoin、rightouterJoin、fullouterJoin、sortBy、sortByKey、gorupBy、groupByKey、reduceBy、reduceByKey、sample、union、mappatition、mappatitionwithindex、zip、zipWithIndex。
      2. action类算子
         count、collect（将task的计算结果拉回到Driver端）、foreach（不会回收所有task计算结果，原理：将用户传入的参数推送到各个节点上去执行，只能去计算节点找结果）、saveAsTextFile(path)、reduce、foreachPatition、take、first。
         （查看计算结果的方式：WEBUI、去各个节点的Worker工作目录查看）
      3. 控制类算子
          cache（相当于MEMOORY_ONLY）、
          persist(MEMORY_ONLY、DISK_ONLY、MEMORY_AND_DISK)
          控制类算子注意点：
          1）、控制类算子后不能紧跟action类算子
          2）、缓存单元是partition
          3）、懒执行、需要action类算子触发执行。（如果application中只有一个job，没必要使用控制类算子）
  ```



- Spark DAG（有向无环图）

   DAG(Directed Acyclic Graph)叫做有向无环图，原始的RDD通过一系列的转换就就形成了DAG，根据RDD之间的依赖关系的不同将DAG划分成不同的Stage，对于窄依赖，partition的转换处理在Stage中完成计算。对于宽依赖，由于有Shuffle的存在，只能在parent RDD处理完成后，才能开始接下来的计算，因此宽依赖是划分Stage的依据。

   https://blog.csdn.net/hu_belif/article/details/83997002
   Job：调用RDD的一个action，如count，即触发一个Job，spark中对应实现为ActiveJob，DAGScheduler中使用集合activeJobs和jobIdToActiveJob维护Job
   Stage：代表一个Job的DAG，会在发生shuffle处被切分，切分后每一个部分即为一个Stage，Stage实现分为ShuffleMapStage和ResultStage，一个Job切分的结果是0个或多个ShuffleMapStage加一个ResultStage
   Task：最终被发送到Executor执行的任务，和stage的ShuffleMapStage和ResultStage对应，其实现分为ShuffleMapTask和ResultTask

   ![img](https://img-blog.csdnimg.cn/20190529212005742.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxNTQ0NTUw,size_16,color_FFFFFF,t_70)![img](https://img-blog.csdnimg.cn/20190529212005802.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxNTQ0NTUw,size_16,color_FFFFFF,t_70)

   

- 掌握 Spark RDD 编程的算子 API（Transformation 和 Action 算子）

  

- RDD 的依赖关系，什么是宽依赖和窄依赖

   https://blog.csdn.net/u011564172/article/details/54312200 

   https://blog.csdn.net/u013384984/article/details/80963455  

  

- RDD 的血缘机制


RDD是只读的分区的数据集，对RDD进行改动只能通过RDD的转换操作来实现，多个互相转换的RDDs之间存在血缘关系，也即RDD之间的依赖，分为Narrow Dependencies（一个父RDD对应一个子RDD）和Wide Dependencies（一个父RDD对应多个子RDD）；

RDD的执行是按照血缘关系进行延时计算，血缘关系可以天然的实现数据的容错，如果数据迭代出错，可以通过血缘关系进行回溯重建；并且如果血缘关系过长，也可以通过持久化RDD来切断血缘；



- Spark 核心的运算机制

  https://www.jianshu.com/p/087992d14ae5

  

- Spark 的任务调度和资源调度

  任务调度 https://blog.csdn.net/u011564172/article/details/65653617 

   spark的资源调度和任务调度  https://www.cnblogs.com/jagel-95/p/9773925.html 

   https://blog.csdn.net/qq_33247435/article/details/83960291 

   https://blog.csdn.net/helloxiaozhe/article/details/81533438 

  

- Spark 的 CheckPoint 和容错


https://www.jianshu.com/p/582198b185ab

https://www.jianshu.com/p/2100e42ee420

（1）缓存Cache一般适用于应用程序多次需要使用同一个RDD，**（高效性）**

eg：保存到HDFS中（saveAsHadoopFile），缓存的RDD只有在第一次计算时会根据血缘关系得到分区数据，后续用到该RDD直接从缓存中取得数据而不再依据血缘关系计算，这样的好处是加速了后期的RDD重用；

因为Cache本身只是MemoryOnly，可能会随着内存释放，这样释放后数据会丢失，不安全；所以Cache并不会切断RDDs的血缘关系，如果Cache丢失还可以通过血缘关系来回溯；

对rdd加上当前时间戳并做Cache
	val cache = rdd.map(_.toString + System.currentTimeMillis).cache

​	cache.collect

（2）checkpoint是将RDD数据保存到持久化存储（eg：HDFS的节点中）中，并通过创建备份保证数据的安全性，这样就可以切断RDD之间的血缘关系，checkpoint后的RDD可以直接从checkpoint拿到数据而并不需要知道其父RDDs，checkpoint是一种常用的RDD缓存手段，相比Cache更加安全。  **安全性**

1. 先在HDFS上设置检查点路径
sc.setCheckpointDir("hdfs://hadoop100:9000/checkpoint")

2. 将rdd转化为携带当前时间戳并做checkpoint
  val ch = rdd.map(_.toString + System.currentTimeMillis)
  ch.checkpoint

  ch.collect



- Spark 的通信机制

  

- Spark Shuffle 原理和过程

  

（2）Spark Streaming：

- 原理剖析（源码级别）和运行机制
- Spark Dstream 及其 API 操作
- Spark Streaming 消费 Kafka 的两种方式
- Spark 消费 Kafka 消息的 Offset 处理
- 数据倾斜的处理方案
- Spark Streaming 的算子调优
- 并行度和广播变量
- Shuffle 调优

（3）Spark SQL：

- Spark SQL 的原理和运行机制
- Catalyst 的整体架构
- Spark SQL 的 DataFrame

Spark SQL 的优化策略：内存列式存储和内存缓存表、列存储压缩、逻辑查询优化、Join 的优化

（4）Structured Streaming

Spark 从 2.3.0 版本开始支持 Structured Streaming，它是一个建立在 Spark SQL 引擎之上可扩展且容错的流处理引擎，统一了批处理和流处理。正是 Structured Streaming 的加入使得 Spark 在统一流、批处理方面能和 Flink 分庭抗礼。

我们需要掌握：

- Structured Streaming 的模型
- Structured Streaming 的结果输出模式
- 事件时间（Event-time）和延迟数据（Late Data）
- 窗口操作
- 水印
- 容错和数据恢复

Spark Mlib：

本部分是 Spark 对机器学习支持的部分，我们学有余力的同学可以了解一下 Spark 对常用的分类、回归、聚类、协同过滤、降维以及底层的优化原语等算法和工具。可以尝试自己使用 Spark Mlib 做一些简单的算法应用。



spark推测执行机制：



### Flink

Apache Flink（以下简称 Flink）项目是大数据处理领域最近冉冉升起的一颗新星，其不同于其他大数据项目的诸多特性吸引了越来越多人的关注。尤其是 2019 年初 Blink 开源将 Flink 的关注度提升到了前所未有的程度。

那么关于 Flink 这个框架我们应该掌握哪些核心知识点？

- Flink 集群的搭建
- Flink 的架构原理
- Flink 的编程模型
- Flink 集群的 HA 配置
- Flink DataSet 和 DataSteam API
- 序列化
- Flink 累加器
- 状态 State 的管理和恢复
- 窗口和时间
- 并行度
- Flink 和消息中间件 Kafka 的结合
- Flink Table 和 SQL 的原理和用法

另外这里重点讲一下，阿里巴巴 Blink 对 SQL 的支持，在阿里云官网上可以看到，Blink 部分最引以为傲的就是对 SQL 的支持，那么 SQL 中最常见的两个问题：1.双流 JOIN 问题，2.State 失效问题也是我们关注的重点。

### 大数据算法

本部分的算法包含两个部分。第一部分是：面试中针对大数据处理的常用算法题；第二部分是：常用的机器学习和数据挖掘算法。

我们重点讲第一部分，第二部分我们学有余力的同学可以去接触一些，在面试的过程中也可以算是一个亮点。

常见的大数据算法问题：

1. 两个超大文件找共同出现的单词
2. 海量数据求 TopN
3. 海量数据找出不重复的数据
4. 布隆过滤器
5. bit-map
6. 堆
7. 字典树
8. 倒排索引
