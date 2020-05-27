package com.dqsy.sparkproject.spark.session;

import com.alibaba.fastjson.JSONObject;
import com.dqsy.sparkproject.conf.ConfigurationManager;
import com.dqsy.sparkproject.constant.Constants;
import com.dqsy.sparkproject.dao.*;
import com.dqsy.sparkproject.dao.factory.DAOFactory;
import com.dqsy.sparkproject.domain.*;
import com.dqsy.sparkproject.util.*;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.Optional;
import org.apache.spark.api.java.function.*;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.hive.HiveContext;
import org.apache.spark.storage.StorageLevel;
import scala.Tuple2;

import java.util.*;

/**
 * 用户访问session分析Spark作业
 * <p>
 * 接收用户创建的分析任务，用户可能指定的条件如下：
 * <p>
 * 1、时间范围：起始日期~结束日期
 * 2、性别：male或female
 * 3、年龄范围
 * 4、职业：多选
 * 5、城市：多选
 * 6、搜索词：多个搜索词，只要某个session中的任何一个action搜索过指定的关键词，那么session就符合条件
 * 7、点击品类：多个品类，只要某个session中的任何一个action点击过某个品类，那么session就符合条件
 * <p>
 * J2EE平台在接收用户创建任务的请求之后，会将任务信息插入MySQL的task表中，任务参数以JSON格式封装在task_param字段中
 * <p>
 * 接着J2EE平台会执行我们的spark-submit shell脚本，并将taskid作为参数传递给spark-submit shell脚本
 * spark-submit shell脚本，在执行时，是可以接收参数的，并且会将接收的参数，传递给Spark作业的main函数
 * 参数就封装在main函数的args数组中
 * <p>
 * 这是spark本身提供的特性
 *
 * @author liusinan
 */
public class UserVisitSession {

    private static JavaSparkContext sc;
    // 创建需要使用的DAO组件
    private static ITaskDAO taskDAO = DAOFactory.getTaskDAO();
    private static long taskid;
    private static Task task;

    public static void main(String[] args) {
        try {
            // 构建Spark上下文
            SparkConf conf = new SparkConf()
                    .setAppName(Constants.SPARK_APP_NAME_SESSION)
                    // 并行度:
                    // 你的spark-submit脚本中，会指定你的application总共要启动多少个executor，100个；
                    // 每个executor多少个cpu core，2~3个；总共application，有cpu core，200个。
                    // 官方推荐，根据你的application的总cpu core数量（在spark-submit中可以指定，200个），自己手动设置spark.default.parallelism参数，
                    // 指定为cpu core总数的2~3倍。400~600个并行度。600。
//                .set("spark.default.parallelism", "100")
                    .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                    .set("spark.storage.memoryFraction", "0.5")
                    .set("spark.shuffle.consolidateFiles", "true")
                    //map端内存缓冲
                    .set("spark.shuffle.file.buffer", "64")
                    //reduce端内存占比
                    .set("spark.shuffle.memoryFraction", "0.3")
                    //缩小reduce端缓冲大小，避免OOM（性能换运行）
                    .set("spark.reducer.maxSizeInFlight", "24")
                    //JVM GC导致的文件拉取失败
                    //第一个参数，意思就是说，shuffle文件拉取的时候，如果没有拉取到（拉取失败），最多或重试几次（会重新拉取几次文件），默认是3次。
                    .set("spark.shuffle.io.maxRetries", "60")
                    //第二个参数，意思就是说，每一次重试拉取文件的时间间隔，默认是5s钟。
                    .set("spark.shuffle.io.retryWait", "60")
                    //用Kryo去序列化和反序列化CategorySortKey
                    .registerKryoClasses(new Class[]{CategorySortKey.class, IntList.class});

            boolean local = ParamUtils.getTaskStusFromArgs(args);

            SparkUtils.setMaster(conf, local);

            sc = new JavaSparkContext(conf);

            //设置checkpoint目录
//        sc.checkpointFile("hdfs://");

            SQLContext sqlContext = SparkUtils.getSQLContext(sc.sc());

            // 导入数据
            SparkUtils.mockData(sc, sqlContext);

            // 首先得查询出来指定的任务，并获取任务的查询参数
            taskid = ParamUtils.getTaskIdFromArgs(args, Constants.SPARK_LOCAL_TASKID_SESSION);
            task = taskDAO.findById(taskid);
            if (task == null) {
                System.out.println(new Date() + ": cannot find this task with id [" + taskid + "].");
                return;
            }

            JSONObject taskParam = JSONObject.parseObject(task.getTaskParam());

            // 如果要进行session粒度的数据聚合
            // 首先要从user_behavior表中，查询出来指定日期范围内的行为数据

            /**
             * actionRDD，就是一个公共RDD
             * 第一，要用ationRDD，获取到一个公共的sessionid为key的PairRDD
             * 第二，actionRDD，用在了session聚合环节里面
             *
             * sessionid为key的PairRDD，后面多次使用
             * 1、与通过筛选的sessionid进行join，获取通过筛选的session的明细数据
             * 2、将这个RDD，直接传入aggregateBySession方法，进行session聚合统计
             *
             * 重构完以后，actionRDD，就只在最开始，使用一次，用来生成以sessionid为key的RDD
             *
             */
            JavaRDD<Row> actionRDD = SparkUtils.getActionRDDByDateRange(sqlContext, taskParam);

            JavaPairRDD<String, Row> sessionid2actionRDD = getSessionid2ActionRDD(actionRDD);

            /**
             * 持久化就是对RDD调用persist()方法，并传入一个持久化级别
             *
             * 如果是persist(StorageLevel.MEMORY_ONLY())，纯内存，无序列化，那么就可以用cache()方法来替代
             * StorageLevel.MEMORY_ONLY_SER()，纯内存，序列化
             * StorageLevel.MEMORY_AND_DISK()，内存和磁盘，无序列化
             * StorageLevel.MEMORY_AND_DISK_SER()，内存和磁盘，序列化
             * StorageLevel.DISK_ONLY()，纯磁盘
             *
             * 如果内存充足，要使用双副本高可靠机制
             * 选择后缀带_2的策略
             * StorageLevel.MEMORY_ONLY_2()
             */
            sessionid2actionRDD = sessionid2actionRDD.persist(StorageLevel.MEMORY_ONLY());

            // checkpoint操作
//         sessionid2actionRDD.checkpoint();

            // 首先将行为数据，按照session_id进行groupByKey分组
            // 此时的数据的粒度就是session粒度
            // 然后将session粒度的数据与用户信息数据进行join
            // 就可以获取到session粒度的数据，同时数据里面还包含了session对应的user的信息
            // 到此数据是<sessionid,(userid,searchKeywords,clickCategoryIds,age,professional,city,sex)>
            JavaPairRDD<String, String> sessionid2AggrInfoRDD =
                    aggregateBySession(sc, sqlContext, sessionid2actionRDD);

            // 接着，就要针对session粒度的聚合数据，按照使用者指定的筛选参数进行数据过滤
            // 匿名内部类（算子函数），访问外部对象，是要给外部对象使用final修饰的

            SessionAggrStatAccumulator sessionAggrStatAccumulator = new SessionAggrStatAccumulator();
            //注册累加器
            sc.sc().register(sessionAggrStatAccumulator, "SessionAggrStatAccumulator");
            // 重构，同时进行过滤和统计
            JavaPairRDD<String, String> filteredSessionid2AggrInfoRDD = filterSessionAndAggrStat(
                    sessionid2AggrInfoRDD, taskParam, sessionAggrStatAccumulator);
            filteredSessionid2AggrInfoRDD = filteredSessionid2AggrInfoRDD.persist(StorageLevel.MEMORY_ONLY());

            // 生成公共的RDD：通过筛选条件的session的访问明细数据
            /**
             * 重构：sessionid2detailRDD代表了通过筛选的session对应的访问明细数据
             */
            JavaPairRDD<String, Row> sessionid2detailRDD = getSessionid2detailRDD(
                    filteredSessionid2AggrInfoRDD, sessionid2actionRDD);
            sessionid2detailRDD = sessionid2detailRDD.persist(StorageLevel.MEMORY_ONLY());

            /**
             * 从Accumulator中，获取数据，插入数据库的时候一定要有一个action操作
             */
            System.out.println(filteredSessionid2AggrInfoRDD.count());

            // 计算出各个范围的session占比，并写入MySQL
            calculateAndPersistAggrStat(sessionAggrStatAccumulator.value(),
                    task.getTaskid());

            randomExtractSession(sc, task.getTaskid(), filteredSessionid2AggrInfoRDD, sessionid2detailRDD);

            /**
             * session聚合统计（统计出浏览时长和浏览深度，各个区间的session数量占总session数量的比例）
             *
             * 直接实现，思路：
             * 1、actionRDD，映射成<sessionid,Row>的格式
             * 2、按sessionid聚合，计算出每个session的浏览时长和浏览深度，生成一个新的RDD
             * 3、遍历新生成的RDD，将每个session的浏览时长和浏览深度，去更新自定义Accumulator中的对应的值
             * 4、使用自定义Accumulator中的统计值，去计算各个区间的比例
             * 5、将最后计算出来的结果，写入MySQL对应的表中
             *
             * 普通实现思路的问题：
             * 之前在session聚合的时候，映射已经做过了。单独去遍历一遍session没有必要。
             *
             * 重构实现思路：
             * 1、不要去生成任何新的RDD
             * 2、不要去单独遍历一遍session的数据
             * 3、在进行session聚合的时候，就直接计算出来每个session的浏览时长和浏览深度
             * 4、在进行过滤的时候，本来就要遍历所有的聚合session信息，此时，就可以在某个session通过筛选条件后
             * 		将其浏览时长和浏览深度，累加到自定义的Accumulator上面去
             *
             * Spark程序准则：
             * 1、尽量少生成RDD（公共RDD持久化了解一下）
             * 2、尽量少对RDD进行算子操作，在一个算子里面，实现多个需要做的功能
             * 3、尽量少对RDD进行shuffle算子操作，（groupByKey、reduceByKey、sortByKey）
             * 		shuffle操作，会导致大量的磁盘读写，严重降低性能
             * 4、性能第一
             * 		优先考虑性能；其次考虑功能代码的划分、解耦合
             */

            //获取top10热门品类
            List<Tuple2<CategorySortKey, String>> top10CategoryList =
                    getTop10Category(task.getTaskid(), sessionid2detailRDD);

            // 获取top10活跃session
            getTop10Session(sc, task.getTaskid(), top10CategoryList, sessionid2detailRDD);

            taskDAO.update(task, "FINISH");

        } catch (Exception e) {
            taskDAO.update(task, "KILLED");
            e.printStackTrace();
        } finally {
            // 关闭Spark上下文
            sc.close();
        }
    }

    /**
     * 获取sessionid2到访问行为数据的映射的RDD
     *
     * @param actionRDD
     * @return
     */
    public static JavaPairRDD<String, Row> getSessionid2ActionRDD(JavaRDD<Row> actionRDD) {
        return actionRDD.mapToPair((PairFunction<Row, String, Row>) row -> new Tuple2<String, Row>(row.getString(2), row));
    }

    /**
     * 对行为数据按session粒度进行聚合
     * <p>
     * 数据倾斜，聚合源数据
     * 放在一个hive etl中来做，形成一个新的表。对每天的用户访问行为数据，都按session粒度进行聚合，写一个hive sql。
     * 在程序中，就不做groupByKey和mapToPair了。
     * 直接从当天的session聚合表中，用Spark SQL查询出来对应的数据，即可。这个RDD在后面就可以使用了。
     * <p>
     * 这里只是说一下数据倾斜的解决方式，这个项目代码中还是用的groupByKey和mapToPair。
     *
     * @param sessionid2ActionRDD 行为数据RDD
     * @return session粒度聚合数据
     */

    private static JavaPairRDD<String, String> aggregateBySession(JavaSparkContext sc,
                                                                  SQLContext sqlContext, JavaPairRDD<String, Row> sessionid2ActionRDD) {

        // 对行为数据按session粒度进行分组
        JavaPairRDD<String, Iterable<Row>> sessionid2ActionsRDD =
                sessionid2ActionRDD.groupByKey();

        // 对每一个session分组进行聚合，将session中所有的搜索词和点击品类都聚合起来
        // sessionid2ActionsRDD：<sessionid,date,userid,pageid,actiontime,searchKeywords,clickCategoryIds....>
        // 最后获取的数据格式：<userid,partAggrInfo(sessionid,searchKeywords,clickCategoryIds)>
        JavaPairRDD<Long, String> userid2PartAggrInfoRDD = sessionid2ActionsRDD.mapToPair(
                (PairFunction<Tuple2<String, Iterable<Row>>, Long, String>) tuple -> {
                    String sessionid = tuple._1;
                    Iterator<Row> iterator = tuple._2.iterator();

                    StringBuffer searchKeywordsBuffer = new StringBuffer("");
                    StringBuffer clickCategoryIdsBuffer = new StringBuffer("");

                    Long userid = null;

                    // session的起始和结束时间
                    Date startTime = null;
                    Date endTime = null;
                    // session的浏览深度
                    int stepLength = 0;

                    // 遍历session所有的访问行为
                    while (iterator.hasNext()) {
                        // 提取每个访问行为的搜索词字段和点击品类字段
                        Row row = iterator.next();
                        if (userid == null) {
                            userid = row.getLong(1);
                        }
                        String searchKeyword = row.getString(5);
                        Long clickCategoryId = row.getLong(6);

                        // searchKeyword和clickCategoryId不会同时有值
                        // 只有搜索行为，有searchKeyword
                        // 只有点击行为，有clickCategoryId
                        // 所以，任何一行行为数据，都不可能两个字段都有，数据是可能出现null值的。
                        // 但是由于clickCategoryId为long类型，如果为null拼接字符串会出错，所以用0表示本行为是非点击行为。

                        if (StringUtils.isNotEmpty(searchKeyword)) {
                            if (!searchKeywordsBuffer.toString().contains(searchKeyword)) {
                                searchKeywordsBuffer.append(searchKeyword + ",");
                            }
                        }
                        if (clickCategoryId != 0L) {
                            if (!clickCategoryIdsBuffer.toString().contains(
                                    String.valueOf(clickCategoryId))) {
                                clickCategoryIdsBuffer.append(clickCategoryId + ",");
                            }
                        }

                        // 计算session开始和结束时间
                        Date actionTime = DateUtils.parseTime(row.getString(4));

                        if (startTime == null) {
                            startTime = actionTime;
                        }
                        if (endTime == null) {
                            endTime = actionTime;
                        }

                        if (actionTime.before(startTime)) {
                            startTime = actionTime;
                        }
                        if (actionTime.after(endTime)) {
                            endTime = actionTime;
                        }

                        // 计算session浏览深度
                        stepLength++;
                    }

                    String searchKeywords = StringUtils.trimComma(searchKeywordsBuffer.toString());
                    String clickCategoryIds = StringUtils.trimComma(clickCategoryIdsBuffer.toString());

                    // 计算session浏览时长（秒）
                    long visitLength = (endTime.getTime() - startTime.getTime()) / 1000;

                    // 返回的数据格式：<sessionid,partAggrInfo>
                    // 将每一行数据，跟对应的用户信息进行聚合
                    // 如果是跟用户信息进行聚合的话，那么key，就不应该是sessionid,而是userid，才能够跟用户信息(<userid,Row>) join
                    // 如果我们这里直接返回<sessionid,partAggrInfo>，还得再做一次mapToPair算子
                    // 将RDD映射成<userid,partAggrInfo>的格式，那么就多此一举

                    // 这里直接返回的数据格式<userid,partAggrInfo>
                    // 然后跟用户信息join的时候，将partAggrInfo关联上userInfo
                    // 然后再直接将返回的Tuple的key设置成sessionid
                    // 最后的数据格式，还是<sessionid,fullAggrInfo>

                    // 聚合数据，用|拼接
                    // key=value|key=value
                    String partAggrInfo = Constants.FIELD_SESSION_ID + "=" + sessionid + "|"
                            + Constants.FIELD_SEARCH_KEYWORDS + "=" + searchKeywords + "|"
                            + Constants.FIELD_CLICK_CATEGORY_IDS + "=" + clickCategoryIds + "|"
                            + Constants.FIELD_VISIT_LENGTH + "=" + visitLength + "|"
                            + Constants.FIELD_STEP_LENGTH + "=" + stepLength + "|"
                            + Constants.FIELD_START_TIME + "=" + DateUtils.formatTime(startTime);

                    return new Tuple2<Long, String>(userid, partAggrInfo);
                });

        // 查询所有用户数据，并映射成<userid,Row>的格式
        String sql = "select * from user_info";
        JavaRDD<Row> userInfoRDD = sqlContext.sql(sql).javaRDD();

        JavaPairRDD<Long, Row> userid2InfoRDD = userInfoRDD.mapToPair(
                (PairFunction<Row, Long, Row>) row -> new Tuple2<Long, Row>(row.getLong(0), row));

        /**
         * 采用reduce join转换为map join的方式
         *
         * userid2PartAggrInfoRDD，可能数据量大
         * userid2InfoRDD，可能数据量小
         */

        // 将session粒度聚合数据，与用户信息进行join
        JavaPairRDD<Long, Tuple2<String, Row>> userid2FullInfoRDD =
                userid2PartAggrInfoRDD.join(userid2InfoRDD);

        // 对join起来的数据进行拼接，并且返回<sessionid,fullAggrInfo>格式的数据
        // <userid,<partAggrInfo(sessionid,searchKeywords,clickCategoryIds),userInfoRow>>
        JavaPairRDD<String, String> sessionid2FullAggrInfoRDD = userid2FullInfoRDD.mapToPair(
                (PairFunction<Tuple2<Long, Tuple2<String, Row>>, String, String>) tuple -> {
                    String partAggrInfo = tuple._2._1;
                    Row userInfoRow = tuple._2._2;

                    String sessionid = StringUtils.getFieldFromConcatString(
                            partAggrInfo, "\\|", Constants.FIELD_SESSION_ID);

                    int age = userInfoRow.getInt(3);
                    String professional = userInfoRow.getString(4);
                    String city = userInfoRow.getString(5);
                    String sex = userInfoRow.getString(6);

                    String fullAggrInfo = partAggrInfo + "|"
                            + Constants.FIELD_AGE + "=" + age + "|"
                            + Constants.FIELD_PROFESSIONAL + "=" + professional + "|"
                            + Constants.FIELD_CITY + "=" + city + "|"
                            + Constants.FIELD_SEX + "=" + sex;

                    return new Tuple2<String, String>(sessionid, fullAggrInfo);
                });

        return sessionid2FullAggrInfoRDD;
    }

    /**
     * 过滤session数据，并进行聚合统计
     *
     * @param sessionid2AggrInfoRDD
     * @return
     */
    private static JavaPairRDD<String, String> filterSessionAndAggrStat(
            JavaPairRDD<String, String> sessionid2AggrInfoRDD,
            final JSONObject taskParam,
            final SessionAggrStatAccumulator sessionAggrStatAccumulator) {
        // 将所有的筛选参数拼接成一个连接串
        String startAge = ParamUtils.getParam(taskParam, Constants.PARAM_START_AGE);
        String endAge = ParamUtils.getParam(taskParam, Constants.PARAM_END_AGE);
        String professionals = ParamUtils.getParam(taskParam, Constants.PARAM_PROFESSIONALS);
        String cities = ParamUtils.getParam(taskParam, Constants.PARAM_CITIES);
        String sex = ParamUtils.getParam(taskParam, Constants.PARAM_SEX);
        String keywords = ParamUtils.getParam(taskParam, Constants.PARAM_KEYWORDS);
        String categoryIds = ParamUtils.getParam(taskParam, Constants.PARAM_CATEGORY_IDS);

        String _parameter = (startAge != null ? Constants.PARAM_START_AGE + "=" + startAge + "|" : "")
                + (endAge != null ? Constants.PARAM_END_AGE + "=" + endAge + "|" : "")
                + (professionals != null ? Constants.PARAM_PROFESSIONALS + "=" + professionals + "|" : "")
                + (cities != null ? Constants.PARAM_CITIES + "=" + cities + "|" : "")
                + (sex != null ? Constants.PARAM_SEX + "=" + sex + "|" : "")
                + (keywords != null ? Constants.PARAM_KEYWORDS + "=" + keywords + "|" : "")
                + (categoryIds != null ? Constants.PARAM_CATEGORY_IDS + "=" + categoryIds : "");

        if (_parameter.endsWith("\\|")) {
            _parameter = _parameter.substring(0, _parameter.length() - 1);
        }

        final String parameter = _parameter;

        // 根据筛选参数进行过滤
        // 用户数据敏感，所以这里只能进行简单判断，做的是一个过程，如果有更详细的数据，可以更改代码，套路是一样的。
        // <sessionid,(userid,searchKeywords,clickCategoryIds,age,professional,city,sex)>
        JavaPairRDD<String, String> filteredSessionid2AggrInfoRDD = sessionid2AggrInfoRDD.filter(
                new Function<Tuple2<String, String>, Boolean>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public Boolean call(Tuple2<String, String> tuple) throws Exception {
                        // 首先，从tuple中，获取聚合数据
                        String aggrInfo = tuple._2;

                        // 接着，依次按照筛选条件进行过滤
                        // 按照年龄范围进行过滤（startAge、endAge）
                        if (!ValidUtils.between(aggrInfo, Constants.FIELD_AGE,
                                parameter, Constants.PARAM_START_AGE, Constants.PARAM_END_AGE)) {
                            return false;
                        }

                        // 按照职业范围进行过滤（professionals）
                        if (!ValidUtils.in(aggrInfo, Constants.FIELD_PROFESSIONAL,
                                parameter, Constants.PARAM_PROFESSIONALS)) {
                            return false;
                        }

                        // 按照城市范围进行过滤（cities）
                        if (!ValidUtils.in(aggrInfo, Constants.FIELD_CITY,
                                parameter, Constants.PARAM_CITIES)) {
                            return false;
                        }

                        // 按照性别进行过滤
                        if (!ValidUtils.equal(aggrInfo, Constants.FIELD_SEX,
                                parameter, Constants.PARAM_SEX)) {
                            return false;
                        }

                        // 按照搜索词进行过滤
                        // in这个校验方法，主要判定session搜索的商品中，有任何一个，与筛选条件中任何一个搜索词相当，即通过
                        if (!ValidUtils.in(aggrInfo, Constants.FIELD_SEARCH_KEYWORDS,
                                parameter, Constants.PARAM_KEYWORDS)) {
                            return false;
                        }

                        // 按照点击品类id进行过滤
                        if (!ValidUtils.in(aggrInfo, Constants.FIELD_CLICK_CATEGORY_IDS,
                                parameter, Constants.PARAM_CATEGORY_IDS)) {
                            return false;
                        }

                        // 到此筛选完
                        // 接下来就要对session的浏览时长和浏览深度，进行统计，根据session对应的范围进行相应的累加计数
                        sessionAggrStatAccumulator.add(Constants.SESSION_COUNT);

                        // 计算出session的浏览时长和浏览深度的范围，并进行相应的累加
                        long visitLength = Long.valueOf(StringUtils.getFieldFromConcatString(
                                aggrInfo, "\\|", Constants.FIELD_VISIT_LENGTH));
                        long stepLength = Long.valueOf(StringUtils.getFieldFromConcatString(
                                aggrInfo, "\\|", Constants.FIELD_STEP_LENGTH));
                        calculateVisitLength(visitLength);
                        calculateStepLength(stepLength);

                        return true;
                    }

                    /**
                     * 计算浏览时长范围
                     * @param visitLength
                     */
                    private void calculateVisitLength(long visitLength) {
                        if (visitLength >= 1 && visitLength <= 3) {
                            sessionAggrStatAccumulator.add(Constants.TIME_PERIOD_1s_3s);
                        } else if (visitLength >= 4 && visitLength <= 6) {
                            sessionAggrStatAccumulator.add(Constants.TIME_PERIOD_4s_6s);
                        } else if (visitLength >= 7 && visitLength <= 9) {
                            sessionAggrStatAccumulator.add(Constants.TIME_PERIOD_7s_9s);
                        } else if (visitLength >= 10 && visitLength <= 30) {
                            sessionAggrStatAccumulator.add(Constants.TIME_PERIOD_10s_30s);
                        } else if (visitLength > 30 && visitLength <= 60) {
                            sessionAggrStatAccumulator.add(Constants.TIME_PERIOD_30s_60s);
                        } else if (visitLength > 60 && visitLength <= 180) {
                            sessionAggrStatAccumulator.add(Constants.TIME_PERIOD_1m_3m);
                        } else if (visitLength > 180 && visitLength <= 600) {
                            sessionAggrStatAccumulator.add(Constants.TIME_PERIOD_3m_10m);
                        } else if (visitLength > 600 && visitLength <= 1800) {
                            sessionAggrStatAccumulator.add(Constants.TIME_PERIOD_10m_30m);
                        } else if (visitLength > 1800) {
                            sessionAggrStatAccumulator.add(Constants.TIME_PERIOD_30m);
                        }
                    }

                    /**
                     * 计算浏览深度范围
                     * @param stepLength
                     */
                    private void calculateStepLength(long stepLength) {
                        if (stepLength >= 1 && stepLength <= 3) {
                            sessionAggrStatAccumulator.add(Constants.STEP_PERIOD_1_3);
                        } else if (stepLength >= 4 && stepLength <= 6) {
                            sessionAggrStatAccumulator.add(Constants.STEP_PERIOD_4_6);
                        } else if (stepLength >= 7 && stepLength <= 9) {
                            sessionAggrStatAccumulator.add(Constants.STEP_PERIOD_7_9);
                        } else if (stepLength >= 10 && stepLength <= 30) {
                            sessionAggrStatAccumulator.add(Constants.STEP_PERIOD_10_30);
                        } else if (stepLength > 30 && stepLength <= 60) {
                            sessionAggrStatAccumulator.add(Constants.STEP_PERIOD_30_60);
                        } else if (stepLength > 60) {
                            sessionAggrStatAccumulator.add(Constants.STEP_PERIOD_60);
                        }
                    }
                });
        return filteredSessionid2AggrInfoRDD;
    }

    /**
     * 获取通过筛选条件的session的访问明细数据RDD
     *
     * @param sessionid2aggrInfoRDD
     * @param sessionid2actionRDD
     * @return sessionid2detailRDD
     */
    private static JavaPairRDD<String, Row> getSessionid2detailRDD(
            JavaPairRDD<String, String> sessionid2aggrInfoRDD,
            JavaPairRDD<String, Row> sessionid2actionRDD) {
        // <sessionid,(userid,searchKeywords,clickCategoryIds,age,professional,city,sex)>
        // 去掉userid
        JavaPairRDD<String, Row> sessionid2detailRDD = sessionid2aggrInfoRDD
                .join(sessionid2actionRDD)
                .mapToPair((PairFunction<Tuple2<String, Tuple2<String, Row>>, String, Row>) tuple ->
                        new Tuple2<>(tuple._1, tuple._2._2));
        return sessionid2detailRDD;
    }

    /**
     * 随机抽取session
     *
     * @param sessionid2AggrInfoRDD
     */
    private static void randomExtractSession(
            JavaSparkContext sc,
            final long taskid,
            JavaPairRDD<String, String> sessionid2AggrInfoRDD,
            JavaPairRDD<String, Row> sessionid2actionRDD) {
        /**
         * 第一步，计算出每天每小时的session数量
         */

        // 获取<yyyy-MM-dd_HH,aggrInfo>格式的RDD
        JavaPairRDD<String, String> time2sessionidRDD = sessionid2AggrInfoRDD.mapToPair(
                (PairFunction<Tuple2<String, String>, String, String>) tuple -> {
                    String aggrInfo = tuple._2;

                    String startTime = StringUtils.getFieldFromConcatString(
                            aggrInfo, "\\|", Constants.FIELD_START_TIME);
                    String dateHour = DateUtils.getDateHour(startTime);

                    return new Tuple2<String, String>(dateHour, aggrInfo);
                });

        /**
         * 每天每小时的session数量，然后计算出每天每小时的session抽取索引，遍历每天每小时session
         * 首先抽取出的session的聚合数据，写入random_session_behavior_line表
         * 所以第一个RDD的value，应该是session聚合数据
         */

        // 得到每天每小时的session数量
        /**
         * 每天每小时的session数量的计算
         * 可能出现数据倾斜（每天高峰期）
         */
        Map<String, Long> countMap = time2sessionidRDD.countByKey();

        /**
         * 第二步，使用按时间比例随机抽取算法，计算出每天每小时要抽取session的索引
         */
        // 将<yyyy-MM-dd_HH,count>格式的map，转换成<yyyy-MM-dd,<HH,count>>的格式
        Map<String, Map<String, Long>> dateHourCountMap =
                new HashMap<String, Map<String, Long>>();

        for (Map.Entry<String, Long> countEntry : countMap.entrySet()) {
            String dateHour = countEntry.getKey();
            String date = dateHour.split("_")[0];
            String hour = dateHour.split("_")[1];

            long count = Long.valueOf(String.valueOf(countEntry.getValue()));

            Map<String, Long> hourCountMap = dateHourCountMap.get(date);
            if (hourCountMap == null) {
                hourCountMap = new HashMap<String, Long>();
                dateHourCountMap.put(date, hourCountMap);
            }

            hourCountMap.put(hour, count);
        }

        // <yyyy-MM-dd,<HH,count>>
        // 开始实现按时间比例随机抽取
        // 总共要抽取100个session，先按照天数，进行平分
        int extractNumberPerDay = 100 / dateHourCountMap.size();

        // <date,<hour,(3,5,20,102)>>
        /**
         * session随机抽取
         *
         * 随机抽取索引map
         * 之前是直接在算子里面使用了这个map
         * 每个task都会拷贝一份map副本，比较消耗内存和网络传输性能
         * 所以将map做成广播变量
         */
        final Map<String, Map<String, List<Integer>>> dateHourExtractMap =
                new HashMap<String, Map<String, List<Integer>>>();

        Random random = new Random();

        // <yyyy-MM-dd,<HH,count>>
        for (Map.Entry<String, Map<String, Long>> dateHourCountEntry : dateHourCountMap.entrySet()) {
            String date = dateHourCountEntry.getKey();
            Map<String, Long> hourCountMap = dateHourCountEntry.getValue();

            // 计算出这一天的session总数
            long sessionCount = 0L;
            for (long hourCount : hourCountMap.values()) {
                sessionCount += hourCount;
            }

            Map<String, List<Integer>> hourExtractMap = dateHourExtractMap.get(date);
            if (hourExtractMap == null) {
                hourExtractMap = new HashMap<String, List<Integer>>();
                dateHourExtractMap.put(date, hourExtractMap);
            }

            // 遍历每个小时
            for (Map.Entry<String, Long> hourCountEntry : hourCountMap.entrySet()) {
                String hour = hourCountEntry.getKey();
                long count = hourCountEntry.getValue();

                // 计算每个小时的session数量，占据当天总session数量的比例，直接乘以每天要抽取的数量
                // 就可以计算出，当前小时需要抽取的session数量
                int hourExtractNumber = (int) (((double) count / (double) sessionCount) * extractNumberPerDay);
                if (hourExtractNumber > count) {
                    hourExtractNumber = (int) count;
                }

                // 先获取当前小时的存放随机数的list
                List<Integer> extractIndexList = hourExtractMap.get(hour);
                if (extractIndexList == null) {
                    extractIndexList = new ArrayList<Integer>();
                    hourExtractMap.put(hour, extractIndexList);
                }

                // 生成上面计算出来的数量的随机数
                for (int i = 0; i < hourExtractNumber; i++) {
                    int extractIndex = random.nextInt((int) count);
                    while (extractIndexList.contains(extractIndex)) {
                        extractIndex = random.nextInt((int) count);
                    }
                    extractIndexList.add(extractIndex);
                }
            }
        }

        /**
         * fastutil
         * List<Integer>的list，对应IntList
         */
        Map<String, Map<String, IntList>> fastutilDateHourExtractMap =
                new HashMap<String, Map<String, IntList>>();

        for (Map.Entry<String, Map<String, List<Integer>>> dateHourExtractEntry : dateHourExtractMap.entrySet()) {
            String date = dateHourExtractEntry.getKey();
            Map<String, List<Integer>> hourExtractMap = dateHourExtractEntry.getValue();

            Map<String, IntList> fastutilHourExtractMap = new HashMap<String, IntList>();
            for (Map.Entry<String, List<Integer>> hourExtractEntry : hourExtractMap.entrySet()) {
                String hour = hourExtractEntry.getKey();
                List<Integer> extractList = hourExtractEntry.getValue();

                IntList fastutilExtractList = new IntArrayList();

                for (int i = 0; i < extractList.size(); i++) {
                    fastutilExtractList.add(extractList.get(i));
                }

                fastutilHourExtractMap.put(hour, fastutilExtractList);
            }

            fastutilDateHourExtractMap.put(date, fastutilHourExtractMap);
        }

        /**
         * 广播变量
         */
        final Broadcast<Map<String, Map<String, IntList>>> dateHourExtractMapBroadcast =
                sc.broadcast(fastutilDateHourExtractMap);

        /**
         * 第三步：遍历每天每小时的session，然后根据随机索引进行抽取
         */
        // 执行groupByKey算子，得到<dateHour,(session aggrInfo)>
        JavaPairRDD<String, Iterable<String>> time2sessionsRDD = time2sessionidRDD.groupByKey();

        // 用flatMap算子，遍历所有的<dateHour,(session aggrInfo)>格式的数据
        // 然后，会遍历每天每小时的session
        // 如果发现某个session恰巧在我们指定的这天这小时的随机抽取索引上
        // 那么抽取该session，直接写入MySQL的random_extract_session表
        // 将抽取出来的session id返回回来，形成一个新的JavaRDD<String>
        // 然后最后一步，是用抽取出来的sessionid，去join它们的访问行为明细数据，写入session表
        JavaPairRDD<String, String> extractSessionidsRDD = time2sessionsRDD.flatMapToPair(
                (PairFlatMapFunction<Tuple2<String, Iterable<String>>, String, String>) tuple -> {
                    List<Tuple2<String, String>> extractSessionids = new ArrayList<>();

                    String dateHour = tuple._1;
                    String date = dateHour.split("_")[0];
                    String hour = dateHour.split("_")[1];
                    Iterator<String> iterator = tuple._2.iterator();

                    /**
                     * 使用广播变量的时候
                     * 直接调用广播变量（Broadcast类型）的value() / getValue()
                     * 可以获取到之前封装的广播变量
                     */
                    Map<String, Map<String, IntList>> dateHourExtractMap1 =
                            dateHourExtractMapBroadcast.value();
                    List<Integer> extractIndexList = dateHourExtractMap1.get(date).get(hour);

                    ISessionRandomExtractDAO sessionRandomExtractDAO =
                            DAOFactory.getSessionRandomExtractDAO();

                    int index = 0;
                    while (iterator.hasNext()) {
                        String sessionAggrInfo = iterator.next();

                        if (extractIndexList.contains(index)) {
                            String sessionid = StringUtils.getFieldFromConcatString(
                                    sessionAggrInfo, "\\|", Constants.FIELD_SESSION_ID);

                            // 将数据写入MySQL
                            SessionRandomExtract sessionRandomExtract = new SessionRandomExtract();
                            sessionRandomExtract.setTaskid(taskid);
                            sessionRandomExtract.setSessionid(sessionid);
                            sessionRandomExtract.setStartTime(StringUtils.getFieldFromConcatString(
                                    sessionAggrInfo, "\\|", Constants.FIELD_START_TIME));
                            sessionRandomExtract.setSearchKeywords(StringUtils.getFieldFromConcatString(
                                    sessionAggrInfo, "\\|", Constants.FIELD_SEARCH_KEYWORDS));
                            sessionRandomExtract.setClickCategoryIds(StringUtils.getFieldFromConcatString(
                                    sessionAggrInfo, "\\|", Constants.FIELD_CLICK_CATEGORY_IDS));

                            sessionRandomExtractDAO.insert(sessionRandomExtract);

                            // 将sessionid加入list
                            extractSessionids.add(new Tuple2<String, String>(sessionid, sessionid));
                        }

                        index++;
                    }

                    return extractSessionids.iterator();
                });

        /**
         * 第四步：获取抽取出来的session的明细数据
         */
        JavaPairRDD<String, Tuple2<String, Row>> extractSessionDetailRDD =
                extractSessionidsRDD.join(sessionid2actionRDD);

        /**
         * 1.foreach直接在每个partition中直接对iterator执行foreach操作,而传入的function只是在foreach内部使用。
         * foreachPartition是在每个partition中把iterator给传入的function,让function自己对iterator进行处理。
         * 可以避免OOM内存溢出!!!）
         * 2.foreach中的每一个元素都会创建连接对象，浪费资源。因此使用foreachPartition来解决这个问题，这样每个partition中只创建一个连接对象。
         */
        extractSessionDetailRDD.foreachPartition(
                (VoidFunction<Iterator<Tuple2<String, Tuple2<String, Row>>>>) iterator -> {
                    List<SessionDetail> sessionDetails = new ArrayList<>();

                    while (iterator.hasNext()) {
                        Tuple2<String, Tuple2<String, Row>> tuple = iterator.next();
                        Row row = tuple._2._2;

                        SessionDetail sessionDetail = new SessionDetail();
                        sessionDetail.setTaskid(taskid);
                        sessionDetail.setUserid(row.getLong(1));
                        sessionDetail.setSessionid(row.getString(2));
                        sessionDetail.setPageid(row.getLong(3));
                        sessionDetail.setActionTime(row.getString(4));
                        sessionDetail.setSearchKeyword(row.getString(5));
                        sessionDetail.setClickCategoryId(row.getLong(6));
                        sessionDetail.setClickProductId(row.getLong(7));
                        sessionDetail.setOrderCategoryIds(row.getString(8));
                        sessionDetail.setOrderProductIds(row.getString(9));
                        sessionDetail.setPayCategoryIds(row.getString(10));
                        sessionDetail.setPayProductIds(row.getString(11));

                        sessionDetails.add(sessionDetail);
                    }

                    ISessionDetailDAO sessionDetailDAO = DAOFactory.getSessionDetailDAO();
                    sessionDetailDAO.insertBatch(sessionDetails);

                });
    }

    /**
     * 计算各session范围占比，并写入MySQL
     *
     * @param value
     */
    private static void calculateAndPersistAggrStat(String value, long taskid) {
        // 从Accumulator统计串中获取值
        long session_count = Long.valueOf(StringUtils.getFieldFromConcatString(
                value, "\\|", Constants.SESSION_COUNT));

        long visit_length_1s_3s = Long.valueOf(StringUtils.getFieldFromConcatString(
                value, "\\|", Constants.TIME_PERIOD_1s_3s));
        long visit_length_4s_6s = Long.valueOf(StringUtils.getFieldFromConcatString(
                value, "\\|", Constants.TIME_PERIOD_4s_6s));
        long visit_length_7s_9s = Long.valueOf(StringUtils.getFieldFromConcatString(
                value, "\\|", Constants.TIME_PERIOD_7s_9s));
        long visit_length_10s_30s = Long.valueOf(StringUtils.getFieldFromConcatString(
                value, "\\|", Constants.TIME_PERIOD_10s_30s));
        long visit_length_30s_60s = Long.valueOf(StringUtils.getFieldFromConcatString(
                value, "\\|", Constants.TIME_PERIOD_30s_60s));
        long visit_length_1m_3m = Long.valueOf(StringUtils.getFieldFromConcatString(
                value, "\\|", Constants.TIME_PERIOD_1m_3m));
        long visit_length_3m_10m = Long.valueOf(StringUtils.getFieldFromConcatString(
                value, "\\|", Constants.TIME_PERIOD_3m_10m));
        long visit_length_10m_30m = Long.valueOf(StringUtils.getFieldFromConcatString(
                value, "\\|", Constants.TIME_PERIOD_10m_30m));
        long visit_length_30m = Long.valueOf(StringUtils.getFieldFromConcatString(
                value, "\\|", Constants.TIME_PERIOD_30m));

        long step_length_1_3 = Long.valueOf(StringUtils.getFieldFromConcatString(
                value, "\\|", Constants.STEP_PERIOD_1_3));
        long step_length_4_6 = Long.valueOf(StringUtils.getFieldFromConcatString(
                value, "\\|", Constants.STEP_PERIOD_4_6));
        long step_length_7_9 = Long.valueOf(StringUtils.getFieldFromConcatString(
                value, "\\|", Constants.STEP_PERIOD_7_9));
        long step_length_10_30 = Long.valueOf(StringUtils.getFieldFromConcatString(
                value, "\\|", Constants.STEP_PERIOD_10_30));
        long step_length_30_60 = Long.valueOf(StringUtils.getFieldFromConcatString(
                value, "\\|", Constants.STEP_PERIOD_30_60));
        long step_length_60 = Long.valueOf(StringUtils.getFieldFromConcatString(
                value, "\\|", Constants.STEP_PERIOD_60));

        // 计算各个浏览时长和浏览深度的范围
        double visit_length_1s_3s_ratio = NumberUtils.formatDouble(
                (double) visit_length_1s_3s / (double) session_count, 2);
        double visit_length_4s_6s_ratio = NumberUtils.formatDouble(
                (double) visit_length_4s_6s / (double) session_count, 2);
        double visit_length_7s_9s_ratio = NumberUtils.formatDouble(
                (double) visit_length_7s_9s / (double) session_count, 2);
        double visit_length_10s_30s_ratio = NumberUtils.formatDouble(
                (double) visit_length_10s_30s / (double) session_count, 2);
        double visit_length_30s_60s_ratio = NumberUtils.formatDouble(
                (double) visit_length_30s_60s / (double) session_count, 2);
        double visit_length_1m_3m_ratio = NumberUtils.formatDouble(
                (double) visit_length_1m_3m / (double) session_count, 2);
        double visit_length_3m_10m_ratio = NumberUtils.formatDouble(
                (double) visit_length_3m_10m / (double) session_count, 2);
        double visit_length_10m_30m_ratio = NumberUtils.formatDouble(
                (double) visit_length_10m_30m / (double) session_count, 2);
        double visit_length_30m_ratio = NumberUtils.formatDouble(
                (double) visit_length_30m / (double) session_count, 2);

        double step_length_1_3_ratio = NumberUtils.formatDouble(
                (double) step_length_1_3 / (double) session_count, 2);
        double step_length_4_6_ratio = NumberUtils.formatDouble(
                (double) step_length_4_6 / (double) session_count, 2);
        double step_length_7_9_ratio = NumberUtils.formatDouble(
                (double) step_length_7_9 / (double) session_count, 2);
        double step_length_10_30_ratio = NumberUtils.formatDouble(
                (double) step_length_10_30 / (double) session_count, 2);
        double step_length_30_60_ratio = NumberUtils.formatDouble(
                (double) step_length_30_60 / (double) session_count, 2);
        double step_length_60_ratio = NumberUtils.formatDouble(
                (double) step_length_60 / (double) session_count, 2);

        // 将统计结果封装为Domain对象
        SessionAggrStat sessionAggrStat = new SessionAggrStat();
        sessionAggrStat.setTaskid(taskid);
        sessionAggrStat.setSession_count(session_count);
        sessionAggrStat.setVisit_length_1s_3s_ratio(visit_length_1s_3s_ratio);
        sessionAggrStat.setVisit_length_4s_6s_ratio(visit_length_4s_6s_ratio);
        sessionAggrStat.setVisit_length_7s_9s_ratio(visit_length_7s_9s_ratio);
        sessionAggrStat.setVisit_length_10s_30s_ratio(visit_length_10s_30s_ratio);
        sessionAggrStat.setVisit_length_30s_60s_ratio(visit_length_30s_60s_ratio);
        sessionAggrStat.setVisit_length_1m_3m_ratio(visit_length_1m_3m_ratio);
        sessionAggrStat.setVisit_length_3m_10m_ratio(visit_length_3m_10m_ratio);
        sessionAggrStat.setVisit_length_10m_30m_ratio(visit_length_10m_30m_ratio);
        sessionAggrStat.setVisit_length_30m_ratio(visit_length_30m_ratio);
        sessionAggrStat.setStep_length_1_3_ratio(step_length_1_3_ratio);
        sessionAggrStat.setStep_length_4_6_ratio(step_length_4_6_ratio);
        sessionAggrStat.setStep_length_7_9_ratio(step_length_7_9_ratio);
        sessionAggrStat.setStep_length_10_30_ratio(step_length_10_30_ratio);
        sessionAggrStat.setStep_length_30_60_ratio(step_length_30_60_ratio);
        sessionAggrStat.setStep_length_60_ratio(step_length_60_ratio);

        // 调用对应的DAO插入统计结果
        ISessionAggrStatDAO sessionAggrStatDAO = DAOFactory.getSessionAggrStatDAO();
        sessionAggrStatDAO.insert(sessionAggrStat);
    }

    /**
     * 获取top10热门品类
     *
     * @param taskid
     * @param sessionid2detailRDD
     */
    private static List<Tuple2<CategorySortKey, String>> getTop10Category(
            final long taskid,
            JavaPairRDD<String, Row> sessionid2detailRDD) {
        /**
         * 第一步：获取符合条件的session访问过的所有品类
         */

        // 获取session访问过的所有品类id
        // 访问过：指的是，点击过、下单过、支付过的品类
        JavaPairRDD<Long, Long> categoryidRDD = sessionid2detailRDD.flatMapToPair(
                (PairFlatMapFunction<Tuple2<String, Row>, Long, Long>) tuple -> {
                    Row row = tuple._2;

                    List<Tuple2<Long, Long>> list = new ArrayList<Tuple2<Long, Long>>();

                    Long clickCategoryId = row.getLong(6);
                    if (clickCategoryId != 0L) {
                        list.add(new Tuple2<Long, Long>(clickCategoryId, clickCategoryId));
                    }

                    String orderCategoryIds = row.getString(8);
                    if (orderCategoryIds != null) {
                        String[] orderCategoryIdsSplited = orderCategoryIds.split(",");
                        for (String orderCategoryId : orderCategoryIdsSplited) {
                            list.add(new Tuple2<Long, Long>(Long.valueOf(orderCategoryId),
                                    Long.valueOf(orderCategoryId)));
                        }
                    }

                    String payCategoryIds = row.getString(10);
                    if (payCategoryIds != null) {
                        String[] payCategoryIdsSplited = payCategoryIds.split(",");
                        for (String payCategoryId : payCategoryIdsSplited) {
                            list.add(new Tuple2<Long, Long>(Long.valueOf(payCategoryId),
                                    Long.valueOf(payCategoryId)));
                        }
                    }

                    return list.iterator();
                });

        /**
         * 会出现重复的categoryid
         * 最后很可能会拿到重复的数据
         */
        categoryidRDD = categoryidRDD.distinct();

        /**
         * 第二步：计算各品类的点击、下单和支付的次数
         */

        // 访问明细中，其中三种访问行为是：点击、下单和支付
        // 分别来计算各品类点击、下单和支付的次数，可以先对访问明细数据进行过滤
        // 分别过滤出点击、下单和支付行为，然后通过map、reduceByKey等算子来进行计算

        // 计算各个品类的点击次数
        JavaPairRDD<Long, Long> clickCategoryId2CountRDD =
                getClickCategoryId2CountRDD(sessionid2detailRDD);
        // 计算各个品类的下单次数
        JavaPairRDD<Long, Long> orderCategoryId2CountRDD =
                getOrderCategoryId2CountRDD(sessionid2detailRDD);
        // 计算各个品类的支付次数
        JavaPairRDD<Long, Long> payCategoryId2CountRDD =
                getPayCategoryId2CountRDD(sessionid2detailRDD);

        /**
         * 第三步：join各品类与它的点击、下单和支付的次数
         * 有的品类，就只是被点击过，但是没有人下单和支付
         *
         * 所以，要使用leftOuterJoin操作
         * 如果categoryidRDD不能oin到自己的某个数据，那么该categoryidRDD还是要保留下来的，没有join到的那个数据，是0
         */
        JavaPairRDD<Long, String> categoryid2countRDD = joinCategoryAndData(
                categoryidRDD, clickCategoryId2CountRDD, orderCategoryId2CountRDD,
                payCategoryId2CountRDD);

        /**
         * 第四步：自定义二次排序key
         */

        /**
         * 第五步：将数据映射成<CategorySortKey,info>格式的RDD，然后进行二次排序（降序）
         */
        JavaPairRDD<CategorySortKey, String> sortKey2countRDD = categoryid2countRDD.mapToPair(
                (PairFunction<Tuple2<Long, String>, CategorySortKey, String>) tuple -> {
                    String countInfo = tuple._2;
                    long clickCount = Long.valueOf(StringUtils.getFieldFromConcatString(
                            countInfo, "\\|", Constants.FIELD_CLICK_COUNT));
                    long orderCount = Long.valueOf(StringUtils.getFieldFromConcatString(
                            countInfo, "\\|", Constants.FIELD_ORDER_COUNT));
                    long payCount = Long.valueOf(StringUtils.getFieldFromConcatString(
                            countInfo, "\\|", Constants.FIELD_PAY_COUNT));

                    CategorySortKey sortKey = new CategorySortKey(clickCount, orderCount, payCount);

                    return new Tuple2<CategorySortKey, String>(sortKey, countInfo);
                });

        //降序
        JavaPairRDD<CategorySortKey, String> sortedCategoryCountRDD = sortKey2countRDD.sortByKey(false);

        /**
         * 第六步：用take(10)取出top10热门品类，并写入MySQL
         */
        ITop10CategoryDAO top10CategoryDAO = DAOFactory.getTop10CategoryDAO();
        List<Tuple2<CategorySortKey, String>> top10CategoryList = sortedCategoryCountRDD.take(10);
        for (Tuple2<CategorySortKey, String> tuple : top10CategoryList) {
            String countInfo = tuple._2;
            long categoryid = Long.valueOf(StringUtils.getFieldFromConcatString(
                    countInfo, "\\|", Constants.FIELD_CATEGORY_ID));
            long clickCount = Long.valueOf(StringUtils.getFieldFromConcatString(
                    countInfo, "\\|", Constants.FIELD_CLICK_COUNT));
            long orderCount = Long.valueOf(StringUtils.getFieldFromConcatString(
                    countInfo, "\\|", Constants.FIELD_ORDER_COUNT));
            long payCount = Long.valueOf(StringUtils.getFieldFromConcatString(
                    countInfo, "\\|", Constants.FIELD_PAY_COUNT));

            Top10Category category = new Top10Category();
            category.setTaskid(taskid);
            category.setCategoryid(categoryid);
            category.setClickCount(clickCount);
            category.setOrderCount(orderCount);
            category.setPayCount(payCount);

            top10CategoryDAO.insert(category);
        }

        return top10CategoryList;
    }

    /**
     * 获取各品类点击次数RDD
     *
     * @param sessionid2detailRDD
     * @return clickCategoryId2CountRDD
     */
    private static JavaPairRDD<Long, Long> getClickCategoryId2CountRDD(
            JavaPairRDD<String, Row> sessionid2detailRDD) {
        JavaPairRDD<String, Row> clickActionRDD = sessionid2detailRDD.filter(
                (Function<Tuple2<String, Row>, Boolean>) tuple -> {
                    Row row = tuple._2;
                    return row.getLong(6) != 0L ? true : false;
                });
//                .coalesce(100);

        JavaPairRDD<Long, Long> clickCategoryIdRDD = clickActionRDD.mapToPair(

                (PairFunction<Tuple2<String, Row>, Long, Long>) tuple -> {
                    long clickCategoryId = tuple._2.getLong(6);
                    return new Tuple2<Long, Long>(clickCategoryId, 1L);
                });

        /**
         * 计算各个品类的点击次数
         *
         * 如果某个品类点击了1000万次，其他品类都是10万次，那么也会数据倾斜
         */
        JavaPairRDD<Long, Long> clickCategoryId2CountRDD = clickCategoryIdRDD.reduceByKey(
                (Function2<Long, Long, Long>) (v1, v2) -> v1 + v2);

        return clickCategoryId2CountRDD;
    }

    /**
     * 获取各品类的下单次数RDD
     *
     * @param sessionid2detailRDD
     * @return orderCategoryId2CountRDD
     */
    private static JavaPairRDD<Long, Long> getOrderCategoryId2CountRDD(
            JavaPairRDD<String, Row> sessionid2detailRDD) {
        JavaPairRDD<String, Row> orderActionRDD = sessionid2detailRDD.filter(
                (Function<Tuple2<String, Row>, Boolean>) tuple -> {
                    Row row = tuple._2;
                    return row.getString(8) != null ? true : false;
                });

        JavaPairRDD<Long, Long> orderCategoryIdRDD = orderActionRDD.flatMapToPair(
                (PairFlatMapFunction<Tuple2<String, Row>, Long, Long>) tuple -> {
                    Row row = tuple._2;
                    String orderCategoryIds = row.getString(8);
                    String[] orderCategoryIdsSplited = orderCategoryIds.split(",");

                    List<Tuple2<Long, Long>> list = new ArrayList<Tuple2<Long, Long>>();

                    for (String orderCategoryId : orderCategoryIdsSplited) {
                        list.add(new Tuple2<Long, Long>(Long.valueOf(orderCategoryId), 1L));
                    }

                    return list.iterator();
                });

        JavaPairRDD<Long, Long> orderCategoryId2CountRDD = orderCategoryIdRDD.reduceByKey(
                (Function2<Long, Long, Long>) (v1, v2) -> v1 + v2);

        return orderCategoryId2CountRDD;
    }

    /**
     * 获取各个品类的支付次数RDD
     *
     * @param sessionid2detailRDD
     * @return
     */
    private static JavaPairRDD<Long, Long> getPayCategoryId2CountRDD(
            JavaPairRDD<String, Row> sessionid2detailRDD) {
        JavaPairRDD<String, Row> payActionRDD = sessionid2detailRDD.filter(
                (Function<Tuple2<String, Row>, Boolean>) tuple -> {
                    Row row = tuple._2;
                    return row.getString(10) != null ? true : false;
                });

        JavaPairRDD<Long, Long> payCategoryIdRDD = payActionRDD.flatMapToPair(
                (PairFlatMapFunction<Tuple2<String, Row>, Long, Long>) tuple -> {
                    Row row = tuple._2;
                    String payCategoryIds = row.getString(10);
                    String[] payCategoryIdsSplited = payCategoryIds.split(",");

                    List<Tuple2<Long, Long>> list = new ArrayList<Tuple2<Long, Long>>();

                    for (String payCategoryId : payCategoryIdsSplited) {
                        list.add(new Tuple2<Long, Long>(Long.valueOf(payCategoryId), 1L));
                    }

                    return list.iterator();
                });

        JavaPairRDD<Long, Long> payCategoryId2CountRDD = payCategoryIdRDD.reduceByKey(
                (Function2<Long, Long, Long>) (v1, v2) -> v1 + v2
        );

        return payCategoryId2CountRDD;
    }

    /**
     * 连接品类RDD与数据RDD
     *
     * @param categoryidRDD
     * @param clickCategoryId2CountRDD
     * @param orderCategoryId2CountRDD
     * @param payCategoryId2CountRDD
     * @return tmpMapRDD
     */
    private static JavaPairRDD<Long, String> joinCategoryAndData(
            JavaPairRDD<Long, Long> categoryidRDD,
            JavaPairRDD<Long, Long> clickCategoryId2CountRDD,
            JavaPairRDD<Long, Long> orderCategoryId2CountRDD,
            JavaPairRDD<Long, Long> payCategoryId2CountRDD) {
        // 如果用leftOuterJoin，就可能出现，右边那个RDD中，join过来时，没有值
        // 所以Tuple中的第二个值用Optional<Long>类型，就代表，可能有值，可能没有值
        JavaPairRDD<Long, Tuple2<Long, Optional<Long>>> tmpJoinRDD =
                categoryidRDD.leftOuterJoin(clickCategoryId2CountRDD);

        JavaPairRDD<Long, String> tmpMapRDD = tmpJoinRDD.mapToPair(
                (PairFunction<Tuple2<Long, Tuple2<Long, Optional<Long>>>, Long, String>) tuple -> {
                    long categoryid = tuple._1;
                    Optional<Long> optional = tuple._2._2;
                    long clickCount = 0L;

                    if (optional.isPresent()) {
                        clickCount = optional.get();
                    }

                    String value = Constants.FIELD_CATEGORY_ID + "=" + categoryid + "|" +
                            Constants.FIELD_CLICK_COUNT + "=" + clickCount;

                    return new Tuple2<Long, String>(categoryid, value);
                });

        tmpMapRDD = tmpMapRDD.leftOuterJoin(orderCategoryId2CountRDD).mapToPair(
                (PairFunction<Tuple2<Long, Tuple2<String, Optional<Long>>>, Long, String>) tuple -> {
                    long categoryid = tuple._1;
                    String value = tuple._2._1;

                    Optional<Long> optional = tuple._2._2;
                    long orderCount = 0L;

                    if (optional.isPresent()) {
                        orderCount = optional.get();
                    }

                    value = value + "|" + Constants.FIELD_ORDER_COUNT + "=" + orderCount;

                    return new Tuple2<Long, String>(categoryid, value);
                });

        tmpMapRDD = tmpMapRDD.leftOuterJoin(payCategoryId2CountRDD).mapToPair(
                (PairFunction<Tuple2<Long, Tuple2<String, Optional<Long>>>, Long, String>) tuple -> {
                    long categoryid = tuple._1;
                    String value = tuple._2._1;

                    Optional<Long> optional = tuple._2._2;
                    long payCount = 0L;

                    if (optional.isPresent()) {
                        payCount = optional.get();
                    }

                    value = value + "|" + Constants.FIELD_PAY_COUNT + "=" + payCount;

                    return new Tuple2<Long, String>(categoryid, value);
                });

        return tmpMapRDD;
    }

    /**
     * 获取top10活跃session
     *
     * @param taskid
     * @param sessionid2detailRDD
     */
    private static void getTop10Session(
            JavaSparkContext sc,
            final long taskid,
            List<Tuple2<CategorySortKey, String>> top10CategoryList,
            JavaPairRDD<String, Row> sessionid2detailRDD) {
        /**
         * 第一步：将top10热门品类的id，生成一份RDD
         */
        List<Tuple2<Long, Long>> top10CategoryIdList =
                new ArrayList<Tuple2<Long, Long>>();

        for (Tuple2<CategorySortKey, String> category : top10CategoryList) {
            long categoryid = Long.valueOf(StringUtils.getFieldFromConcatString(
                    category._2, "\\|", Constants.FIELD_CATEGORY_ID));
            top10CategoryIdList.add(new Tuple2<Long, Long>(categoryid, categoryid));
        }

        JavaPairRDD<Long, Long> top10CategoryIdRDD =
                sc.parallelizePairs(top10CategoryIdList);

        /**
         * 第二步：计算top10品类被各session点击的次数
         */
        JavaPairRDD<String, Iterable<Row>> sessionid2detailsRDD =
                sessionid2detailRDD.groupByKey();

        JavaPairRDD<Long, String> categoryid2sessionCountRDD = sessionid2detailsRDD.flatMapToPair(
                (PairFlatMapFunction<Tuple2<String, Iterable<Row>>, Long, String>) tuple -> {
                    String sessionid = tuple._1;
                    Iterator<Row> iterator = tuple._2.iterator();

                    Map<Long, Long> categoryCountMap = new HashMap<Long, Long>();

                    // 计算出该session，对每个品类的点击次数
                    while (iterator.hasNext()) {
                        Row row = iterator.next();

                        if (row.getLong(6) != 0L) {
                            long categoryid = row.getLong(6);

                            Long count = categoryCountMap.get(categoryid);
                            if (count == null) {
                                count = 0L;
                            }

                            count++;

                            categoryCountMap.put(categoryid, count);
                        }
                    }

                    // 返回结果，<categoryid,sessionid,count>格式
                    List<Tuple2<Long, String>> list = new ArrayList<Tuple2<Long, String>>();

                    for (Map.Entry<Long, Long> categoryCountEntry : categoryCountMap.entrySet()) {
                        long categoryid = categoryCountEntry.getKey();
                        long count = categoryCountEntry.getValue();
                        String value = sessionid + "," + count;
                        list.add(new Tuple2<Long, String>(categoryid, value));
                    }

                    return list.iterator();
                });

        // 获取到to10热门品类，被各个session点击的次数
        JavaPairRDD<Long, String> top10CategorySessionCountRDD = top10CategoryIdRDD
                .join(categoryid2sessionCountRDD)
                .mapToPair((PairFunction<Tuple2<Long, Tuple2<Long, String>>, Long, String>) tuple ->
                        new Tuple2<Long, String>(tuple._1, tuple._2._2));

        /**
         * 第三步：分组取TopN算法实现，获取每个品类的top10活跃用户
         */
        JavaPairRDD<Long, Iterable<String>> top10CategorySessionCountsRDD =
                top10CategorySessionCountRDD.groupByKey();

        JavaPairRDD<String, String> top10SessionRDD = top10CategorySessionCountsRDD.flatMapToPair(
                (PairFlatMapFunction<Tuple2<Long, Iterable<String>>, String, String>) tuple -> {
                    long categoryid = tuple._1;
                    Iterator<String> iterator = tuple._2.iterator();

                    // 定义取topn的排序数组
                    String[] top10Sessions = new String[10];

                    while (iterator.hasNext()) {
                        String sessionCount = iterator.next();
                        long count = Long.valueOf(sessionCount.split(",")[1]);

                        // 遍历排序数组
                        for (int i = 0; i < top10Sessions.length; i++) {
                            // 如果当前i位，没有数据，那么直接将i位数据赋值为当前sessionCount
                            if (top10Sessions[i] == null) {
                                top10Sessions[i] = sessionCount;
                                break;
                            } else {
                                long _count = Long.valueOf(top10Sessions[i].split(",")[1]);

                                // 如果sessionCount比i位的sessionCount要大
                                if (count > _count) {
                                    // 从排序数组最后一位开始，到i位，所有数据往后挪一位
                                    for (int j = 9; j > i; j--) {
                                        top10Sessions[j] = top10Sessions[j - 1];
                                    }
                                    // 将i位赋值为sessionCount
                                    top10Sessions[i] = sessionCount;
                                    break;
                                }
                                // 比较小，继续外层for循环
                            }
                        }
                    }

                    // 将数据写入MySQL表
                    List<Tuple2<String, String>> list = new ArrayList<Tuple2<String, String>>();

                    for (String sessionCount : top10Sessions) {
                        if (sessionCount != null) {
                            String sessionid = sessionCount.split(",")[0];
                            long count = Long.valueOf(sessionCount.split(",")[1]);

                            // 将top10 session插入MySQL表
                            Top10Session top10Session = new Top10Session();
                            top10Session.setTaskid(taskid);
                            top10Session.setCategoryid(categoryid);
                            top10Session.setSessionid(sessionid);
                            top10Session.setClickCount(count);

                            ITop10SessionDAO top10SessionDAO = DAOFactory.getTop10SessionDAO();
                            top10SessionDAO.insert(top10Session);

                            // 放入list
                            list.add(new Tuple2<String, String>(sessionid, sessionid));
                        }
                    }
                    return list.iterator();
                });

        /**
         * 第四步：获取top10活跃session的明细数据，并写入MySQL
         */
        JavaPairRDD<String, Tuple2<String, Row>> sessionDetailRDD =
                top10SessionRDD.join(sessionid2detailRDD);
        sessionDetailRDD.foreach((VoidFunction<Tuple2<String, Tuple2<String, Row>>>) tuple -> {
            Row row = tuple._2._2;

            SessionDetail sessionDetail = new SessionDetail();
            sessionDetail.setTaskid(taskid);
            sessionDetail.setUserid(row.getLong(1));
            sessionDetail.setSessionid(row.getString(2));
            sessionDetail.setPageid(row.getLong(3));
            sessionDetail.setActionTime(row.getString(4));
            sessionDetail.setSearchKeyword(row.getString(5));
            sessionDetail.setClickCategoryId(row.getLong(6));
            sessionDetail.setClickProductId(row.getLong(7));
            sessionDetail.setOrderCategoryIds(row.getString(8));
            sessionDetail.setOrderProductIds(row.getString(9));
            sessionDetail.setPayCategoryIds(row.getString(10));
            sessionDetail.setPayProductIds(row.getString(11));

            ISessionDetailDAO sessionDetailDAO = DAOFactory.getSessionDetailDAO();
            sessionDetailDAO.insert(sessionDetail);
        });
    }
}
