package com.dqsy.sparkproject.dao.impl;

import com.dqsy.sparkproject.dao.ISessionDetailDAO;
import com.dqsy.sparkproject.domain.SessionDetail;
import com.dqsy.sparkproject.util.JDBCUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * session明细DAO实现类
 *
 * @author liusinan
 */
public class SessionDetailDAOImpl implements ISessionDetailDAO {

    /**
     * 插入一条Top10 session明细数据
     *
     * @param sessionDetail
     */
    @Override
    public void insert(SessionDetail sessionDetail) {
        String sql = "insert into top10_cg_session_details values(?,?,?,?,?,?,?,?,?,?,?,?)";

        Object[] params = new Object[]{sessionDetail.getTaskid(),
                sessionDetail.getUserid(),
                sessionDetail.getSessionid(),
                sessionDetail.getPageid(),
                sessionDetail.getActionTime(),
                sessionDetail.getSearchKeyword(),
                sessionDetail.getClickCategoryId(),
                sessionDetail.getClickProductId(),
                sessionDetail.getOrderCategoryIds(),
                sessionDetail.getOrderProductIds(),
                sessionDetail.getPayCategoryIds(),
                sessionDetail.getPayProductIds()};

        JDBCUtil jdbcUtil = JDBCUtil.getInstance();
        jdbcUtil.executeUpdate(sql, params);
    }

    /**
     * 批量插入随机取样的session明细数据
     *
     * @param sessionDetails
     */
    @Override
    public void insertBatch(List<SessionDetail> sessionDetails) {
        String sql = "insert into random_session_detail values(?,?,?,?,?,?,?,?,?,?,?,?)";

        List<Object[]> paramsList = new ArrayList<Object[]>();
        for (SessionDetail sessionDetail : sessionDetails) {
            Object[] params = new Object[]{sessionDetail.getTaskid(),
                    sessionDetail.getUserid(),
                    sessionDetail.getSessionid(),
                    sessionDetail.getPageid(),
                    sessionDetail.getActionTime(),
                    sessionDetail.getSearchKeyword(),
                    sessionDetail.getClickCategoryId(),
                    sessionDetail.getClickProductId(),
                    sessionDetail.getOrderCategoryIds(),
                    sessionDetail.getOrderProductIds(),
                    sessionDetail.getPayCategoryIds(),
                    sessionDetail.getPayProductIds()};
            paramsList.add(params);
        }

        JDBCUtil jdbcUtil = JDBCUtil.getInstance();
        jdbcUtil.executeBatch(sql, paramsList);
    }

}
