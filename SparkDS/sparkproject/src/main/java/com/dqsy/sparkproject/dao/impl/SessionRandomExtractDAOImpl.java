package com.dqsy.sparkproject.dao.impl;

import com.dqsy.sparkproject.dao.ISessionRandomExtractDAO;
import com.dqsy.sparkproject.domain.SessionRandomExtract;
import com.dqsy.sparkproject.jdbc.JDBCHelper;

/**
 * 随机抽取session的DAO实现
 * @author liusinan
 *
 */
public class SessionRandomExtractDAOImpl implements ISessionRandomExtractDAO {
    /**
     * 插入session随机抽取
     * @param sessionRandomExtract
     */
    public void insert(SessionRandomExtract sessionRandomExtract) {
        String sql = "insert into session_random_extract values(?,?,?,?,?)";

        Object[] params = new Object[]{sessionRandomExtract.getTaskid(),
                sessionRandomExtract.getSessionid(),
                sessionRandomExtract.getStartTime(),
                sessionRandomExtract.getSearchKeywords(),
                sessionRandomExtract.getClickCategoryIds()};

        JDBCHelper jdbcHelper = JDBCHelper.getInstance();
        jdbcHelper.executeUpdate(sql, params);
    }
}
