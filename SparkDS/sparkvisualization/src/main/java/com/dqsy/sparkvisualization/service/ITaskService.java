package com.dqsy.sparkvisualization.service;

import com.dqsy.sparkvisualization.entity.Task;

import java.io.IOException;
import java.util.List;

/**
 * @author liusinan
 * @version 1.0.0
 * @ClassName TaskService.java
 * @Description TODO
 * @createTime 2020年03月27日 15:35:00
 */
public interface ITaskService {

    List<Task> getAllTaskList();

    void runJobOnce(Integer taskid, Task task);

    void runPageJobOnce(Integer taskid, Task task);

    void runAreaJobOnce(Integer taskid, Task task);

    Task getTask(Integer taskid);

    int deleteTask(Integer taskid);

    void saveOrupdate(Task task) throws Exception;

    void addTask(Task task) throws Exception;

    void updateTask(Task task) throws Exception;

}
