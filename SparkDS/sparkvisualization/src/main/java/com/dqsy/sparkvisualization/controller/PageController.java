package com.dqsy.sparkvisualization.controller;

import com.alibaba.fastjson.JSONObject;
import com.dqsy.sparkvisualization.entity.*;
import com.dqsy.sparkvisualization.service.IPageService;
import com.dqsy.sparkvisualization.service.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author liusinan
 * @version 1.0.0
 * @ClassName SessionController.java
 * @Description TODO
 * @createTime 2020年03月29日 19:07:00
 */
@RestController
@RequestMapping("/pageshow")
@Slf4j
public class PageController {

    @Autowired
    private IPageService pageService;

    @Autowired
    private ITaskService taskService;

    @RequestMapping(value = "/show", method = RequestMethod.GET, produces = "application/json")
    public List<Tmp> show(Integer taskid) {
        PageSplitConvertRate page = pageService.getPage(taskid);
        String pageresult = page.getConvertRate();
        String[] res = pageresult.split("\\|");
        Map<String, Double> map = new HashMap<>();
        for (int i = 0; i < res.length; i++) {
            String[] tmp = res[i].split("=");
            map.put(tmp[0], Double.valueOf(tmp[1]));
        }

        Task task = taskService.getTask(taskid);
        List<Tmp> list = new ArrayList<>();
        list.add(new Tmp("taskid", task.getTaskid()));
        JSONObject taskParam = JSONObject.parseObject(task.getTaskParam());
        String str = taskParam.getString("targetPageFlow");
        str = str.substring(2, str.length() - 2);
        System.out.println(str);
        String[] strS = str.split(",");
        for (int i = 0; i < strS.length - 1; i++) {
            String name = strS[i] + "_" + strS[i + 1];
            list.add(new Tmp(name, map.get(name)));
        }
        return list;
    }

    @RequestMapping(value = "/showJob", method = {RequestMethod.GET, RequestMethod.POST})
    public Object showJob(Integer taskid) {
        Task task = taskService.getTask(taskid);
        if (pageService.getPage(taskid) == null || !task.getTaskParam().contains("targetPageFlow")) {
            log.error("查看的不是page任务" + task.toString());
            return null;
        } else {
            if (task.getTaskStatus().equals("FINISH")) {
                log.info("被查看：" + task.toString());
                return pageService.getAllPageList(taskid);
            } else if (task.getTaskStatus().equals("CREATE")) {
                log.error("未执行：" + task.toString());
            } else if ((task.getTaskStatus().equals("KILLED"))) {
                log.error("未成功：" + task.toString());
            }
        }
        return null;
    }

}
