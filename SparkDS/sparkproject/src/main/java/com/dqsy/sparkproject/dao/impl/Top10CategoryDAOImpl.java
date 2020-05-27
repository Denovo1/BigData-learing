package com.dqsy.sparkproject.dao.impl;

import com.dqsy.sparkproject.dao.ITop10CategoryDAO;
import com.dqsy.sparkproject.domain.Top10Category;
import com.dqsy.sparkproject.util.JDBCUtil;

/**
 * top10品类DAO实现
 *
 * @author liusinan
 */
public class Top10CategoryDAOImpl implements ITop10CategoryDAO {
    @Override
    public void insert(Top10Category category) {
        String sql = "insert into top10_cg_behavior_count values(?,?,?,?,?)";

        Object[] params = new Object[]{category.getTaskid(),
                category.getCategoryid(),
                category.getClickCount(),
                category.getOrderCount(),
                category.getPayCount()};

        JDBCUtil jdbcUtil = JDBCUtil.getInstance();
        jdbcUtil.executeUpdate(sql, params);
    }
}
