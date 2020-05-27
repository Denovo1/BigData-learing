package com.dqsy.sparkproject.spark.ad;

import com.dqsy.sparkproject.conf.ConfigurationManager;
import com.dqsy.sparkproject.constant.Constants;
import com.dqsy.sparkproject.dao.*;
import com.dqsy.sparkproject.dao.factory.DAOFactory;
import com.dqsy.sparkproject.domain.*;
import com.dqsy.sparkproject.util.DateUtils;
import kafka.serializer.StringDecoder;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.Optional;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.hive.HiveContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

import java.util.*;

/**
 * 广告点击实时统计
 *
 * @author liusinan
 */
public class AdRealTimeClick {

    public static void main(String[] args) throws InterruptedException {
        try {
            SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("AdRealTimeClick");
//				    .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
//				    .set("spark.default.parallelism", "1000");

            // 每隔5秒钟，收集最近5秒内的数据源接收过来的数据
            JavaStreamingContext jssc = new JavaStreamingContext(
                    conf, Durations.seconds(5));

            // updateStateByKey、window等有状态的操作，设置checkpoint目录
            jssc.checkpoint("hdfs://192.168.109.135:8020/streaming_checkpoint");

            // 构建kafka参数map,broker集群的地址列表
            Map<String, String> kafkaParams = new HashMap<String, String>();
            kafkaParams.put("metadata.broker.list", ConfigurationManager.getProperty(Constants.KAFKA_METADATA_BROKER_LIST));

            // 构建topic set
            String kafkaTopics = ConfigurationManager.getProperty(Constants.KAFKA_TOPICS);
            String[] kafkaTopicsSplited = kafkaTopics.split(",");

            Set<String> topics = new HashSet<String>();
            for (String kafkaTopic : kafkaTopicsSplited) {
                topics.add(kafkaTopic);
            }

            // （无意义，日志数据）
            JavaPairInputDStream<String, String> adRealTimeLogDStream = KafkaUtils.createDirectStream(jssc, String.class, String.class, StringDecoder.class, StringDecoder.class, kafkaParams, topics);

//            adRealTimeLogDStream.repartition(1000);

            // 黑名单数据过滤
            JavaPairDStream<String, String> filteredAdRealTimeLogDStream = filterByBlacklist(adRealTimeLogDStream);

            // 生成黑名单
            generateDynamicBlacklist(filteredAdRealTimeLogDStream);

            // 统计各省各城市各广告点击数（yyyyMMdd_province_city_adid,clickCount）
            JavaPairDStream<String, Long> adRealTimeStatDStream = calculateRealTimeStat(
                    filteredAdRealTimeLogDStream);

            // 获取各省前三热门广告
            calculateProvinceTop3Ad(adRealTimeStatDStream);

            // 实时统计每天每个广告在最近1小时的滑动窗口内的点击趋势（每分钟的点击量）
            calculateAdClickCountByWindow(filteredAdRealTimeLogDStream);

            jssc.start();
            jssc.awaitTermination();
            jssc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据黑名单进行过滤
     *
     * @param adRealTimeLogDStream
     * @return filteredAdRealTimeLogDStream
     */
    private static JavaPairDStream<String, String> filterByBlacklist(
            JavaPairInputDStream<String, String> adRealTimeLogDStream) {
        // 接受到原始的用户点击行为日志之后
        // 使用transform算子（将dstream中的每个batch RDD进行处理，转换为任意的其他RDD）
        // 在黑名单上的数据直接不要

        JavaPairDStream<String, String> filteredAdRealTimeLogDStream = adRealTimeLogDStream.transformToPair(
                new Function<JavaPairRDD<String, String>, JavaPairRDD<String, String>>() {
                    private static final long serialVersionUID = 1L;

                    @SuppressWarnings("resource")
                    @Override
                    public JavaPairRDD<String, String> call(
                            JavaPairRDD<String, String> rdd) throws Exception {

                        // 首先，从mysql中查询所有黑名单用户，将其转换为一个rdd
                        IAdBlacklistDAO adBlacklistDAO = DAOFactory.getAdBlacklistDAO();
                        List<AdBlacklist> adBlacklists = adBlacklistDAO.findAll();

                        List<Tuple2<Long, Boolean>> tuples = new ArrayList<Tuple2<Long, Boolean>>();

                        for (AdBlacklist adBlacklist : adBlacklists) {
                            tuples.add(new Tuple2<Long, Boolean>(adBlacklist.getUserid(), true));
                        }

                        JavaSparkContext sc = new JavaSparkContext(rdd.context());
                        JavaPairRDD<Long, Boolean> blacklistRDD = sc.parallelizePairs(tuples);

                        // 将原始数据rdd映射成<userid, tuple2<string, string>>
                        JavaPairRDD<Long, Tuple2<String, String>> mappedRDD = rdd.mapToPair(
                                new PairFunction<Tuple2<String, String>, Long, Tuple2<String, String>>() {

                                    private static final long serialVersionUID = 1L;

                                    @Override
                                    public Tuple2<Long, Tuple2<String, String>> call(Tuple2<String, String> tuple) throws Exception {
                                        // 日志格式：timestamp province city userid adid
                                        String log = tuple._2;
                                        String[] logSplited = log.split(" ");
                                        long userid = Long.valueOf(logSplited[3]);
                                        return new Tuple2<Long, Tuple2<String, String>>(userid, tuple);
                                    }
                                });

                        // 左外连接
                        // 如果原始日志的userid，没有在对应的黑名单中，join不到，保留；join到了丢弃
                        // 用inner join，内连接，没join到的数据就丢失了

                        JavaPairRDD<Long, Tuple2<Tuple2<String, String>, Optional<Boolean>>> joinedRDD =
                                mappedRDD.leftOuterJoin(blacklistRDD);

                        JavaPairRDD<Long, Tuple2<Tuple2<String, String>, Optional<Boolean>>> filteredRDD = joinedRDD.filter(
                                new Function<Tuple2<Long, Tuple2<Tuple2<String, String>, Optional<Boolean>>>, Boolean>() {
                                    private static final long serialVersionUID = 1L;

                                    @Override
                                    public Boolean call(
                                            Tuple2<Long, Tuple2<Tuple2<String, String>, Optional<Boolean>>> tuple)
                                            throws Exception {
                                        Optional<Boolean> optional = tuple._2._2;

                                        // 如果这个值存在  optional.isPresent()，
                                        // 这个值为true   optional.get()
                                        // 那么说明原始日志中的userid，join到了某个黑名单用户
                                        if (optional.isPresent() && optional.get()) {
                                            return false;
                                        }
                                        return true;
                                    }
                                });

                        JavaPairRDD<String, String> resultRDD = filteredRDD.mapToPair(
                                new PairFunction<Tuple2<Long, Tuple2<Tuple2<String, String>, Optional<Boolean>>>, String, String>() {
                                    private static final long serialVersionUID = 1L;

                                    @Override
                                    public Tuple2<String, String> call(
                                            Tuple2<Long, Tuple2<Tuple2<String, String>, Optional<Boolean>>> tuple)
                                            throws Exception {
                                        // 最终筛选后的log
                                        return tuple._2._1;
                                    }
                                });

                        return resultRDD;
                    }
                });

        return filteredAdRealTimeLogDStream;
    }

    /**
     * 生成动态黑名单
     *
     * @param filteredAdRealTimeLogDStream
     */
    private static void generateDynamicBlacklist(
            JavaPairDStream<String, String> filteredAdRealTimeLogDStream) {
        // timestamp province city userid adid
        // 某个时间点 某个省份 某个城市 某个用户 某个广告

        // 计算出每5个秒内的数据中，每天每个用户每个广告的点击量

        // 通过对原始实时日志的处理
        // 将日志的格式处理成<yyyyMMdd_userid_adid, 1L>格式
        JavaPairDStream<String, Long> dailyUserAdClickDStream = filteredAdRealTimeLogDStream.mapToPair(
                new PairFunction<Tuple2<String, String>, String, Long>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public Tuple2<String, Long> call(Tuple2<String, String> tuple)
                            throws Exception {
                        // 从tuple中获取到每一条原始的实时日志
                        String log = tuple._2;
                        String[] logSplited = log.split(" ");

                        // 提取出日期（yyyyMMdd）、userid、adid
                        String timestamp = logSplited[0];
                        Date date = new Date(Long.valueOf(timestamp));
                        String datekey = DateUtils.formatDateKey(date);

                        long userid = Long.valueOf(logSplited[3]);
                        long adid = Long.valueOf(logSplited[4]);

                        // 拼接key
                        String key = datekey + "_" + userid + "_" + adid;

                        return new Tuple2<String, Long>(key, 1L);
                    }
                });

        // （每个batch中）每天每个用户对每个广告的点击量
        JavaPairDStream<String, Long> dailyUserAdClickCountDStream = dailyUserAdClickDStream.reduceByKey(
                new Function2<Long, Long, Long>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public Long call(Long v1, Long v2) throws Exception {
                        return v1 + v2;
                    }
                });

        // <yyyyMMdd_userid_adid, clickCount>
        dailyUserAdClickCountDStream.foreachRDD(
                (VoidFunction<JavaPairRDD<String, Long>>) rdd ->
                        // foreachPartition每个分区的数据获取一次连接对象
                        // 每次都是从连接池中获取，而不是每次都创建
                        rdd.foreachPartition(
                                (VoidFunction<Iterator<Tuple2<String, Long>>>) iterator -> {
                                    List<AdUserClickCount> adUserClickCounts = new ArrayList<AdUserClickCount>();

                                    while (iterator.hasNext()) {
                                        Tuple2<String, Long> tuple = iterator.next();

                                        String[] keySplited = tuple._1.split("_");
                                        String date = DateUtils.formatDate(DateUtils.parseDateKey(keySplited[0]));
                                        // yyyy-MM-dd
                                        long userid = Long.valueOf(keySplited[1]);
                                        long adid = Long.valueOf(keySplited[2]);
                                        long clickCount = tuple._2;

                                        AdUserClickCount adUserClickCount = new AdUserClickCount();
                                        adUserClickCount.setDate(date);
                                        adUserClickCount.setUserid(userid);
                                        adUserClickCount.setAdid(adid);
                                        adUserClickCount.setClickCount(clickCount);

                                        adUserClickCounts.add(adUserClickCount);
                                    }

                                    IAdUserClickCountDAO adUserClickCountDAO = DAOFactory.getAdUserClickCountDAO();
                                    adUserClickCountDAO.updateBatch(adUserClickCounts);
                                }));

        // 每天各用户对各广告的点击量：advertisement_user_clickcount
        // 遍历每个batch中的所有记录从mysql中查询，点击量已经大于等于100了
        // 那么就判定这个用户就是黑名单用户，就写入mysql的表中，持久化

        // 对batch中的数据，去查询mysql中的点击次数
        // 使用按照yyyyMMdd_userid_adid进行过聚合了的dailyUserAdClickCountDStream
        // 减少了要处理的数据量

        JavaPairDStream<String, Long> blacklistDStream = dailyUserAdClickCountDStream.filter(
                (Function<Tuple2<String, Long>, Boolean>) tuple -> {
                    String key = tuple._1;
                    String[] keySplited = key.split("_");

                    // yyyyMMdd -> yyyy-MM-dd
                    String date = DateUtils.formatDate(DateUtils.parseDateKey(keySplited[0]));
                    long userid = Long.valueOf(keySplited[1]);
                    long adid = Long.valueOf(keySplited[2]);

                    // 从mysql中查询指定日期指定用户对指定广告的点击量
                    IAdUserClickCountDAO adUserClickCountDAO = DAOFactory.getAdUserClickCountDAO();
                    int clickCount = adUserClickCountDAO.findClickCountByMultiKey(
                            date, userid, adid);

                    if (clickCount >= 100) {
                        return true;
                    }

                    return false;
                });

        // blacklistDStream
        // 里面的每个batch，其实就是都是过滤出来的已经在某天对某个广告点击量超过100的用户
        // 遍历这个dstream中的每个rdd，然后将黑名单用户增加到mysql中
        // 这里一旦增加以后，在整个这段程序的前面，会加上根据黑名单动态过滤用户的逻辑
        // 我们可以认为，一旦用户被拉入黑名单之后，以后就不会再出现在这里了
        // 所以直接插入mysql即可

        // blacklistDStream中，可能有userid是重复的，如果直接这样插入的话
        // 那么是不是会发生，插入重复的黑明单用户
        // 我们在插入前要进行去重
        // yyyyMMdd_userid_adid
        // 20151220_10001_10002 100
        // 20151220_10001_10003 100
        // 10001这个userid就重复了

        // 实际上，是要通过对dstream执行操作，对其中的rdd中的userid进行全局的去重
        JavaDStream<Long> blacklistUseridDStream = blacklistDStream.map(
                (Function<Tuple2<String, Long>, Long>) tuple -> {
                    String key = tuple._1;
                    String[] keySplited = key.split("_");
                    Long userid = Long.valueOf(keySplited[1]);
                    return userid;
                });

        JavaDStream<Long> distinctBlacklistUseridDStream = blacklistUseridDStream.transform(
                (Function<JavaRDD<Long>, JavaRDD<Long>>) rdd ->
                        rdd.distinct());

        // 每一次过滤出来的黑名单用户都没有重复的

        distinctBlacklistUseridDStream.foreachRDD((VoidFunction<JavaRDD<Long>>) rdd ->
                rdd.foreachPartition((VoidFunction<Iterator<Long>>) iterator -> {

                    List<AdBlacklist> adBlacklists = new ArrayList<>();

                    while (iterator.hasNext()) {
                        long userid = iterator.next();

                        AdBlacklist adBlacklist = new AdBlacklist(userid);
                        adBlacklists.add(adBlacklist);
                    }

                    IAdBlacklistDAO adBlacklistDAO = DAOFactory.getAdBlacklistDAO();
                    adBlacklistDAO.insertBatch(adBlacklists);

                }));
    }

    /**
     * 计算广告点击流量实时统计
     *
     * @param filteredAdRealTimeLogDStream
     * @return
     */
    private static JavaPairDStream<String, Long> calculateRealTimeStat(
            JavaPairDStream<String, String> filteredAdRealTimeLogDStream) {

        // date province city userid adid
        // date_province_city_adid，作为key；1作为value
        // 统计出全局的点击次数，在spark集群中保留一份；在mysql中，也保留一份
        // 对原始数据进行map，映射成<date_province_city_adid,1>格式
        // 然后对上述格式的数据，执行updateStateByKey算子
        // spark streaming特有的一种算子，在spark集群内存中，维护一份key的全局状态
        JavaPairDStream<String, Long> mappedDStream = filteredAdRealTimeLogDStream.mapToPair(
                new PairFunction<Tuple2<String, String>, String, Long>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public Tuple2<String, Long> call(Tuple2<String, String> tuple) throws Exception {
                        String log = tuple._2;
                        String[] logSplited = log.split(" ");

                        String timestamp = logSplited[0];
                        Date date = new Date(Long.valueOf(timestamp));
                        String datekey = DateUtils.formatDateKey(date);    // yyyyMMdd

                        String province = logSplited[1];
                        String city = logSplited[2];
                        long adid = Long.valueOf(logSplited[4]);

                        String key = datekey + "_" + province + "_" + city + "_" + adid;

                        return new Tuple2<String, Long>(key, 1L);
                    }
                });

        // 在这个dstream中，就相当于，有每个batch rdd累加的各个key（各天各省份各城市各广告的点击次数）
        // 每次计算出最新的值，就在aggregatedDStream中的每个batch rdd中反应出来
        JavaPairDStream<String, Long> aggregatedDStream = mappedDStream.updateStateByKey(
                new Function2<List<Long>, Optional<Long>, Optional<Long>>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public Optional<Long> call(List<Long> values, Optional<Long> optional)
                            throws Exception {
                        // <20200425_Jiangsu_Nanjing_10001,1>
                        // 10个values，(1,1,1,1,1,1,1,1,1,1)

                        // optional判断，之前这个key，是否有对应的状态
                        long clickCount = 0L;

                        // 如果说，之前是存在这个状态的，那么就以之前的状态作为起点，进行值的累加
                        if (optional.isPresent()) {
                            clickCount = optional.get();
                        }

                        // values，代表了，batch rdd中，每个key对应的所有的值
                        for (Long value : values) {
                            clickCount += value;
                        }

                        return Optional.of(clickCount);
                    }
                });

        // 最新结果同步到mysql中，以便于j2ee系统使用
        aggregatedDStream.foreachRDD((VoidFunction<JavaPairRDD<String, Long>>) rdd ->
                rdd.foreachPartition((VoidFunction<Iterator<Tuple2<String, Long>>>) iterator -> {
                    List<AdStat> adStats = new ArrayList<AdStat>();

                    while (iterator.hasNext()) {
                        Tuple2<String, Long> tuple = iterator.next();

                        String[] keySplited = tuple._1.split("_");
                        String date = DateUtils.formatDate(DateUtils.parseDateKey(keySplited[0]));
                        String province = keySplited[1];
                        String city = keySplited[2];
                        long adid = Long.valueOf(keySplited[3]);

                        long clickCount = tuple._2;

                        AdStat adStat = new AdStat();
                        adStat.setDate(date);
                        adStat.setProvince(province);
                        adStat.setCity(city);
                        adStat.setAdid(adid);
                        adStat.setClickCount(clickCount);

                        adStats.add(adStat);
                    }

                    IAdStatDAO adStatDAO = DAOFactory.getAdStatDAO();
                    adStatDAO.updateBatch(adStats);
                }));

        return aggregatedDStream;
    }

    /**
     * 计算每天各省份的top3热门广告
     *
     * @param adRealTimeStatDStream
     */
    private static void calculateProvinceTop3Ad(
            JavaPairDStream<String, Long> adRealTimeStatDStream) {
        // adRealTimeStatDStream
        // 每一个batch rdd，都代表了最新的全量的每天各省份各城市各广告的点击量

        JavaDStream<Row> rowsDStream = adRealTimeStatDStream.transform(
                new Function<JavaPairRDD<String, Long>, JavaRDD<Row>>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public JavaRDD<Row> call(JavaPairRDD<String, Long> rdd) throws Exception {

                        // <yyyyMMdd_province_city_adid, clickCount> -> <yyyyMMdd_province_adid, clickCount>

                        JavaPairRDD<String, Long> mappedRDD = rdd.mapToPair(

                                new PairFunction<Tuple2<String, Long>, String, Long>() {

                                    private static final long serialVersionUID = 1L;

                                    @Override
                                    public Tuple2<String, Long> call(Tuple2<String, Long> tuple) throws Exception {
                                        String[] keySplited = tuple._1.split("_");
                                        String date = keySplited[0];
                                        String province = keySplited[1];
                                        long adid = Long.valueOf(keySplited[3]);
                                        long clickCount = tuple._2;

                                        String key = date + "_" + province + "_" + adid;

                                        return new Tuple2<String, Long>(key, clickCount);
                                    }

                                });

                        JavaPairRDD<String, Long> dailyAdClickCountByProvinceRDD = mappedRDD.reduceByKey(

                                new Function2<Long, Long, Long>() {

                                    private static final long serialVersionUID = 1L;

                                    @Override
                                    public Long call(Long v1, Long v2) throws Exception {
                                        return v1 + v2;
                                    }

                                });

                        JavaRDD<Row> rowsRDD = dailyAdClickCountByProvinceRDD.map(
                                new Function<Tuple2<String, Long>, Row>() {
                                    private static final long serialVersionUID = 1L;

                                    @Override
                                    public Row call(Tuple2<String, Long> tuple)
                                            throws Exception {
                                        String[] keySplited = tuple._1.split("_");
                                        String datekey = keySplited[0];
                                        String province = keySplited[1];
                                        long adid = Long.valueOf(keySplited[2]);
                                        long clickCount = tuple._2;

                                        String date = DateUtils.formatDate(DateUtils.parseDateKey(datekey));

                                        return RowFactory.create(date, province, adid, clickCount);
                                    }

                                });

                        StructType schema = DataTypes.createStructType(Arrays.asList(
                                DataTypes.createStructField("date", DataTypes.StringType, true),
                                DataTypes.createStructField("province", DataTypes.StringType, true),
                                DataTypes.createStructField("ad_id", DataTypes.LongType, true),
                                DataTypes.createStructField("click_count", DataTypes.LongType, true)));

                        HiveContext sqlContext = new HiveContext(rdd.context());

                        Dataset<Row> dailyAdClickCountByProvinceDF = sqlContext.createDataFrame(rowsRDD, schema);

                        dailyAdClickCountByProvinceDF.registerTempTable("tmp_daily_ad_click_count_by_prov");

                        // 使用Spark SQL执行SQL语句，配合开窗函数，统计出各身份top3热门的广告
                        Dataset<Row> provinceTop3AdDF = sqlContext.sql(
                                "SELECT "
                                        + "date,"
                                        + "province,"
                                        + "ad_id,"
                                        + "click_count "
                                        + "FROM ( "
                                        + "SELECT "
                                        + "date,"
                                        + "province,"
                                        + "ad_id,"
                                        + "click_count,"
                                        + "ROW_NUMBER() OVER(PARTITION BY province ORDER BY click_count DESC) rank "
                                        + "FROM tmp_daily_ad_click_count_by_prov "
                                        + ") t "
                                        + "WHERE rank<=3"
                        );

                        return provinceTop3AdDF.javaRDD();
                    }

                });

        // rowsDStream
        // 每次都是刷新出来各个省份最热门的top3广告
        // 将其中的数据批量更新到MySQL中
        rowsDStream.foreachRDD((VoidFunction<JavaRDD<Row>>) rdd ->
                rdd.foreachPartition((VoidFunction<Iterator<Row>>) iterator -> {
                    List<AdProvinceTop3> adProvinceTop3s = new ArrayList<AdProvinceTop3>();

                    while (iterator.hasNext()) {
                        Row row = iterator.next();
                        String date = row.getString(0);
                        String province = row.getString(1);
                        long adid = row.getLong(2);
                        long clickCount = row.getLong(3);

                        AdProvinceTop3 adProvinceTop3 = new AdProvinceTop3();
                        adProvinceTop3.setDate(date);
                        adProvinceTop3.setProvince(province);
                        adProvinceTop3.setAdid(adid);
                        adProvinceTop3.setClickCount(clickCount);

                        adProvinceTop3s.add(adProvinceTop3);
                    }

                    IAdProvinceTop3DAO adProvinceTop3DAO = DAOFactory.getAdProvinceTop3DAO();
                    adProvinceTop3DAO.updateBatch(adProvinceTop3s);
                }));
    }

    /**
     * 计算最近1小时滑动窗口内的广告点击趋势
     *
     * @param filteredAdRealTimeLogDStream
     */
    private static void calculateAdClickCountByWindow(
            JavaPairDStream<String, String> filteredAdRealTimeLogDStream) {

        // 映射成<yyyyMMddHHMM_adid,1L>格式
        JavaPairDStream<String, Long> pairDStream = filteredAdRealTimeLogDStream.mapToPair(

                new PairFunction<Tuple2<String, String>, String, Long>() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public Tuple2<String, Long> call(Tuple2<String, String> tuple) throws Exception {
                        // timestamp province city userid adid
                        String[] logSplited = tuple._2.split(" ");
                        String timeMinute = DateUtils.formatTimeMinute(new Date(Long.valueOf(logSplited[0])));
                        long adid = Long.valueOf(logSplited[4]);

                        return new Tuple2<String, Long>(timeMinute + "_" + adid, 1L);
                    }

                });

        // 过来的每个batch rdd，都会被映射成<yyyyMMddHHMM_adid,1L>的格式
        // 每次出来一个新的batch，都要获取最近1小时内的所有的batch
        // 然后根据key进行reduceByKey操作，统计出来最近一小时内的各分钟各广告的点击次数
        // 1小时滑动窗口内的广告点击趋势

        // Durations.minutes(60)：窗口的宽度，可以说是处理多大的范围内的数据
        // Durations.seconds(10)：窗口的间隔时间，可以说是多长时间处理一次
        JavaPairDStream<String, Long> aggrRDD = pairDStream.reduceByKeyAndWindow(

                new Function2<Long, Long, Long>() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public Long call(Long v1, Long v2) throws Exception {
                        return v1 + v2;
                    }
                }, Durations.minutes(60), Durations.seconds(10));

        // aggrRDD
        // 每次都可以拿到，最近1小时内，各分钟（yyyyMMddHHMM）各广告的点击量
        // 各广告，在最近1小时内，各分钟的点击量
        aggrRDD.foreachRDD((VoidFunction<JavaPairRDD<String, Long>>) rdd ->
                rdd.foreachPartition((VoidFunction<Iterator<Tuple2<String, Long>>>) iterator -> {
                    List<AdClickTrend> adClickTrends = new ArrayList<AdClickTrend>();

                    while (iterator.hasNext()) {
                        Tuple2<String, Long> tuple = iterator.next();
                        String[] keySplited = tuple._1.split("_");
                        // yyyyMMddHHmm
                        String dateMinute = keySplited[0];
                        long adid = Long.valueOf(keySplited[1]);
                        long clickCount = tuple._2;

                        String date = DateUtils.formatDate(DateUtils.parseDateKey(
                                dateMinute.substring(0, 8)));
                        String hour = dateMinute.substring(8, 10);
                        String minute = dateMinute.substring(10);

                        AdClickTrend adClickTrend = new AdClickTrend();
                        adClickTrend.setDate(date);
                        adClickTrend.setHour(hour);
                        adClickTrend.setMinute(minute);
                        adClickTrend.setAdid(adid);
                        adClickTrend.setClickCount(clickCount);

                        adClickTrends.add(adClickTrend);
                    }

                    IAdClickTrendDAO adClickTrendDAO = DAOFactory.getAdClickTrendDAO();
                    adClickTrendDAO.updateBatch(adClickTrends);
                }));
    }
}
