package com.dqsy.sparkproject.dao.impl;

import com.dqsy.sparkproject.dao.IPageSplitConvertRateDAO;
import com.dqsy.sparkproject.domain.PageSplitConvertRate;
import com.dqsy.sparkproject.util.JDBCUtil;

/**
 * 页面切片转化率DAO实现类
 *
 * @author liusinan
 */
public class PageSplitConvertRateDAOImpl implements IPageSplitConvertRateDAO {

    @Override
    public void insert(PageSplitConvertRate pageSplitConvertRate) {
        String sql = "insert into page_flow_jump_rate values(?,?)";
        Object[] params = new Object[]{pageSplitConvertRate.getTaskid(),
                pageSplitConvertRate.getConvertRate()};

        JDBCUtil jdbcUtil = JDBCUtil.getInstance();
        jdbcUtil.executeUpdate(sql, params);
    }

}
