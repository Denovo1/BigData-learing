package com.dqsy.sparkproject.spark.product;

import com.alibaba.fastjson.JSONObject;
import com.dqsy.sparkproject.constant.Constants;
import com.dqsy.sparkproject.dao.IAreaTop3ProductDAO;
import com.dqsy.sparkproject.dao.ITaskDAO;
import com.dqsy.sparkproject.dao.factory.DAOFactory;
import com.dqsy.sparkproject.domain.Task;
import com.dqsy.sparkproject.data.ProdDataSource;
import com.dqsy.sparkproject.util.ParamUtils;
import com.dqsy.sparkproject.util.SparkUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.hive.HiveContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;

/**
 * 各区域top3热门商品统计Spark作业
 *
 * @author liusinan
 */
public class AreaHotProduct {

    private static JavaSparkContext sc;
    // 创建需要使用的DAO组件
    private static ITaskDAO taskDAO = DAOFactory.getTaskDAO();
    private static long taskid;
    private static Task task;

    public static void main(String[] args) {
        try {
            // 创建SparkConf
            SparkConf conf = new SparkConf()
                    .setAppName("AreaHotProduct");

            boolean local = ParamUtils.getTaskStusFromArgs(args);

            SparkUtils.setMaster(conf, local);

            // 构建Spark上下文
            sc = new JavaSparkContext(conf);
            HiveContext sqlContext = new HiveContext(sc.sc());
            SQLContext mysqlContext = new SQLContext(sc.sc());
//		sqlContext.setConf("spark.sql.shuffle.partitions", "1000");
//		sqlContext.setConf("spark.sql.autoBroadcastJoinThreshold", "20971520");

            // 注册自定义函数
            sqlContext.udf().register("concat_long_string",
                    new ConcatLongStringUDF(), DataTypes.StringType);
            sqlContext.udf().register("get_json_object",
                    new GetJsonObjectUDF(), DataTypes.StringType);
            sqlContext.udf().register("random_prefix",
                    new RandomPrefixUDF(), DataTypes.StringType);
            sqlContext.udf().register("remove_random_prefix",
                    new RemoveRandomPrefixUDF(), DataTypes.StringType);
            sqlContext.udf().register("group_concat_distinct",
                    new GroupConcatDistinctUDAF());

            // 导入数据
            ProdDataSource.mock(sc, sqlContext);

            taskid = ParamUtils.getTaskIdFromArgs(args,
                    Constants.SPARK_LOCAL_TASKID_PRODUCT);
            task = taskDAO.findById(taskid);

            JSONObject taskParam = JSONObject.parseObject(task.getTaskParam());
            String startDate = ParamUtils.getParam(taskParam, Constants.PARAM_START_DATE);
            String endDate = ParamUtils.getParam(taskParam, Constants.PARAM_END_DATE);

            // 查询用户指定日期范围内的点击行为数据（city_id，在哪个城市发生的点击行为）
            // 1：Hive数据源的使用
            JavaPairRDD<Long, Row> cityid2clickActionRDD = getcityid2ClickActionRDDByDate(
                    sqlContext, startDate, endDate);
            System.out.println("cityid2clickActionRDD: " + cityid2clickActionRDD.count());

            // 从MySQL中查询城市信息
            // 2：MySQL数据源的使用
            JavaPairRDD<Long, Row> cityid2cityInfoRDD = getcityid2CityInfoRDD(mysqlContext);
            System.out.println("cityid2cityInfoRDD: " + cityid2cityInfoRDD.count());

            // 生成点击商品基础信息临时表
            // 3：将RDD转换为DataFrame，并注册临时表
            generateTempClickProductBasicTable(sqlContext, cityid2clickActionRDD, cityid2cityInfoRDD);

            // 生成各区域各商品点击次数的临时表
            generateTempAreaPrdocutClickCountTable(sqlContext);

            // 生成包含完整商品信息的各区域各商品点击次数的临时表
            generateTempAreaFullProductClickCountTable(sqlContext);

            // 使用开窗函数获取各个区域内点击次数排名前3的热门商品
            JavaRDD<Row> areaTop3ProductRDD = getAreaTop3ProductRDD(sqlContext);
            System.out.println("areaTop3ProductRDD: " + areaTop3ProductRDD.count());

            // 最后数据量几十个，所以可以直接将数据collect()到driver本地
            // 用批量插入的方式，一次性插入mysql
            List<Row> rows = areaTop3ProductRDD.collect();
            System.out.println("rows: " + rows.size());
            persistAreaTop3Product(taskid, rows);

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
     * 查询指定日期范围内的点击行为数据
     *
     * @param sqlContext
     * @param startDate  起始日期
     * @param endDate    截止日期
     * @return 点击行为数据
     */
    private static JavaPairRDD<Long, Row> getcityid2ClickActionRDDByDate(
            HiveContext sqlContext, String startDate, String endDate) {
        // 从user_behavior中，查询用户访问行为数据
        // 第一个限定：click_product_id，限定为不为空的访问行为，那么就代表着点击行为
        // 第二个限定：在用户指定的日期范围内的数据

        String sql =
                "SELECT "
                        + "city_id,"
                        + "click_product_id product_id "
                        + "FROM user_behavior "
                        + "WHERE click_product_id <> 0 "
                        + "AND date>='" + startDate + "' "
                        + "AND date<='" + endDate + "'";

        Dataset<Row> clickActionDF = sqlContext.sql(sql);

        JavaRDD<Row> clickActionRDD = clickActionDF.toJavaRDD();

        JavaPairRDD<Long, Row> cityid2clickActionRDD = clickActionRDD.mapToPair(
                (PairFunction<Row, Long, Row>) row -> {
                    Long cityid = row.getLong(0);
                    return new Tuple2<Long, Row>(cityid, row);
                });

        return cityid2clickActionRDD;
    }

    /**
     * 使用Spark SQL从MySQL中查询城市信息
     *
     * @param mysqlContext SQLContext
     * @return
     */
    private static JavaPairRDD<Long, Row> getcityid2CityInfoRDD(SQLContext mysqlContext) {

        JavaRDD<Row> cityInfoRDD = mysqlContext.read().format("jdbc")
                .option("url", "jdbc:mysql://192.168.109.135:3306/spark_project?useUnicode=true&characterEncoding=utf8")
                .option("dbtable", "city_info")
                .option("user", "root")
                .option("password", "123456").load().toJavaRDD();

        JavaPairRDD<Long, Row> cityid2cityInfoRDD = cityInfoRDD.mapToPair(
                (PairFunction<Row, Long, Row>) row -> {
                    long cityid = Long.valueOf(String.valueOf(row.get(0)));
                    return new Tuple2<Long, Row>(cityid, row);
                });

        return cityid2cityInfoRDD;
    }

    /**
     * 生成点击商品基础信息临时表
     *
     * @param sqlContext
     * @param cityid2clickActionRDD
     * @param cityid2cityInfoRDD
     */
    private static void generateTempClickProductBasicTable(
            HiveContext sqlContext,
            JavaPairRDD<Long, Row> cityid2clickActionRDD,
            JavaPairRDD<Long, Row> cityid2cityInfoRDD) {
        // 执行join操作，进行点击行为数据和城市数据的关联
        JavaPairRDD<Long, Tuple2<Row, Row>> joinedRDD =
                cityid2clickActionRDD.join(cityid2cityInfoRDD);

        // 将上面的JavaPairRDD，转换成一个JavaRDD<Row>，并转换为DataFrame
        JavaRDD<Row> mappedRDD = joinedRDD.map(
                (Function<Tuple2<Long, Tuple2<Row, Row>>, Row>) tuple -> {
                    long cityid = tuple._1;
                    Row clickAction = tuple._2._1;
                    Row cityInfo = tuple._2._2;

                    long productid = clickAction.getLong(1);
                    String cityName = cityInfo.getString(1);
                    String area = cityInfo.getString(2);

                    return RowFactory.create(cityid, cityName, area, productid);
                });

        List<StructField> structFields = new ArrayList<StructField>();
        structFields.add(DataTypes.createStructField("city_id", DataTypes.LongType, true));
        structFields.add(DataTypes.createStructField("city_name", DataTypes.StringType, true));
        structFields.add(DataTypes.createStructField("area", DataTypes.StringType, true));
        structFields.add(DataTypes.createStructField("product_id", DataTypes.LongType, true));

        // 1 北京
        // 2 上海
        // 1 北京
        // group by area,product_id
        // 1:北京,2:上海

        // 两个函数
        // UDF：concat2()，将两个字段拼接起来，用指定的分隔符
        // UDAF：group_concat_distinct()，将一个分组中的多个字段值，用逗号拼接起来，同时进行去重

        StructType schema = DataTypes.createStructType(structFields);

        Dataset<Row> df = sqlContext.createDataFrame(mappedRDD, schema);
        System.out.println("tmp_click_product_basic: " + df.count());

        // 点击商品基础信息临时表
        df.createOrReplaceTempView("tmp_click_product_basic");
    }

    /**
     * 生成各区域各商品点击次数临时表
     *
     * @param sqlContext
     */
    private static void generateTempAreaPrdocutClickCountTable(
            HiveContext sqlContext) {
        // 按照area和product_id两个字段进行分组
        // 计算出各区域各商品的点击次数
        // 可以获取到每个area下的每个product_id的城市信息拼接起来的串
        String sql =
                "SELECT "
                        + "area,"
                        + "product_id,"
                        + "count(*) click_count, "
                        + "group_concat_distinct(concat_long_string(city_id,city_name,':')) city_infos "
                        + "FROM tmp_click_product_basic "
                        + "GROUP BY area,product_id ";

        /**
         * 双重group by
         */

//		String _sql =
//				"SELECT "
//					+ "product_id_area,"
//					+ "count(click_count) click_count,"
//					+ "group_concat_distinct(city_infos) city_infos "
//				+ "FROM ( "
//					+ "SELECT "
//						+ "remove_random_prefix(product_id_area) product_id_area,"
//						+ "click_count,"
//						+ "city_infos "
//					+ "FROM ( "
//						+ "SELECT "
//							+ "product_id_area,"
//							+ "count(*) click_count,"
//							+ "group_concat_distinct(concat_long_string(city_id,city_name,':')) city_infos "
//						+ "FROM ( "
//							+ "SELECT "
//								+ "random_prefix(concat_long_string(product_id,area,':'), 10) product_id_area,"
//								+ "city_id,"
//								+ "city_name "
//							+ "FROM tmp_click_product_basic "
//						+ ") t1 "
//						+ "GROUP BY product_id_area "
//					+ ") t2 "
//				+ ") t3 "
//				+ "GROUP BY product_id_area ";

        // 使用Spark SQL执行这条SQL语句
        Dataset<Row> df = sqlContext.sql(sql);

        System.out.println("tmp_area_product_click_count: " + df.count());

        // 再次将查询出来的数据注册为一个临时表
        // 各区域各商品的点击次数（以及额外的城市列表）
        df.createOrReplaceTempView("tmp_area_product_click_count");
    }

    /**
     * 生成区域商品点击次数临时表（包含了商品的完整信息）
     *
     * @param sqlContext
     */
    private static void generateTempAreaFullProductClickCountTable(HiveContext sqlContext) {
        // 将之前得到的各区域各商品点击次数表，product_id
        // 去关联商品信息表，product_id，product_name和product_status
        // product_status要特殊处理，0，1，分别代表了自营和第三方的商品，放在了一个json串里面
        // get_json_object()函数，可以从json串中获取指定的字段的值
        // if()函数，判断，如果product_status是0，那么就是自营商品；如果是1，那么就是第三方商品
        // area, product_id, click_count, city_infos, product_name, product_status

        String sql =
                "SELECT "
                        + "tapcc.area,"
                        + "tapcc.product_id,"
                        + "tapcc.click_count,"
                        + "tapcc.city_infos,"
                        + "pi.product_name,"
                        + "if(get_json_object(pi.extend_info,'product_status')='0','Self','Third Party') product_status "
                        + "FROM tmp_area_product_click_count tapcc "
                        + "JOIN product_info pi ON tapcc.product_id=pi.product_id ";

        Dataset<Row> df = sqlContext.sql(sql);

        System.out.println("tmp_area_fullprod_click_count: " + df.count());

        df.createOrReplaceTempView("tmp_area_fullprod_click_count");
    }

    /**
     * 获取各区域top3热门商品
     *
     * @param sqlContext
     * @return
     */
    private static JavaRDD<Row> getAreaTop3ProductRDD(HiveContext sqlContext) {
        // 开窗函数

        // 使用开窗函数先进行一个子查询
        // 按照area进行分组，给每个分组内的数据，按照点击次数降序排序，打上一个组内的行号
        // 接着在外层查询中，过滤出各个组内的行号排名前3的数据
        // 其实就是咱们的各个区域下top3热门商品

        // 华北、华东、华南、华中、西北、西南、东北
        // A级：华北、华东
        // B级：华南、华中
        // C级：西北、西南
        // D级：东北

        // case when
        // 根据多个条件，不同的条件对应不同的值
        // case when then ... when then ... else ... end

        String sql =
                "SELECT "
                        + "area,"
                        + "CASE "
                        + "WHEN area='China North' OR area='China East' THEN 'A Level' "
                        + "WHEN area='China South' OR area='China Middle' THEN 'B Level' "
                        + "WHEN area='East North' OR area='West South' THEN 'C Level' "
                        + "ELSE 'D Level' "
                        + "END area_level,"
                        + "product_id,"
                        + "click_count,"
                        + "city_infos,"
                        + "product_name,"
                        + "product_status "
                        + "FROM ("
                        + "SELECT "
                        + "area,"
                        + "product_id,"
                        + "click_count,"
                        + "city_infos,"
                        + "product_name,"
                        + "product_status,"
                        + "row_number() OVER (PARTITION BY area ORDER BY click_count DESC) rank "
                        + "FROM tmp_area_fullprod_click_count "
                        + ") t "
                        + "WHERE rank<=3";

        Dataset<Row> df = sqlContext.sql(sql);

        return df.toJavaRDD();
    }

    /**
     * 将计算出来的各区域top3热门商品写入MySQL中
     *
     * @param taskid
     * @param rows
     */
    private static void persistAreaTop3Product(long taskid, List<Row> rows) {
        List<com.dqsy.sparkproject.domain.AreaTop3Product> areaTop3Products = new ArrayList<com.dqsy.sparkproject.domain.AreaTop3Product>();

        for (Row row : rows) {
            com.dqsy.sparkproject.domain.AreaTop3Product areaTop3Product = new com.dqsy.sparkproject.domain.AreaTop3Product();
            areaTop3Product.setTaskid(taskid);
            areaTop3Product.setArea(row.getString(0));
            areaTop3Product.setAreaLevel(row.getString(1));
            areaTop3Product.setProductid(row.getLong(2));
            areaTop3Product.setClickCount(Long.valueOf(String.valueOf(row.get(3))));
            areaTop3Product.setCityInfos(row.getString(4));
            areaTop3Product.setProductName(row.getString(5));
            areaTop3Product.setProductStatus(row.getString(6));
            areaTop3Products.add(areaTop3Product);
        }

        IAreaTop3ProductDAO areTop3ProductDAO = DAOFactory.getAreaTop3ProductDAO();
        areTop3ProductDAO.insertBatch(areaTop3Products);
    }

}
