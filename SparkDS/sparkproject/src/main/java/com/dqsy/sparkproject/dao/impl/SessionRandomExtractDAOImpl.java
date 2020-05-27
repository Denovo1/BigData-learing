package com.dqsy.sparkproject.dao.impl;

import com.dqsy.sparkproject.dao.ISessionRandomExtractDAO;
import com.dqsy.sparkproject.domain.SessionRandomExtract;
import com.dqsy.sparkproject.util.JDBCUtil;

/**
 * 随机抽取session的DAO实现
 *
 * @author liusinan
 */
public class SessionRandomExtractDAOImpl implements ISessionRandomExtractDAO {
    /**
     * 插入session随机抽取
     *
     * @param sessionRandomExtract
     */
    @Override
    public void insert(SessionRandomExtract sessionRandomExtract) {
        String sql = "insert into random_session_behavior_line values(?,?,?,?,?)";

        Object[] params = new Object[]{sessionRandomExtract.getTaskid(),
                sessionRandomExtract.getSessionid(),
                sessionRandomExtract.getStartTime(),
                sessionRandomExtract.getSearchKeywords(),
                sessionRandomExtract.getClickCategoryIds()};

        JDBCUtil jdbcUtil = JDBCUtil.getInstance();
        jdbcUtil.executeUpdate(sql, params);
    }
}
