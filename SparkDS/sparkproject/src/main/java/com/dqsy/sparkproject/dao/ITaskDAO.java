package com.dqsy.sparkproject.dao;

import com.dqsy.sparkproject.domain.Task;

/**
 * 任务管理DAO接口
 * @author liusinan
 *
 */
public interface ITaskDAO {

    /**
     * 根据主键查询任务
     * @param taskid 主键
     * @return 任务
     */
    Task findById(long taskid);

}
