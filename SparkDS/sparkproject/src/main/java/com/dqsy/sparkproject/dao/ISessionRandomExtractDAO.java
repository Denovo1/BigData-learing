package com.dqsy.sparkproject.dao;

import com.dqsy.sparkproject.domain.SessionRandomExtract;

/**
 * session随机抽取模块DAO接口
 *
 * @author liusinan
 */
public interface ISessionRandomExtractDAO {

    /**
     * 插入session随机抽取
     *
     * @param sessionRandomExtract
     */
    void insert(SessionRandomExtract sessionRandomExtract);

}