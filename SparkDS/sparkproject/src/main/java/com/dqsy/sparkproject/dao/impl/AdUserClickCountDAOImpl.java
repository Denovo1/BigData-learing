package com.dqsy.sparkproject.dao.impl;

import com.dqsy.sparkproject.dao.IAdUserClickCountDAO;
import com.dqsy.sparkproject.domain.AdUserClickCount;
import com.dqsy.sparkproject.util.DBCPUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * 用户广告点击量DAO实现类
 *
 * @author liusinan
 */
public class AdUserClickCountDAOImpl implements IAdUserClickCountDAO {

    private QueryRunner qr = new QueryRunner(DBCPUtil.getDataSource());

    @Override
    public void updateBatch(List<AdUserClickCount> adUserClickCounts) {
        //①准备两个容器分别存储要更新的AdUserClickCount实例和要插入的AdUserClickCount实例
        LinkedList<AdUserClickCount> updateContainer = new LinkedList<>();
        LinkedList<AdUserClickCount> insertContainer = new LinkedList<>();
        try {
            //②填充容器（一次与db中的记录进行比对，若存在，就添加到更新容器中；否则，添加到保存的容器中）
            String sql = "select click_count from advertisement_user_clickcount where `date`=? and user_id=? and ad_id=?";
            for (AdUserClickCount bean : adUserClickCounts) {
                //ScalarHandler:用于统计表记录的条数
                //BeanHandler:用来将表中每条记录封装到一个实例中
                //BeanListHandler: 用来将表中所有记录封装到一个集合中，集合中每个元素即为：每条记录所封装的实体类对象

                Object click_count = qr.query(sql, new ScalarHandler<>("click_count"), bean.getDate(), bean.getUserid(), bean.getAdid());
                if (click_count == null) {
                    insertContainer.add(bean);
                } else {
                    updateContainer.add(bean);
                }
            }

            //③对更新的容器进行批量update操作
            // click_count=click_count+?  <~ ? 证明?传过来的是本batch新增的click_count,不包括过往的历史  (调用处调用：reduceByKey)
            // click_count=?  <~ ? 证明?传过来的是总的click_count （调用出：使用了updateStateByKey）

            sql = "update advertisement_user_clickcount set click_count=click_count+? where `date`=? and user_id=? and ad_id=?";
            Object[][] params = new Object[updateContainer.size()][];
            for (int i = 0; i < params.length; i++) {
                AdUserClickCount bean = updateContainer.get(i);
                params[i] = new Object[]{bean.getClickCount(), bean.getDate(), bean.getUserid(), bean.getAdid()};

            }
            qr.batch(sql, params);

            //④对保存的容器进行批量insert操作
            sql = "insert into advertisement_user_clickcount values(?,?,?,?)";
            params = new Object[insertContainer.size()][];
            for (int i = 0; i < params.length; i++) {
                AdUserClickCount bean = insertContainer.get(i);
                params[i] = new Object[]{bean.getDate(), bean.getUserid(), bean.getAdid(), bean.getClickCount()};
            }
            qr.batch(sql, params);

        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 根据多个key查询用户广告点击量
     *
     * @param date   日期
     * @param userid 用户id
     * @param adid   广告id
     * @return
     */
    @Override
    public int findClickCountByMultiKey(String date, long userid, long adid) {
        int result = 0;
        try {
            String sql = "SELECT click_count "
                    + "FROM advertisement_user_clickcount "
                    + "WHERE date=? "
                    + "AND user_id=? "
                    + "AND ad_id=?";
            int click_count = qr.query(sql, new ScalarHandler<>("click_count"), date, userid, adid);
            result = click_count;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

}
