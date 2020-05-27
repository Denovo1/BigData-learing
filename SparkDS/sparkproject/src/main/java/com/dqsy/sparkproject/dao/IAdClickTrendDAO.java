package com.dqsy.sparkproject.dao;

import com.dqsy.sparkproject.domain.AdClickTrend;

import java.util.List;

/**
 * 广告点击趋势DAO接口
 *
 * @author liusinan
 */
public interface IAdClickTrendDAO {

    void updateBatch(List<AdClickTrend> adClickTrends);

}
