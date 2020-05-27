package com.dqsy.sparkproject.test;

import com.dqsy.sparkproject.conf.ConfigurationManager;

public class ConfigurationManagerTest {

    public static void main(String[] args) {
        String testkey1 = ConfigurationManager.getProperty("jdbc.url");
        String testkey2 = ConfigurationManager.getProperty("jdbc.url.prod");
        System.out.println(testkey1);
        System.out.println(testkey2);
    }

}
