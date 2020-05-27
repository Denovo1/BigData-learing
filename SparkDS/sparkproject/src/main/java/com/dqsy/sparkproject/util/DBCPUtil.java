package com.dqsy.sparkproject.util;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import com.dqsy.sparkproject.conf.ConfigurationManager;
import com.dqsy.sparkproject.constant.Constants;
import org.apache.commons.dbcp.BasicDataSourceFactory;

/**
 * @author liusinan
 * @version 1.0.0
 * @ClassName DBCPUtil.java
 * @Description TODO
 * @createTime 2020年04月04日 15:24:00
 */
public class DBCPUtil {
    private static DataSource ds;

    static {
        try {

            Properties properties = new Properties();

            /**
             * 根据conf.properties中的配置信息来判断是执行本地 测试还是生产环境
             */

            String filePath = "ad" + File.separator + ConfigurationManager.getProperty(Constants.DBCP_CONFIG_FILE);
            InputStream in = DBCPUtil.class.getClassLoader().getResourceAsStream(filePath);
            properties.load(in);//装在配置文件

            ds = BasicDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {//初始化异常

            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * 获得数据源（连接池）
     *
     * @return
     */
    public static DataSource getDataSource() {
        return ds;
    }

    /**
     * 从连接池中获得连接的实例
     *
     * @return
     */
    public static Connection getConnection() {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
