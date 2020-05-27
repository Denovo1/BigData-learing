package com.dqsy.sparkproject.dao.impl;

import com.dqsy.sparkproject.dao.IAdStatDAO;
import com.dqsy.sparkproject.domain.AdStat;
import com.dqsy.sparkproject.util.DBCPUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * 广告实时统计DAO实现类
 *
 * @author liusinan
 */
public class AdStatDAOImpl implements IAdStatDAO {

    private QueryRunner qr = new QueryRunner(DBCPUtil.getDataSource());

    @Override
    public void updateBatch(List<AdStat> adStats) {
        //步骤：
        //①准备两个容器分别存储要更新的AdUserClickCount实例和要插入的AdUserClickCount实例
        List<AdStat> updateContainer = new LinkedList<>();
        List<AdStat> insertContainer = new LinkedList<>();
        try {
            //②填充容器（一次与db中的记录进行比对，若存在，就添加到更新容器中；否则，添加到保存的容器中）
            String sql = "select click_count from advertisement_position_clickcount where `date`=? and province=? and city=? and ad_id=?";
            for (AdStat bean : adStats) {

                Object click_count = qr.query(sql, new ScalarHandler<>("click_count"), bean.getDate(), bean.getProvince(), bean.getCity(), bean.getAdid());
                if (click_count == null) {
                    insertContainer.add(bean);
                } else {
                    updateContainer.add(bean);
                }
            }

            //③对更新的容器进行批量update操作
            // click_count=click_count+?  <~ ? 证明?传过来的是本batch新增的click_count,不包括过往的历史  (调用处调用：reduceByKey)
            // click_count=?  <~ ? 证明?传过来的是总的click_count （调用出：使用了updateStateByKey）
            sql = "update advertisement_position_clickcount set click_count=? where `date`=? and province=? and city=? and ad_id=?";
            Object[][] params = new Object[updateContainer.size()][];
            for (int i = 0; i < params.length; i++) {
                AdStat bean = updateContainer.get(i);
                params[i] = new Object[]{bean.getClickCount(), bean.getDate(), bean.getProvince(), bean.getCity(), bean.getAdid()};
            }
            qr.batch(sql, params);
            //④对保存的容器进行批量insert操作
            sql = "insert into advertisement_position_clickcount values(?,?,?,?,?)";
            params = new Object[insertContainer.size()][];
            for (int i = 0; i < params.length; i++) {
                AdStat bean = insertContainer.get(i);
                params[i] = new Object[]{bean.getDate(), bean.getProvince(), bean.getCity(), bean.getAdid(), bean.getClickCount()};
            }
            qr.batch(sql, params);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}