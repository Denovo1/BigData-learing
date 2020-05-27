package com.dqsy.sparkproject.dao.impl;

import com.dqsy.sparkproject.dao.ITop10SessionDAO;
import com.dqsy.sparkproject.domain.Top10Session;
import com.dqsy.sparkproject.util.JDBCUtil;

/**
 * top10活跃session的DAO实现
 *
 * @author liusinan
 */
public class Top10SessionDAOImpl implements ITop10SessionDAO {

    @Override
    public void insert(Top10Session top10Session) {
        String sql = "insert into top10_cg_session_clickcount values(?,?,?,?)";

        Object[] params = new Object[]{top10Session.getTaskid(),
                top10Session.getCategoryid(),
                top10Session.getSessionid(),
                top10Session.getClickCount()};

        JDBCUtil jdbcUtil = JDBCUtil.getInstance();
        jdbcUtil.executeUpdate(sql, params);
    }

}
