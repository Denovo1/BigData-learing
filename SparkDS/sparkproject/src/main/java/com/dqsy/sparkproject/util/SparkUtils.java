package com.dqsy.sparkproject.util;

import com.alibaba.fastjson.JSONObject;
import com.dqsy.sparkproject.conf.ConfigurationManager;
import com.dqsy.sparkproject.constant.Constants;
import com.dqsy.sparkproject.data.DataSource;
import com.dqsy.sparkproject.data.ProdDataSource;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.hive.HiveContext;

/**
 * Spark工具类
 *
 * @author liusinan
 */
public class SparkUtils {

    /**
     * 根据当前是否本地测试的配置
     * 决定，如何设置SparkConf的master
     */
    public static void setMaster(SparkConf conf, boolean local) {
        ConfigurationManager.setBoolean(local);
    }

    /**
     * 获取SQLContext
     * 如果spark.local设置为true，那么就创建SQLContext；否则，创建HiveContext
     *
     * @param sc
     * @return
     */
    public static SQLContext getSQLContext(SparkContext sc) {
        boolean local = ConfigurationManager.getBoolean(Constants.SPARK_LOCAL);
        if (local) {
            return new SQLContext(sc);
        } else {
            return new HiveContext(sc);
        }
    }

    /**
     * 导入数据源
     *
     * @param sc
     * @param sqlContext
     */
    public static void mockData(JavaSparkContext sc, SQLContext sqlContext) {
        boolean local = ConfigurationManager.getBoolean(Constants.SPARK_LOCAL);
        if (local) {
            DataSource.mock(sc, sqlContext);
        } else {
            ProdDataSource.mock(sc, sqlContext);
        }
    }

    /**
     * 获取指定日期范围内的用户访问行为数据RDD
     *
     * @param sqlContext SQLContext
     * @param taskParam  任务参数
     * @return 行为数据RDD
     */
    public static JavaRDD<Row> getActionRDDByDateRange(SQLContext sqlContext, JSONObject taskParam) {
        String startDate = ParamUtils.getParam(taskParam, Constants.PARAM_START_DATE);
        String endDate = ParamUtils.getParam(taskParam, Constants.PARAM_END_DATE);

        String sql =
                "select * "
                        + "from user_behavior "
                        + "where date>='" + startDate + "' "
                        + "and date<='" + endDate + "'";
        /**
         * 数据倾斜过滤导致倾斜的key
         *
         * 100万个key。只有2个key，数据量达到10万的。其他所有的key，对应的数量都是几十个。
         * 这个时候，可以去取舍，在从hive表查询源数据的时候，直接在sql中用where条件，过滤掉某几个key。
         */
//        				+ "and session_id not in('','','')"

        Dataset<Row> actionDF = sqlContext.sql(sql);

        /**
         * 这里就很有可能发生上面说的问题
         * 比如说，Spark SQl默认就给第一个stage设置了20个task，但是根据你的数据量以及算法的复杂度
         * 实际上，你需要1000个task去并行执行
         *
         * 所以说，在这里，就可以对Spark SQL刚刚查询出来的RDD执行repartition重分区操作
         */

//		return actionDF.javaRDD().repartition(1000);

        return actionDF.javaRDD();
    }

}
