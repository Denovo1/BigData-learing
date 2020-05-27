package com.dqsy.sparkproject.test;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.util.*;

/**
 * 任务管理DAO测试类
 *
 * @author liusinan
 */
public class TaskDAOTest {

    public static void main(String[] args) {
        /*ITaskDAO taskDAO = DAOFactory.getTaskDAO();
        Task task = taskDAO.findById(4);
        System.out.println(task);*/
//        System.out.println(Long.MIN_VALUE);
//        9223372036854775807
        SparkConf conf = new SparkConf().setAppName("JDBCDataSource").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);
        SQLContext sqlContext = new SQLContext(sc);

        Dataset<Row> t1 = sqlContext.read().format("jdbc")
                .option("url", "jdbc:mysql://localhost:3306/spark")
                .option("dbtable", "user_behavior")
                .option("user", "root")
                .option("password", "123456").load();
        t1.createOrReplaceTempView("user_behavior");

        String sql = "select t.date,t.uid " +
                "from (SELECT DISTINCT date,uid from user_behavior) t";
        Dataset<Row> sql1 = sqlContext.sql(sql);
        JavaRDD<Row> map = sql1.toJavaRDD().map(new Function<Row, Row>() {
            @Override
            public Row call(Row v1) throws Exception {
                String sessionid = UUID.randomUUID().toString().replace("-", "");
                return RowFactory.create(v1.get(0), v1.get(1), sessionid);
            }
        });
        StructType schema = DataTypes.createStructType(Arrays.asList(
                DataTypes.createStructField("date", DataTypes.StringType, true),
                DataTypes.createStructField("uid", DataTypes.LongType, true),
                DataTypes.createStructField("sid", DataTypes.StringType, true)
        ));

        Dataset<Row> t2 = sqlContext.createDataFrame(map, schema);
        t2.createOrReplaceTempView("t2");
        String s = "select t1.date,t1.uid,t2.sid as sid,t1.pid,t1.action_time,t1.search_key,t1.click_cag_id,t1.click_pd_id,t1.order_cg_id,t1.order_pd_id,t1.pay_cg_id,t1.pay_pd_id,t1.city_id " +
                "from user_behavior t1 join t2 " +
                "on t1.date=t2.date and t1.uid=t2.uid " +
                "order by uid,date";
        Dataset<Row> sql2 = sqlContext.sql(s);
        sql2.show(100);


        /*List<Row> rows = new ArrayList<Row>();
        for (int j = 3; j < 104; j++) {
            String sessionid = UUID.randomUUID().toString().replace("-", "");
            Row row = RowFactory.create(sessionid);
            rows.add(row);
        }
        JavaRDD<Row> rowsRDD = sc.parallelize(rows);
        StructType schema = DataTypes.createStructType(Arrays.asList(
                DataTypes.createStructField("session_id", DataTypes.StringType, true)));

        Dataset<Row> t2 = sqlContext.createDataFrame(rowsRDD, schema);
        t2.createOrReplaceTempView("t2");

        t2.write().format("jdbc")
                .option("url", "jdbc:mysql://localhost:3306/sessionid")
                .option("dbtable", "data")
                .option("user", "root")
                .option("password", "123456").save();
        sc.close();*/


        /*Dataset<Row> t1 = sqlContext.read().format("jdbc")
                .option("url", "jdbc:mysql://localhost:3306/mybatis")
                .option("dbtable", "city")
                .option("user", "root")
                .option("password", "123456").load();
        t1.createOrReplaceTempView("t1");*/

        /*String str = "北京市,天津市,上海市,重庆市,河北省,山西省,台湾省,辽宁省,吉林省,黑龙江省,江苏省,浙江省,安徽省,福建省,江西省,山东省,河南省,湖北省,湖南省,广东省,甘肃省,四川省,贵州省,海南省,云南省,青海省,陕西省,广西壮族自治区,西藏自治区,宁夏回族自治区,新疆维吾尔自治区,内蒙古自治区,澳门特别行政区,香港特别行政区";
        String[] provinces = str.split(",");
        System.out.println(provinces.length);
        for (int i = 0; i < provinces.length; i++) {
            String province = provinces[i];
            System.out.println(province);
        }*/
        /*Random random = new Random();
        System.out.println(((random.nextInt(8) + 1) * 100000 + random.nextInt(100000)));*/

        /*Dataset<Row> t1 = sqlContext.read().format("jdbc")
                .option("url", "jdbc:mysql://localhost:3306/spark_project")
                .option("dbtable", "user_visit_action_copy")
                .option("user", "root")
                .option("password", "123456").load();
        t1.createOrReplaceTempView("t1");

        *//*Dataset<Row> t2 = sqlContext.read().format("jdbc")
                .option("url", "jdbc:mysql://localhost:3306/sessionid")
                .option("dbtable", "user_visit_action_copy1")
                .option("user", "root")
                .option("password", "123456").load();
        t2.createOrReplaceTempView("user_visit_action_copy_copy1");*//*
        String sql = "select * from t1 where t1.page_id=1";
        Dataset<Row> t2 = sqlContext.sql(sql);

        t2.createOrReplaceTempView("t2");



        for (int k = 0; k < t2.count(); k++) {
            Random random = new Random();
            Map map = new HashMap();
            map.put(1, random.nextInt(100));
            t2.na().replace("page_id", map);
        }
        t2.write().format("jdbc")
                .mode(SaveMode.Overwrite)
                .option("url", "jdbc:mysql://localhost:3306/spark_project")
                .option("dbtable", "user_visit_action_copy_copy")
                .option("user", "root")
                .option("password", "123456")
                .save();*/



        /*JavaRDD<Row> rdd = t2.toJavaRDD();

        JavaPairRDD<String, Integer> javaPairRDD = rdd.mapToPair(new PairFunction<Row, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(Row row) throws Exception {
                Random random = new Random();
                Integer pageid = row.getAs("page_id");
                pageid = random.nextInt(100);
                String sessionid = row.getAs("session_id");
                return new Tuple2<>(sessionid, pageid);
            }
        });
        JavaRDD<Row> map = javaPairRDD.map(new Function<Tuple2<String, Integer>, Row>() {
            @Override
            public Row call(Tuple2<String, Integer> v1) throws Exception {

                return null;
            }
        });

        StructType schema = DataTypes.createStructType(Arrays.asList(
                DataTypes.createStructField("date", DataTypes.StringType, true),
                DataTypes.createStructField("user_id", DataTypes.LongType, true),
                DataTypes.createStructField("session_id", DataTypes.StringType, true),
                DataTypes.createStructField("page_id", DataTypes.LongType, true),
                DataTypes.createStructField("action_time", DataTypes.StringType, true),
                DataTypes.createStructField("search_keyword", DataTypes.StringType, true),
                DataTypes.createStructField("click_category_id", DataTypes.LongType, true),
                DataTypes.createStructField("click_product_id", DataTypes.LongType, true),
                DataTypes.createStructField("order_category_ids", DataTypes.StringType, true),
                DataTypes.createStructField("order_product_ids", DataTypes.StringType, true),
                DataTypes.createStructField("pay_category_ids", DataTypes.StringType, true),
                DataTypes.createStructField("pay_product_ids", DataTypes.StringType, true),
                DataTypes.createStructField("city_id", DataTypes.LongType, true),
                DataTypes.createStructField("id", DataTypes.IntegerType, true)
        ));
        Dataset<Row> df = sqlContext.createDataFrame(rowsRDD, schema);*/






        /*        sqlContext.setConf("hive.metastore.uris", "thrift://METASTORE:9083");*/
        /*HiveContext sqlContext = new HiveContext(sc);
        JavaRDD<Row> rowsRDD = sqlContext.sql("select * from user_visit_action").toJavaRDD();
        StructType schema = DataTypes.createStructType(Arrays.asList(
                DataTypes.createStructField("date", DataTypes.StringType, true),
                DataTypes.createStructField("user_id", DataTypes.LongType, true),
                DataTypes.createStructField("session_id", DataTypes.StringType, true),
                DataTypes.createStructField("page_id", DataTypes.LongType, true),
                DataTypes.createStructField("action_time", DataTypes.StringType, true),
                DataTypes.createStructField("search_keyword", DataTypes.StringType, true),
                DataTypes.createStructField("click_category_id", DataTypes.LongType, true),
                DataTypes.createStructField("click_product_id", DataTypes.LongType, true),
                DataTypes.createStructField("order_category_ids", DataTypes.StringType, true),
                DataTypes.createStructField("order_product_ids", DataTypes.StringType, true),
                DataTypes.createStructField("pay_category_ids", DataTypes.StringType, true),
                DataTypes.createStructField("pay_product_ids", DataTypes.StringType, true),
                DataTypes.createStructField("city_id", DataTypes.LongType, true)));

        Dataset<Row> df = sqlContext.createDataFrame(rowsRDD, schema);

        df.createOrReplaceTempView("user_visit_action");
        for (Row _row : df.javaRDD().take(1)) {
            System.out.println(_row);
        }*/

//        SQLContext sqlContext = new SQLContext(sc);

        /*Dataset<Row> t1 = sqlContext.read().format("jdbc")
                .option("url", "jdbc:mysql://localhost:3306/spark_project")
                .option("dbtable", "user_visit_action")
                .option("user", "root")
                .option("password", "123456").load();
        t1.createOrReplaceTempView("user_visit_action");

        Dataset<Row> t2 = sqlContext.read().format("jdbc")
                .option("url", "jdbc:mysql://localhost:3306/sessionid")
                .option("dbtable", "user_visit_action_copy_copy1")
                .option("user", "root")
                .option("password", "123456").load();
        t2.createOrReplaceTempView("user_visit_action_copy_copy1");

        String sql = "SELECT distinct t1.user_id,t1.session_id,t1.id from user_visit_action t1,(select * from user_visit_action) t2 " +
                "WHERE t1.user_id!=t2.user_id AND t1.session_id=t2.session_id";
        Dataset<Row> df = sqlContext.sql(sql);
        df.show();*/

        /*df.write().format("jdbc")
                .option("url", "jdbc:mysql://localhost:3306/spark_project")
                .option("dbtable", "user_visit_action_copy_copy1")
                .option("user", "root")
                .option("password", "123456").save();*/

        /*Dataset<Row> t1 = sqlContext.read().format("jdbc")
                .option("url", "jdbc:mysql://localhost:3306/spark_project")
                .option("dbtable", "user_visit_action_copy_copy1")
                .option("user", "root")
                .option("password", "123456").load();
        t1.createOrReplaceTempView("t1");*/

        //session_id
        /*List<Row> rows = new ArrayList<Row>();
        for (int j = 3; j < 104; j++) {
            String sessionid = UUID.randomUUID().toString().replace("-", "");
            Row row = RowFactory.create(sessionid);
            rows.add(row);
        }
        JavaRDD<Row> rowsRDD = sc.parallelize(rows);
        StructType schema = DataTypes.createStructType(Arrays.asList(
                DataTypes.createStructField("session_id", DataTypes.StringType, true)));

        Dataset<Row> t2 = sqlContext.createDataFrame(rowsRDD, schema);
        t2.createOrReplaceTempView("t2");

        t2.write().format("jdbc")
                .option("url", "jdbc:mysql://localhost:3306/sessionid")
                .option("dbtable", "data")
                .option("user", "root")
                .option("password", "123456").save();
        sc.close();*/

        /*Dataset<Row> t1 = sqlContext.read().format("jdbc")
                .option("url", "jdbc:mysql://localhost:3306/spark_project")
                .option("dbtable", "useractions")
                .option("user", "root")
                .option("password", "123456").load();
        t1.createOrReplaceTempView("t");
        String sql = "select distinct user_id from t1";
        Dataset<Row> df = sqlContext.sql(sql);
        df.createOrReplaceTempView("t1");*/






        /*Dataset<Row> t1 = sqlContext.read().format("jdbc")
                .option("url", "jdbc:mysql://localhost:3306/spark_project")
                .option("dbtable", "user_visit_action_copy_copy")
                .option("user", "root")
                .option("password", "123456").load();
        t1.createOrReplaceTempView("t");

        Dataset<Row> t2 = sqlContext.read().format("jdbc")
                .option("url", "jdbc:mysql://localhost:3306/spark_project")
                .option("dbtable", "user_visit_action_copy_copy1")
                .option("user", "root")
                .option("password", "123456").load();
        t2.createOrReplaceTempView("t1");

        String sql = "UPDATE t JOIN t1 on t1.id=t.id set t.session_id=t1.session_id";
        String sql1 = "create table usernew as (select * from t JOIN t1 on t1.id=t.id set t.session_id=t1.session_id)";

        Dataset<Row> df = sqlContext.sql(sql);

        df.write().format("jdbc")
                .mode(SaveMode.Overwrite)
                .option("url", "jdbc:mysql://localhost:3306/spark_project")
                .option("dbtable", "user_visit_action_copy_copy")
                .option("user", "root")
                .option("password", "123456").save();*/



        /*Dataset<Row> t1 = sqlContext.read().format("jdbc")
                .option("url", "jdbc:mysql://localhost:3306/sessionid")
                .option("dbtable", "last")
                .option("user", "root")
                .option("password", "123456").load();
        t1.createOrReplaceTempView("user_visit_action");*/

//        String sql = "select DISTINCT t1.date,t1.user_id,t1.session_id,t1.id FROM user_visit_action_copy_copy_bf t1,(select * from user_visit_action_copy_copy_bf) t2 \n" +
//                "WHERE t1.user_id=t2.user_id AND t1.date!=t2.date and t1.session_id=t2.session_id";
        /*String sql = "SELECT distinct t1.user_id,t1.session_id,t1.id from user_visit_action t1,(select * from user_visit_action) t2 " +
                "WHERE t1.user_id!=t2.user_id AND t1.session_id=t2.session_id";
        Dataset<Row> df = sqlContext.sql(sql);
        df.show();*/
        /*df.write().format("jdbc")
                .mode(SaveMode.Overwrite)
                .option("url", "jdbc:mysql://localhost:3306/sessionid")
                .option("dbtable", "t")
                .option("user", "root")
                .option("password", "123456").save();*/

        /*Dataset<Row> t1 = sqlContext.read().format("jdbc")
                .option("url", "jdbc:mysql://localhost:3306/sessionid")
                .option("dbtable", "t")
                .option("user", "root")
                .option("password", "123456").load();
        t1.createOrReplaceTempView("t");

        Dataset<Row> t2 = sqlContext.read().format("jdbc")
                .option("url", "jdbc:mysql://localhost:3306/sessionid")
                .option("dbtable", "data")
                .option("user", "root")
                .option("password", "123456").load();
        t2.createOrReplaceTempView("t1");

        String sql1 = "create table usernew as (select * from t JOIN t1 on t1.id=t.id  t.session_id=t1.session_id)";*/




        /*Dataset<Row> t1 = sqlContext.read().format("jdbc")
                .option("url", "jdbc:mysql://localhost:3306/sessionid")
                .option("dbtable", "user_visit_action_copy_copy_bf")
                .option("user", "root")
                .option("password", "123456").load();
        t1.createOrReplaceTempView("t1");
        Dataset<Row> t2 = sqlContext.read().format("jdbc")
                .option("url", "jdbc:mysql://localhost:3306/sessionid")
                .option("dbtable", "result")
                .option("user", "root")
                .option("password", "123456").load();
        t2.createOrReplaceTempView("t2");
//        String sql = "SELECT DISTINCT t.date,t.user_id from t,(select * from t) t1 where t.date!=t1.date and t.user_id=t1.user_id";
//        String sql1 = "select t.date,t.user_id,t2.session_id,t.id from t join t2 on t.date=t2.date and t.user_id=t2.user_id";
        String sql2 = "select t1.date,t1.user_id,t2.session_id,t1.page_id,t1.action_time,t1.search_keyword,t1.click_category_id,t1.click_product_id,t1.order_category_ids,t1.order_product_ids,t1.pay_category_ids,t1.pay_product_ids,t1.city_id,t1.id " +
                "from t1 join t2 on t1.id=t2.id order by t1.id";
        Dataset<Row> df = sqlContext.sql(sql2);

        df.write().format("jdbc")
                .mode(SaveMode.Overwrite)
                .option("url", "jdbc:mysql://localhost:3306/sessionid")
                .option("dbtable", "last")
                .option("user", "root")
                .option("password", "123456").save();*/

//        sc.close();
    }

}
