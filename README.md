# 大数据开发面试指南

### Java 基础篇

整个大数据开发技术栈我们从实时性的角度来看，主要包含了离线计算和实时计算两大部分，而整个大数据生态中的框架绝大部分都是用 Java 开发或者兼容了 Java 的 API 调用，那么作为基于 JVM 的第一语言 Java 就是我们绕不过去的坎，Java 语言的基础也是我们阅读源码和进行代码调优的基础。

Java 基础主要包含以下部分：

- 语言基础
- 锁
- 多线程
- 并发包中常用的并发容器（J.U.C）

##### 语言基础

https://mp.weixin.qq.com/s?__biz=MzU3MzgwNTU2Mg==&mid=100001237&idx=1&sn=ab88f25074e5b736f9c189cd51b08a5f&chksm=7d3d43404a4aca5680027d7890721413a363986b02b58a54534a8125850db9a457690c074d3e#rd

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

https://www.cnblogs.com/f-ck-need-u/p/11161481.html

https://blog.csdn.net/u014427391/article/details/85019834

- 线程与进程的区别

1.进程是资源分配最小单位，线程是程序执行的最小单位；

2.进程有自己独立的地址空间，每启动一个进程，系统都会为其分配地址空间，建立数据表来维护代码段、堆栈段和数据段，线程没有独立的地址空间，它使用相同的地址空间共享数据；

3.CPU切换一个线程比切换进程花费小；

4.创建一个线程比进程开销小；

5.线程占用的资源要⽐进程少很多。

6.线程之间通信更方便，同一个进程下，线程共享全局变量，静态变量等数据，进程之间的通信需要以通信的方式（IPC）进行；（但多线程程序处理好同步与互斥是个难点）

7.多进程程序更安全，生命力更强，一个进程死掉不会对另一个进程造成影响（源于有独立的地址空间），多线程程序更不易维护，一个线程死掉，整个进程就死掉了（因为共享地址空间）；

8.进程对资源保护要求高，开销大，效率相对较低，线程资源保护要求不高，但开销小，效率高，可频繁切换；

- 线程的实现、线程的状态、优先级、线程调度、创建线程的多种方式、守护线程

1）线程的实现

https://blog.csdn.net/java_zyq/article/details/87917734

https://www.cnblogs.com/sunhaoyu/articles/6955923.html

```java
import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExcutorDemo {
     private static int produceTaskSleepTime = 5;
     private static int consumeTaskSleepTime = 5000;
     private static int produceTaskMaxNumber = 20; //定义最大添加10个线程到线程池中
     public static void main(String[] args) {
            //构造一个线程池
           ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2, 4, 3,
                     TimeUnit. SECONDS, new ArrayBlockingQueue<Runnable>(3),
                      new ThreadPoolExecutor.DiscardOldestPolicy());
            for( int i=1; i<= produceTaskMaxNumber;i++){
                 try {
                      //一个任务，并将其加入到线程池
                     String work= "work@ " + i;
                     System. out.println( "put ：" +work);
                      threadPool.execute( new ThreadPoolTask(work));
                      //便于观察，等待一段时间
                     Thread. sleep(produceTaskSleepTime);
                } catch (Exception e) {
                      e.printStackTrace();
                     }
                }
     }

/**
 * 线程池执行的任务
 * @author zhu
 */
 public static class ThreadPoolTask implements Runnable,Serializable{
        private static final long serialVersionUID = 0;
        //保存任务所需要的数据
        private Object threadPoolTaskData;
       	ThreadPoolTask(Object works){
             this. threadPoolTaskData =works;
       	}
        public void run(){
            //处理一个任务，这里的处理方式太简单了，仅仅是一个打印语句
            System. out.println( "start------"+threadPoolTaskData );
            try {
                 //便于观察，等待一段时间
                 Thread. sleep(consumeTaskSleepTime);
            } catch (Exception e) {
                 e.printStackTrace();
            }
            threadPoolTaskData = null;
        }
        public Object getTask(){
        	return this. threadPoolTaskData;
        }
 	}
}
```

2）线程的状态

https://blog.csdn.net/qq_36186690/article/details/82939190

https://www.cnblogs.com/rouqinglangzi/p/10803194.html#_label0_0

![img](https://img2018.cnblogs.com/blog/424830/201909/424830-20190925141255294-265623141.png)

3）优先级

https://www.cnblogs.com/HelloBigTable/p/10827269.html

4）线程调度

https://www.jianshu.com/p/f87d4a334d55

5）创建线程的多种方式

https://www.cnblogs.com/zhou-test/p/9811771.html

6）守护线程

https://blog.csdn.net/weixin_42447959/article/details/83018923


- 自己设计线程池、submit() 和 execute()、线程池原理

1）https://blog.csdn.net/qq_16525279/article/details/83686045

https://www.jianshu.com/p/2c4cc5c22736

https://blog.csdn.net/jgteng/article/details/54411423

2）https://blog.csdn.net/guhong5153/article/details/71247266

3）在一个应用程序中，我们需要多次使用线程，也就意味着，我们需要多次创建并销毁线程。而创建并销毁线程的过程势必会消耗内存。而在Java中，内存资源是及其宝贵的，所以，我们就提出了线程池的概念。

线程池：Java中开辟出了一种管理线程的概念，这个概念叫做线程池，从概念以及应用场景中，我们可以看出，线程池的好处，就是可以方便的管理线程，也可以减少内存的消耗。

那么，我们应该如何创建一个线程池那?Java中已经提供了创建线程池的一个类：Executor

而我们创建时，一般使用它的子类：ThreadPoolExecutor.

```java
public ThreadPoolExecutor(int corePoolSize,  
                              int maximumPoolSize,  
                              long keepAliveTime,  
                              TimeUnit unit,  
                              BlockingQueue<Runnable> workQueue,  
                              ThreadFactory threadFactory,  
                              RejectedExecutionHandler handler)
```

这是其中最重要的一个构造方法，这个方法决定了创建出来的线程池的各种属性，下面依靠一张图来更好的理解线程池和这几个参数：

![img](https://img-blog.csdn.net/20180419002550514)

我们可以看出，线程池中的corePoolSize就是线程池中的核心线程数量，这几个核心线程，只是在没有用的时候，也不会被回收，maximumPoolSize就是线程池中可以容纳的最大线程的数量，而keepAliveTime，就是线程池中除了核心线程之外的其他的最长可以保留的时间，因为在线程池中，除了核心线程即使在无任务的情况下也不能被清除，其余的都是有存活时间的，意思就是非核心线程可以保留的最长的空闲时间，而util，就是计算这个时间的一个单位，workQueue，就是等待队列，任务可以储存在任务队列中等待被执行，执行的是FIFIO原则（先进先出）。threadFactory，就是创建线程的线程工厂，最后一个handler,是一种拒绝策略，我们可以在任务满了知乎，拒绝执行某些任务。

线程池的执行流程又是怎样的呢？

![img](https://img-blog.csdn.net/2018041900353665)

我们可以看出，任务进来时，首先执行判断，判断核心线程是否处于空闲状态，如果不是，核心线程就先就执行任务，如果核心线程已满，则判断任务队列是否有地方存放该任务，若果有，就将任务保存在任务队列中，等待执行，如果满了，在判断最大可容纳的线程数，如果没有超出这个数量，就开创非核心线程执行任务，如果超出了，就调用handler实现拒绝策略。



线程使应用能够更加充分合理地协调利用CPU、内存、网络、I/O等系统资源.
线程的创建需要开辟虚拟机栈、本地方法栈、程序计数器等线程私有的内存空间;
在线程销毁时需要回收这些系统资源.
频繁地创建和销毁线程会浪费大量的系统资源,增加并发编程风险.

在服务器负载过大的时候,如何让新的线程等待或者友好地拒绝服务?

这些都是线程自身无法解决的;
所以需要通过线程池协调多个线程,并实现类似主次线程隔离、定时执行、周期执行等任务.

线程池的作用包括:
●利用线程池管理并复用线程、控制最大并发数等
●实现任务线程队列缓存策略和拒绝机制
●实现某些与时间相关的功能
如定时执行、周期执行等
●隔离线程环境
比如，交易服务和搜索服务在同一台服务器上,分别开启两个线程池,交易线程的资源消耗明显要大;
因此,通过配置独立的线程池,将较慢的交易服务与搜索服务隔离开,避免各服务线程相互影响.

在开发中,合理地使用线程池能够带来3个好处

- **降低资源消耗** 通过重复利用已创建的线程,降低创建和销毁线程造成的系统资源消耗
- **提高响应速度** 当任务到达时,任务可以不需要等到线程创建就能立即执行
- **提高线程的可管理性** 线程是稀缺资源,如果过多地创建,不仅会消耗系统资源，还会降低系统的稳定性，导致使用线程池可以进行统一分配、调优和监控。

​            


- 为什么不允许使用 Executors 创建线程池

https://blog.csdn.net/fly910905/article/details/81584675

https://blog.csdn.net/u010321349/article/details/83927012




- 死锁、死锁如何排查、线程安全和内存模型的关系

https://www.cnblogs.com/xiaoxi/p/8311034.html

https://zhuanlan.zhihu.com/p/74738566

https://www.cnblogs.com/shindo/p/7560058.html




- ThreadLocal 变量

https://www.jianshu.com/p/3c5d7f09dfbd

https://www.jianshu.com/p/e200e96a41a0




- Executor 创建线程池的几种方式：
  - newFixedThreadPool(int nThreads)
  - newCachedThreadPool()
  - newSingleThreadExecutor()
  - newScheduledThreadPool(int corePoolSize)
  - newSingleThreadExecutor()

`ExecutorService` 的抽象类`AbstractExecutorService`提供了`submit`、`invokeAll` 等方法的实现;
但是核心方法`Executor.execute()`并没有在这里实现.
因为所有的任务都在该方法执行,不同实现会带来不同的执行策略.

通过`Executors`的静态工厂方法可以创建三个线程池的包装对象

- ForkJoinPool、
- ThreadPoolExecutor
- ScheduledThreadPoolExecutor

  ● Executors.newWorkStealingPool
JDK8 引入,创建持有足够线程的线程池支持给定的并行度;
并通过使用多个队列减少竞争;
构造方法中把CPU数量设置为默认的并行度.
返回`ForkJoinPool` ( JDK7引入)对象,它也是`AbstractExecutorService` 的子类
![img](https://uploadfiles.nowcoder.com/files/20190625/5088755_1561473852906_4685968-3b4a8e8c1408f892.png)

● Executors.newCachedThreadPool
`maximumPoolSize` 最大可以至`Integer.MAX_VALUE`,是高度可伸缩的线程池.
若达到该上限,相信没有服务器能够继续工作,直接OOM.
`keepAliveTime` 默认为60秒;
工作线程处于空闲状态,则回收工作线程;
如果任务数增加,再次创建出新线程处理任务.

● Executors.newScheduledThreadPool
线程数最大至`Integer.MAX_ VALUE`,与上述相同,存在OOM风险.
`ScheduledExecutorService`接口的实现类,支持**定时及周期性任务执行**;
相比`Timer`,`ScheduledExecutorService` 更安全,功能更强大.
与`newCachedThreadPool`的区别是**不回收工作线程**.

● Executors.newSingleThreadExecutor
创建一个单线程的线程池,相当于单线程串行执行所有任务,保证按任务的提交顺序依次执行.

● Executors.newFixedThreadPool
输入的参数即是固定线程数;
既是核心线程数也是最大线程数;
不存在空闲线程,所以`keepAliveTime`等于0.
![img](https://uploadfiles.nowcoder.com/files/20190625/5088755_1561473852819_4685968-8cd91f3c6eada9de.png)
其中使用了 LinkedBlockingQueue, 但是没有设置上限!!!,堆积过多任务!!!

下面介绍`LinkedBlockingQueue`的构造方法
![img](https://uploadfiles.nowcoder.com/files/20190625/5088755_1561473852910_4685968-fce261dbe30def71.png)
使用这样的***队列,如果瞬间请求非常大,会有OOM的风险;
除`newWorkStealingPool` 外,其他四个创建方式都存在资源耗尽的风险.

不推荐使用其中的任何创建线程池的方法,因为都没有任何限制,存在安全隐患.

 `Executors`中默认的线程工厂和拒绝策略过于简单,通常对用户不够友好.
线程工厂需要做创建前的准备工作,对线程池创建的线程必须明确标识,就像药品的生产批号一样,为线程本身指定有意义的名称和相应的序列号.
拒绝策略应该考虑到业务场景,返回相应的提示或者友好地跳转.
以下为简单的ThreadFactory 示例
![img](https://uploadfiles.nowcoder.com/files/20190625/5088755_1561473852797_4685968-d2025287a82add95.png)

上述示例包括线程工厂和任务执行体的定义;
通过newThread方法快速、统一地创建线程任务,强调线程一定要有特定意义的名称,方便出错时回溯.

- 单线程池：newSingleThreadExecutor()方法创建，五个参数分别是ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue())。含义是池中保持一个线程，最多也只有一个线程，也就是说这个线程池是顺序执行任务的，多余的任务就在队列中排队。 
- 固定线程池：newFixedThreadPool(nThreads)方法创建
  ![图片标题](https://uploadfiles.nowcoder.com/images/20190625/5088755_1561474494512_5D0DD7BCB7171E9002EAD3AEF42149E6) 

池中保持nThreads个线程，最多也只有nThreads个线程，多余的任务也在队列中排队。
![图片标题](https://uploadfiles.nowcoder.com/images/20190625/5088755_1561476084467_4A47A0DB6E60853DEDFCFDF08A5CA249) 

![图片标题](https://uploadfiles.nowcoder.com/images/20190625/5088755_1561476102425_FB5C81ED3A220004B71069645F112867)
线程数固定且线程不超时

- 缓存线程池：newCachedThreadPool()创建，五个参数分别是ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue())。
  含义是池中不保持固定数量的线程，随需创建，最多可以创建Integer.MAX_VALUE个线程（说一句，这个数量已经大大超过目前任何操作系统允许的线程数了），空闲的线程最多保持60秒，多余的任务在SynchronousQueue（所有阻塞、并发队列在后续文章中具体介绍）中等待。 

为什么单线程池和固定线程池使用的任务阻塞队列是LinkedBlockingQueue()，而缓存线程池使用的是SynchronousQueue()呢？
因为单线程池和固定线程池中，线程数量是有限的，因此提交的任务需要在LinkedBlockingQueue队列中等待空余的线程；而缓存线程池中，线程数量几乎无限（上限为Integer.MAX_VALUE），因此提交的任务只需要在SynchronousQueue队列中同步移交给空余线程即可。

- 单线程调度线程池：newSingleThreadScheduledExecutor()创建，五个参数分别是 (1, Integer.MAX_VALUE, 0, NANOSECONDS, new DelayedWorkQueue())。含义是池中保持1个线程，多余的任务在DelayedWorkQueue中等待。 
- 固定调度线程池：newScheduledThreadPool(n)创建，五个参数分别是 (n, Integer.MAX_VALUE, 0, NANOSECONDS, new DelayedWorkQueue())。含义是池中保持n个线程，多余的任务在DelayedWorkQueue中等待。

有一项技术可以缓解执行时间较长任务造成的影响，即限定任务等待资源的时间，而不要无限的等待

先看第一个例子，测试单线程池、固定线程池和缓存线程池（注意增加和取消注释）：

```java
public class ThreadPoolExam {
    public static void main(String[] args) {
        //first test for singleThreadPool
        ExecutorService pool = Executors.newSingleThreadExecutor();
        //second test for fixedThreadPool
//        ExecutorService pool = Executors.newFixedThreadPool(2);
        //third test for ***dThreadPool
//        ExecutorService pool = Executors.newCachedThreadPool();
        for (int i = 0; i < 5; i++) {
            pool.execute(new TaskInPool(i));
        }
        pool.shutdown();
    }
}
 
class TaskInPool implements Runnable {
    private final int id;
 
    TaskInPool(int id) {
        this.id = id;
    }
 
    @Override
    public void run() {
        try {
            for (int i = 0; i < 5; i++) {
                System.out.println("TaskInPool-["+id+"] is running phase-"+i);
                TimeUnit.SECONDS.sleep(1);
            }
            System.out.println("TaskInPool-["+id+"] is over");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

如图为排查底层公共缓存调用出错时的截图
![有意义的线程命名](https://uploadfiles.nowcoder.com/files/20190625/5088755_1561473852749_4685968-85502533906c33f2.png)
绿色框采用自定义的线程工厂,明显比蓝色框默认的线程工厂创建的线程名称拥有更多的额外信息:如调用来源、线程的业务含义，有助于快速定位到死锁、StackOverflowError 等问题.




- ThreadPoolExecutor 创建线程池、拒绝策略

https://www.nowcoder.com/discuss/165008?type=0&order=0&pos=6&page=0


- 线程池关闭的方式

shutdown:
1、调用之后不允许继续往线程池内继续添加线程;
2、线程池的状态变为SHUTDOWN状态;
3、所有在调用shutdown()方法之前提交到ExecutorSrvice的任务都会执行;
4、一旦所有线程结束执行当前任务，ExecutorService才会真正关闭。

shutdownNow():
1、该方法返回尚未执行的 task 的 List;
2、线程池的状态变为STOP状态;
3、阻止所有正在等待启动的任务, 并且停止当前正在执行的任务。

简单点来说，就是:
shutdown()调用后，不可以再 submit 新的 task，已经 submit 的将继续执行
shutdownNow()调用后，试图停止当前正在执行的 task，并返回尚未执行的 task 的 list

https://blog.csdn.net/riemann_/article/details/97621077



##### 并发容器（J.U.C）

- JUC 包中 List 接口的实现类：CopyOnWriteArrayList

它相当于线程安全的ArrayList。和ArrayList一样，它是个可变数组；但是和ArrayList不同的是，它具有以下特性：

1. 它最适合于具有以下特征的应用程序：List 大小通常保持很小，只读操作远多于可变操作，需要在遍历期间防止线程间的冲突。
2. 它是线程安全的。
3. 因为通常需要复制整个基础数组，所以可变操作（add()、set() 和 remove() 等等）的开销很大。
4. 迭代器支持hasNext(), next()等不可变操作，但不支持可变 remove()等操作。
5. 使用迭代器进行遍历的速度很快,并且不会与其他线程发生冲突。在构造迭代器时,迭代器依赖于不变的数组快照。

**CopyOnWriteArrayList原理和数据结构：**

```
1.CopyOnWriteArrayList实现了List接口,因此它是一个队列。
2.CopyOnWriteArrayList包含了成员lock。每一个CopyOnWriteArrayList都和一个互斥锁lock绑定,通过lock，实现了对CopyOnWriteArrayList的互斥访问。
3. CopyOnWriteArrayList包含了成员array数组,这说明CopyOnWriteArrayList本质上通过数组实现的。
下面从“动态数组”和“线程安全”两个方面进一步对CopyOnWriteArrayList的原理进行说明。
4. CopyOnWriteArrayList的“动态数组”机制 -- 它内部有个“volatile数组”(array)来保持数据。在“添加/修改/删除”数据时，都会新建一个数组，并将更新后的数据拷贝到新建的数组中，最后再将该数组赋值给“volatile数组”。这就是它叫做CopyOnWriteArrayList的原因！CopyOnWriteArrayList就是通过这种方式实现的动态数组；不过正由于它在“添加/修改/删除”数据时，都会新建数组，所以涉及到修改数据的操作，CopyOnWriteArrayList效率很低；但是单单只是进行遍历查找的话，效率比较高。
5. CopyOnWriteArrayList的“线程安全”机制 -- 是通过volatile和互斥锁来实现的。(01) CopyOnWriteArrayList是通过"volatile数组"来保存数据的。一个线程读取volatile数组时，总能看到其它线程对该volatile变量最后的写入;就这样，通过volatile提供了"读取到的数据总是最新的"这个机制的保证。(02) CopyOnWriteArrayList通过互斥锁来保护数据。在"添加/修改/删除"数据时，会先"获取互斥锁",再修改完毕之后，先将数据更新到“volatile数组”中，然后再"释放互斥锁",这样,就达到了保护数据的目的。 
```

​	

- JUC 包中 Set 接口的实现类：CopyOnWriteArraySet、ConcurrentSkipListSet
- JUC 包中 Map 接口的实现类：ConcurrentHashMap、ConcurrentSkipListMap
- JUC包中Queue接口的实现类：ConcurrentLinkedQueue、ConcurrentLinkedDeque、ArrayBlockingQueue、LinkedBlockingQueue、LinkedBlockingDeque

阻塞队列常用于生产者和消费者的场景：

​		生产者是往队列里添加元素的线程，消费者是从队列里拿元素的线程。

它的主要用途并不是作为容器，而是作为线程同步的的工具，因此他具有一个很明显的特性：

- 当生产者线程试图向 BlockingQueue 放入元素时，如果队列已满，则线程被阻塞。
- 当消费者线程试图从中取出一个元素时，如果队列为空，则该线程会被阻塞。
- 正是因为它所具有这个特性，所以在程序中多个线程交替向BlockingQueue中 放入元素，取出元素，它可以很好的控制线程之间的通信。

阻塞队列使用最经典的场景，就是 Socket 客户端数据的读取和解析：

- 读取数据的线程不断将数据放入队列。
- 然后，解析线程不断从队列取数据解析。

**阻塞队列有7个：**

【最常用】ArrayBlockingQueue ：一个由数组结构组成的有界阻塞队列。

> 此队列按照先进先出（FIFO）的原则对元素进行排序，但是默认情况下不保证线程公平的访问队列，即如果队列满了，那么被阻塞在外面的线程对队列访问的顺序是不能保证线程公平（即先阻塞，先插入）的。

LinkedBlockingQueue ：一个由链表结构组成的有界阻塞队列。

> 此队列按照先出先进的原则对元素进行排序

PriorityBlockingQueue ：一个支持优先级排序的无界阻塞队列

DelayQueue：支持延时获取元素的无界阻塞队列，即可以指定多久才能从队列中获取当前元素。

SynchronousQueue：一个不存储元素的阻塞队列

> 每一个 put 必须等待一个 take 操作，否则不能继续添加元素。并且他支持公平访问队列。

LinkedTransferQueue：一个由链表结构组成的无界阻塞队列

> 相对于其他阻塞队列，多了 tryTransfer 和 transfer 方法。

- - transfer 方法：如果当前有消费者正在等待接收元素（take 或者待时间限制的 poll 方法），transfer 可以把生产者传入的元素立刻传给消费者。如果没有消费者等待接收元素，则将元素放在队列的 tail 节点，并等到该元素被消费者消费了才返回。
  - tryTransfer 方法：用来试探生产者传入的元素能否直接传给消费者。如果没有消费者在等待，则返回 false 。和上述方法的区别是该方法无论消费者是否接收，方法立即返回。而 transfer 方法是必须等到消费者消费了才返回。

LinkedBlockingDeque：一个由链表结构组成的双向阻塞队列。

优势在于多线程入队时，减少一半的竞争。



### Java 进阶篇

进阶篇部分是对 Java 基础篇的补充，这部分内容是我们熟读大数据框架的源码必备的技能，也是我们在面试高级职位的时候的面试重灾区。

##### JVM

https://mp.weixin.qq.com/s?__biz=MzU3MzgwNTU2Mg==&mid=100001450&idx=1&sn=1fe93c9de54c1a89f742eedc114e96d0&chksm=7d3d403f4a4ac9299f840a4389f0ecc8033672bdb4bd2447c539fd5db021613b732b189522d7

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

[https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/%E5%88%86%E5%B8%83%E5%BC%8F%E7%90%86%E8%AE%BA/%E5%88%86%E5%B8%83%E5%BC%8F%E7%B3%BB%E7%BB%9F%E7%9A%84%E4%B8%80%E4%BA%9B%E5%9F%BA%E6%9C%AC%E6%A6%82%E5%BF%B5.md](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/分布式理论/分布式系统的一些基本概念.md)

- 分布式系统理论基础： 一致性、2PC 和 3PC

[https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/%E5%88%86%E5%B8%83%E5%BC%8F%E7%90%86%E8%AE%BA/%E5%88%86%E5%B8%83%E5%BC%8F%E7%B3%BB%E7%BB%9F%E7%90%86%E8%AE%BA%E5%9F%BA%E7%A1%80%E4%B8%80%EF%BC%9A%20%E4%B8%80%E8%87%B4%E6%80%A7%E3%80%812PC%E5%92%8C3PC.md](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/分布式理论/分布式系统理论基础一： 一致性、2PC和3PC.md)

- 分布式系统理论基础：CAP

[https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/%E5%88%86%E5%B8%83%E5%BC%8F%E7%90%86%E8%AE%BA/%E5%88%86%E5%B8%83%E5%BC%8F%E7%B3%BB%E7%BB%9F%E7%90%86%E8%AE%BA%E5%9F%BA%E7%A1%80%E4%BA%8C-CAP.md](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/分布式理论/分布式系统理论基础二-CAP.md)

- 分布式系统理论基础：时间、时钟和事件顺序

[https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/%E5%88%86%E5%B8%83%E5%BC%8F%E7%90%86%E8%AE%BA/%E5%88%86%E5%B8%83%E5%BC%8F%E7%B3%BB%E7%BB%9F%E7%90%86%E8%AE%BA%E5%9F%BA%E7%A1%80%E4%B8%89-%E6%97%B6%E9%97%B4%E3%80%81%E6%97%B6%E9%92%9F%E5%92%8C%E4%BA%8B%E4%BB%B6%E9%A1%BA%E5%BA%8F.md](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/分布式理论/分布式系统理论基础三-时间、时钟和事件顺序.md)

- 分布式系统理论进阶：Paxos

[https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/%E5%88%86%E5%B8%83%E5%BC%8F%E7%90%86%E8%AE%BA/%E5%88%86%E5%B8%83%E5%BC%8F%E7%B3%BB%E7%BB%9F%E7%90%86%E8%AE%BA%E8%BF%9B%E9%98%B6%20-%20Paxos.md](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/分布式理论/分布式系统理论进阶 - Paxos.md)

- 分布式系统理论进阶：Raft、Zab

[https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/%E5%88%86%E5%B8%83%E5%BC%8F%E7%90%86%E8%AE%BA/%E5%88%86%E5%B8%83%E5%BC%8F%E7%B3%BB%E7%BB%9F%E7%90%86%E8%AE%BA%E8%BF%9B%E9%98%B6%20-%20Raft%E3%80%81Zab.md](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/分布式理论/分布式系统理论进阶 - Raft、Zab.md)

- 分布式系统理论进阶：选举、多数派和租约

[https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/%E5%88%86%E5%B8%83%E5%BC%8F%E7%90%86%E8%AE%BA/%E5%88%86%E5%B8%83%E5%BC%8F%E7%B3%BB%E7%BB%9F%E7%90%86%E8%AE%BA%E8%BF%9B%E9%98%B6%EF%BC%9A%E9%80%89%E4%B8%BE%E3%80%81%E5%A4%9A%E6%95%B0%E6%B4%BE%E5%92%8C%E7%A7%9F%E7%BA%A6.md](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/分布式理论/分布式系统理论进阶：选举、多数派和租约.md)

- 分布式锁的解决方案

[https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/%E5%88%86%E5%B8%83%E5%BC%8F%E7%90%86%E8%AE%BA/%E5%88%86%E5%B8%83%E5%BC%8F%E9%94%81%E7%9A%84%E8%A7%A3%E5%86%B3%E6%96%B9%E6%A1%88.md](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/分布式理论/分布式锁的解决方案.md)

[https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/%E5%88%86%E5%B8%83%E5%BC%8F%E7%90%86%E8%AE%BA/%E5%88%86%E5%B8%83%E5%BC%8F%E9%94%81%E7%9A%84%E8%A7%A3%E5%86%B3%E6%96%B9%E6%A1%88(%E4%BA%8C).md](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/分布式理论/分布式锁的解决方案(二).md)

- 分布式事务的解决方案

[https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/%E5%88%86%E5%B8%83%E5%BC%8F%E7%90%86%E8%AE%BA/%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1%E7%9A%84%E8%A7%A3%E5%86%B3%E6%96%B9%E6%A1%88.md](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/分布式理论/分布式事务的解决方案.md)

- 分布式 ID 生成器解决方案

[https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/%E5%88%86%E5%B8%83%E5%BC%8F%E7%90%86%E8%AE%BA/%E5%88%86%E5%B8%83%E5%BC%8FID%E7%94%9F%E6%88%90%E5%99%A8%E8%A7%A3%E5%86%B3%E6%96%B9%E6%A1%88.md](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/分布式理论/分布式ID生成器解决方案.md)

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

https://blog.csdn.net/qq_39210208/article/details/87898553

- 掌握 MapReduce 的工作原理

https://www.cnblogs.com/huifeidezhuzai/p/9245542.html

https://www.cnblogs.com/laowangc/p/8961946.html

![img](http://ww3.sinaimg.cn/mw690/005WTVurjw1eoyphlssyej30j60f30ti.jpg)

1 在客户端启动一个作业。

2 向JobTracker请求一个**Job ID**。

3 将运行作业所需要的资源文件复制到HDFS上，包括MapReduce程序打包的**jar文件、配置文件和客户端计算所得的计算划分信息**。这些文件都存放在JobTracker专门为该作业创建的文件夹中。文件夹名为该作业的Job ID。jar文件默认会有10个副本（mapred.submit.replication属性控制）；输入划分信息告诉了JobTracker应该为这个作业启动多少个map任务等信息。

4 JobTracker接收到作业后，将其放在一个**作业队列**里，等待**作业调度器**对其进行调度（这里是不是很像微机中的进程调度呢），当作业调度器根据自己的调度算法调度到该作业时，会根据输入划分信息为每个划分创建一个map任务，并将map任务分配给TaskTracker执行。对于map和reduce任务，TaskTracker根据主机核的数量和内存的大小有固定数量的**map槽和reduce槽**。**这里需强调的是**：map任务不是随随便便地分配给某个TaskTracker的，这里有个概念叫：**数据本地化**（Data-Local）。意思是：将map任务分配给含有该map处理的数据块的TaskTracker上，同事将程序jar包复制到该TaskTracker上来运行，这叫“**运算移动，数据不移动**”。而分配reduce任务时并不考虑数据本地化。

5 TaskTracker每隔一段时间会给JobTracker发送一个**心跳**，告诉JobTracker它依然在运行，同时心跳中还携带者很多信息，比如当前map任务完成的进度等信息。当JobTracker收到作业的最后一个任务完成信息时，便把该作业设置成“成功”。当JobTracker查询状态时，它将得知任务已完成，便显示一条消息给用户。

- 能用 MapReduce 手写代码实现简单的 WordCount 或者 TopN 算法

[https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/%E5%A4%A7%E6%95%B0%E6%8D%AE%E6%A1%86%E6%9E%B6%E5%AD%A6%E4%B9%A0/Hadoop-MapReduce.md#%E4%B8%80MapReduce%E6%A6%82%E8%BF%B0](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/大数据框架学习/Hadoop-MapReduce.md#一MapReduce概述)

- 掌握 MapReduce Combiner 和 Partitioner的作用

https://blog.csdn.net/yangjjuan/article/details/78119399

https://www.jianshu.com/p/8fddf4d6f728

- 熟悉 Hadoop 集群的搭建过程，并且能解决常见的错误
- 熟悉 Hadoop 集群的扩容过程和常见的坑
- 如何解决 MapReduce 的数据倾斜

https://www.cnblogs.com/yinzhengjie/p/9194525.html

https://www.zhihu.com/question/27593027

- Shuffle 原理和减少 Shuffle 的方法

https://blog.csdn.net/peter_changyb/article/details/82682422

https://blog.csdn.net/shujuelin/article/details/83992061?depth_1-utm_source=distribute.pc_relevant.none-task&utm_source=distribute.pc_relevant.none-task

https://blog.csdn.net/sofeld/article/details/94775478

Map端
map函数开始产生输出时，并不是简单的将它写到磁盘，而是利用缓冲的方式写到内存，并出于效率考虑，进行排序。

1）每个输入分片由一个Map任务处理。(HDFS一个块的大小默认为128M，可以设置块的大小)
2）map输出的结果会暂存在一个环形内存缓冲区中。（缓冲区默认大小为100M，由io.sort.mb属性控制）
3）当缓冲区快要溢出时（默认为缓冲区大小的80%，由io.sort.spill.percent属性控制），由一个后台线程将该缓冲区中的数据写到磁盘新建的溢出文件中。在溢出写到磁盘的过程中，map输出继续写到缓冲区，但是如果在此期间缓冲区被填满，map会被阻塞直到写磁盘过程完成。
4）在写入磁盘之前，线程首先根据Reduce任务的数目将数据划分为相同数目的分区，也就是一个Reduce任务对应一个分区的数据，这样避免Reduce任务分配到的数据不均匀。（分区就是对数据进行Hash的过程）；
5）然后对每个分区中的数据进行排序（第一次排序）；
6）如果此时设置了Combiner，将排序后的结果进行Combia操作，使得Map输出结果更紧凑，使得让尽可能少的数据写入到磁盘和传递给Reducer；
7）当Map任务输出最后一个记录时，可能会有很多的溢出文件，这时需要将这些文件合并，合并的过程中会不断地进行排序和Combia操作。（属性io.sort.factor控制一次最多合并多少流，默认10）。这样做的目的1，尽量减少每次写入磁盘的数据量，目的2，尽量减少下一复制阶段网络传输的数据量。最后合并成一个已分区且已排序的文件（第二次排序）。
8）为了减少网络传输数据量，节约磁盘空间，可以在这里将数据压缩。（mapred.compress.map.out设置为ture,mapred.map.output.compression.codec指定使用的压缩库）
9）将分区中的数据拷贝给相对应的Reduce任务。Reducer通过HTTP方式得到输出文件的分区。
Reduce端
1）Reduce会接收到不同Map任务传来的数据，并且每个Map传来的数据都是有序的。
2）如果Reduce端接收的数据量少，则直接存在内存中（缓冲区大小由mapred.job.shuffle.input.buffer.percent属性控制）；如果数据量超过了缓冲区大小的一定比例（由mapred.job.shuffle.merge.percent决定）则对数据合并后溢写到磁盘中。
3）随着溢写文件的增多，后台线程会将这些文件合并成一个更大的有序的文件，这样做是为了给后面的合并节省时间；
4）复制完所有Map输出后，Reduce任务进入排序阶段，这个阶段将合并Map输出，维持其顺序排序（第三次排序），这是循环进行的。例如，有50个Map输出，而合并因子默认是10，合并会进行5次，每次将10个文件合并成一个文件，过程中产生5个中间文件。
5）合并的过程中会产生许多的中间文件写入磁盘，但MapReduce会让写入磁盘的数据尽可能少，并且最后一次合并的结果并没有写入磁盘，而是直接输入到Reduce函数。
6）在Reduce阶段，对已排序输出中的每个键调用Reduce函数，此阶段的输出直接写入到输出文件系统HDFS。



HDFS：

https://blog.csdn.net/qq_39210208/article/details/87881593

https://blog.csdn.net/qq_39210208/article/details/87887778

https://blog.csdn.net/qq_39210208/article/details/87892298

- 十分熟悉 HDFS 的架构图和读写流程
- 十分熟悉 HDFS 的配置
- 熟悉 DataNode 和 NameNode 的作用
- NameNode 的 HA 搭建和配置，Fsimage 和 EditJournal 的作用的场景

![img](https://img-blog.csdnimg.cn/2019022300134765.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM5MjEwMjA4,size_16,color_FFFFFF,t_70)

- HDFS 操作文件的常用命令
- HDFS 的安全模式



Yarn：

https://blog.csdn.net/qq_39210208/article/details/87947855

![img](https://img-blog.csdnimg.cn/20190226193919911.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM5MjEwMjA4,size_16,color_FFFFFF,t_70)

- Yarn 的产生背景和架构
- Yarn 中的角色划分和各自的作用

https://blog.csdn.net/sxiaobei/article/details/80875062

https://blog.csdn.net/weixin_39182877/article/details/79099225

- Yarn 的配置和常用的资源调度策略

FIFO Scheduler(先进先出调度器)

Capacity Scheduler(容量调度器) 

Fair Scheduler(公平调度器)

- Yarn 进行一次任务资源调度的过程

### OLAP 引擎 Hive

Hive 是一个数据仓库基础工具，在 Hadoop 中用来处理结构化数据。它架构在 Hadoop 之上，总归为大数据，并使得查询和分析方便。Hive 是应用最广泛的 OLAP 框架。Hive SQL 也是我们进行 SQL 开发用的最多的框架。

关于 Hive 你必须掌握的知识点如下：

- HiveSQL 的原理：我们都知道 HiveSQL 会被翻译成 MapReduce 任务执行，那么一条 SQL 是如何翻译成 MapReduce 的？

https://www.jianshu.com/p/660fd157c5eb

SQL Parser：Antlr定义SQL的语法规则，完成SQL词法，语法解析，将SQL转化为抽象 语法树AST Tree；
 Semantic Analyzer：遍历AST Tree，抽象出查询的基本组成单元QueryBlock；
 Logical plan：遍历QueryBlock，翻译为执行操作树OperatorTree；
 Logical plan optimizer: 逻辑层优化器进行OperatorTree变换，合并不必要的ReduceSinkOperator，减少shuffle数据量；
 Physical plan：遍历OperatorTree，翻译为MapReduce任务；
 Logical plan optimizer：物理层优化器进行MapReduce任务的变换，生成最终的执行计划；



- Hive 和普通关系型数据库有什么区别？

1. 查询语言。由于 SQL 被广泛的应用在数据仓库中，因此，专门针对 Hive 的特性设计了类 SQL 的查询语言 HQL。熟悉 SQL 开发的开发者可以很方便的使用 Hive 进行开发。


2. 数据格式。Hive 是建立在 Hadoop 之上的，所有 Hive 的数据都是存储在 HDFS 中的。Hive 中没有定义专门的数据格式，数据格式可以由用户指定，用户定义数据格式需要指定三个属性：列分隔符（通常为空格、”\t”、”\x001″）、行分隔符（”\n”）以及读取文件数据的方法（Hive 中默认有三个文件格式 TextFile，SequenceFile 以及 RCFile）。由于在加载数据的过程中，不需要从用户数据格式到 Hive 定义的数据格式的转换，因此，Hive 在加载的过程中不会对数据本身进行任何修改，而只是将数据内容复制或者移动到相应的 HDFS 目录中。而在数据库中，不同的数据库有不同的存储引擎，定义了自己的数据格式。所有数据都会按照一定的组织存储，因此，数据库加载数据的过程会比较耗时。

3.事务性。关系型数据库应用广泛，能进行事务处理和表连接等复杂查询。相对地，NoSQL数据库只应用在特定领域，基本上不进行复杂的处理，但它恰恰弥补了之前所列举的关系型数据库的不足之处——易于数据的分散。

4. 数据更新。由于 Hive 是针对数据仓库应用设计的，而数据仓库的内容是读多写少的。因此，Hive 中不支持对数据的改写和添加，所有的数据都是在加载的时候中确定好的。而数据库中的数据通常是需要经常进行修改的，因此可以使用 INSERT INTO ...  VALUES 添加数据，使用 UPDATE ... SET修改数据。

5. 索引。之前已经说过，Hive 在加载数据的过程中不会对数据进行任何处理，甚至不会对数据进行扫描，因此也没有对数据中的某些 Key 建立索引。Hive 要访问数据中满足条件的特定值时，需要暴力扫描整个数据，因此访问延迟较高。由于 MapReduce 的引入， Hive 可以并行访问数据，因此即使没有索引，对于大数据量的访问，Hive 仍然可以体现出优势。数据库中，通常会针对一个或者几个列建立索引，因此对于少量的特定条件的数据的访问，数据库可以有很高的效率，较低的延迟。由于数据的访问延迟较高，决定了 Hive 不适合在线数据查询。

6. 执行。Hive 中大多数查询的执行是通过 Hadoop 提供的 MapReduce 来实现的（类似 select * from tbl 的查询不需要 MapReduce）。而数据库通常有自己的执行引擎。

7. 执行延迟。之前提到，Hive 在查询数据的时候，由于没有索引，需要扫描整个表，因此延迟较高。另外一个导致 Hive 执行延迟高的因素是 MapReduce 框架。由于 MapReduce 本身具有较高的延迟，因此在利用 MapReduce 执行 Hive 查询时，也会有较高的延迟。相对的，数据库的执行延迟较低。当然，这个低是有条件的，即数据规模较小，当数据规模大到超过数据库的处理能力的时候，Hive 的并行计算显然能体现出优势。

8. 可扩展性。由于 Hive 是建立在 Hadoop 之上的，因此 Hive 的可扩展性是和 Hadoop 的可扩展性是一致的（世界上最大的 Hadoop 集群在 Yahoo!，2009年的规模在 4000 台节点左右）。而数据库由于 ACID 语义的严格限制，扩展行非常有限。



- Hive 支持哪些数据格式

原始数据类型：

| 类型名称  | 大小                     | 备注                             |
| --------- | ------------------------ | -------------------------------- |
| TINYINT   | 1字节整数                | 45Y                              |
| SMALLINT  | 2字节整数                | 12S                              |
| INT       | 4字节整数                | 10                               |
| BIGINT    | 8字节整数                | 244L                             |
| FLOAT     | 4字节单精度浮点数        | 1.0                              |
| DOUBLE    | 8字节双精度浮点数        | 1.0                              |
| DECIMAL   | 任意精度带符号小数       | DECIMAL(4, 2)范围：-99.99到99.99 |
| BOOLEAN   | true/false               | TRUE                             |
| STRING    | 字符串，长度不定         | “a”, ‘b’                         |
| VARCHAR   | 字符串，长度不定，有上限 | 0.12.0版本引入                   |
| CHAR      | 字符串，固定长度         | “a”, ‘b’                         |
| BINARY    | 存储变长的二进制数据     |                                  |
| TIMESTAMP | 时间戳，纳秒精度         | 122327493795                     |
| DATE      | 日期                     | ‘2016-07-03’                     |

　　复杂类型：

| 类型名称 | 大小                                             | 示例                                                   |
| -------- | ------------------------------------------------ | ------------------------------------------------------ |
| ARRAY    | 存储同类型数据                                   | ARRAY< data_type>                                      |
| MAP      | key-value,key必须为原始类型，value可以是任意类型 | MAP< primitive_type, data_type>                        |
| STRUCT   | 类型可以不同                                     | STRUCT< col_name : data_type [COMMENT col_comment], …> |
| UNION    | 在有限取值范围内的一个值                         | UNIONTYPE< data_type, data_type, …>                    |

**hive的文件格式**

TEXTFILE //文本，默认值

SEQUENCEFILE // 二进制序列文件

RCFILE //列式存储格式文件 Hive0.6以后开始支持

ORC //列式存储格式文件，比RCFILE有更高的压缩比和读写效率，Hive0.11以后开始支持

PARQUET //列出存储格式文件，Hive0.13以后开始支持



- Hive 在底层是如何存储 NULL 的

https://blog.csdn.net/lsxy117/article/details/50387324?depth_1-utm_source=distribute.pc_relevant.none-task&utm_source=distribute.pc_relevant.none-task



- HiveSQL 支持的几种排序各代表什么意思（Sort By/Order By/Cluster By/Distrbute By）

order by：会对输入做全局排序，因此只有一个reducer（多个reducer无法保证全局有序）。只有一个reducer，会导致当输入规模较大时，需要较长的计算时间。
sort by：不是全局排序，其在数据进入reducer前完成排序。
distribute by：按照指定的字段对数据进行划分输出到不同的reduce中。
cluster by：除了具有 distribute by 的功能外还兼具 sort by 的功能。



- Hive 的动态分区

https://www.cnblogs.com/sunpengblog/p/10396442.html



- HQL 和 SQL 有哪些常见的区别

用法上的区别：

\1. HQL不支持行级别的增、改、删，所有数据在加载时就已经确定，不可更改。

\2. 不支持事务。

\3. 支持分区存储。

hive下的SQL特点： 

  1.不支持等值连接，一般使用left join、right join 或者inner join替代。

  2.不能智能识别concat(‘;’,key)，只会将‘；’当做SQL结束符号。

  3.不支持INSERT INTO 表 Values（）, UPDATE, DELETE等操作

  4.HiveQL中String类型的字段若是空(empty)字符串, 即长度为0, 那么对它进行IS NULL的判断结果是False，使用left join可以进行筛选行。

  5.不支持 ‘< dt <’这种格式的范围查找，可以用dt in(”,”)或者between替代。



- Hive 中的内部表和外部表的区别

https://blog.csdn.net/qq_36743482/article/details/78393678?depth_1-utm_source=distribute.pc_relevant.none-task&utm_source=distribute.pc_relevant.none-task



- Hive 表进行关联查询如何解决长尾和数据倾斜问题

https://www.jianshu.com/p/4f41bcdd7462



- HiveSQL 的优化（系统参数调整、SQL 语句优化）

https://juejin.im/post/5cd83b9ff265da038364e35d

https://mp.weixin.qq.com/s/DfvN7S_00oYw1hqAQDr48g



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

https://mp.weixin.qq.com/s?__biz=MzU3MzgwNTU2Mg==&mid=100001204&idx=1&sn=eaeef90f6a2a1905eeab3496a1e20161&chksm=7d3d43214a4aca377a1d4a868b8bc099e8d5cbbca88b9cdd670c614bcdc85e1bb5926f371809#rd

https://mp.weixin.qq.com/s/hAw2KEnZJNIq_qVw8H1UMg

- Kafka 的特性和使用场景

https://blog.csdn.net/qq_35078688/article/details/86083627

持久化、高吞吐量、可扩展性、多客户端支持、安全机制、数据备份（副本机制）、支持压缩

1、同时为发布和订阅提供高吞吐量。据了解，Kafka 每秒可以生产约 25 万消息（50MB），每秒处理 55 万消息（110MB）。

2、可进行持久化操作。将消息持久化到磁盘，因此可用于批量消费，例如 ETL ，以及实时应用程序。通过将数据持久化到硬盘，以及replication ，可以防止数据丢失。

3、分布式系统，易于向外扩展。所有的 Producer、Broker 和Consumer 都会有多个，均为分布式的。并且，无需停机即可扩展机器。

4、消息被处理的状态是在 Consumer 端维护，而不是由 Broker 端维护。当失败时，能自动平衡。

这段是从网络上找来的。感觉想要表达的意思是

- 消息是否被处理完成，是通过 Consumer 提交消费进度给 Broker ，而不是 Broker 消息被 Consumer 拉取后，就标记为已消费。

- 当 Consumer 异常崩溃时，可以重新分配消息分区到其它的 Consumer 们，然后继续消费。

5、支持 online 和 offline 的场景

**Kafka 的应用场景**

1）消息队列

比起大多数的消息系统来说，Kafka 有更好的吞吐量，内置的分区，冗余及容错性，这让 Kafka 成为了一个很好的大规模消息处理应用的解决方案。消息系统一般吞吐量相对较低，但是需要更小的端到端延时，并常常依赖于 Kafka 提供的强大的持久性保障。在这个领域，Kafka 足以媲美传统消息系统，如 ActiveMQ 或 RabbitMQ 。

2）行为跟踪

Kafka 的另一个应用场景，是跟踪用户浏览页面、搜索及其他行为，以发布订阅的模式实时记录到对应的 Topic 里。那么这些结果被订阅者拿到后，就可以做进一步的实时处理，或实时监控，或放到 Hadoop / 离线数据仓库里处理。

3）元信息监控

作为操作记录的监控模块来使用，即汇集记录一些操作信息，可以理解为运维性质的数据监控吧。

4）日志收集

日志收集方面，其实开源产品有很多，包括 Scribe、Apache Flume 。很多人使用 Kafka 代替日志聚合（log aggregation）。日志聚合一般来说是从服务器上收集日志文件，然后放到一个集中的位置（文件服务器或 HDFS）进行处理。

然而， Kafka 忽略掉文件的细节，将其更清晰地抽象成一个个日志或事件的消息流。这就让 Kafka 处理过程延迟更低，更容易支持多数据源和分布式数据处理。比起以日志为中心的系统比如 Scribe 或者 Flume 来说，Kafka 提供同样高效的性能和因为复制导致的更高的耐用性保证，以及更低的端到端延迟。

5）流处理

这个场景可能比较多，也很好理解。保存收集流数据，以提供之后对接的 Storm 或其他流式计算框架进行处理。很多用户会将那些从原始 Topic 来的数据进行阶段性处理，汇总，扩充或者以其他的方式转换到新的 Topic 下再继续后面的处理。

例如一个文章推荐的处理流程，可能是先从 RSS 数据源中抓取文章的内容，然后将其丢入一个叫做 “文章” 的 Topic 中。后续操作可能是需要对这个内容进行清理，比如回复正常数据或者删除重复数据，最后再将内容匹配的结果返还给用户。这就在一个独立的 Topic 之外，产生了一系列的实时数据处理的流程。Strom 和 Samza 是非常著名的实现这种类型数据转换的框架。

6）事件源

事件源，是一种应用程序设计的方式。该方式的状态转移被记录为按时间顺序排序的记录序列。Kafka 可以存储大量的日志数据，这使得它成为一个对这种方式的应用来说绝佳的后台。比如动态汇总（News feed）。

7）持久性日志（Commit Log）

Kafka 可以为一种外部的持久性日志的分布式系统提供服务。这种日志可以在节点间备份数据，并为故障节点数据回复提供一种重新同步的机制。Kafka 中日志压缩功能为这种用法提供了条件。在这种用法中，Kafka 类似于 Apache BookKeeper 项目。



- Kafka 中的一些概念：Leader、Broker、Producer、Consumer、Topic、Group、Offset、Partition、ISR

broker：缓存代理，Kafka 集群中的一台或多台服务器统称为 broker 。负责消息存储和转发

​	Controller：Kafka 集群中，通过 Zookeeper 选举某个 Broker 作为 Controller ，用来进行 leader election 以	及 各种 failover 。

topic：特指 Kafka 处理的消息源（feeds of messages）的不同分类。

Partition：Topic 物理上的分组（分区），一个 Topic 可以分为多个 Partition 。每个 Partition 都是一个有序的队列。Partition 中的每条消息都会被分配一个有序的 id（offset）。

- replicas：Partition 的副本集，保障 Partition 的高可用。
- leader：replicas 中的一个角色，Producer 和 Consumer 只跟 Leader 交互。
- follower：replicas 中的一个角色，从 leader 中复制数据，作为副本，一旦 leader 挂掉，会从它的 followers 中选举出一个新的 leader 继续提供服务。

offset：消息在日志中的位置，可以理解是消息在 partition 上的偏移量，也是代表该消息的
唯一序号

Message：消息，是通信的基本单位，每个 Producer 可以向一个 Topic（主题）发布一些消息。

Producer：消息和数据生产者，向 Kafka 的一个 Topic 发布消息的过程，叫做 producers 。

Consumer：消息和数据消费者，订阅 Topic ，并处理其发布的消息的过程，叫做 consumers 。

Consumer Group：消费者分组，每个 Consumer 都属于一个 Consumer group，每条消息只能被 Consumer group 中的一个 Consumer 消费，但可以被多个 Consumer group 消费。

ZooKeeper：Kafka 通过 ZooKeeper 来存储集群的 Topic、Partition 等元信息等。

ISR：In-Sync Replicas 副本同步队列



- Kafka 的整体架构

![img](https://mmbiz.qpic.cn/mmbiz_png/US10Gcd0tQHLwHLA3icQKicV63C3yeibBhN9wySp8c7fdvLnIcSobPibF0AeibBzqsk83IlYr28e5X6E049JvDDH8ZQ/640?tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

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
   
   https://www.jianshu.com/p/fb8b792a7a9b
   
   窄依赖：父RDD每个分区内的数据，都只会被子RDD中指定的唯一一个分区所消费：
   
   宽依赖：父RDD中每个分区内的数据，在子RDD中不完全处于一个分区；

  

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

[性能调优之在实际项目中分配更多资源.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\性能调优之在实际项目中分配更多资源.xls) 

[性能调优之在实际项目中使用fastutil优化数据格式.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\性能调优之在实际项目中使用fastutil优化数据格式.xls) 

[性能调优之在实际项目中使用Kryo序列化.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\性能调优之在实际项目中使用Kryo序列化.xls)

 [性能调优之在实际项目中调节数据本地化等待时长.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\性能调优之在实际项目中调节数据本地化等待时长.xls) 

[性能调优之在实际项目中重构RDD架构以及RDD持久化.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\性能调优之在实际项目中重构RDD架构以及RDD持久化.xls) 

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

4. 布隆过滤器：

   https://blog.csdn.net/DEMON3344/article/details/85778279

   https://www.cnblogs.com/CodeBear/p/10911177.html

5. bit-map

6. 堆

7. 字典树

8. 倒排索引

### 关于面试

我们先来看几个典型的 BAT 招聘大数据开发工程师的要求：

![百度](https://images.gitbook.cn/7d8115b0-5dbb-11e9-816b-9f83d06952bd)

![阿里](https://images.gitbook.cn/87fc09a0-5dbb-11e9-816b-9f83d06952bd)

![腾讯](https://images.gitbook.cn/8ef4b4a0-5dbb-11e9-816b-9f83d06952bd)

以上三则招聘分别来自百度阿里和腾讯，那么我们把他们的要求分类归纳：

1. 1~2 门语言基础
2. 扎实的后台开发基础
3. 离线计算方向（Hadoop/Hbase/Hive 等）
4. 实时计算方向（Spark/Flink/Kafka 等）
5. 知识面更宽优先（对口经验 + 其他）

如果你是 Apache 顶级项目的 Committer 那么恭喜你，你将会是各大公司竞相挖角对象。

我们在写简历时应该注意什么？

我曾经作为面试官面试过很多人，我认为一个比较优秀的简历应该包含：

1. 漂亮的排版，杜绝使用 word，格式化的模板，推荐使用 MarkDown 生成 PDF
2. 不要堆砌技术名词，不会的不了解的不要写，否则你会被虐的体无完肤
3. 1~2 个突出的项目经历，不要让你的简历看起来像培训班或者应届生一样浅显
4. 写在简历上的项目我建议你要熟悉每一个细节，即使不是你开发的也要知道是如何实现的
5. 如果有一段知名企业的实习或者工作经历那么是很大的加分

技术深度和广度？

在技术方向，大家更喜欢一专多能，深度广度兼具的同学，当然这个要求已经很高了。但是最起码应该做到的是，你用到的技术不仅要熟悉如何使用，也应该要知晓原理。

如果你曾经作为组内的核心开发或者技术 leader 那么要突出自己的技术优势和前瞻性，不仅要熟悉使用现在已经有的`轮子`的优劣，也要对未来的技术发展有一定的前瞻性和预见性。

如何投递简历？

最建议的方式是直接找到招聘组的负责人或者让同学或者同事内推。

我会在后面陆续推出针对某个知识点的专题，欢迎大家和我继续交流。



# 面试题

## 1、**HashMap 和 Hashtable 区别**

HashMap和Hashtable的存储和遍历都是无序的！

- 继承的类不同：HashMap继承的是AbstractMap类；Hashtable 继承Dictionary类。但是都实现了Map接口。
- 线程安全问题：hashmap是非线程安全的，底层是一个Entry数组，put进来的数据，会计算其hash值，然后放到对应的bucket上去，当发生hash冲突的时候，hashmap是采用链表的方式来解决的，在对应的数组位置存放链表的头结点，对链表而言，新加入的节点会从头结点加入；Hashtable是线程安全的，是个同步容器，其中实现线程安全的方式就是给每个方法都加上Synchronized关键字。
- 关于contains方法：HashMap没有Hashtable的contains方法，有的是containsValue和containsKey；Hashtable有contains，containsValue和containsKey三个方法，其中contains和containsValue功能相同。
- key和value是否可以为null：HashMap和HashTable都不能包含重复的key，但是value可以重复。HashTable，kv都不允许出现null，但是由于kv都是Object类型的对象，put（null，null）操作编译可以通过，但是在运行的时候会抛出NullPointerException（空指针）异常；而HashMap，null可以作为k，但是这样的k只有一个，而v的话，可以有多个k的v为null值。当get方法返回null值的时候，可能是没有该k，也有可能该键对应值为null，所以不能用get方法查询HashMap存不存在指定的k，而应该用containsKey方法判断。
- 遍历方式的不同：HashMap使用的是keySet、entrySet、values和entrySet+Iterator；Hashtable使用的是keySet、keys+Enumeration、entrySet+Iterator和elements；
- hash值不同：HashTable直接使用对象的hashCode；HashMap重新计算hash值。(h = key.hashCode()) ^ (h >>> 16);
- 内部实现的数组初始化和扩容不同：在不指定容量的情况下，HashTable的默认容量为11，而HashMap为16；Hashtable不要求底层数组的容量一定要为2的整数次幂，所以扩容时，将容量变为原来的2倍加1。而HashMap则要求一定为2的整数次幂，扩容时将容量变为原来的2倍。



## 2、**Java 垃圾回收机制和生命周期**

**对象是否会被回收的两个经典算法：引用计数法，和可达性分析算法。**

**引用计数法**

简单的来说就是判断对象的引用数量。实现方式：给对象共添加一个引用计数器，每当有引用对他进行引用时，计数器的值就加1，当引用失效，也就是不在执行此对象是，他的计数器的值就减1，若某一个对象的计数器的值为0，那么表示这个对象没有人对他进行引用，也就是意味着是一个失效的垃圾对象，就会被gc进行回收。

　但是这种简单的算法在当前的jvm中并没有采用，原因是他并不能解决对象之间循环引用的问题。

　假设有A和B两个对象之间互相引用，也就是说A对象中的一个属性是B，B中的一个属性时A,这种情况下由于他们的相互引用，从而是垃圾回收机制无法识别。



![img](https://upload-images.jianshu.io/upload_images/7946172-39bbaf7199aaabc6?imageMogr2/auto-orient/strip|imageView2/2/w/778/format/webp)

**可达性分析（Reachability Analysis）**

从 GC Roots 开始向下搜索，搜索所走过的路径称为引用链。当一个对象到 GC Roots 没有任何引用链相连时，则证明此对象是不可用的。不可达对象。

**1）标记-清除算法**

标记-清除（Mark-Sweep）算法，是现代垃圾回收算法的思想基础。

标记-清除算法将垃圾回收分为两个阶段：标记阶段和清除阶段。

一种可行的实现是，在标记阶段，首先通过根节点，标记所有从根节点开始的可达对象。因此，未被标记的对象就是未被引用的垃圾对象（好多资料说标记出要回收的对象，其实明白大概意思就可以了）。然后，在清除阶段，清除所有未被标记的对象。

![img](https://mmbiz.qpic.cn/mmbiz_jpg/US10Gcd0tQHUhBdy57lJfb9VnUEMsHg9pxcrcdibO8UEdegCMxaGvYHXBJljxQ6aHJathceoZFW7lEDPic71QReQ/640?tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

- 缺点：

- - 1、效率问题，标记和清除两个过程的效率都不高。
  - 2、空间问题，标记清除之后会产生大量不连续的内存碎片，空间碎片太多可能会导致以后在程序运行过程中需要分配较大的对象时，无法找到足够的连续内存而不得不提前触发另一次垃圾收集动作。

**2）标记-整理算法**

标记整理算法，类似与标记清除算法，不过它标记完对象后，不是直接对可回收对象进行清理，而是让所有存活的对象都向一端移动，然后直接清理掉边界以外的内存。

![img](https://mmbiz.qpic.cn/mmbiz_jpg/US10Gcd0tQHUhBdy57lJfb9VnUEMsHg9JUaKNjJzpBORIV9MLGd1wnHPRgrAseFjrfZtiaJ0UpzVAr7IXKbJLQw/640?tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

- 优点：

- - 1、相对标记清除算法，解决了内存碎片问题。
  - 2、没有内存碎片后，对象创建内存分配也更快速了（可以使用TLAB进行分配）。

- 缺点：

- - 1、效率问题，（同标记清除算法）标记和整理两个过程的效率都不高。

**3）复制算法**

复制算法，可以解决效率问题，它将可用内存按容量划分为大小相等的两块，每次只使用其中的一块，当这一块内存用完了，就将还存活着的对象复制到另一块上面，然后再把已经使用过的内存空间一次清理掉，这样使得每次都是对整个半区进行内存回收，内存分配时也就不用考虑内存碎片等复杂情况，只要移动堆顶指针，按顺序分配内存即可（还可使用TLAB进行高效分配内存）。

![img](https://mmbiz.qpic.cn/mmbiz_jpg/US10Gcd0tQHUhBdy57lJfb9VnUEMsHg9M5fV8EOEicGDicfU03s1eog4cXic0ZkbJlLxGBP83v48nt5Ik8uuTh0aA/640?tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

- 图的上半部分是未回收前的内存区域，图的下半部分是回收后的内存区域。通过图，我们发现不管回收前还是回收后都有一半的空间未被利用。

- 优点：

- - 1、效率高，没有内存碎片。

- 缺点：

- - 1、浪费一半的内存空间。
  - 2、复制收集算法在对象存活率较高时就要进行较多的复制操作，效率将会变低。

**4）分代收集算法**

当前商业虚拟机都是采用分代收集算法，它根据对象存活周期的不同将内存划分为几块，一般是把 Java 堆分为新生代和老年代，然后根据各个年代的特点采用最适当的收集算法。

- 在新生代中，每次垃圾收集都发现有大批对象死去，只有少量存活，就选用复制算法。
- 而老年代中，因为对象存活率高，没有额外空间对它进行分配担保，就必须使用“标记清理”或者“标记整理”算法来进行回收。

- 图的左半部分是未回收前的内存区域，右半部分是回收后的内存区域。

- 对象分配策略：

- - 对象优先在 Eden 区域分配，如果对象过大直接分配到 Old 区域。
  - 长时间存活的对象进入到 Old 区域。

- 改进自复制算法
  - 现在的商业虚拟机都采用这种收集算法来回收新生代，IBM 公司的专门研究表明，新生代中的对象 98% 是“朝生夕死”的，所以并不需要按照 `1:1` 的比例来划分内存空间，而是将内存分为一块较大的 Eden 空间和两块较小的 Survivor 空间，每次使用 Eden 和其中一块 Survivor 。当回收时，将 Eden 和 Survivor 中还存活着的对象一次性地复制到另外一块 Survivor 空间上，最后清理掉 Eden 和刚才用过的 Survivor 空间。
  - HotSpot 虚拟机默认 Eden 和 2 块 Survivor 的大小比例是 `8:1:1`，也就是每次新生代中可用内存空间为整个新生代容量的 90%（80%+10%），只有 10% 的内存会被“浪费”。当然，98% 的对象可回收只是一般场景下的数据，我们没有办法保证每次回收都只有不多于 10% 的对象存活，当 Survivor 空间不够用时，需要依赖其他内存（这里指老年代）进行分配担保（Handle Promotion）。


对象分配规则：

- 对象优先分配在 Eden 区。

如果 Eden 区无法分配，那么尝试把活着的对象放到 Survivor0 中去（Minor GC）ps：清除 Eden、Survivor 区，就是 Minor GC 。总结来说，分配的顺序是：新生代（Eden => Survivor0 => Survivor1）=> 老年代

- - 如果 Survivor1 可以放入，那么放入 Survivor1 之后清除 Eden 和 Survivor0 ，之后再把 Survivor1 中的对象复制到 Survivor0 中，保持 Survivor1 一直为空。
  - 如果 Survivor1 不可以放入，那么直接把它们放入到老年代中，并清除 Eden 和 Survivor0 ，这个过程也称为**分配担保**。
  - 如果 Survivor0 可以放入，那么放入之后清除 Eden 区。
  - 如果 Survivor0 不可以放入，那么尝试把 Eden 和 Survivor0 的存活对象放到 Survivor1 中。

- 大对象直接进入老年代（大对象是指需要大量连续内存空间的对象）。

  这样做的目的是，避免在 Eden 区和两个 Survivor 区之间发生大量的内存拷贝（新生代采用复制算法收集内存）。

- 长期存活的对象进入老年代。

  虚拟机为每个对象定义了一个年龄计数器，如果对象经过了 1 次 Minor GC 那么对象会进入 Survivor 区，之后每经过一次 Minor GC 那么对象的年龄加 1 ，知道达到阀值对象进入老年区。

- 动态判断对象的年龄。

  为了更好的适用不同程序的内存情况，虚拟机并不是永远要求对象的年龄必须达到 MaxTenuringThreshold 才能晋升老年代。如果 Survivor 区中相同年龄的所有对象大小的总和大于 Survivor 空间的一半，年龄大于或等于该年龄的对象可以直接进入老年代。

- 空间分配担保。

  每次进行 Minor GC 时，JVM 会计算 Survivor 区移至老年区的对象的平均大小，如果这个值大于老年区的剩余值大小则进行一次 Full GC ，如果小于检查 HandlePromotionFailure 设置，如果 `true` 则只进行 Monitor GC ，如果 `false` 则进行 Full GC 。

如下是一张对象创建时，分配内存的图：![img](https://mmbiz.qpic.cn/mmbiz_png/US10Gcd0tQHUhBdy57lJfb9VnUEMsHg96smy1tmmt0ImIPHDBNAuLvSTapc5kqUTvD4PhdXjNsI0zIlLA2zakA/640?tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)



## 3、怎么解决 Kafka 数据丢失的问题

**Broker端：**

Kafka 某个 Broker 宕机，然后重新选举 Partition 的 leader，此时其他的 follower 刚好还有些数据没有同步，结果此时 leader 挂了，然后选举某个 follower 成 leader 之后，就少了一些数据。

- 给 Topic 设置 `replication.factor` 参数：这个值必须大于 1，要求每个 partition 必须有至少 2 个副本。
- 在 Producer 端设置 `acks=all`：这个是要求Producer 需要等待 ISR 中的所有 Follower 都确认接收到数据后才算一次发送完成。
- 但是如果Broker 集群里，ISR中只有leader（其他节点都和zookeeper断开连接，或者是都没追上）的情况。此时，`acks=all` 和 `acks=1` 就等价了。所以在Borker端设置 `min.insync.replicas` 参数：这个值必须大于 1 ，这个是要求一个 leader 至少感知到有至少一个 follower 还保持连接，没掉队，这样才能确保 leader 挂了至少还有一个 follower 。
- 关闭unclean leader选举，即不允许非ISR中的副本被选举为leader，以避免数据丢失

某种状态下，follower2副本落后leader副本很多，并且也不在leader副本和follower1副本所在的ISR（In-Sync Replicas）集合之中。follower2副本正在努力的追赶leader副本以求迅速同步，并且能够加入到ISR中。但是很不幸的是，此时ISR中的所有副本都突然下线，情形如下图所示：

![img](https://img-blog.csdn.net/20180624114304782?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTMyNTY4MTY=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

此时follower2副本还在，就会进行新的选举，不过在选举之前首先要判断unclean.leader.election.enable参数的值。如果unclean.leader.election.enable参数的值为false，那么就意味着非ISR中的副本不能够参与选举，此时无法进行新的选举，此时整个分区处于不可用状态。如果unclean.leader.election.enable参数的值为true，那么可以从非ISR集合中选举follower副本称为新的leader。

![img](https://img-blog.csdn.net/20180624114320443?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTMyNTY4MTY=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)


我们进一步考虑unclean.leader.election.enable参数为true的情况，在上面的这种情形中follower2副本就顺其自然的称为了新的leader。随着时间的推进，新的leader副本从客户端收到了新的消息，如上图所示。

此时，原来的leader副本恢复，成为了新的follower副本，准备向新的leader副本同步消息，但是它发现自身的LEO比leader副本的LEO还要大。Kafka中有一个准则，follower副本的LEO是不能够大于leader副本的，所以新的follower副本就需要截断日志至leader副本的LEO处。

![img](https://img-blog.csdn.net/20180624114334568?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTMyNTY4MTY=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

如上图所示，新的follower副本需要删除消息4和消息5，之后才能与新的leader副本进行同步。之后新的follower副本和新的leader副本组成了新的ISR集合，参考下图。

![img](https://img-blog.csdn.net/20180624114343727?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTMyNTY4MTY=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)


原本客户端已经成功的写入了消息4和消息5，而在发生日志截断之后就意味着这2条消息就丢失了，并且新的follower副本和新的leader副本之间的消息也不一致。也就是说如果unclean.leader.election.enable参数设置为true，就有可能发生数据丢失和数据不一致的情况，Kafka的可靠性就会降低；而如果unclean.leader.election.enable参数设置为false，Kafka的可用性就会降低。具体怎么选择需要读者更具实际的业务逻辑进行权衡，可靠性优先还是可用性优先。

**producer端来说：**

- push为同步，producer.type设置为 sync； 

  如果是异步会丢失数据，flush是kafka的内部机制,kafka优先在内存中完成数据的交换,然后将数据持久化到磁盘.kafka首先会把数据缓存(缓存到内存中)起来再批量flush.可以通过log.flush.interval.messages和log.flush.interval.ms来配置flush间隔。内存缓冲池过满，内存溢出会让数据写入过快，但是落入磁盘过慢，有可能会造成数据的丢失。

- 设置 retries=MAX（很大很大很大的一个值，无限次重试的意思）：这个是要求一旦写入失败，就无限重试，卡在这里了。

**Consumer端：**

消费者是poll拉取数据进行消费的，唯一导致消费者弄丢数据的情况为，消费到了数据，consumer自动提交offset，让broker以为你已经消费好了数据，但是如果你才刚开始准备处理这个数据就挂了，那么这条数据就丢失了。

high-level版本自动提交 offset 

所以说要使用low-level版本关闭自动提交 offset ，在处理完之后自己手动提交 offset ，就可以保证数据不会丢。但是这样的话保证不了消息的幂等性，也就是说，刚处理完还未提交offset，自己就挂了，下一次消费的时候会重复消费数据。所以为了保证幂等性：

- 写库操作，可以先用主键查一下，如果有这条数据就update。
- 写入redis，没问题，直接set幂等。
- 让生产者发送每条数据的时候加一个全局唯一id，消费一条数据先去缓存中查找，缓存可以是写入内存的queue或者是redis，没有这条数据就处理并加入缓存，有这条数据就说明之前消费过，就不处理了。
- 数据库的唯一键约束，保证数据不会重复插入多条，只会报错但是不会有重复数据。

```java
	 //producer用于压缩数据的压缩类型。默认是无压缩。正确的选项值是none、gzip、snappy。压缩最好用于批量处理，批量处理消息越多，压缩性能越好
     props.put("compression.type", "gzip");
     //增加延迟
     props.put("linger.ms", "50");
     //这意味着leader需要等待所有备份都成功写入日志，这种策略会保证只要有一个备份存活就不会丢失数据。这是最强的保证。，
     props.put("acks", "all");
     //无限重试，直到你意识到出现了问题，设置大于0的值将使客户端重新发送任何数据，一旦这些数据发送失败。注意，这些重试与客户端接收到发送错误时的重试没有什么不同。允许重试将潜在的改变数据的顺序，如果这两个消息记录都是发送到同一个partition，则第一个消息失败第二个发送成功，则第二条消息会比第一条消息出现要早。
     props.put("retries ", MAX_VALUE);
     props.put("reconnect.backoff.ms ", 20000);
     props.put("retry.backoff.ms", 20000);
     //关闭unclean leader选举，即不允许非ISR中的副本被选举为leader，以避免数据丢失
     props.put("unclean.leader.election.enable", false);
     //关闭自动提交offset
     props.put("enable.auto.commit", false);
     限制客户端在单个连接上能够发送的未响应请求的个数。设置此值是1表示kafka broker在响应请求之前client不能再向同一个broker发送请求。注意：设置此参数是为了避免消息乱序
     props.put("max.in.flight.requests.per.connection", 1);
```



## 4、zookeeper 是如何保证数据一致性的

Zookeeper保证了顺序一致性（满足最终一致性）。从整体（read 操作 +write 操作）上来说是 sequential consistency(顺序一致性)，写操作实现了 Linearizability（线性一致性：也叫强一致性，或者原子一致性）。

**顺序一致性：**

从同一个客户端发起的事务请求，最终将会严格地按照其发起顺序被应用到 Zookeeper 中去。

顺序一致性是 Zookeeper 中非常重要的一个特性。

- - 所有的更新都是全局有序的，每个更新都有一个唯一的时间戳，这个时间戳称为zxid(Zookeeper Transaction Id)。
  - 而读请求只会相对于更新有序，也就是读请求的返回结果中会带有这个 Zookeeper 最新的 zxid 。

**Paxos的基本思路：(**[深入解读zookeeper一致性原理](https://links.jianshu.com/go?to=http%3A%2F%2Fwely.iteye.com%2Fblog%2F2362118)**)**

假设有一个社团，其中有团员、议员（决议小组成员）两个角色

团员可以向议员申请提案来修改社团制度

议员坐在一起，拿出自己收到的提案，对每个提案进行投票表决，超过半数通过即可生效

为了秩序，规定每个提案都有编号ID，按顺序自增

每个议员都有一个社团制度笔记本，上面记着所有社团制度，和最近处理的提案编号，初始为0

投票通过的规则：

新提案ID 是否大于 议员本中的ID，是议员举手赞同

如果举手人数大于议员人数的半数，即让新提案生效

**例如：**

刚开始，每个议员本子上的ID都为0，现在有一个议员拿出一个提案：团费降为100元，这个提案的ID自增为1

每个议员都和自己ID对比，一看 1>0，举手赞同，同时修改自己本中的ID为1

发出提案的议员一看超过半数同意，就宣布：1号提案生效

然后所有议员都修改自己笔记本中的团费为100元

以后任何一个团员咨询任何一个议员："团费是多少？"，议员可以直接打开笔记本查看，并回答：团费为100元

可能会有极端的情况，就是多个议员一起发出了提案，就是并发的情况

**例如**

刚开始，每个议员本子上的编号都为0，现在有两个议员（A和B）同时发出了提案，那么根据自增规则，这两个提案的编号都为1，但只会有一个被先处理

假设A的提案在B的上面，议员们先处理A提案并通过了，这时，议员们的本子上的ID已经变为了1，接下来处理B的提案，由于它的ID是1，不大于议员本子上的ID，B提案就被拒绝了，B议员需要重新发起提案

上面就是Paxos的基本思路，对照ZooKeeper，对应关系就是：

团员 -client

议员 -server

议员的笔记本 -server中的数据

提案 -变更数据的请求

提案编号 -zxid（ZooKeeper Transaction Id）

提案生效 -执行变更数据的操作

ZooKeeper中还有一个leader的概念，就是把发起提案的权利收紧了，以前是每个议员都可以发起提案，现在有了leader，大家就不要七嘴八舌了，先把提案都交给leader，由leader一个个发起提案

Paxos算法就是通过投票、全局编号机制，使同一时刻只有一个写操作被批准，同时并发的写操作要去争取选票，只有获得过半数选票的写操作才会被批准，所以永远只会有一个写操作得到批准，其他的写操作竞争失败只好再发起一轮投票

**ZooKeeper采用了Zab协议。**

Zab协议 zookeeper automatic broadcast 两种模式

1. 恢复(选主)模式，可用性 - 当服务重启或者在leader崩溃之后，进入恢复模式，当leader被选举出来且大多数server完成了和leader的状态同步后恢复模式结束。
2. 广播模式（同步）一致性

Zab做了如下几条保证，来达到ZooKeeper要求的一致性。

(a) Zab要保证同一个leader的发起的事务要按顺序被apply，同时还要保证只有先前的leader的所有事务都被apply之后，新选的leader才能在发起事务。这个是为了保证每个Server的数据视图的一致性

(b) 一些已经Skip的消息，需要仍然被Skip。

(c)如果任何一个server按T、T’的顺序提交了事务，那么所有server都必须按T、T’的顺序提交事务。

为了能够实现，Skip已经被skip的消息。我们在Zxid中引入了epoch。

ZooKeeper 采用了递增的事务 id 来识别，所有的 proposal（提议）都在被提出的时候加上了 zxid 。zxid 实际上是一个 64 位数字。

- 高 32 位是 epoch 用来标识 Leader 是否发生了改变，如果有新的 Leader 产生出来，epoch会自增。
- 低 32 位用来递增计数。

当新产生的 peoposal 的时候，会依据数据库的两阶段过程，首先会向其他的 Server 发出事务执行请求，如果超过半数的机器都能执行并且能够成功，那么就会开始执行。

假设ZK集群由三台机器组成，Server1、Server2、Server3。Server1为Leader，他生成了三条Proposal，P1、P2、P3。但是在发送完P1之后，Server1就挂了。

![img](http://images.cnitblog.com/blog/671563/201412/031301242331261.png)

Server1挂掉之后，Server3被选举成为Leader，因为在Server3里只有一条Proposal—P1。所以，Server3在P1的基础之上又发出了一条新Proposal—P2＇，由于Leader发生了变换，epoch要加1，所以epoch由原来的0变成了1，而counter要置0。那么，P2＇的Zxid为10。

![img](http://images.cnitblog.com/blog/671563/201412/031301245926217.png)

Server2发送完P2＇之后，它也挂了。此时Server1已经重启恢复，并再次成为了Leader。那么，Server1将发送还没有被deliver的Proposal—P2和P3。由于Server2中P2＇的Zxid为10，而Leader-Server1中P2和P3的Zxid分别为02和03，P2＇的epoch位高于P2和P3。所以此时Leader-Server1的P2和P3都会被拒绝,那么我们Zab的第二条保证也就实现了。

![img](http://images.cnitblog.com/blog/671563/201412/031301251394117.png)



## 5、hadoop 和 spark 在处理数据时，处理出现内存溢出的方法有哪些？

**Hadoop来说**

**堆内存溢出：**

mapreduce.map.java.opts=-Xmx2048m	表示jvm堆内存

mapreduce.map.memory.mb=2304	(container的内存）

mapreduce.reduce.java.opts=-=-Xmx2048m	(默认参数，表示jvm堆内存)

mapreduce.reduce.memory.mb=2304	(container的内存）

**mapreduce.{map|reduce}.java.opts能够通过Xmx设置JVM最大的heap的使用，一般设置为0.75倍的memory.mb，因为需要为java code等预留些空间**

**栈内存溢出：**

StackOverflowError，递归深度太大，在程序中减少递归。

**MRAppMaster内存不足：**

yarn.app.mapreduce.am.command-opts=-Xmx1024m(默认参数，表示jvm堆内存)

yarn.app.mapreduce.am.resource.mb=1536(container的内存)

**Spark来说**

**Map过程产生大量对象内存溢出：**

​	rdd.map(x => for(i <- 1 to 10000000) yiled i.toString)，每个rdd产生大量对象会造成内存溢出问题，通过减少Task大小，也就是先调用repartion方法增加分区再map。

mappartion和foreachpartion：

​	这两个方法，虽然对比map方法，每个task只执行function1次，会一次把整个partion的数据拿进内存，性能比较高。写入数据库的时候会减少创建数据库连接的次数。但是如果一个partion内数据太多，会直接oom。可以进行repartion操作。

**数据不平衡导致内存溢出**

​	repartion操作。

**coalesce算子**

​	coalesce合并多个小文件，开始有100个文件，coalesce（10）后产生10个文件，但是coalesce不产生shuffle，之后的所有操作都是变成10个task，每个task一次读取10个文件执行，用的是原先的10倍内存，会oom。解决办法就是开始执行100个task，然后处理后的结果经过用repation（10）的shuffle过程变成10个分区。

**shuffle内存溢出**

​	 Executor 端的任务并发度，多个同时运行的 Task 会共享 Executor 端的内存，使得单个 Task 可使用的内存减少。

​	数据倾斜，有可能造成单个 Block 的数据非常的大

**RDD中重复数据可以转换成字符串，公用一个对象。**

**standalone模式下资源分配不均匀**

​	配置了–total-executor-cores 和 –executor-memory 这两个参数，但是没有配置–executor-cores这个参数的话，就有可能导致，每个Executor的memory是一样的，但是cores的数量不同，那么在cores数量多的Executor中，由于能够同时执行多个Task，就容易导致内存溢出的情况。这种情况的解决方法就是同时配置–executor-cores或者spark.executor.cores参数，确保Executor资源分配均匀。



## 6、java 实现快速排序

```java
public class QuickSort {
	public static void quickSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        quicksort(arr, 0, arr.length - 1);
    }
    private static void quickSort(int[] arr, int l, int r) {
        if (l < r) {
            //数组中随机一个数做划分值
            swap(arr, l + (int)(Math.random() * (r - l +1)), r);
            int[] p = partition(arr, l, r);
            quickSort(arr, l, p[0] - 1);
            quickSort(arr, p[1] + 1, r);
        }
    }
    public static int[] partition(int[] arr, int l, int r) {
    	int less = l - 1;
        int more = r;
        while (l < more) {
            if (arr[l] < arr[r]) {
                swap(arr, ++less, l++);
            } else if (arr[l] > arr[r]) {
                swap(arr, --more, l);
            } else {
                l++;
            }
        }
        swap(arr, more, r);
        // 等于区域的下标位置
        return new int[]{less + 1, more};
    }
    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
```



## 7、设计微信群发红包数据库表结构（包含表名称、字段名称、类型）

```
drop table if exists wc_groupsend_rp;
create external table wc_groupsend_rp (
     imid string, --设备ID
     wcid string, --微信号
     wcname string, --微信名
     wcgroupName string, --群名称
     rpamount double, --红包金额
     rpid string, --红包标识
     rpcount int, --红包数量
     rptype int, --红包类型 比如1拼手气红包,2为普通红包,3为指定人领取红包
     giverpdt string, --发红包时间
    setuprpdt string, --创建红包时间 点击红包按钮的时间     paydt string, --支付时间
) COMMENT '群发红包表'
PARTITIONED BY (`giverpdt` string)
row format delimited fields terminated by '\t';

create external table wc_groupcash_rp (
    rpid string, --红包标识
     imid string, --设备ID
     wcid string, --微信号
     wcname string, --微信名
    wcgroupName string, --群名称
     cashdt stirng, --红包领取时间 每领取一次更新一条数据 
     cashcount int, --领取人数
     cashamount double, --领取金额
     cashwcid string, --领取人的微信
     cashwcname string, --领取人微信昵称
     cashsum double, --已领取总金额
) COMMENT '红包领取表'
PARTITIONED BY (`rpid` string)
row format delimited fields terminated by '\t'; 
```



## 8、如何选型：业务场景、性能要求、维护和扩展性、成本、开源活跃度





## 9、spark 调优

- 算子调优 

  [算子调优之filter过后使用coalesce减少分区数量.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\算子调优之filter过后使用coalesce减少分区数量.xls) 

   [算子调优之MapPartitions提升Map类操作性能.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\算子调优之MapPartitions提升Map类操作性能.xls) 

   [算子调优之reduceByKey本地聚合介绍.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\算子调优之reduceByKey本地聚合介绍.xls) 

   [算子调优之使用foreachPartition优化写数据库性能.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\算子调优之使用foreachPartition优化写数据库性能.xls) 

   [算子调优之使用repartition解决Spark SQL低并行度的性能问题.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\算子调优之使用repartition解决Spark SQL低并行度的性能问题.xls) 

- 并行度和广播变量

 [性能调优之在实际项目中广播大变量.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\性能调优之在实际项目中广播大变量.xls) 

 [性能调优之在实际项目中调节并行度.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\性能调优之在实际项目中调节并行度.xls) 

（ [性能调优之在实际项目中分配更多资源.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\性能调优之在实际项目中分配更多资源.xls) ，

 [性能调优之在实际项目中使用fastutil优化数据格式.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\性能调优之在实际项目中使用fastutil优化数据格式.xls) ，

 [性能调优之在实际项目中使用Kryo序列化.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\性能调优之在实际项目中使用Kryo序列化.xls) ，

 [性能调优之在实际项目中调节数据本地化等待时长.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\性能调优之在实际项目中调节数据本地化等待时长.xls)  ，

[性能调优之在实际项目中重构RDD架构以及RDD持久化.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\性能调优之在实际项目中重构RDD架构以及RDD持久化.xls) ）

- Shuffle 调优

 [Shuffle调优之原理概述.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\Shuffle调优之原理概述.xls) 

 [Shuffle调优之HashShuffleManager与SortShuffleManager.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\Shuffle调优之HashShuffleManager与SortShuffleManager.xls) 

 [Shuffle调优之合并map端输出文件.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\Shuffle调优之合并map端输出文件.xls) 

 [Shuffle调优之调节map端内存缓冲与reduce端内存占比.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\Shuffle调优之调节map端内存缓冲与reduce端内存占比.xls) 

 [JVM调优之调节executor堆外内存与连接等待时长.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\JVM调优之调节executor堆外内存与连接等待时长.xls) 

 [JVM调优之原理概述以及降低cache操作的内存占比.xls](D:\Java\大数据\知识星球\SparkStormHadoop\spark\spark调优\JVM调优之原理概述以及降低cache操作的内存占比.xls) 

```java
1）使用foreachPartitions替代foreach。
原理类似于“使用mapPartitions替代map”，也是一次函数调用处理一个partition的所有数据，而不是一次函数调用处理一条数据。在实践中发现，foreachPartitions类的算子，对性能的提升还是很有帮助的。比如在foreach函数中，将RDD中所有数据写MySQL，那么如果是普通的foreach算子，就会一条数据一条数据地写，每次函数调用可能就会创建一个数据库连接，此时就势必会频繁地创建和销毁数据库连接，性能是非常低下；但是如果用foreachPartitions算子一次性处理一个partition的数据，那么对于每个partition，只要创建一个数据库连接即可，然后执行批量插入操作，此时性能是比较高的。实践中发现，对于1万条左右的数据量写MySQL，性能可以提升30%以上。
2）设置num-executors参数
参数说明：该参数用于设置Spark作业总共要用多少个Executor进程来执行。Driver在向YARN集群管理器申请资源时，YARN集群管理器会尽可能按照你的设置来在集群的各个工作节点上，启动相应数量的Executor进程。这个参数非常之重要，如果不设置的话，默认只会给你启动少量的Executor进程，此时你的Spark作业的运行速度是非常慢的。
参数调优建议：该参数设置的太少，无法充分利用集群资源；设置的太多的话，大部分队列可能无法给予充分的资源。针对数据交换的业务场景，建议该参数设置1-5。
3）设置executor-memory参数
参数说明：该参数用于设置每个Executor进程的内存。Executor内存的大小，很多时候直接决定了Spark作业的性能，而且跟常见的JVM OOM异常也有直接的关联。
参数调优建议：针对数据交换的业务场景，建议本参数设置在512M及以下。
4） executor-cores
参数说明：该参数用于设置每个Executor进程的CPU core数量。这个参数决定了每个Executor进程并行执行task线程的能力。因为每个CPU core同一时间只能执行一个task线程，因此每个Executor进程的CPU core数量越多，越能够快速地执行完分配给自己的所有task线程。
参数调优建议：Executor的CPU core数量设置为2~4个较为合适。建议，如果是跟他人共享一个队列，那么num-executors * executor-cores不要超过队列总CPU core的1/3~1/2左右比较合适，避免影响其他人的作业运行。
5） driver-memory
参数说明：该参数用于设置Driver进程的内存。
参数调优建议：Driver的内存通常来说不设置，或者设置512M以下就够了。唯一需要注意的一点是，如果需要使用collect算子将RDD的数据全部拉取到Driver上进行处理，那么必须确保Driver的内存足够大，否则会出现OOM内存溢出的问题。
6） spark.default.parallelism
参数说明：该参数用于设置每个stage的默认task数量。这个参数极为重要，如果不设置可能会直接影响你的Spark作业性能。
参数调优建议：如果不设置这个参数， Spark自己根据底层HDFS的block数量来设置task的数量，默认是一个HDFS block对应一个task。Spark官网建议的设置原则是，设置该参数为num-executors * executor-cores的2~3倍较为合适，此时可以充分地利用Spark集群的资源。针对数据交换的场景，建议此参数设置为1-10。
7） spark.storage.memoryFraction
参数说明：该参数用于设置RDD持久化数据在Executor内存中能占的比例，默认是0.6。也就是说，默认Executor 60%的内存，可以用来保存持久化的RDD数据。根据你选择的不同的持久化策略，如果内存不够时，可能数据就不会持久化，或者数据会写入磁盘。
参数调优建议：如果Spark作业中，有较多的RDD持久化操作，该参数的值可以适当提高一些，保证持久化的数据能够容纳在内存中。避免内存不够缓存所有的数据，导致数据只能写入磁盘中，降低了性能。但是如果Spark作业中的shuffle类操作比较多，而持久化操作比较少，那么这个参数的值适当降低一些比较合适。如果发现作业由于频繁的gc导致运行缓慢（通过spark web ui可以观察到作业的gc耗时），意味着task执行用户代码的内存不够用，那么同样建议调低这个参数的值。针对数据交换的场景，建议降低此参数值到0.2-0.4。
8） spark.shuffle.memoryFraction
参数说明：该参数用于设置shuffle过程中一个task拉取到上个stage的task的输出后，进行聚合操作时能够使用的Executor内存的比例，默认是0.2。也就是说，Executor默认只有20%的内存用来进行该操作。shuffle操作在进行聚合时，如果发现使用的内存超出了这个20%的限制，那么多余的数据就会溢写到磁盘文件中去，此时就会极大地降低性能。

参数调优建议：如果Spark作业中的RDD持久化操作较少，shuffle操作较多时，建议降低持久化操作的内存占比，提高shuffle操作的内存占比比例，避免shuffle过程中数据过多时内存不够用，必须溢写到磁盘上，降低了性能。如果发现作业由于频繁的gc导致运行缓慢，意味着task执行用户代码的内存不够用，那么同样建议调低这个参数的值。针对数据交换的场景，建议此值设置为0.1或以下。

资源参数参考示例:
./bin/spark-submit \

  --master yarn-cluster \

  --num-executors 1 \

  --executor-memory 512M \

  --executor-cores 2 \

  --driver-memory 512M \

  --conf spark.default.parallelism=2 \

  --conf spark.storage.memoryFraction=0.2 \

  --conf spark.shuffle.memoryFraction=0.1;
```



## 10、Flink和spark的通信框架

spark用netty，flink用akak



## 11、java 代理

java代理分为静态代理和动态代理和Cglib代理，下面进行逐个说明。

**静态代理:**

接口类AdminService.java接口

```java
package com.lance.proxy.demo.service;

public interface AdminService {
    void update();
    Object find();
}
```

实现类AdminServiceImpl.java

```java
package com.lance.proxy.demo.service;

public class AdminServiceImpl implements AdminService{
    public void update() {
        System.out.println("修改管理系统数据");
    }

    public Object find() {
        System.out.println("查看管理系统数据");
        return new Object();
    }
}
```

代理类AdminServiceProxy.java

```csharp
package com.lance.proxy.demo.service;

public class AdminServiceProxy implements AdminService {

    private AdminService adminService;

    public AdminServiceProxy(AdminService adminService) {
        this.adminService = adminService;
    }

    public void update() {
        System.out.println("判断用户是否有权限进行update操作");
        adminService.update();
        System.out.println("记录用户执行update操作的用户信息、更改内容和时间等");
    }

    public Object find() {
        System.out.println("判断用户是否有权限进行find操作");
        System.out.println("记录用户执行find操作的用户信息、查看内容和时间等");
        return adminService.find();
    }
}
```

测试类StaticProxyTest.java

```csharp
package com.lance.proxy.demo.service;

public class StaticProxyTest {
    public static void main(String[] args) {
        AdminService adminService = new AdminServiceImpl();
        AdminServiceProxy proxy = new AdminServiceProxy(adminService);
        proxy.update();
        System.out.println("=============================");
        proxy.find();
    }
}
```

输出：

```swift
判断用户是否有权限进行update操作
修改管理系统数据
记录用户执行update操作的用户信息、更改内容和时间等
=============================
判断用户是否有权限进行find操作
记录用户执行find操作的用户信息、查看内容和时间等
查看管理系统数据
```

总结：
静态代理模式在不改变目标对象的前提下，实现了对目标对象的功能扩展。
不足：静态代理实现了目标对象的所有方法，一旦目标接口增加方法，代理对象和目标对象都要进行相应的修改，增加维护成本。

**JDK动态代理**

为解决静态代理对象必须实现接口的所有方法的问题，Java给出了动态代理，动态代理具有如下特点：
 1.Proxy对象不需要implements接口；
 2.Proxy对象的生成利用JDK的Api，在JVM内存中动态的构建Proxy对象。需要使用java.lang.reflect.Proxy类

```dart
  /**
     * Returns an instance of a proxy class for the specified interfaces
     * that dispatches method invocations to the specified invocation
     * handler.
 
     * @param   loader the class loader to define the proxy class
     * @param   interfaces the list of interfaces for the proxy class
     *          to implement
     * @param   h the invocation handler to dispatch method invocations to
     * @return  a proxy instance with the specified invocation handler of a
     *          proxy class that is defined by the specified class loader
     *          and that implements the specified interfaces
     */
static Object newProxyInstance(ClassLoader loader, Class<?>[]interfaces,InvocationHandler invocationHandler );
```

方法参数说明：
 a.ClassLoader loader：指定当前target对象使用类加载器，获取加载器的方法是固定的；
 b.Class<?>[] interfaces：target对象实现的接口的类型，使用泛型方式确认类型
 c.InvocationHandler invocationHandler:事件处理,执行target对象的方法时，会触发事件处理器的方法，会把当前执行target对象的方法作为参数传入。

代码为：

AdminServiceImpl.java和AdminService.java和原来一样

AdminServiceInvocation.java

```java
package com.lance.proxy.demo.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class AdminServiceInvocation  implements InvocationHandler {

    private Object target;

    public AdminServiceInvocation(Object target) {
        this.target = target;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("判断用户是否有权限进行操作");
       Object obj = method.invoke(target);
        System.out.println("记录用户执行操作的用户信息、更改内容和时间等");
        return obj;
    }
}
```

AdminServiceDynamicProxy.java

```kotlin
package com.lance.proxy.demo.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class AdminServiceDynamicProxy {
    private Object target;
    private InvocationHandler invocationHandler;
    public AdminServiceDynamicProxy(Object target,InvocationHandler invocationHandler){
        this.target = target;
        this.invocationHandler = invocationHandler;
    }

    public Object getPersonProxy() {
        Object obj = Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), invocationHandler);
        return obj;
    }
}
```

DynamicProxyTest.java

```swift
package com.lance.proxy.demo.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicProxyTest {
    public static void main(String[] args) {

        // 方法一
        System.out.println("============ 方法一 ==============");
        AdminService adminService = new AdminServiceImpl();
        System.out.println("代理的目标对象：" + adminService.getClass());
        AdminServiceInvocation adminServiceInvocation = new AdminServiceInvocation(adminService);
        AdminService proxy = (AdminService) new AdminServiceDynamicProxy(adminService, adminServiceInvocation).getPersonProxy();
        System.out.println("代理对象：" + proxy.getClass());
        Object obj = proxy.find();
        System.out.println("find 返回对象：" + obj.getClass());
        System.out.println("----------------------------------");
        proxy.update();

        // 方法二
        System.out.println("============ 方法二 ==============");
        AdminService target = new AdminServiceImpl();
        AdminServiceInvocation invocation = new AdminServiceInvocation(adminService);
        AdminService proxy2 = (AdminService) Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), invocation);
        Object obj2 = proxy2.find();
        System.out.println("find 返回对象：" + obj2.getClass());
        System.out.println("----------------------------------");
        proxy2.update();

        // 方法三
        System.out.println("============ 方法三 ==============");
        final AdminService target3 = new AdminServiceImpl();
        AdminService proxy3 = (AdminService) Proxy.newProxyInstance(target3.getClass().getClassLoader(), target3.getClass().getInterfaces(), new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("判断用户是否有权限进行操作");
                Object obj = method.invoke(target3, args);
                System.out.println("记录用户执行操作的用户信息、更改内容和时间等");
                return obj;
            }
        });
        Object obj3 = proxy3.find();
        System.out.println("find 返回对象：" + obj3.getClass());
        System.out.println("----------------------------------");
        proxy3.update();
    }
}
```

输出结果：

```kotlin
============ 方法一 ==============
代理的目标对象：class com.lance.proxy.demo.service.AdminServiceImpl
代理对象：class com.sun.proxy.$Proxy0
判断用户是否有权限进行操作
查看管理系统数据
记录用户执行操作的用户信息、更改内容和时间等
find 返回对象：class java.lang.Object
----------------------------------
判断用户是否有权限进行操作
修改管理系统数据
记录用户执行操作的用户信息、更改内容和时间等
============ 方法二 ==============
判断用户是否有权限进行操作
查看管理系统数据
记录用户执行操作的用户信息、更改内容和时间等
find 返回对象：class java.lang.Object
----------------------------------
判断用户是否有权限进行操作
修改管理系统数据
记录用户执行操作的用户信息、更改内容和时间等
============ 方法三 ==============
判断用户是否有权限进行操作
查看管理系统数据
记录用户执行操作的用户信息、更改内容和时间等
find 返回对象：class java.lang.Object
----------------------------------
判断用户是否有权限进行操作
修改管理系统数据
记录用户执行操作的用户信息、更改内容和时间等
```

**缺点：**可以看出静态代理和JDK代理有一个共同的缺点，就是目标对象必须实现一个或多个接口。

**Cglib代理**

JDK动态代理要求target对象是一个接口的实现对象，假如target对象只是一个单独的对象，并没有实现任何接口，这时候就会用到Cglib代理(Code Generation Library)，即通过构建一个子类对象，从而实现对target对象的代理，因此目标对象不能是final类(报错)，且目标对象的方法不能是final或static（不执行代理功能）。
 Cglib依赖的jar包

```xml
  <dependency>
            <groupId>cglib</groupId>
            <artifactId>cglib</artifactId>
            <version>3.2.10</version>
  </dependency>
```

代码：

目标对象类AdminCglibService.java

```csharp
package com.lance.proxy.demo.service;

public class AdminCglibService {
    public void update() {
        System.out.println("修改管理系统数据");
    }

    public Object find() {
        System.out.println("查看管理系统数据");
        return new Object();
    }
}
```

代理类AdminServiceCglibProxy.java

```java
package com.lance.proxy.demo.service;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class AdminServiceCglibProxy implements MethodInterceptor {

    private Object target;

    public AdminServiceCglibProxy(Object target) {
        this.target = target;
    }

    //给目标对象创建一个代理对象
    public Object getProxyInstance() {
        //工具类
        Enhancer en = new Enhancer();
        //设置父类
        en.setSuperclass(target.getClass());
        //设置回调函数
        en.setCallback(this);
        //创建子类代理对象
        return en.create();
    }

    public Object intercept(Object object, Method method, Object[] arg2, MethodProxy proxy) throws Throwable {

        System.out.println("判断用户是否有权限进行操作");
        Object obj = method.invoke(target);
        System.out.println("记录用户执行操作的用户信息、更改内容和时间等");
        return obj;
    }
}
```

Cglib代理测试类CglibProxyTest.java

```csharp
package com.lance.proxy.demo.service;

public class CglibProxyTest {
    public static void main(String[] args) {

        AdminCglibService target = new AdminCglibService();
        AdminServiceCglibProxy proxyFactory = new AdminServiceCglibProxy(target);
        AdminCglibService proxy = (AdminCglibService)proxyFactory.getProxyInstance();

        System.out.println("代理对象：" + proxy.getClass());

        Object obj = proxy.find();
        System.out.println("find 返回对象：" + obj.getClass());
        System.out.println("----------------------------------");
        proxy.update();
    }
}
```

输出结果：

```kotlin
代理对象：class com.lance.proxy.demo.service.AdminCglibService$$EnhancerByCGLIB$$41b156f9
判断用户是否有权限进行操作
查看管理系统数据
记录用户执行操作的用户信息、更改内容和时间等
find 返回对象：class java.lang.Object
----------------------------------
判断用户是否有权限进行操作
修改管理系统数据
记录用户执行操作的用户信息、更改内容和时间等
```

**Spring AOP的代理实现模式，即加入Spring中的target是接口的实现时，就使用JDK动态代理，否是就使用Cglib代理。Spring也可以通过<aop:config proxy-target-class="true">强制使用Cglib代理，使用Java字节码编辑类库ASM操作字节码来实现，直接以二进制形式动态地生成 stub 类或其他代理类，性能比JDK更强。**



## 12、**java的内存溢出和内存泄漏**

**内存溢出和内存泄漏是啥**

　　。内存溢出 out of memory，是指程序在申请内存时，没有足够的内存空间供其使用，出现out of memory；比如申请了一个integer,但给它存了long才能存下的数，那就是内存溢出。

　　。内存泄露 memory leak，是指程序在申请内存后，无法释放已申请的内存空间。向系统申请分配内存进行使用(new)，可是使用完了以后却不归还(delete)，结果你申请到的那块内存你自己也不能再访问（也许你把它的地址给弄丢了），而系统也不能再次将它分配给需要的程序。一次内存泄露危害可以忽略，但内存泄露堆积后果很严重，无论多少内存,迟早会被占光。

注：**memory leak会最终会导致out of memory！**

内存泄漏的分类（按发生方式来分类）

**常发性内存泄漏**。发生内存泄漏的代码会被多次执行到，每次被执行的时候都会导致一块内存泄漏。
**偶发性内存泄漏**。发生内存泄漏的代码只有在某些特定环境或操作过程下才会发生。常发性和偶发性是相对的。对于特定的环境，偶发性的也许就变成了常发性的。所以测试环境和测试方法对检测内存泄漏至关重要。
**一次性内存泄漏**。发生内存泄漏的代码只会被执行一次，或者由于算法上的缺陷，导致总会有一块仅且一块内存发生泄漏。比如，在类的构造函数中分配内存，在析构函数中却没有释放该内存，所以内存泄漏只会发生一次。
**隐式内存泄漏**。程序在运行过程中不停的分配内存，但是直到结束的时候才释放内存。严格的说这里并没有发生内存泄漏，因为最终程序释放了所有申请的内存。但是对于一个服务器程序，需要运行几天，几周甚至几个月，不及时释放内存也可能导致最终耗尽系统的所有内存。所以，我们称这类内存泄漏为隐式内存泄漏。

**1）Java 堆溢出**

重现方式，参见 《Java 堆溢出》 文章。https://blog.csdn.net/sells2012/article/details/18654915

另外，Java 堆溢出的原因，有可能是内存泄露，可以使用 MAT 进行分析。

**2）虚拟机栈和本地方法栈溢出**

由于在 HotSpot 虚拟机中并不区分虚拟机栈和本地方法栈，因此，对于 HotSpot 来说，虽然 `-Xoss` 参数（设置本地方法栈大小）存在，但实际上是无效的，栈容量只由 `-Xss`参数设定。

关于虚拟机栈和本地方法栈，在 Java 虚拟机规范中描述了两种异常：

- 如果线程请求的栈深度大于虚拟机所允许的最大深度，将抛出 StackOverflowError 异常。StackOverflowError 不属于 OOM 异常。
- 如果虚拟机在扩展栈时无法申请到足够的内存空间，则抛出 OutOfMemoryError 异常。

重现方式，参见 《OutOfMemoryError 异常 —— 虚拟机栈和本地方法栈溢出》 文章。

https://blog.csdn.net/en_joker/article/details/79727675

**3）运行时常量池溢出**

因为 JDK7 将常量池和静态变量放到 Java 堆里，所以无法触发运行时常量池溢出。如果想要触发，可以使用 JDK6 的版本。

重现方式，参见 《JVM 内存溢出 - 方法区及运行时常量池溢出》 文章。https://www.niuhp.com/java/jvm-oom-pg.html

**4）方法区的内存溢出**

因为 JDK8 将方法区溢出，所以无法触发方法区的内存溢出溢出。如果想要触发，可以使用 JDK7 的版本。

重现方式，参见 《Java 方法区溢出》 文章。

https://blog.csdn.net/tanga842428/article/details/52636836

**5）元数据区的内存溢出**

实际上，方法区的内存溢出在 JDK8 中，变成了元数据区的内存溢出。所以，重现方式，还是参见 《Java 方法区溢出》 文章，只是说，需要增加 `-XX:MaxMetaspaceSize=10m` VM 配置项。

**6）本机直接内存溢出**

重现方式，参见 《JVM 内存溢出 —— 直接内存溢出》 文章https://www.niuhp.com/java/jvm-oom-direct.html

另外，非常推荐一篇文章，《Java 内存溢出(OOM)异常完全指南》 

https://www.jianshu.com/p/2fdee831ed03

理论上 Java 因为有垃圾回收机制（GC）不会存在**内存泄露**问题（这也是 Java 被广泛使用于服务器端编程的一个重要原因）。然而在实际开发中，可能会存在无用但可达的对象，这些对象不能被 GC 回收也会发生内存泄露。例如说：

- Hibernate 的 Session（一级缓存）中的对象属于持久态，垃圾回收器是不会回收这些对象的，然而这些对象中可能存在无用的垃圾对象。
- 使用 Netty 的堆外的 ByteBuf 对象，在使用完后，并未归还，导致使用的一点一点在泄露。

垃圾回收机制：

- Java 中对象是采用 `new` 或者反射的方法创建的，这些对象的创建都是在堆(Heap)中分配的，所有对象的回收都是由 Java 虚拟机通过垃圾回收机制完成的。GC 为了能够正确释放对象，会监控每个对象的运行状况，对他们的申请、引用、被引用、赋值等状况进行监控。
- Java 程序员不用担心内存管理，因为垃圾收集器会自动进行管理。
- 可以调用下面的方法之一：`System#gc()` 或 `Runtime#getRuntime()#gc()` ，但 JVM 也可以屏蔽掉显示的垃圾回收调用。



## 13、hadoop 的组件有哪些？Yarn 的调度器有哪些？

工具类，HDFS（NameNode,DataNode），MapReduce，YARN

FIFO Scheduler,

​	默认情况下使用的是该调度器，即所有的应用程序都是按照提交的顺序来执行的，这些应用程序都放在一个队列中，只有在前面的一个任务执行完成之后，才可以执行后面的任务，依次执行

　缺点：如果有某个任务执行时间较长的话，后面的任务都要处于等待状态，这样的话会造成资源的使用率不高；如果是多人共享集群资源的话，缺点更是明显

Capacoty Scheduler,

​	集群会有多个队列，按照队列划分资源，每个队列中是按照FIFO方式调度的。如果某个队列的资源使用紧张，但是另一个队列的资源比较空闲，此时可以将空闲的资源暂时借用，但是一旦被借用资源的队列有新的任务提交之后，此时被借用出去的资源将会被释放还回给原队列

Fair Scheduler

​	作业都是放在作业池中的，默认情况下，每个用户都有自己的作业池，如果该队列池中只有一个任务的话，则该任务会使用该池中的所有资源。提交作业数较多的用户，不会因此而获得更多的集群资源。在特定的时间内未能公平的共享资源，就会终止池中占用过多资源的任务，将空出来的任务槽让给运行资源不足的作业池。



## 14、hadoop 的 shuffle 过程

Map端
map函数开始产生输出时，并不是简单的将它写到磁盘，而是利用缓冲的方式写到内存，并出于效率考虑，进行排序。

1）每个输入分片由一个Map任务处理。(HDFS一个块的大小默认为128M，可以设置块的大小)
2）map输出的结果会暂存在一个环形内存缓冲区中。（缓冲区默认大小为100M，由io.sort.mb属性控制）
3）当缓冲区快要溢出时（默认为缓冲区大小的80%，由io.sort.spill.percent属性控制），由一个后台线程将该缓冲区中的数据写到磁盘新建的溢出文件中。在溢出写到磁盘的过程中，map输出继续写到缓冲区，但是如果在此期间缓冲区被填满，map会被阻塞直到写磁盘过程完成。
4）在写入磁盘之前，线程首先根据Reduce任务的数目将数据划分为相同数目的分区，也就是一个Reduce任务对应一个分区的数据，这样避免Reduce任务分配到的数据不均匀。（分区就是对数据进行Hash的过程）；
5）然后对每个分区中的数据进行排序（第一次排序）；
6）如果此时设置了Combiner，将排序后的结果进行Combia操作，使得Map输出结果更紧凑，使得让尽可能少的数据写入到磁盘和传递给Reducer；
7）当Map任务输出最后一个记录时，可能会有很多的溢出文件，这时需要将这些文件合并，合并的过程中会不断地进行排序和Combia操作。（属性io.sort.factor控制一次最多合并多少流，默认10）。这样做的目的1，尽量减少每次写入磁盘的数据量，目的2，尽量减少下一复制阶段网络传输的数据量。最后合并成一个已分区且已排序的文件（第二次排序）。
8）为了减少网络传输数据量，节约磁盘空间，可以在这里将数据压缩。（mapred.compress.map.out设置为ture,mapred.map.output.compression.codec指定使用的压缩库）
9）将分区中的数据拷贝给相对应的Reduce任务。Reducer通过HTTP方式得到输出文件的分区。
Reduce端
1）Reduce会接收到不同Map任务传来的数据，并且每个Map传来的数据都是有序的。
2）如果Reduce端接收的数据量少，则直接存在内存中（缓冲区大小由mapred.job.shuffle.input.buffer.percent属性控制）；如果数据量超过了缓冲区大小的一定比例（由mapred.job.shuffle.merge.percent决定）则对数据合并后溢写到磁盘中。
3）随着溢写文件的增多，后台线程会将这些文件合并成一个更大的有序的文件，这样做是为了给后面的合并节省时间；
4）复制完所有Map输出后，Reduce任务进入排序阶段，这个阶段将合并Map输出，维持其顺序排序（第三次排序），这是循环进行的。例如，有50个Map输出，而合并因子默认是10，合并会进行5次，每次将10个文件合并成一个文件，过程中产生5个中间文件。
5）合并的过程中会产生许多的中间文件写入磁盘，但MapReduce会让写入磁盘的数据尽可能少，并且最后一次合并的结果并没有写入磁盘，而是直接输入到Reduce函数。
6）在Reduce阶段，对已排序输出中的每个键调用Reduce函数，此阶段的输出直接写入到输出文件系统HDFS。



## 15、简述 spark 集群运行的几种模式

local本地模式

standalone集群模式，clinet模式driver在客户端也就是提交application的机器上，cluster模式driver在其中一个worker上

Yarn集群模式，clinet模式driver在客户端也就是提交application的机器上，cluster模式下ApplicationMaster充当了driver，在其中一个nodeManager上



## 16、RDD 中的 reducebyKey 与 groupByKey 哪个性能高？

reducebyKey 性能高

reduceByKey会先在map端进行本地combine，减少了到reduce端数据量。

![img](https://img-blog.csdnimg.cn/20190102223245968.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjg4NDcy,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190102223503876.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjg4NDcy,size_16,color_FFFFFF,t_70)



## 17、@@@@@@简述 HBase 的读写过程





## 18、在 2.5亿个整数中，找出不重复的整数，注意：内存不足以容纳 2.5亿个整数。

​		使用2bitmap，一个整数用2个bit表示，其中00为整数不存在，01表示整数存在且仅存在一次，10表示整数存在且存在多次，11无意义。需要2^32 * 2bit = 1g然后扫描这些整数，查看与2bitmap的对应位，如果是发现了一个就把00变成01，如果发现第二次就把01变成10，再重复10不动。未发现就00不动。扫描完后，直接输出对应位是01的整数就行了。

​		内存不足以容纳这2.5亿个整数是什么意思？是不能同时就这2.5亿个整数载入内存吗？那么也就是说它的内存空间是小于250M*4B=1GB（sizeof(int)=4）???what???

​		那就用一个一个1bitmap，把存在的数写到磁盘中，然后1bitmap



## 19、CDH 和 HDP 的区别

1. CDH支持的存储组件更丰富
2. HDP支持的数据分析组件更丰富
3. HDP对多维分析及可视化有了支持，引入Druid和Superset
4. HDP的HBase数据使用Phoenix的jdbc查询；CDH的HBase数据使用映射Hive到Impala的jdbc查询，但分析数据可以存储Impala内部表，提高查询响应
5. 多维分析Druid纳入集群，会方便管理；但可视化工具Superset可以单独安装使用
6. CDH没有时序数据库，HDP将Druid作为时序数据库使用



## 20、java 原子操作

**1、什么是原子操作**
原子操作：一个或多个操作在CPU执行过程中不被中断的特性

当我们说原子操作时，需要分清楚针对的是CPU指令级别还是高级语言级别。

比如：经典的银行转账场景，是语言级别的原子操作；
而当我们说volatile修饰的变量的复合操作，其原子性不能被保证，指的是CPU指令级别。
二者的本质是一致的。

“原子操作”的实质其实并不是指“不可分割”，这只是外在表现，本质在于多个资源之间有一致性的要求，操作的中间态对外不可见。

比如：在32位机器上写64位的long变量有中间状态（只写了64位中的32位）；银行转账操作中也有中间状态（A向B转账，A扣钱了，B还没来得及加钱）

**2、Java中原子操作的实现方式**
Java使用锁和自旋CAS实现原子操作

**2.1 用CAS实现原子操作**
**2.1.2 使用CAS实现原子操作**

```java
public class Counter {

    private final AtomicInteger atomicI = new AtomicInteger(0);
    private int i = 0;

    public static void main(String[] args) {
        Counter counter = new Counter();
        ArrayList<Thread> list = new ArrayList<>(1000);
        long start = System.currentTimeMillis();
        IntStream.range(0, 100).forEach(u -> {
            list.add(new Thread(() ->
                    IntStream.range(0, 1000).forEach(v -> {
                        counter.safeCount();
                        counter.count();
                    })));
        });
        list.forEach(Thread::start);

        /* wait for all the threads to complete*/
        list.forEach(u -> {
            try {
                u.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println(counter.i);
        System.out.println(counter.atomicI.get());
        System.out.println(System.currentTimeMillis() - start);
    }

    /* 使用CAS 来实现原子操作*/
    public void safeCount() {
        for (; ; ) {
            int i = atomicI.get();
            /*Atomically sets the value to the given updated value if the current value == the expected value.*/
            /*Parameters:
                    expect - the expected value
                    update - the new value*/
            /* 其实，假如使用 原子类来实现计数器，不需要直接用 cas 的API，原子类已经提供了现成的API了*/
            boolean success = atomicI.compareAndSet(i, i + 1);
            if (success) {
                break;
            }
        }
    }

    /* 使用 锁 来实现原子操作*/
    public synchronized void safeCount1() {
        i++;
    }

    /* 线程不安全的累加*/
    public void count() {
        i++;
    }
}
```

并发包中提供了很多原子类来支持原子操作：

- AtomicInteger
- AtomicLong
- AtomicBoolean
- AtomicReference
- LongAdder

**2.1.3 CAS实现原子操作的问题**

CAS是并发包的基石，但用CAS有三个问题：

1）**ABA问题**
根源：CAS的本质是对变量的current value ,期望值 expected value 进行比较，二者相等时，再将 给定值 given update value 设为当前值。

因此会存在一种场景，变量值原来是A，变成了B，又变成了A，使用CAS检查时会发现值并未变化，实际上是变化了。
对于数值类型的变量，比如int，这种问题关系不大，但对于引用类型，则会产生很大影响。

ABA问题解决思路：版本号。在变量前加版本号，每次变量更新时将版本号加1,A -> B -> A，就变成 1A -> 2B -> 3A。
JDK5之后Atomic包中提供了AtomicStampedReference#compareAndSet来解决ABA问题。

```java
public boolean compareAndSet(@Nullable V expectedReference,
                         V newReference,
                         int expectedStamp,
                         int newStamp)
Atomically sets the value of both the reference and stamp to the given update values if the current reference is == to the expected reference and the current stamp is equal to the expected stamp.
Parameters:
expectedReference - the expected value of the reference
newReference - the new value for the reference
expectedStamp - the expected value of the stamp
newStamp - the new value for the stamp
```

2）**循环时间长则开销大**

自旋CAS若长时间不成功，会对CPU造成较大开销。不过有的JVM可支持CPU的pause指令的话，效率可有一定提升。

pause作用：

延迟流水线指令(de-pipeline)，使CPU不至于消耗过多执行资源。
可避免退出循环时因内存顺序冲突(memorey order violation )引起CPU流水线被清空(CPU pipeline flush),从而提高CPU的执行效率。

3）**只能保证一个共享变量的原子操作**

CAS只能对单个共享变量如是操作，对多个共享变量操作时则无法保证原子性，此时可以用锁。

另外，也可“取巧”，将多个共享变量合成一个共享变量来操作。比如a=2,b=t,合并起来ab=2t,然后用CAS操作ab.

JDK5提供AtomicReference保证引用对象间的原子性，它可将多个变量放在一个对象中来进行CAS操作。

**3、Java中使用锁实现原子操作**

锁机制保证只有拿到锁的线程才能操作锁定的内存区域。
JVM内部实现了多种锁，偏向锁、轻量锁、互斥锁。不过轻量锁、互斥锁（即不包括偏向锁），实现锁时还是使用了CAS，即：一个线程进入同步代码时用自CAS拿锁，退出块的时候用CAS释放锁。
synchronized锁定的临界区代码对共享变量的操作是原子操作。





## 21、Java 封装、继承和多态

- **封装**：给对象提供了隐藏内部特性和行为的能力。四种修饰符：default、public、private、protected

| 作用域    | 当前类 | 同一个package | 子孙类 | 其他package |
| --------- | ------ | ------------- | ------ | ----------- |
| public    | √      | √             | √      | √           |
| protected | √      | √             | √      | ×           |
| defalut   | √      | √             | ×      | ×           |
| private   | √      | ×             | ×      | ×           |

private只能本类可见；

public所有类都可见；

protected同一个包下的所有类可见，不同包的子类可以访问，不同包不是子类不能访问；

default只有本包可见（默认情况）

**好处：**

​		隐藏并保护对象属性和内部的状态；可以对外公开简单的接口，便于外界使用，从而提高系统的扩展性、可维护性。禁止了一些不正确的交互。

- **继承**

  extends关键字。给对象提供了从基类获取字段和方法的能力，在不修改类的情况下还可以给类添加新特性，重用了代码。

  只支持单继承；

  如果没有声明继承关系，默认继承Object类；

  子类使用父类的private属性，可以用get、set方法；

  可以通过super访问父类中被子类覆盖的方法或属性，普通方法随意调用，构造方法如果不显式调用super（），那么会默认用super（）作为父类的初始化方法。

- **多态**

  一个多态类型上的操作可以应用到其它类型的值上。

```java
class Animal{
      public String name;
      public Animal(String name){
           this.name = name;
      }
}
class Dog extends Animal{
      public String folorColor;
      public Dog(String name,StringfolorColor){
           super(name); this.folorColor = folorColor;
       }
}
class Cat extends Animal{
       public String eyesColor;
       public Cat(String name,String eyesColor){
             super(name); this.eyesColor = eyesColor;
       }
}
public class TestCasting{
     public static void main(String args[]){
         Animal a = new Animal("a");
         Cat c = new Cat("c","cEyesColor");
         Dog d = new Dog("d","dForlorColor");                 
         System.out.println(a instanceof Animal); //true      
         System.out.println(c instanceof Animal); //true     
         System.out.println(d instanceof Animal); //true
         System.out.println(a instanceof Dog); //false
         a = new Dog("d2","d2ForlorColor"); //父类引用指向子类对象，向上转型
         System.out.println(a.name); //可以访问            
         //System.out.println(a.folorColor);
         //!error 不可以访问超出Animal自身的任何属性
         System.out.println(a instanceof Animal); //是一只动物       System.out.println(a instanceof Dog); //是一只狗，但是不能访问狗里面的属性      
          Dog d2 = (Dog)a; //强制转换
          System.out.println(d2.folorColor); //将a强制转换之后，就可以访问狗里面的属性了
     }
}
```



## 22、JVM 模型

类加载器，运行时数据区（方法区和堆为线程共享数据区，java栈、本地方法栈和程序计数器为线程私有的数据区），执行引擎，本地库接口，本地方法库

- **程序计数器**： Java 线程私有，类似于操作系统里的 PC 计数器，它可以看做是当前线程所执行的字节码的行号指示器。

- - 如果线程正在执行的是一个 Java 方法，这个计数器记录的是正在执行的虚拟机字节码指令的地址；如果正在执行的是 Native 方法，这个计数器值则为空（Undefined）。
  - 此内存区域是唯一一个在 Java 虚拟机规范中没有规定任何 OutOfMemoryError 情况的区域。

- **虚拟机栈（栈内存）**：Java线程私有，虚拟机栈描述的是 Java 方法执行的内存模型：

- - 每个方法在执行的时候，都会创建一个栈帧用于存储局部变量、操作数、动态链接、方法出口等信息。
  - 每个方法调用都意味着一个栈帧在虚拟机栈中入栈到出栈的过程。

- **本地方法栈** ：和 Java 虚拟机栈的作用类似，区别是该区域为 JVM 提供使用 Native 方法的服务。

- **堆内存**（线程共享）：所有线程共享的一块区域，垃圾收集器管理的主要区域。

- - 目前主要的垃圾回收算法都是分代收集算法，所以 Java 堆中还可以细分为：新生代和老年代；再细致一点的有 Eden 空间、From Survivor 空间、To Survivor 空间等，默认情况下新生代按照 `8:1:1` 的比例来分配。
  - 根据 Java 虚拟机规范的规定，Java 堆可以处于物理上不连续的内存空间中，只要逻辑上是连续的即可，就像我们的磁盘一样。

- **方法区**（线程共享）：各个线程共享的一个区域，用于存储虚拟机加载的类信息、常量、静态变量、即时编译器编译后的代码等数据。

- - 虽然 Java 虚拟机规范把方法区描述为堆的一个逻辑部分，但是它却有一个别名叫做 Non-Heap（非堆），目的应该是与 Java 堆区分开来。

  - 运行时常量池：是方法区的一部分，用于存放编译器生成的各种字面量和符号引用。

  - **实际上，后续的版本，主要对【方法区】做了一定的调整**

  - - JDK7 的改变
    - - 存储在永久代的部分数据就已经转移到了 Java Heap 或者是 Native Heap。但永久代仍存在于 JDK7 中，但是并没完全移除。
      - 常量池和静态变量放到 Java 堆里。

    - JDK8 的改变
    - - 废弃 PermGen（永久代），新增 Metaspace（元数据区）。
      - 那么方法区还在么？FROM 狼哥 的解答：方法区在 Metaspace 中了，方法区都是一个概念的东西。😈 狼哥通过撸源码获得该信息。

    - > 因为，《Java 虚拟机规范》只是规定了有方法区这么个概念和它的作用，并没有规定如何去实现它。那么，在不同的 JVM 上方法区的实现肯定是不同的了。
      >
      > 同时，大多数用的 JVM 都是 Sun 公司的 HotSpot 。在 HotSpot 上把 GC 分代收集扩展至方法区，或者说使用永久带来实现方法区。

**JDK8 之后 Perm Space 有哪些变动? MetaSpace ⼤⼩默认是⽆限的么? 还是你们会通过什么⽅式来指定⼤⼩?**

- JDK8 后用元空间替代了 Perm Space ；字符串常量存放到堆内存中。

- MetaSpace 大小默认没有限制，一般根据系统内存的大小。JVM 会动态改变此值。

- 可以通过 JVM 参数配置

- - `-XX:MetaspaceSize` ： 分配给类元数据空间（以字节计）的初始大小（Oracle 逻辑存储上的初始高水位，the initial high-water-mark）。此值为估计值，MetaspaceSize 的值设置的过大会延长垃圾回收时间。垃圾回收过后，引起下一次垃圾回收的类元数据空间的大小可能会变大。
  - `-XX:MaxMetaspaceSize` ：分配给类元数据空间的最大值，超过此值就会触发Full GC 。此值默认没有限制，但应取决于系统内存的大小，JVM 会动态地改变此值。

**为什么要废弃永久代？**

1）现实使用中易出问题。

由于永久代内存经常不够用或发生内存泄露，爆出异常 `java.lang.OutOfMemoryError: PermGen` 。

- 字符串存在永久代中，容易出现性能问题和内存溢出。
- 类及方法的信息等比较难确定其大小，因此对于永久代的大小指定比较困难，太小容易出现永久代溢出，太大则容易导致老年代溢出。



## 23、@@@@@@Flume taildirSorce 重复读取数据解决方法





## 24、@@@@@@Flume 如何保证数据不丢





## 25、Java 类加载过程

![img](https://mmbiz.qpic.cn/mmbiz_png/US10Gcd0tQHUhBdy57lJfb9VnUEMsHg9NxrxiaZoNFVTibOKLyM45nfd8gIlaMNFhL1Q82BLRB6YFUsbtFRZSjEA/640?tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

在加载阶段，虚拟机需要完成以下三件事情：

- 通过一个类的全限定名来获取其定义的二进制字节流。
- 将这个字节流所代表的静态存储结构转化为方法区的运行时数据结构。
- 在Java堆中生成一个代表这个类的 `java.lang.Class` 对象，作为对方法区中这些数据的访问入口。

**验证阶段**

​	为了确保 Class 文件的字节流中包含的信息符合当前虚拟机的要求，并且不会危害虚拟机自身的安全。

- 文件格式验证：验证字节流是否符合 Class 文件格式的规范。例如：是否以 `0xCAFEBABE` 开头、主次版本号是否在当前虚拟机的处理范围之内、常量池中的常量是否有不被支持的类型。
- 元数据验证：对字节码描述的信息进行语义分析（注意：对比 javac 编译阶段的语义分析），以保证其描述的信息符合 Java 语言规范的要求。例如：这个类是否有父类，除了 `java.lang.Object` 之外。
- 字节码验证：通过数据流和控制流分析，确定程序语义是合法的、符合逻辑的。
- 符号引用验证：确保解析动作能正确执行。

**准备阶段**

​	正式为类变量分配内存并设置类变量初始值的阶段

​	pirvate static int size = 12。那么在这个阶段，size的值为0，而不是12。 final修饰的类变量将会赋值成真实的值。

**解析阶段**

​	是虚拟机将常量池内的符号引用替换为直接引用的过程。对类或接口、字段、类方法、接口方法、方法类型、方法句柄和调用点限定符 7 类符号引用进行

**初始化阶段**

​	在准备阶段，类变量已经经过一次初始化了，在这个阶段，则是根据程序员通过程序制定的计划去初始化类的变量和其他资源。这些资源有static{}块，构造函数，父类的初始化等。

**JVM 初始化步骤：**

- 1、假如这个类还没有被加载和连接，则程序先加载并连接该类。
- 2、假如该类的直接父类还没有被初始化，则先初始化其直接父类。
- 3、假如类中有初始化语句，则系统依次执行这些初始化语句。

https://www.cnblogs.com/javaee6/p/3714716.html

```java
public class SingleTon {
    private static SingleTon singleTon = new SingleTon();
    public static int count1;
    public static int count2 = 3;

    private SingleTon() {
        count1++;
        count2++;
    }

    public static SingleTon getInstance() {
        return singleTon;
    }

    public static void main(String[] args) {
        SingleTon singleTon = SingleTon.getInstance();
        System.out.println("count1=" + singleTon.count1);
        System.out.println("count2=" + singleTon.count2);
    }
}

输出为：
count1=1
count2=3
```



## 26、Spark Task 运行原理

![img](https://images2017.cnblogs.com/blog/1209698/201801/1209698-20180121000905099-719226531.png)

​	task运行之前的工作是Driver启动Executor，接着Executor准备好一切运行环境，并向Driver反向注册，最终Driver向Executor发送LunchTask事件消息，从Executor接受到LanchTask那一刻起，task开始通过java线程来进行以后的工作。在正式工作之前taskScheduler会进行“**数据的本地化**”

| PROCESS_LOCAL |
| ------------- |
| NODE_LOCAL    |
| NO_PREF       |
| RACK_LOCAL    |
| ANY           |

​	TaskScheduler在发送task的时候，会根据数据所在的节点发送task,这时候的数据本地化的级别是最高的，如果这个task在这个Executor中等待了三秒，重试发射了5次还是依然无法执行，那么TaskScheduler就会认为这个Executor的计算资源满了，TaskScheduler会降低一级数据本地化的级别，重新发送task到其他的Executor中执行，如果还是依然无法执行，那么继续降低数据本地化的级别。

task所做的事情：

- 通过网络拉取运行所需的资源，并反序列化
- 获取shuffleManager，从shuffleManager中获取shuffleWriter(shuffleWriter用于后面的数据处理并把返回的数据结果写入磁盘)
- 调用rdd.iterator()，并传入当前task要处理的partition(针对RDD的某个partition执行自定义的算子或逻辑函数，返回的数据都是通过上面生成的ShuffleWriter，经过HashPartitioner[默认是这个]分区之后写入对应的分区backet，其实就是写入磁盘文件中)
- 封装数据结果为MapStatus ，发送给MapOutputTracker，供ResultTask拉取。(MapStatus里面封装了ShuffleMaptask计算后的数据和存储位置地址等数据信息。其实也就是BlockManager相关信息，BlockManager 是Spark底层的内存，数据，磁盘数据管理的组件)
- ResultTask拉取ShuffleMapTask的结果数据

https://www.cnblogs.com/itboys/p/9207725.html



## 27、手写一个线程安全的单例

```
public class SingleTon {
	private static volatile SingleTon singleTon;
	private SingleTon() {
	
	}
	public static SingleTon getInstance() {
		if (singleTon == null) {
			synchronized (SingleTon.class) {
				if (singleTon == null) {
					singleTon = new SingleTon();
				}
			}
		}
		return singleTon;
	}
}
```



## 28、@@@@@@设计模式





## 29、@@@@@@impala 和 kudu 的适用场景，读写性能如何





## 30、kafka ack

当 Producer 向 Leader 发送数据时，可以通过`request.required.acks` 参数来设置数据可靠性的级别：

**acks=1（默认）**：意味着ISR中的Leader成功接收到消息，并进行了确认后才发送下一条。

**acks=0**：意味着Producer无需等待来自Broker的确认就发送下一条数据，这种模式下数据传输效率最快，但是数据会丢失，可靠性最低。

**acks=all或者是-1**：意味着Producer需要等待ISR中所有的Follower都成功接收到了数据并进行了确认，才发送下一条数据，可靠性最高，但是也不会完全保证数据不丢失，比如只有leader存在，其他节点与zookeeper断开连接或者是都没追上，全进了OSR。这样变成了acks=1。所以在Borker端设置 `min.insync.replicas` 参数：这个值必须大于 1 ，这个是要求一个 leader 至少感知到有至少一个 follower 还保持连接，没掉队，这样才能确保 leader 挂了至少还有一个 follower 。



## 31、@@@@@@phoenix 创建索引的方式及区别





## 32、@@@@@@Flink TaskManager 和 Job Manager 通信





## 33、@@@@@@Flink 双流 join方式





## 34、@@@@@@Flink state 管理和 checkpoint 的流程





## 35、@@@@@@Flink 分层架构





## 36、@@@@@@Flink 窗口





## 37、@@@@@@Flink watermark 如何处理乱序数据





## 38、@@@@@@Flink time

## 39、@@@@@@Flink 支持exactly-once 的 sink 和 source

## 40、@@@@@@Flink 提交作业的流程

## 41、@@@@@@Flink connect 和 join 区别

## 42、@@@@@@重启 task 的策略

## 43、hive 的锁





44、hive sql 优化方式

45、hadoop shuffle 过程和架构

46、如何优化 shuffle过程

47、冒泡排序和快速排序

48、Spark stage

49、spark mkrdd和Parrallilaze函数区别

50、Spark checkpoint 过程

51、二次排序

52、注册 hive udf

53、SQL 去重方法

54、Hive 分析和窗口函数

55、Hadoop 容错，一个节点挂掉然后又上线

56、掌握 JVM 原理

57、Java 并发原理

58、多线程的实现方法

59、RocksDBStatebackend实现（源码级别）

60、HashMap、ConcurrentMap和 Hashtable 区别：https://www.jianshu.com/p/a91f72310545

61、Flink Checkpoint 是怎么做的，作用到算子还是chain

62、Checkpoint失败了的监控

63、String、StringBuffer和 StringBuilder的区别

64、Kafka存储流程，为什么高吞吐：https://blog.51cto.com/12445535/2432350

65、Spark 优化方法举例

66、keyby 的最大并行度

67、Flink 优化方法

68、kafka isr 机制

69、kafka partition 的 4个状态

70、kafka 副本的 7个状态

71、taskmanager 的数量：https://cloud.tencent.com/developer/article/1500184

72、if 和 switch 的性能及 switch 支持的参数

73、kafka 零拷贝：https://cloud.tencent.com/developer/article/1421266

74、hadoop 节点容错机制：https://www.cnblogs.com/zhangyinhua/p/7681146.html

75、HDFS 的副本分布策略

76、hadoop 汇总：https://www.cnblogs.com/gala1021/p/8552850.html

77、Kudu 和Impala 权限控制

78、Time_wait状态？当server处理完client的请求后立刻closesocket此时会出现time_wait状态.





79、三次握手交换了什么？ SYN,ACK,SEQ,窗口大小：https://blog.csdn.net/whuslei/article/details/6667471

3次握手建立链接，4次握手断开链接。





80、hashmap 1.7和1.8 的区别？ ：https://blog.csdn.net/qq_36520235/article/details/82417949

1.7 是 数组+链表；1.8 是数组+链表+红黑树，为了避免死循环、提高插入效率 log(N)





81、concurrenthashmap 1.7和1.8？

分段锁，属于细粒度，比 hashtable 效率高， cas

为了保证原子操作和线程安全的





82、Kafka 的ack

-1 producer 只有收到分区内所有副本的成功写入的通知才认为推送消息成功了。

0 producer 发送一次就不再发送了，不管是否发送成功

1 producer 只要收到一个分区副本(leader的)成功写入的通知就认为推送消息成功了





83、sql 去重方法

group by 、distinct、窗口函数





84、哪些 hive sql 不能在 spark sql 上运行：https://spark.apache.org/docs/2.2.0/sql-programming-guide.html#unsupported-hive-functionality





85、 什么情况下发生死锁？ （就是说说条件，然后举个例子）：https://blog.csdn.net/hd12370/article/details/82814348

多个进程在运行过程中因争夺资源而造成的一种僵局，当进程处于这种僵持状态时，若无外力作用，它们都将无法再向前推进。

86、事务隔离级别？ 可重复读、不可重复读、读未提交、串行化

87、spark shuffle 和 hadoop shuffle ：https://0x0fff.com/spark-architecture-shuffle/

88、spark 静态内存和动态内存：https://blog.csdn.net/high2011/article/details/84575442

89、mysql btree 和 hash tree 的区别。btree 需要唯一主键，hash tree 适合>= 等，精确匹配，不适合 范围检索

90、udf、udtf和 udaf 的区别

91、hive sql 的执行过程

92、client 端，spark sql 的执行过程

93、找出数组中最长的 top10 字符串：https://www.nowcoder.com/questionTerminal/2c81f88ecd5a4cc395b5308a99afbbec

94、Flink 数据处理流程

95、Flink 与 Spark streaming 对比

96、Flink watermark 使用

97、窗口与流的结合

98、Flink 实时告警设计

99、Java：面向对象、容器、多线程、单例

100、Flink：部署、API、状态、checkpoint、savepoint、watermark、重启策略、datastream 算子和优化、job和task状态

101、Spark：原理、部署、优化

102、Kafka：读写原理、使用、优化

103、hive的外部表问题

104、spark的函数式编程

105、线性数据结构和数据结构

106、映射，rdd。

107、java的内存溢出和内存泄漏。

108、多线程的实现方法

109、HashMap、ConcurrentMap和 Hashtable 区别

110、Flink Checkpoint 是怎么做的，作用到算子还是chain

111、Checkpoint失败了的监控

112、String、StringBuffer和 StringBuilder的区别

113、Kafka存储流程，为什么高吞吐

114、Spark 优化方法举例

115、keyby 的最大并行度

116、Flink 优化方法

117、kafka isr 机制

118、kafka partition 的 4个状态

119、kafka 副本的 7个状态

120、taskmanager 的数量

121、if 和 switch 的性能

122、Hdfs读写流程（结合cap理论讲）

123、技术选型原则

124、Kafka组件介绍

125、g1和csm区别

126、熟悉的数据结构

127、spark oom处理方法

128、看了哪些源码

129、Spark task原理

130、解决过的问题

131、Hbase读写流程