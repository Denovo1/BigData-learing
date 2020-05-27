package com.dqsy.sparkproject.spark.product;

import org.apache.spark.sql.api.java.UDF2;

import java.util.Random;

/**
 * random_prefix()随即前缀
 *
 * @author liusinan
 */
public class RandomPrefixUDF implements UDF2<String, Integer, String> {

    private static final long serialVersionUID = 1L;

    @Override
    public String call(String val, Integer num) throws Exception {
        Random random = new Random();
        int randNum = random.nextInt(10);
        return randNum + "_" + val;
    }

}
