package com.dqsy.sparkvisualization.controller;

import com.alibaba.fastjson.JSONObject;
import com.dqsy.sparkvisualization.entity.AreaTop3Product;
import com.dqsy.sparkvisualization.entity.Task;
import com.dqsy.sparkvisualization.entity.Tmp;
import com.dqsy.sparkvisualization.service.IAreaService;
import com.dqsy.sparkvisualization.service.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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
@RequestMapping("/areashow")
@Slf4j
public class AreaController {

    @Autowired
    private IAreaService areaService;

    @Autowired
    private ITaskService taskService;

    @RequestMapping(value = "/show", method = RequestMethod.GET, produces = "application/json")
    public List<Tmp> show(Integer taskid) {
        List<AreaTop3Product> area = areaService.getAreaList(taskid);

        List<Tmp> list = new ArrayList<>();
        for (int i = 0; i < area.size(); i++) {
            String name = area.get(i).getArea();
            if (name.equals("China South")) {
                name = "华南";
            } else if (name.equals("China Middle")) {
                name = "华中";
            } else if (name.equals("China North")) {
                name = "华北";
            } else if (name.equals("West North")) {
                name = "西北";
            } else if (name.equals("China East")) {
                name = "华东";
            } else if (name.equals("East North")) {
                name = "东北";
            } else if (name.equals("West South")) {
                name = "西南";
            }
            String value = String.valueOf(area.get(i).getProductid());
            list.add(new Tmp(name, Integer.valueOf(value)));
        }

        return list;
    }

    @RequestMapping(value = "/showJob", method = {RequestMethod.GET, RequestMethod.POST})
    public Object showJob(Integer taskid) {
        Task task = taskService.getTask(taskid);
        if (areaService.getAreaList(taskid).size() == 0 || task.getTaskParam().contains("targetPageFlow")) {
            log.error("查看的不是area任务" + task.toString());
            return null;
        } else {
            if (task.getTaskStatus().equals("FINISH")) {
                log.info("被查看：" + task.toString());
                return areaService.getAreaList(taskid);
            } else if (task.getTaskStatus().equals("CREATE")) {
                log.error("未执行：" + task.toString());
            } else if ((task.getTaskStatus().equals("KILLED"))) {
                log.error("未成功：" + task.toString());
            }
        }
        return null;
    }

}
