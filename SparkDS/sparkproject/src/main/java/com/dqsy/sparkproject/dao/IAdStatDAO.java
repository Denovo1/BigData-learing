package com.dqsy.sparkproject.dao;

import com.dqsy.sparkproject.domain.AdStat;

import java.util.List;

/**
 * 广告实时统计DAO接口
 *
 * @author liusinan
 */
public interface IAdStatDAO {

    void updateBatch(List<AdStat> adStats);

}
