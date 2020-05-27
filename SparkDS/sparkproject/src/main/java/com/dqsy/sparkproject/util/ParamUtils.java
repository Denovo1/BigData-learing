package com.dqsy.sparkproject.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dqsy.sparkproject.conf.ConfigurationManager;
import com.dqsy.sparkproject.constant.Constants;

/**
 * 参数工具类
 *
 * @author liusinan
 */
public class ParamUtils {

    /**
     * 从命令行参数中提取任务id
     *
     * @param args 命令行参数
     * @return 任务id
     */
    public static Long getTaskIdFromArgs(String[] args, String taskType) {
        boolean local = ConfigurationManager.getBoolean(Constants.SPARK_LOCAL);

        if (local) {
            try {
                if (args != null && args.length > 0) {
                    return Long.valueOf(args[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (args != null && args.length > 0) {
                    return Long.valueOf(args[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 从命令行参数中提取local
     *
     * @param args 命令行参数
     * @return local
     */
    public static boolean getTaskStusFromArgs(String[] args) {
        if (args[1].equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 从JSON对象中提取参数
     *
     * @param jsonObject JSON对象
     * @return 参数
     */
    public static String getParam(JSONObject jsonObject, String field) {
        JSONArray jsonArray = jsonObject.getJSONArray(field);
        if (jsonArray != null && jsonArray.size() > 0) {
            return jsonArray.getString(0);
        }
        return null;
    }

}
