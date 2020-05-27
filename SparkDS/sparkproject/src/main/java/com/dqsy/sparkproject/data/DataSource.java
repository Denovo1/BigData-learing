package com.dqsy.sparkproject.data;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import java.util.*;

/**
 * @Description TODO
 * @Author liusinan
 * @Date 2020/1/14 19:14
 * @Version 1.0
 **/
public class DataSource {
    public static void mock(JavaSparkContext sc,
                            SQLContext sqlContext) {

        JavaRDD<Row> rowsRDD = sqlContext.read().format("jdbc")
                .option("url", "jdbc:mysql://192.168.109.135:3306/spark_project")
                .option("dbtable", "user_behavior")
                .option("user", "root")
                .option("password", "123456").load().toJavaRDD();

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

        df.createOrReplaceTempView("user_behavior");

        /**
         * ==================================================================
         */

        JavaRDD<Row> userinfoRDD = sqlContext.read().format("jdbc")
                .option("url", "jdbc:mysql://192.168.109.135:3306/spark_project")
                .option("dbtable", "user_info")
                .option("user", "root")
                .option("password", "123456").load().toJavaRDD();

        StructType schema2 = DataTypes.createStructType(Arrays.asList(
                DataTypes.createStructField("user_id", DataTypes.LongType, true),
                DataTypes.createStructField("username", DataTypes.StringType, true),
                DataTypes.createStructField("name", DataTypes.StringType, true),
                DataTypes.createStructField("age", DataTypes.IntegerType, true),
                DataTypes.createStructField("professional", DataTypes.StringType, true),
                DataTypes.createStructField("city", DataTypes.StringType, true),
                DataTypes.createStructField("sex", DataTypes.StringType, true)));

        Dataset<Row> userinfodf = sqlContext.createDataFrame(userinfoRDD, schema2);
        userinfodf.createOrReplaceTempView("user_info");

        /**
         * ==================================================================
         */

        JavaRDD<Row> productinfoRDD = sqlContext.read().format("jdbc")
                .option("url", "jdbc:mysql://192.168.109.135:3306/spark_project")
                .option("dbtable", "product_info")
                .option("user", "root")
                .option("password", "123456").load().toJavaRDD();

        StructType schema3 = DataTypes.createStructType(Arrays.asList(
                DataTypes.createStructField("product_id", DataTypes.LongType, true),
                DataTypes.createStructField("product_name", DataTypes.StringType, true),
                DataTypes.createStructField("extend_info", DataTypes.StringType, true)));
        Dataset<Row> df3 = sqlContext.createDataFrame(productinfoRDD, schema3);
        df3.createOrReplaceTempView("product_info");
    }

}
