package com.dqsy.sparkvisualization.controller;

import com.alibaba.fastjson.JSONObject;
import com.dqsy.sparkvisualization.entity.SessionAggrStat;
import com.dqsy.sparkvisualization.entity.Task;
import com.dqsy.sparkvisualization.entity.Top10Category;
import com.dqsy.sparkvisualization.mapper.SessionMapper;
import com.dqsy.sparkvisualization.service.ISessionService;
import com.dqsy.sparkvisualization.service.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liusinan
 * @version 1.0.0
 * @ClassName SessionController.java
 * @Description TODO
 * @createTime 2020年03月29日 19:07:00
 */
@RestController
@RequestMapping("/sessionshow")
@Slf4j
public class SessionController {

    @Autowired
    private ISessionService sessionService;

    @Autowired
    private ITaskService taskService;

    @RequestMapping(value = "/show", method = RequestMethod.GET, produces = "application/json")
    public List<SessionAggrStat> show(Integer taskid) {
        List<SessionAggrStat> sessionList = sessionService.getAllSessionAggrStatList(taskid);
        return sessionList;
    }

    @RequestMapping(value = "/topshow", method = RequestMethod.GET, produces = "application/json")
    public List<Top10Category> topshow(Integer taskid) {
        List<Top10Category> top10CategoryList = sessionService.getAllTop10CategoryList(taskid);
        return top10CategoryList;
    }

    @RequestMapping(value = "/showJob", method = {RequestMethod.GET, RequestMethod.POST})
    public Object showJob(Integer taskid) {
        Task task = taskService.getTask(taskid);
        if (sessionService.getAllSessionAggrStat(taskid) == null || task.getTaskParam().contains("targetPageFlow")) {
            log.error("查看的不是session任务" + task.toString());
            return null;
        } else {
            if (task.getTaskStatus().equals("FINISH")) {
                log.info("被查看：" + task.toString());
                return sessionService.getAllSessionAggrStatList(taskid);
            } else if (task.getTaskStatus().equals("CREATE")) {
                log.error("未执行：" + task.toString());
            } else if ((task.getTaskStatus().equals("KILLED"))) {
                log.error("未成功：" + task.toString());
            }
        }
        return null;
    }

}
