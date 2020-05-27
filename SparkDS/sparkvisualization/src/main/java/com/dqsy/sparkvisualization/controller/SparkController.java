package com.dqsy.sparkvisualization.controller;

import com.dqsy.sparkvisualization.entity.Task;
import com.dqsy.sparkvisualization.mapper.TaskMapper;
import com.dqsy.sparkvisualization.service.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author liusinan
 * @version 1.0.0
 * @ClassName SparkController.java
 * @Description TODO
 * @createTime 2020年03月27日 19:43:00
 */
@RestController
@RequestMapping("/sparkapi")
@Slf4j
@ResponseBody
public class SparkController {
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private ITaskService taskService;

    /*@GetMapping(value = "/session/{taskid}")
    public Task find(@PathVariable Integer taskid) {
        Task task = taskMapper.getTask(taskid);
        taskService.runJobOnce(taskid);
        return task;
    }*/

    @RequestMapping(value = "/runJob", method = {RequestMethod.GET, RequestMethod.POST})
    public Object runJob(Integer taskid) {
        Task task = taskMapper.getTask(taskid);
        String status = task.getTaskStatus();
        String param = task.getTaskParam();
        if (param.contains("targetPageFlow")) {
            log.error("请执行page任务 " + task.toString());
            return null;
        } else {
            if (!param.contains("startDate")) {
                log.error("缺少startDate参数 " + task.toString());
                return null;
            } else if (!param.contains("endDate")) {
                log.error("缺少startDate参数 " + task.toString());
                return null;
            } else {
                if (task.getTaskStatus().equals("FINISH")) {
                    log.info("已执行" + task.toString());
                    return null;
                } else {
                    SimpleDateFormat startsdf = new SimpleDateFormat();
                    startsdf.applyPattern("yyyy-MM-dd HH:mm:ss");
                    Date startdate = new Date();
                    task.setStartTime(startsdf.format(startdate));
                    task.setFinishTime(null);
                    taskMapper.updateTime(task);

                    task.setTaskStatus("RUNNING");
                    taskMapper.updateStatus(task);

                    log.info("任务运行" + "[taskid=" + task.getTaskid()
                            + ", taskName=" + "'" + task.getTaskName() + "'"
                            + ", startTime=" + "'" + task.getStartTime() + "'"
                            + ", finishTime=" + "'" + task.getFinishTime() + "'"
                            + ", taskStatus=" + "'" + task.getTaskStatus() + "'" + "]");
                    try {
                        taskService.runJobOnce(taskid, task);
                    } catch (Exception e) {
                        task.setTaskStatus("KILLED");
                        taskMapper.updateStatus(task);
                        e.printStackTrace();
                    }
                    SimpleDateFormat finishsdf = new SimpleDateFormat();
                    finishsdf.applyPattern("yyyy-MM-dd HH:mm:ss");
                    Date finishdate = new Date();
                    task.setFinishTime(finishsdf.format(finishdate));
                    taskMapper.updateTime(task);
                }
            }
        }

        return task;
    }

    @RequestMapping(value = "/runPageJob", method = {RequestMethod.GET, RequestMethod.POST})
    public Object runPageJob(Integer taskid) {
        Task task = taskMapper.getTask(taskid);
        String param = task.getTaskParam();
        if (!param.contains("startDate")) {
            log.error("缺少startDate参数 " + task.toString());
            return null;
        } else if (!param.contains("endDate")) {
            log.error("缺少startDate参数 " + task.toString());
            return null;
        } else if (!param.contains("targetPageFlow")) {
            log.error("缺少targetPageFlow必要参数，或是Session任务 " + task.toString());
            return null;
        } else {
            if (task.getTaskStatus().equals("FINISH")) {
                log.info("已执行" + task.toString());
                return null;
            } else {
                SimpleDateFormat startsdf = new SimpleDateFormat();
                startsdf.applyPattern("yyyy-MM-dd HH:mm:ss");
                Date startdate = new Date();
                task.setStartTime(startsdf.format(startdate));
                task.setFinishTime(null);
                taskMapper.updateTime(task);

                task.setTaskStatus("RUNNING");
                taskMapper.updateStatus(task);

                log.info("任务运行" + "[taskid=" + task.getTaskid()
                        + ", taskName=" + "'" + task.getTaskName() + "'"
                        + ", startTime=" + "'" + task.getStartTime() + "'"
                        + ", finishTime=" + "'" + task.getFinishTime() + "'"
                        + ", taskStatus=" + "'" + task.getTaskStatus() + "'" + "]");
                try {
                    taskService.runPageJobOnce(taskid, task);
                } catch (Exception e) {
                    task.setTaskStatus("KILLED");
                    taskMapper.updateStatus(task);
                    e.printStackTrace();
                }
                SimpleDateFormat finishsdf = new SimpleDateFormat();
                finishsdf.applyPattern("yyyy-MM-dd HH:mm:ss");
                Date finishdate = new Date();
                task.setFinishTime(finishsdf.format(finishdate));
                taskMapper.updateTime(task);
            }
        }
        return task;
    }

    @RequestMapping(value = "/runAreaJob", method = {RequestMethod.GET, RequestMethod.POST})
    public Object runAreaJob(Integer taskid) {
        Task task = taskMapper.getTask(taskid);
        String param = task.getTaskParam();
        if (param.contains("targetPageFlow")) {
            log.error("请执行page任务 " + task.toString());
            return null;
        } else {
            if (!param.contains("startDate")) {
                log.error("缺少startDate参数 " + task.toString());
                return null;
            } else if (!param.contains("endDate")) {
                log.error("缺少startDate参数 " + task.toString());
                return null;
            } else {
                if (task.getTaskStatus().equals("FINISH")) {
                    log.info("已执行" + task.toString());
                    return null;
                } else {
                    SimpleDateFormat startsdf = new SimpleDateFormat();
                    startsdf.applyPattern("yyyy-MM-dd HH:mm:ss");
                    Date startdate = new Date();
                    task.setStartTime(startsdf.format(startdate));
                    task.setFinishTime(null);
                    taskMapper.updateTime(task);

                    task.setTaskStatus("RUNNING");
                    taskMapper.updateStatus(task);

                    log.info("任务运行" + "[taskid=" + task.getTaskid()
                            + ", taskName=" + "'" + task.getTaskName() + "'"
                            + ", startTime=" + "'" + task.getStartTime() + "'"
                            + ", finishTime=" + "'" + task.getFinishTime() + "'"
                            + ", taskStatus=" + "'" + task.getTaskStatus() + "'" + "]");
                    try {
                        taskService.runAreaJobOnce(taskid, task);
                    } catch (Exception e) {
                        task.setTaskStatus("KILLED");
                        taskMapper.updateStatus(task);
                        e.printStackTrace();
                    }
                    SimpleDateFormat finishsdf = new SimpleDateFormat();
                    finishsdf.applyPattern("yyyy-MM-dd HH:mm:ss");
                    Date finishdate = new Date();
                    task.setFinishTime(finishsdf.format(finishdate));
                    taskMapper.updateTime(task);
                }
            }
        }

        return task;
    }

    @RequestMapping(value = "/deleteJob", method = {RequestMethod.GET, RequestMethod.POST})
    public Object deleteJob(Integer taskid) {
        if (taskService.getTask(taskid).getTaskStatus().equals("RUNNING")){
            log.error("任务正在运行请勿删除！");
            return null;
        } else {
            log.info("删除taskid为" + taskid + "的任务");
            return taskService.deleteTask(taskid);
        }
    }

    @RequestMapping(value = "/saveOrUpdate", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String saveOrUpdate(Task task) throws Exception {
        log.info("params, job = {}", task);
        taskService.saveOrupdate(task);
        return "index";
    }

}
