package com.dqsy.sparkvisualization.mapper;

import com.dqsy.sparkvisualization.entity.Task;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liusinan
 * @version 1.0.0
 * @ClassName TaskMapper.java
 * @Description TODO
 * @createTime 2020年03月27日 14:39:00
 */
@Repository
public interface TaskMapper {

    @Select("select * from task")
    @Results({
            @Result(property = "taskid", column = "task_id"),
            @Result(property = "taskName", column = "task_name"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "startTime", column = "start_time"),
            @Result(property = "finishTime", column = "finish_time"),
            @Result(property = "taskType", column = "task_type"),
            @Result(property = "taskStatus", column = "task_status"),
            @Result(property = "taskParam", column = "task_param")
    })
    List<Task> getAllTaskList();

    void runJobOnce(Integer taskid);

    @Select("select * from task where task_id=#{taskid}")
    @Results({
            @Result(property = "taskid", column = "task_id"),
            @Result(property = "taskName", column = "task_name"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "startTime", column = "start_time"),
            @Result(property = "finishTime", column = "finish_time"),
            @Result(property = "taskType", column = "task_type"),
            @Result(property = "taskStatus", column = "task_status"),
            @Result(property = "taskParam", column = "task_param")
    })
    Task getTask(Integer taskid);

    @Select("select * from task where task_name=#{taskName}")
    @Results({
            @Result(property = "taskid", column = "task_id"),
            @Result(property = "taskName", column = "task_name"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "startTime", column = "start_time"),
            @Result(property = "finishTime", column = "finish_time"),
            @Result(property = "taskType", column = "task_type"),
            @Result(property = "taskStatus", column = "task_status"),
            @Result(property = "taskParam", column = "task_param")
    })
    Task getTaskByName(String taskName);

    @Select("select * from task where task_id!=#{taskid} and task_name=#{taskName}")
    @Results({
            @Result(property = "taskid", column = "task_id"),
            @Result(property = "taskName", column = "task_name"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "startTime", column = "start_time"),
            @Result(property = "finishTime", column = "finish_time"),
            @Result(property = "taskType", column = "task_type"),
            @Result(property = "taskStatus", column = "task_status"),
            @Result(property = "taskParam", column = "task_param")
    })
    Task getTaskByNameAndId(Integer taskid, String taskName);

    @Delete("delete from task where task_id = #{taskid}")
    int deleteTask(Integer taskid);

    @Insert("insert into task(task_name,create_time,start_time,finish_time,task_type,task_status,task_param) " +
            "values(#{taskName},#{createTime},#{startTime},#{finishTime},#{taskType},'CREATE',#{taskParam})")
    int add(Task task);

    @Update("update task set task_name=#{taskName},task_type=#{taskType},task_param=#{taskParam} where task_id=#{taskid}")
    int update(Task task);

    @Update("update task set start_time=#{startTime},finish_time=#{finishTime} where task_id=#{taskid}")
    void updateTime(Task task);

    @Update("update task set task_status=#{taskStatus} where task_id=#{taskid}")
    void updateStatus(Task task);

}
