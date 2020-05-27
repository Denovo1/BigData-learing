package com.dqsy.sparkproject.dao.impl;

import com.dqsy.sparkproject.dao.ITaskDAO;
import com.dqsy.sparkproject.domain.Task;
import com.dqsy.sparkproject.util.JDBCUtil;

import java.sql.ResultSet;

/**
 * 任务管理DAO实现类
 *
 * @author liusinan
 */
public class TaskDAOImpl implements ITaskDAO {
    /**
     * 根据主键查询任务
     *
     * @param taskid 主键
     * @return 任务
     */
    @Override
    public Task findById(long taskid) {
        final Task task = new Task();

        String sql = "select * from task where task_id=?";
        Object[] params = new Object[]{taskid};

        JDBCUtil jdbcUtil = JDBCUtil.getInstance();
        jdbcUtil.executeQuery(sql, params, new JDBCUtil.QueryCallback() {

            @Override
            public void process(ResultSet rs) throws Exception {
                if (rs.next()) {
                    long taskid = rs.getLong(1);
                    String taskName = rs.getString(2);
                    String createTime = rs.getString(3);
                    String startTime = rs.getString(4);
                    String finishTime = rs.getString(5);
                    String taskType = rs.getString(6);
                    String taskStatus = rs.getString(7);
                    String taskParam = rs.getString(8);

                    task.setTaskid(taskid);
                    task.setTaskName(taskName);
                    task.setCreateTime(createTime);
                    task.setStartTime(startTime);
                    task.setFinishTime(finishTime);
                    task.setTaskType(taskType);
                    task.setTaskStatus(taskStatus);
                    task.setTaskParam(taskParam);
                }
            }

        });

        return task;
    }

    @Override
    public void update(Task task, String taskStatus) {
        String sql = "update task set task_status=? where task_id=?";
        Object[] params = new Object[]{taskStatus, task.getTaskid()};

        JDBCUtil jdbcUtil = JDBCUtil.getInstance();
        jdbcUtil.executeUpdate(sql, params);
    }

}