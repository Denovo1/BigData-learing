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

###### （1）Spark Core：

- Spark的集群搭建和集群架构（Spark 集群中的角色）

  https://blog.csdn.net/weixin_42685589/article/details/81030438 

   ![img](https://images2015.cnblogs.com/blog/1122015/201703/1122015-20170324171609033-312322432.png) 

   ![img](https://images2015.cnblogs.com/blog/1122015/201703/1122015-20170327114818967-165213638.png) 

  

- Spark Cluster 和 Client 模式的区别

  yarn：https://blog.csdn.net/wyqwilliam/article/details/81123191

  ​            https://www.cnblogs.com/Transkai/p/11366049.html

  https://blog.csdn.net/wyqwilliam/article/details/81123227



- Spark 的弹性分布式数据集 RDD

  1）、RDD五大特性：(重点)

          1. RDD是由一系列的Paratition组成的。（partition个数=split切片数 约等于 block数；Spark没有读文件的方法，依赖MR读文件的方法）
          2. RDD提供的每一个算子实际上是作用在每一个Paratition上的。
          3. RDD实际上是有一系列的依赖关系的，依赖于其他的RDD。（计算的容错性；体现了RDD的弹性；父RDD不一定知道子RDD是谁，子RDD一定知道父RDD是谁）
          4. 可选：分区器作用在内部计算逻辑的返回值是kv格式的RDD上。
          5. 可选：优先选择本地最优的位置去计算每个分片（例如，HDFS文件块位置）即数据的本地性

     2）、算子

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

RDD，全称Resilient Distributed Dataset，弹性分布式数据集，作为Spark中最基础的数据抽象，类似Java中对象的概念；

它代表一个不可变（只读）、可分区、里面的元素可并行计算的集合，List、Set、Map都是RDD的常见形式。

特点：只读、分区、血缘、缓存、checkpoint



- Spark DAG（有向无环图）

  DAG(Directed Acyclic Graph)叫做有向无环图，原始的RDD通过一系列的转换就就形成了DAG，根据RDD之间的依赖关系的不同将DAG划分成不同的Stage，对于窄依赖，partition的转换处理在Stage中完成计算。对于宽依赖，由于有Shuffle的存在，只能在parent RDD处理完成后，才能开始接下来的计算，因此宽依赖是划分Stage的依据。

  https://blog.csdn.net/hu_belif/article/details/83997002
  Job：调用RDD的一个action，如count，即触发一个Job，spark中对应实现为ActiveJob，DAGScheduler中使用集合activeJobs和jobIdToActiveJob维护Job
  Stage：代表一个Job的DAG，会在发生shuffle处被切分，切分后每一个部分即为一个Stage，Stage实现分为ShuffleMapStage和ResultStage，一个Job切分的结果是0个或多个ShuffleMapStage加一个ResultStage
  Task：最终被发送到Executor执行的任务，和stage的ShuffleMapStage和ResultStage对应，其实现分为ShuffleMapTask和ResultTask

  ![img](https://img-blog.csdnimg.cn/20190529212005742.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxNTQ0NTUw,size_16,color_FFFFFF,t_70)![img](https://img-blog.csdnimg.cn/20190529212005802.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxNTQ0NTUw,size_16,color_FFFFFF,t_70)

  

- 掌握 Spark RDD 编程的算子 API（Transformation 和 Action 算子）

https://blog.csdn.net/ljp812184246/article/details/53895299

https://blog.csdn.net/qq_29763559/article/details/88530228

https://blog.csdn.net/qq_19316267/article/details/80937181

https://blog.csdn.net/Fortuna_i/article/details/81170565



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

  https://blog.csdn.net/chenguangchun1993/article/details/78732929

  https://blog.csdn.net/hebaojing/article/details/87906596

  ###### Spark启动消息通信

  1. 当master启动之后再启动worker，worker会自动建立通信环境RpcEnv和EndPoint，并且worker会向master发送注册信息RegisterWorker，master处理完毕后返回注册成功或者是失败的消息。HA条件下，worker可能需要注册到多个master上。在worker的tryRegisterAllMasters方法中创建注册线程池RegisterMasterThreadPool，把需要注册的请求放入这个线程池，通过此线程池启动注册线程。注册过程中，（注册线程）获取master EndPoint引用，（注册线程）调用RegisterWithMaster方法，根据master EndPoint引用的send方法给master发送注册消息RegisterWorker。

  2. master接到注册消息RegisterWorker后，master会对worker发送的消息进行验证，记录。如果注册成功，master发送registeredWorker给对应的worker，告诉worker已经完成注册，进行第3步骤，即worker定期发送心跳给master；如果注册失败，master会给worker发送registerWorkerFailed消息，worker会打印日志并结束worker的启动。master接到worker注册消息后，master先判断自己当前状态是否是STANDBY，如果是则忽略该消息；如果在注册列表发现了该worker编号，master则对worker发送注册失败的消息。判断完毕无误之后，调用registerWorker方法将此worker加入到master自己的列表，用于后续任务调度使用。

  3. worker收到注册成功的消息后，会定时发送心跳给master，间隔时间是spark.，方便master了解Worker状态。（注意timeout的四分之一才是心跳间隔哦）

     ![img](https://img-blog.csdnimg.cn/20190224204334728.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2hlYmFvamluZw==,size_16,color_FFFFFF,t_70)

Spark运行时消息通信

应用程序SparkContext向mater发送注册信息，并由master为该应用分配Exectour，exectour启动之后会向SparkContext发送注册成功消息，然SparkContext的rdd触发Action之后会形成一个DAG，通过DAGScheduler进行划分Stage并将其转化成TaskSet，然后TaskScheduler向Executor发送执行消息，Executor接收到信息之后启动并且运行，最后是由Driver处理结果并回收资源。

详细过程如下：

​	1.启动SparkContext，启动SparkContext时会实例化一个Schedulerbackend对象，standalone模式下实际创建的是SparkDeploySchedulerbackend对象，在这个对象启动中会继承父类DriverEndpoint和ClientEndpoint这两个endpoint。ClientEndpoint的tryRegisterAllMasters方法中创建注册线程池RegisterMasterThreadPool，在该线程池中启动注册线程并向master发送registerApplication注册应用的消息。master接收到注册应用的消息，在registerApplication方法中记录应用信息，并将该应用加入等待运行应用列表，注册完毕后master发送成功消息RegisteredApplication给ClientEndPoint，同时调用startExecutorOnworker方法运行应用。

2. ClientEndpoint接收到Master发送的RegisteredApplication消息，需要把注册标识registered置为true。
3. 在Master类的starExecutorsOnWorkers方法中分配资源运行应用程序时，调用allocateWorkerResourceToExecutors方法实现在Worker中启动Executor。当Worker收到Master发送过来的LaunchExecutor消息，先实例化ExecutorRunner对象，在ExecutorRunner启动中会创建进程生成器ProcessBuilder, 然后由该生成器使用command创建CoarseGrainedExecutorBackend对象，该对象是Executor运行的容器，最后Worker发送ExecutorStateChanged消息给Master，通知Executor容器【CoarseGrainedExecutorBackend】已经创建完毕。
4. Master接收到Worker发送的ExecutorStateChanged消息
5. 在3中的CoarseGrainedExecutorBackend启动方法onStart中，会发送注册Executor消息RegisterExecutor给DriverEndpoint，DriverEndpoint先判断该Executor是否已经注册。
6. CoarseGrainedExecutorBackend接收到Executor注册成功RegisteredExecutor消息时，在CoarseGrainedExecutorBackend容器中实例化Executor对象。启动完毕后，会定时向Driver发送心跳信息, 等待接收从DriverEndpoint发送执行任务的消息。
7. CoarseGrainedExecutorBackend的Executor启动后接收从DriverEndpoint发送的LaunchTask执行任务消息，任务执行是在Executor的launchTask方法实现的。在执行时会创建TaskRunner进程，由该进程进行任务处理，处理完毕后发送StateUpdate消息返回给CoarseGrainedExecutorBackend。
8. 在TaskRunner执行任务完成时，会向DriverEndpoint发送StatusUpdate消息，DriverEndpoint接收到消息会调用TaskSchedulerImpl的statusUpdate方法，根据任务执行不同的结果处理，处理完毕后再给该Executor分配执行任务。

![img](https://img-blog.csdnimg.cn/20190224225143646.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2hlYmFvamluZw==,size_16,color_FFFFFF,t_70)



- Spark Shuffle 原理和过程


https://www.cnblogs.com/itboys/p/9226479.html

Hash Shuffle：https://blog.csdn.net/u011564172/article/details/71170234、

Sort Shuffle（SortShuffleWriter）：https://blog.csdn.net/u011564172/article/details/72763978

tungsten-sort（UnsafeShuffleWriter）：https://blog.csdn.net/u011564172/article/details/72764010

没有map端聚合操作，且RDD的Partition数小于200，使用BypassMergeSortShuffleWriter。

![img](https://img-blog.csdn.net/20170527173819501?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMTU2NDE3Mg==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

没有map端聚合操作，RDD的Partition数小于16777216，且Serializer支持relocation，使用UnsafeShuffleWriter。
上述条件都不满足，使用SortShuffleWriter。



###### （2）Spark Streaming：

- 原理剖析（源码级别）和运行机制

**原理剖析**：

![Spark Streaming原理剖析_看图王](D:\Java\大数据\知识星球\SparkStormHadoop\spark\Spark Streaming原理剖析_看图王.png)

**运行机制**：客户端提交作业后启动Driver，Driver是spark作业的Master。
每个作业包含多个Executor，每个Executor以线程的方式运行task，Spark Streaming至少包含一个receiver task。
Receiver接收数据后生成Block，并把BlockId汇报给Driver，然后备份到另外一个Executor上。
ReceiverTracker维护Reciver汇报的BlockId。
Driver定时启动JobGenerator，根据Dstream的关系生成逻辑RDD，然后创建Jobset，交给JobScheduler。
JobScheduler负责调度Jobset，交给DAGScheduler，DAGScheduler根据逻辑RDD，生成相应的Stages，每个stage包含一到多个task。
TaskScheduler负责把task调度到Executor上，并维护task的运行状态。
当tasks，stages，jobset完成后，单个batch才算完成。



- Spark Dstream 及其 API 操作

Discretized Stream是Spark Streaming的基础抽象，代表持续性的数据流和经过各种Spark原语操作后的结果数据流。在内部实现上，DStream是一系列连续的RDD来表示。每个RDD含有一段时间间隔内的数据，如下图：

![image-20200109153727127](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20200109153727127.png)

对数据的操作也是按照RDD为单位来进行的

![image-20200109153829006](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20200109153829006.png)

计算过程由Spark engine来完成

![image-20200109153840874](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20200109153840874.png)

https://github.com/xubo245/SparkLearning/tree/master/SparkLearning1/src/main/scala/org/apache/spark/Streaming/learning

NetworkWordCount：https://blog.csdn.net/xubo245/article/details/51251970，https://blog.csdn.net/stark_summer/article/details/49251709

StatefulNetworkWordCount：https://blog.csdn.net/xubo245/article/details/51252142，https://blog.csdn.net/qq_36764089/article/details/79333605

SqlNetworkWordCount：https://blog.csdn.net/xubo245/article/details/51252229

HdfsWordCount：https://blog.csdn.net/xubo245/article/details/51254412

WindowsWordCount：https://blog.csdn.net/xubo245/article/details/51254839



- Spark Streaming 消费 Kafka 的两种方式

-1,基于接收者的方法

算子：KafkaUtils.createStream 
方法：PUSH，从topic中去推送数据，将数据推送过来 
API：调用的Kafka高级API 
效果：SparkStreaming中的Receivers，恰好Kafka有发布/订阅 ，然而：此种方式企业不常用，说明有BUG，不符合企业需求。因为：接收到的数据存储在Executor的内存，会出现数据漏处理或者多处理状况 
解释：这种方法使用Receiver来接收数据。Receiver是使用Kafka高级消费者API实现的。与所有的接收者一样，通过Receiver从Kafka接收的数据存储在Spark执行程序exector中，然后由Spark Streaming启动的作业处理数据。但是，在默认配置下，这种方法可能会在失败时丢失数据。为了确保零数据丢失，您必须在Spark Streaming中额外启用写入日志，同时保存所有接收到的Kafka数据写入分布式文件系统（例如HDFS）的预先写入日志，以便所有数据都可以在失败时恢复。 
缺点： 
①、Kafka中的主题分区与Spark Streaming中生成的RDD的分区不相关。因此，增加主题特定分区KafkaUtils.createStream()的数量只会增加在单个接收器中使用哪些主题消耗的线程的数量。在处理数据时不会增加Spark的并行性 
②、多个kafka输入到DStream会创建多个group和topic，用于使用多个接收器并行接收数据 
③、如果已经使用HDFS等复制文件系统启用了写入日志，则接收到的数据已经在日志中复制。因此，输入流的存储级别为存储级别StorageLevel.MEMORY_AND_DISK_SER

-2,直接方法（无接收者）

算子：KafkaUtils.createDirectStream 
方式：PULL，到topic中去拉取数据。 
API：kafka低级API 
效果：每次到Topic的每个分区依据偏移量进行获取数据，拉取数据以后进行处理，可以实现高可用 
解释：在Spark 1.3中引入了这种新的无接收器“直接”方法，以确保更强大的端到端保证。这种方法不是使用接收器来接收数据，而是定期查询Kafka在每个topic+分partition中的最新偏移量，并相应地定义要在每个批次中处理的偏移量范围。当处理数据的作业启动时，Kafka简单的客户API用于读取Kafka中定义的偏移范围（类似于从文件系统读取文件）。请注意，此功能在Spark 1.3中为Scala和Java API引入，在Spark 1.4中针对Python API引入。 
优势： 
①、简化的并行性：不需要创建多个输入Kafka流并将其合并。与此同时directStream，Spark Streaming将创建与使用Kafka分区一样多的RDD分区，这些分区将全部从Kafka并行读取数据。所以在Kafka和RDD分区之间有一对一的映射关系，这更容易理解和调整。

②、效率：在第一种方法中实现零数据丢失需要将数据存储在预写日志中，这会进一步复制数据。这实际上是效率低下的，因为数据被有效地复制了两次，一次是由Kafka，另一次是由预先写入日志（Write Ahead Log）复制。此方法消除了这个问题，因为没有接收器，因此不需要预先写入日志。只要你有足够的kafka保留，消息可以从kafka恢复。

③、精确语义：第一种方法是使用Kafka的高级API在Zookeeper中存储消耗的偏移量。传统上这是从Kafka消费数据的方式。虽然这种方法（合并日志）可以确保零数据丢失，但在某些失败情况下，很小的几率两次数据都同时丢失，发生这种情况是因为Spark Streaming可靠接收到的数据与Zookeeper跟踪的偏移之间的不一致。因此，在第二种方法中，我们使用不使用Zookeeper的简单Kafka API。在其检查点内，Spark Streaming跟踪偏移量。这消除了Spark Streaming和Zookeeper / Kafka之间的不一致性，因此Spark Streaming每次记录都会在发生故障时有效地接收一次。

请注意，这种方法的一个缺点是它不会更新Zookeeper中的偏移量，因此基于Zookeeper的Kafka监控工具将不会显示进度。但是，您可以在每个批次中访问由此方法处理的偏移量，并自己更新Zookeeper



- Spark 消费 Kafka 消息的 Offset 处理

[https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/%E5%A4%A7%E6%95%B0%E6%8D%AE%E6%A1%86%E6%9E%B6%E5%AD%A6%E4%B9%A0/Spark_Streaming%E6%95%B4%E5%90%88Kafka.md](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/大数据框架学习/Spark_Streaming整合Kafka.md)



- 数据倾斜的处理方案

1. [数据倾斜解决方案之原理以及现象分析.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\数据倾斜解决方案之原理以及现象分析.xls) 

2. [数据倾斜解决方案之聚合源数据.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\数据倾斜解决方案之聚合源数据.xls) (使用hive先进行聚合或者**map 端先局部聚合**，过滤异常数据)

   在 map 端加个 combiner 函数进行局部聚合。加上 combiner 相当于提前进行 reduce ,就会把一个 mapper 中的相同 key 进行聚合，减少 shuffle 过程中数据量 以及 reduce 端的计算量。这种方法可以有效的缓解数据倾斜问题，但是如果导致数据倾斜的 key 大量分布在不同的 mapper 的时候，这种方法就不是很有效了。

   TIPS 使用 reduceByKey 而不是 groupByKey。

3. [数据倾斜解决方案之提高shuffle操作reduce并行度.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\数据倾斜解决方案之提高shuffle操作reduce并行度.xls) （提高 shuffle 并行度）

RDD 操作 可在需要 Shuffle 的操作算子上直接设置并行度或者使用 spark.default.parallelism 设置。如果是 Spark SQL，还可通过 SET spark.sql.shuffle.partitions=[num_tasks] 设置并行度。默认参数由不同的 Cluster Manager 控制。

dataFrame 和 sparkSql 可以设置 spark.sql.shuffle.partitions=[num_tasks] 参数控制 shuffle 的并发度，默认为200。

（2）适用场景

大量不同的 Key 被分配到了相同的 Task 造成该 Task 数据量过大。

（3）解决方案

调整并行度。一般是增大并行度，但有时如减小并行度也可达到效果。

（4）优势

实现简单，只需要参数调优。可用最小的代价解决问题。一般如果出现数据倾斜，都可以通过这种方法先试验几次，如果问题未解决，再尝试其它方法。

（5）劣势

适用场景少，只是让每个 task 执行更少的不同的key。无法解决个别key特别大的情况造成的倾斜，如果某些 key 的大小非常大，即使一个 task 单独执行它，也会受到数据倾斜的困扰。并且该方法一般只能缓解数据倾斜，没有彻底消除问题。从实践经验来看，其效果一般。

TIPS 可以把数据倾斜类比为 hash 冲突。提高并行度就类似于 提高 hash 表的大小。

4. [数据倾斜解决方案之使用随机key实现双重聚合.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\数据倾斜解决方案之使用随机key实现双重聚合.xls) (加盐局部聚合 + 去盐全局聚合)

这个方案的核心实现思路就是进行两阶段聚合。第一次是**局部聚合**，先给每个 key 都打上一个 1~n 的随机数，比如 3 以内的随机数，此时原先一样的 key 就变成不一样的了，比如 (hello, 1) (hello, 1) (hello, 1) (hello, 1) (hello, 1)，就会变成 (1_hello, 1) (3_hello, 1) (2_hello, 1) (1_hello, 1) (2_hello, 1)。接着对打上随机数后的数据，执行 reduceByKey 等聚合操作，进行局部聚合，那么局部聚合结果，就会变成了 (1_hello, 2) (2_hello, 2) (3_hello, 1)。然后将各个 key 的前缀给去掉，就会变成 (hello, 2) (hello, 2) (hello, 1)，再次进行**全局聚合**操作，就可以得到最终结果了，比如 (hello, 5)。

```scala
def antiSkew(): RDD[(String, Int)] = {    
    val SPLIT = "-"    
    val prefix = new Random().nextInt(10)    
    pairs.map(t => ( prefix + SPLIT + t._1, 1))        
    .reduceByKey((v1, v2) => v1 + v2)        
    .map(t => (t._1.split(SPLIT)(1), t2._2))        
    .reduceByKey((v1, v2) => v1 + v2)
}
不过进行两次 mapreduce，性能稍微比一次的差些。
```

5. [数据倾斜解决方案之将reduce join转换为map join.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\数据倾斜解决方案之将reduce join转换为map join.xls) 

（1）适用场景

参与Join的一边数据集足够小，可被加载进 Driver 并通过 Broadcast 方法广播到各个 Executor 中。

（2）解决方案

在 Java/Scala 代码中将小数据集数据拉取到 Driver，然后通过 Broadcast 方案将小数据集的数据广播到各 Executor。或者在使用 SQL 前，将 Broadcast 的阈值调整得足够大，从而使 Broadcast 生效。进而将 Reduce Join 替换为 Map Join。

（3）优势

避免了 Shuffle，彻底消除了数据倾斜产生的条件，可极大提升性能。

（4）劣势

因为是先将小数据通过 Broadcase 发送到每个 executor 上，所以需要参与 Join 的一方数据集足够小，并且主要适用于 Join 的场景，不适合聚合的场景，适用条件有限。

NOTES：使用Spark SQL时需要通过 SET spark.sql.autoBroadcastJoinThreshold=104857600 将 Broadcast 的阈值设置得足够大，才会生效。

6. [数据倾斜解决方案之sample采样倾斜key进行两次join.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\数据倾斜解决方案之sample采样倾斜key进行两次join.xls) （拆分 join 再 union）

将一个 join 拆分成 倾斜数据集 Join 和 非倾斜数据集 Join，最后进行 union:

1. 对包含少数几个数据量过大的 key 的那个 RDD (假设是 leftRDD)，通过 sample 算子采样出一份样本来，然后统计一下每个 key 的数量，计算出来数据量最大的是哪几个 key。具体方法上面已经介绍过了，这里不赘述。
2. 然后将这 k 个 key 对应的数据从 leftRDD 中单独过滤出来，并给每个 key 都打上 1~n 以内的随机数作为前缀，形成一个单独的 leftSkewRDD；而不会导致倾斜的大部分 key 形成另外一个 leftUnSkewRDD。
3. 接着将需要 join 的另一个 rightRDD，也过滤出来那几个倾斜 key 并通过 flatMap 操作将该数据集中每条数据均转换为 n 条数据（这 n 条数据都按顺序附加一个 0~n 的前缀），形成单独的 rightSkewRDD；不会导致倾斜的大部分 key 也形成另外一个 rightUnSkewRDD。
4. 现在将 leftSkewRDD 与 膨胀 n 倍的 rightSkewRDD 进行 join，且在 Join 过程中将随机前缀去掉，得到倾斜数据集的 Join 结果 skewedJoinRDD。注意到此时我们已经成功将原先相同的 key 打散成 n 份，分散到多个 task 中去进行 join 了。
5. 对 leftUnSkewRDD 与 rightUnRDD 进行Join，得到 Join 结果 unskewedJoinRDD。
6. 通过 union 算子将 skewedJoinRDD 与 unskewedJoinRDD 进行合并，从而得到完整的 Join 结果集。

（1）适用场景

两张表都比较大，无法使用 Map 端 Join。其中一个 RDD 有少数几个 Key 的数据量过大，另外一个 RDD 的 Key 分布较为均匀。

（2）解决方案

将有数据倾斜的 RDD 中倾斜 Key 对应的数据集单独抽取出来加上随机前缀，另外一个 RDD 每条数据分别与随机前缀结合形成新的RDD（相当于将其数据增到到原来的N倍，N即为随机前缀的总个数），然后将二者Join并去掉前缀。然后将不包含倾斜Key的剩余数据进行Join。最后将两次Join的结果集通过union合并，即可得到全部Join结果。

（3）优势

相对于 Map 则 Join，更能适应大数据集的 Join。如果资源充足，倾斜部分数据集与非倾斜部分数据集可并行进行，效率提升明显。且只针对倾斜部分的数据做数据扩展，增加的资源消耗有限。

（4）劣势

如果倾斜 Key 非常多，则另一侧数据膨胀非常大，此方案不适用。而且此时对倾斜 Key 与非倾斜 Key 分开处理，需要扫描数据集两遍，增加了开销。

7. [数据倾斜解决方案之使用随机数以及扩容表进行join.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\数据倾斜解决方案之使用随机数以及扩容表进行join.xls) （大表 key 加盐，小表扩大 N 倍 jion）

如果出现数据倾斜的 Key 比较多，上一种方法将这些大量的倾斜 Key 分拆出来，意义不大。此时更适合直接对存在数据倾斜的数据集全部加上随机前缀，然后对另外一个不存在严重数据倾斜的数据集整体与随机前缀集作笛卡尔乘积（即将数据量扩大N倍）。

其实就是上一个方法的特例或者简化。少了拆分，也就没有 union。

（1）适用场景

一个数据集存在的倾斜 Key 比较多，另外一个数据集数据分布比较均匀。

（2）优势

对大部分场景都适用，效果不错。

（3）劣势

需要将一个数据集整体扩大 N 倍，会增加资源消耗。

​	**8.自定义 Partitioner**

（1）原理

使用自定义的 Partitioner（默认为 HashPartitioner），将原本被分配到同一个 Task 的不同 Key 分配到不同 Task。

例如，我们在 groupByKey 算子上，使用自定义的 Partitioner:

```scala
.groupByKey(new Partitioner() {  
    @Override  
    public int numPartitions() {    
        return 12;  
    }   
    @Override  
    public int getPartition(Object key) {    
        int id = Integer.parseInt(key.toString());    
        if(id >= 9500000 && id <= 9500084 && ((id - 9500000) % 12) == 0) {     
            return (id - 9500000) / 12;    
        } else {    
            return id % 12;  
        }  
    }
})
```

TIPS 这个做法相当于自定义 hash 表的 哈希函数。

（2）适用场景

大量不同的 Key 被分配到了相同的 Task 造成该 Task 数据量过大。

（3）解决方案

使用自定义的 Partitioner 实现类代替默认的 HashPartitioner，尽量将所有不同的 Key 均匀分配到不同的 Task 中。

（4）优势

不影响原有的并行度设计。如果改变并行度，后续 Stage 的并行度也会默认改变，可能会影响后续 Stage。

（5）劣势

适用场景有限，只能将不同 Key 分散开，对于同一 Key 对应数据集非常大的场景不适用。效果与调整并行度类似，只能缓解数据倾斜而不能完全消除数据倾斜。而且需要根据数据特点自定义专用的 Partitioner，不够灵活。

https://blog.csdn.net/qq_38247150/article/details/80366769

https://mp.weixin.qq.com/s/piW10KGJVgaSB_i72OVntA



- Spark Streaming 的算子调优 

  [算子调优之filter过后使用coalesce减少分区数量.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\算子调优之filter过后使用coalesce减少分区数量.xls) 

   [算子调优之MapPartitions提升Map类操作性能.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\算子调优之MapPartitions提升Map类操作性能.xls) 

   [算子调优之reduceByKey本地聚合介绍.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\算子调优之reduceByKey本地聚合介绍.xls) 

   [算子调优之使用foreachPartition优化写数据库性能.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\算子调优之使用foreachPartition优化写数据库性能.xls) 

   [算子调优之使用repartition解决Spark SQL低并行度的性能问题.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\算子调优之使用repartition解决Spark SQL低并行度的性能问题.xls) 

- 并行度和广播变量

 [性能调优之在实际项目中广播大变量.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\性能调优之在实际项目中广播大变量.xls) 

 [性能调优之在实际项目中调节并行度.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\性能调优之在实际项目中调节并行度.xls) 

（ [性能调优之在实际项目中分配更多资源.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\性能调优之在实际项目中分配更多资源.xls) ， [性能调优之在实际项目中使用fastutil优化数据格式.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\性能调优之在实际项目中使用fastutil优化数据格式.xls) ， [性能调优之在实际项目中使用Kryo序列化.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\性能调优之在实际项目中使用Kryo序列化.xls) ， [性能调优之在实际项目中调节数据本地化等待时长.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\性能调优之在实际项目中调节数据本地化等待时长.xls)  ，[性能调优之在实际项目中重构RDD架构以及RDD持久化.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\性能调优之在实际项目中重构RDD架构以及RDD持久化.xls) ）

- Shuffle 调优

 [Shuffle调优之原理概述.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\Shuffle调优之原理概述.xls) 

 [Shuffle调优之HashShuffleManager与SortShuffleManager.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\Shuffle调优之HashShuffleManager与SortShuffleManager.xls) 

 [Shuffle调优之合并map端输出文件.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\Shuffle调优之合并map端输出文件.xls) 

 [Shuffle调优之调节map端内存缓冲与reduce端内存占比.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\Shuffle调优之调节map端内存缓冲与reduce端内存占比.xls) 



 [JVM调优之调节executor堆外内存与连接等待时长.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\JVM调优之调节executor堆外内存与连接等待时长.xls) 

 [JVM调优之原理概述以及降低cache操作的内存占比.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\JVM调优之原理概述以及降低cache操作的内存占比.xls) 



 [troubleshooting之错误的持久化方式以及checkpoint的使用.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\troubleshooting之错误的持久化方式以及checkpoint的使用.xls) 

 [troubleshooting之解决JVM GC导致的shuffle文件拉取失败.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\troubleshooting之解决JVM GC导致的shuffle文件拉取失败.xls) 

 [troubleshooting之解决yarn-client模式导致的网卡流量激增问题.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\troubleshooting之解决yarn-client模式导致的网卡流量激增问题.xls) 

 [troubleshooting之解决yarn-cluster模式的JVM内存溢出无法执行问题.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\troubleshooting之解决yarn-cluster模式的JVM内存溢出无法执行问题.xls) 

 [troubleshooting之解决YARN队列资源不足导致的application直接失败.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\troubleshooting之解决YARN队列资源不足导致的application直接失败.xls) 

 [troubleshooting之解决各种序列化导致的报错.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\troubleshooting之解决各种序列化导致的报错.xls) 

 [troubleshooting之解决算子函数返回NULL导致的问题.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\troubleshooting之解决算子函数返回NULL导致的问题.xls) 

 [troubleshooting之控制shuffle reduce端缓冲大小以避免OOM.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\troubleshooting之控制shuffle reduce端缓冲大小以避免OOM.xls) 



###### （3）Spark SQL：

- Spark SQL 的原理和运行机制

https://blog.csdn.net/accptanggang/article/details/79518643

https://blog.csdn.net/Mirror_w/article/details/90729036

Spark SQL 架构中主要有这几个关键的组件：SqlParser(Sql分析程序) ，Analyser(分析器) ，Optimizer（优化器） ，SparkPlan（Spark计划）
SparkSQL大致的执行流程是这样的：

1. SQL 语句经过SqlParser 完成sql 语句的语法解析功能，解析成Unresolved LogicalPlan（未解析的逻辑计划）;
2. 使用分析器Analyser将不同来源的Unresolved LogicalPlan和元数据（如hive metastore、Schema catalog）进行绑定，生成Resolved LogicalPlan（解析后的逻辑计划）;
3. 使用优化器Optimizer 对Resolved LogicalPlan 进行优化，生成Optimized LogicalPlan（优化后的逻辑计划）;
4. 使用SparkPlan 将LogicalPlan（逻辑计划） 转换成PhysicalPlan（物理计划）;
5. 使用prepareForExecution() 将PhysicalPlan转换成可执行物理计划;
6. 使用execute() 执行可执行物理计划，生成SchemaRDD 即Dataset或DataFrame。
   具体流程如下图所示：

![img](https://img-blog.csdn.net/20180311182747101?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvYWNjcHRhbmdnYW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)



- Catalyst 的整体架构

![img](https://img-blog.csdnimg.cn/20181212221509667.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTIxMzc0NzM=,size_16,color_FFFFFF,t_70)

catalyst是spark sql的调度核心，遵循传统数据库查询解析步骤，对sql进行解析，转换为逻辑查询计划，物理查询计划，最终转化为Spark的DAG后在执行，下图为Catalyst的执行流程。

<img src="https://img-blog.csdnimg.cn/20181212223048264.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTIxMzc0NzM=,size_16,color_FFFFFF,t_70" alt="img" style="zoom: 50%;" />

SqlParser将SQL语句被解析为语法树(AST)，也就是未解析的逻辑查询计划。Parser简单来说是将SQL字符串切分成一个一个Token，再根据一定语义规则解析为一棵语法树。(ANTLR实现)
Analyzer对逻辑查询计划进行属性和关系关联检验，也就是通过定义的一系列规则将未解析的逻辑查询计划借助catalog去解析，如将之前提到的未解析的逻辑查询计划转换成逻辑查询计划。（再次遍历整个语法树，对树上的每个节点进行数据类型绑定以及函数绑定）
Optimizer通过逻辑查询优化将逻辑查询计划转化为优化的逻辑查询计划,优化器是整个Catalyst的核心。下面一篇文章会详细介绍。包括谓词下推（Predicate Pushdown）、常量累加（Constant Folding）和列值裁剪（Column Pruning）。
QueryPlanner将逻辑查询计划转换为物理查询计划
prepareForExecution调整数据分布，最后将物理查询计划转换为执行计划进入Spark执行任务



- Spark SQL 的 DataFrame

DataFrame 和 RDDs 最主要的区别在于一个面向的是结构化数据，一个面向的是非结构化数据，它们内部的数据结构如下：

<div align="center"> <img src="D:\Java\大数据\God-Of-BigData-master\pictures\spark-dataFrame+RDDs.png"/> </div>

DataFrame 内部的有明确 Scheme 结构，即列名、列字段类型都是已知的，这带来的好处是可以减少数据读取以及更好地优化执行计划，从而保证查询效率。

**DataFrame 和 RDDs 应该如何选择？**

+ 如果你想使用函数式编程而不是 DataFrame API，则使用 RDDs；
+ 如果你的数据是非结构化的 (比如流媒体或者字符流)，则使用 RDDs，
+ 如果你的数据是结构化的 (如 RDBMS 中的数据) 或者半结构化的 (如日志)，出于性能上的考虑，应优先使用 DataFrame。

**Spark SQL 的优化策略：内存列式存储和内存缓存表、列存储压缩、逻辑查询优化、Join 的优化**

内存列式存储与内存缓存表
Spark SQL通过cacheTable将数据存储转换为列式存储，同时将数据加载到内存进行缓存。cacheTable相当于在分布式集群的内存物化试图，将数据进行缓存，这样迭代的或者交互的查询不用在从HDFS读数据，直接从内存读取数据大大减少了I/O开销。列式存储的优势在于Spark SQL只要读取用户需要的列，而不需要想行存储那样需要每次把所有的列读取出来，从而减少了内存缓存的数据量，更高效地利用内存缓存数据，同时减少网络传输和I/O开销。数据按照列式存储，由于是数据类型相同的数据连续存储，能够利用序列化和压缩减少内存的空间占用。

列式存储压缩
为了减少内存和磁盘空间占用Spark SQL采用了一些压缩策略对内存列存储数据进行压缩。Spark SQL支持PassThrough,RunLengthEncoding,IntDelta等多种压缩方式，这样能大幅度减少内存占用，网络开销和I/O开销

逻辑查询优化
Spark SQL在逻辑查询优化上支持列剪枝，谓词下压，属性合并等逻辑查询优化方法。列剪枝为了减少不必要的列属性，减少数据传输和计算开销，在查询优化器进行转换的时候会进行列剪枝优化，如下图

![img](https://img-blog.csdnimg.cn/20181213230602246.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTIxMzc0NzM=,size_16,color_FFFFFF,t_70)

例: SELECT Class FROM (SELECT ID, NAME, Class FROM STUDENT) S WHERE S.ID=1
Catalyst将原来的查询通过谓词下压，选择ID=1先执行，这样过滤掉了大部分数据，通过属性合并将最后的投影只做一次最终保留Class属性

Join优化
Spark SQL借鉴了传统数据库查询优化的精髓，并在分布式环境下进行特定的优化策略调整和创新。Spark SQL对join进行了优化并支持多种连接算法，例如: BroadcastHashJoin,BroadcastNestedLoopJoin,HashJoin,LeftSemiJoin等

BroadcastHashJoin将小表转换为广播变量进行广播，避免shuffle开销，最后在分区内进行Hash连接。这里用的就是hive中的Map Side Join 思想。同时用了DBMS中的Hash连接算法做连接。



###### （4）Structured Streaming

Spark 从 2.3.0 版本开始支持 Structured Streaming，它是一个建立在 Spark SQL 引擎之上可扩展且容错的流处理引擎，统一了批处理和流处理。正是 Structured Streaming 的加入使得 Spark 在统一流、批处理方面能和 Flink 分庭抗礼。

我们需要掌握：

- Structured Streaming 的模型

如果把输入流看作是“Input Table”，那么流中到达的每个数据元素都像是添加到 Input Table中的一行新数据。

![img](https://img-blog.csdnimg.cn/20181102170354149.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01hdGhpZXU2Ng==,size_16,color_FFFFFF,t_70)

 输入表上的查询会产生一个 “Result Table”。每个触发间隔中（假设1s一次），新的行都会被追加到 Input Table,最终会更新 Result Table。每当result table有更新时，我们会想把更新后的结果行写出到外部sink.

![img](https://img-blog.csdnimg.cn/20181102170900210.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01hdGhpZXU2Ng==,size_16,color_FFFFFF,t_70)



- Structured Streaming 的结果输出模式

 图中的 Output代表输出到外部存储的数据。这个输出有三种模式：

Append模式：只有自上次触发后在Result Table表中附加的新行将被写入外部存储器。重点模式，一般使用它。

Complete模式： 将整个更新表写入到外部存储。每次batch触发计算，整张Result Table的结果都会被写出到外部存储介质。

Update模式：只有自上次触发后在Result Table表中更新的行将被写入外部存储器。注意，这与完全模式不同，因为此模式不输出未更改的行。



- 事件时间（Event-time）和延迟数据（Late Data）

```java
a、基于Event time聚合，更准确
  首先，介绍下两种时间的概念：
  Event time 事件时间: 就是数据真正发生的时间，比如用户浏览了一个页面可能会产生一条用户的该时间点的浏览日志。        
  Process time 处理时间: 则是这条日志数据真正到达计算框架中被处理的时间点，简单的说，就是你的Spark程序是什么时候读到这条日志的。
    事件时间是嵌入在数据本身中的时间。对于许多应用程序，用户可能希望在此事件时间操作。例如，如果要获取IoT设备每分钟生成的事件数，则可能需要使用生成数据的时间（即数据中的事件时间），而不是Spark接收他们的时间。事件时间在此模型中非常自然地表示 - 来自设备的每个事件都是表中的一行，事件时间是该行中的一个列值。这允许基于窗口的聚合（例如每分钟的事件数）仅仅是时间列上的特殊类型的分组和聚合 - 每个时间窗口是一个组，并且每一行可以属于多个窗口/组。因此，可以在静态数据集（例如，来自收集的设备事件日志）以及数据流上一致地定义这种基于事件时间窗的聚合查询，操作起来更方便。

b、延迟数据处理Watermark
Structured Streaming基于Event time能自然地处理延迟到达的数据，保留或者丢弃。
由于Spark正在更新Result Table，因此当存在延迟数据时，它可以完全控制更新旧聚合，以及清除旧聚合以限制中间状态数据的大小。
使用Watermark，允许用户指定数据的延期阈值，并允许引擎相应地清除旧的状态。
      
```

- 窗口操作

使用Structured Streaming基于事件时间的滑动窗口的聚合操作是很简单的，很像分组聚合。在一个分组聚合操作中，聚合值被唯一保存在用户指定的列中。在基于窗口的聚合的情况下，对于行的事件时间的每个窗口，维护聚合值。

如前面的例子，我们运行wordcount操作，希望以10min窗口计算，每五分钟滑动一次窗口。也即，12:00 - 12:10, 12:05 - 12:15, 12:10 - 12:20 这些十分钟窗口中进行单词统计。12:00 - 12:10意思是在12:00之后到达12:10之前到达的数据，比如一个单词在12:07收到。这个单词会影响12:00 - 12:10, 12:05 - 12:15两个窗口。

结果表将如下所示。

![640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1](https://ss.csdn.net/p?https://mmbiz.qpic.cn/mmbiz_jpg/adI0ApTVBFUM7VjQtqKt8icdRaV4GkleXJaFpd1sfGicfs9rQ7GdDzgJUCJtd5fXrOicqic68ibVNVS3Fo3EJLlRSbA/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1)

```scala
import org.apache.spark.sql.streaming.Trigger
import java.sql.Timestamp
import org.apache.spark.sql.functions._
import spark.implicits._

val lines = spark.readStream.format("socket").option("host", "127.0.0.1").option("port", 9999).option("includeTimestamp", true).load()

val words = lines.as[(String, Timestamp)].flatMap(line =>line.1.split(" ").map(word => (word, line.2))).toDF("word", "timestamp")

val windowedCounts = words.withWatermark("timestamp", "30 seconds").groupBy(window("timestamp", "30 seconds", "15 seconds"), "word").count()

val query = windowedCounts.writeStream.outputMode("Append").format("console").trigger(Trigger.ProcessingTime(5000)).option("truncate", "false").start()
query.awaitTermination()
```



- 水印

  现在考虑如果一个事件延迟到达这个应用将会发生什么。例如，12:04产生了一个word，可能在12:11才能被应用接收到。应用应该使用12:04这个时间去更新窗口12:00-12:10中的单词计数，而不是12:11。这在基于窗口的分组中自然发生——结构化流可以维持部分聚合的中间状态很长一段时间以满足延迟数据来正确更新旧窗口中的聚合。如下所示

![Handling Late Data](http://spark.apache.org/docs/latest/img/structured-streaming-late-data.png)

延迟数据dog，在12:11才被应用接受到，事实上，它在12:04已经产生。在前两次结果表中都未被统计，但是统计在了最后一次结果表中。这次在统计中，Spark引擎一致维持中间数据状态，直到延迟数据到达，并统计到结果表中。

对于一个运行好几天的查询来说，有必要绑定累计中间内存状态的数量。这意味着系统需要知道什么时候可以从内存状态中删除旧聚合，由于应用对那个聚合不再接收延迟数据。在Spark 2.1中，引入了水印，它使引擎自动跟踪当前数据中的事件时间，尝试一致地删除旧的状态。通过确定事件时间列和按照事件时间数据预期延迟的阈值可以定义查询的水印。对于一个以时间T开始的特定窗口，引擎将会维持状态，并且允许延迟数据更新状态直到(max event time seen by the engine - late threshold > T)。

换句话说，阈值内的延迟数据将会倍聚合，但是比阈值更延迟的数据将被删除。让我们来看个例子，late threshold=10分钟，输出模式为Update Mode。

![Watermarking in Update Mode](http://spark.apache.org/docs/latest/img/structured-streaming-watermark-update-mode.png)

图中的圆点表示数据，由数据产生的时间和word组成。坐标轴的横坐标表示数据被应用看到或接收的时间，纵坐标表示数据产生的时间。圆点有三类，

黄色实心圆：准时到达应用的数据，例如第一个dog单词，12:07产生，12:07到达（产生和到达可能相差一些秒）。

红色实心圆：延迟到达应用的数据，例如12:09产生的cat单词，12:09+10分钟=12:19，实际到达时间小于12:15，在水印之内。

红色空心圆：延迟到达应用的数据，例如donkey这个词在12:04产生，但在12:04+10分钟=12:14之后到达应用，在水印之外。

为了说清楚整个过程，我们对圆点进行标记，格式（序号,事件时间,单词），如下：

黄色实心圆从左到右标记为

(1,12:07,dog)，(2,12:08,owl)，(3,12:14,dog)，(4,12:15,cat)，(5,12:21,owl)；

红色实心圆从左到右标记为

(6,12:09,cat)，(7,12:08,dog)，(8,12:13,owl)，(9,12:17,owl)，

红色空心圆标记为(10,12:04,donkey)

第一次统计：

窗口12:00-12:10，单词序号1，2

12:05-12:15，单词序号2，3

也就是说第二个黄色实心圆在两个窗口中都有统计。

第二次统计：

12:00-12:10窗口新增了一个延迟的单词6

12:05-12:15窗口新增了两个延迟的单词6和7。

增加了一个窗口12:10-12:20，统计延迟的单词7。

第四次统计：

统计的时间是12:25，单词donkey在12:04产生，大约在12:22到达应用，超过了水印的10分钟阈值（应当要在12:04+10=12:14内到达），所以12:00-12:10窗口不再统计这个单词。

（1）水印删除聚合状态的条件

水印清除聚合查询中的状态需要满足下面的条件：

a、输出模式必须是追加（Append Mode）和更新模式（Update Mode），完全模式（Complete Mode）要求所有聚合数据持久化，所以不能使用水印删除中间状态。

b、聚合必须有事件-时间列或者一个事件-时间列上的窗口。

c、withWatermark必须在相同的列上调用，如聚合中使用的时间戳列。例如，

df.withWatermark("time", "1 min").groupBy("time2").count()在Append Mode中是无效的，因为对于聚合的列水印定义在了不同的列上。

d、withWatermark必须在水印聚合被使用前调用。例如 

df.groupBy("time").count().withWatermark("time", "1 min")在Append Mode中是无效的。

在窗口操作中，水印如下使用：

// 计数（添加水印 阈值是）
    val late =windowDuration
    val windowedCounts = words.withWatermark("timestamp", late)
      .groupBy(window($"timestamp", windowDuration, slideDuration), $"word")
      .count().orderBy("window")
late为数据允许延迟的时间，等于窗口长度，由执行命令时输入。



- 容错和数据恢复

```java
 a、容错语义

流式数据处理系统的可靠性语义通常是通过系统可以处理每个记录的次数来定义的。系统可以在所有可能的操作情形下提供三种类型的保证（无论出现何种故障）：

At most once：每个记录将被处理一次或不处理。
At least once:  每个记录将被处理一次或多次。 这比“最多一次”更强，因为它确保不会丢失任何数据。但可能有重复处理。
Exactly once：每个记录将被精确处理一次 - 不会丢失数据，并且不会多次处理数据。 这显然是三者中最强的保证。
    
Structured Streaming能保证At least once的语义，目标是Exactly once。
```

```java
b、容错机制

    在故障或故意关闭的情况下，用户可以恢复先前进度和状态，并继续在其停止的地方，简称断点续传。这是通过使用检查点checkpoint和预写日志write ahead logs来完成的。
    用户可以指定checkpoint的位置，Spark将保存所有进度信息（如每个触发器中处理的offset偏移范围）和正在运行的聚合到checkpoint中。任务重启后，使用这些信息继续执行。

    注：checkpoint的目录地址必须是HDFS兼容文件系统中的路径。
```

https://blog.csdn.net/lovechendongxing/article/details/81748553

https://blog.csdn.net/l_15156024189/article/details/81612860



Spark Mlib：

本部分是 Spark 对机器学习支持的部分，我们学有余力的同学可以了解一下 Spark 对常用的分类、回归、聚类、协同过滤、降维以及底层的优化原语等算法和工具。可以尝试自己使用 Spark Mlib 做一些简单的算法应用。



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
