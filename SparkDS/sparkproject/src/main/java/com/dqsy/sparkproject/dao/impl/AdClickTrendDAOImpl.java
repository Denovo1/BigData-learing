package com.dqsy.sparkproject.dao.impl;

import com.dqsy.sparkproject.dao.IAdClickTrendDAO;
import com.dqsy.sparkproject.domain.AdClickTrend;
import com.dqsy.sparkproject.util.DBCPUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * 广告点击趋势DAO实现类
 *
 * @author liusinan
 */
public class AdClickTrendDAOImpl implements IAdClickTrendDAO {

    private QueryRunner qr = new QueryRunner(DBCPUtil.getDataSource());

    @Override
    public void updateBatch(List<AdClickTrend> adClickTrends) {
        try {
            //步骤：
            //①准备两个容器分别存储要更新的AdUserClickCount实例和要插入的AdUserClickCount实例
            LinkedList<AdClickTrend> updateContainer = new LinkedList<>();
            LinkedList<AdClickTrend> insertContainer = new LinkedList<>();
            //②填充容器（一次与db中的记录进行比对，若存在，就添加到更新容器中；否则，添加到保存的容器中）
            String sql = "select click_count from advertisement_real_time_click where `date`=? and ad_id=? and minute=? and hour=?";
            for (AdClickTrend bean : adClickTrends) {
                Object click_count = qr.query(sql, new ScalarHandler<>("click_count"), bean.getDate(), bean.getAdid(), bean.getMinute(), bean.getHour());
                if (click_count == null) {
                    insertContainer.add(bean);
                } else {
                    updateContainer.add(bean);
                }
            }

            //③对更新的容器进行批量update操作
            // click_count=click_count+?  <~ ? 证明?传过来的是本batch新增的click_count,不包括过往的历史  (调用处调用：reduceByKey)
            // click_count=?  <~ ? 证明?传过来的是总的click_count （调用出：使用了updateStateByKey）
            sql = "update advertisement_real_time_click set click_count=?  where `date`=? and ad_id=? and minute=? and hour=?";
            Object[][] params = new Object[updateContainer.size()][];
            for (int i = 0; i < params.length; i++) {
                AdClickTrend bean = updateContainer.get(i);
                params[i] = new Object[]{bean.getClickCount(), bean.getDate(), bean.getAdid(), bean.getMinute(), bean.getHour()};
            }
            qr.batch(sql, params);

            //④对保存的容器进行批量insert操作
            sql = "insert into advertisement_real_time_click values(?,?,?,?,?)";
            params = new Object[insertContainer.size()][];
            for (int i = 0; i < params.length; i++) {
                AdClickTrend bean = insertContainer.get(i);
                params[i] = new Object[]{bean.getDate(), bean.getHour(), bean.getMinute(), bean.getAdid(), bean.getClickCount()};
            }
            qr.batch(sql, params);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 通常来说，同一个key的数据（比如rdd，包含了多条相同的key）
        // 通常是在一个分区内的
        // 一般不会出现重复插入的
        // 但是会出现key重复插入的情况就给一个create_time字段

    }

}
