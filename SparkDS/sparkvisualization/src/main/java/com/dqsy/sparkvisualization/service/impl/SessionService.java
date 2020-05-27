package com.dqsy.sparkvisualization.service.impl;

import com.dqsy.sparkvisualization.entity.SessionAggrStat;
import com.dqsy.sparkvisualization.entity.Task;
import com.dqsy.sparkvisualization.entity.Top10Category;
import com.dqsy.sparkvisualization.mapper.SessionMapper;
import com.dqsy.sparkvisualization.mapper.TaskMapper;
import com.dqsy.sparkvisualization.service.ISessionService;
import com.dqsy.sparkvisualization.service.ITaskService;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author liusinan
 * @version 1.0.0
 * @ClassName TaskService.java
 * @Description TODO
 * @createTime 2020年03月27日 15:35:00
 */
@Service
@Slf4j
public class SessionService implements ISessionService {

    @Autowired
    private SessionMapper sessionMapper;

    @Override
    public SessionAggrStat getAllSessionAggrStat(Integer taskid) {
        return sessionMapper.getAllSessionAggrStat(taskid);
    }

    @Override
    public List<SessionAggrStat> getAllSessionAggrStatList(Integer taskid) {
        return sessionMapper.getAllSessionAggrStatList(taskid);
    }

    @Override
    public String getstr(SessionAggrStat session) {
        Map<String, Object> map = new HashMap<String, Object>();
        double[] visitLength = new double[9];
        double[] visitStep = new double[6];
        long[] taskid = new long[1];
        long[] sessionCount = new long[1];
        taskid[0] = session.getTaskid();
        sessionCount[0] = session.getSession_count();
        visitLength[0] = session.getVisit_length_1s_3s_ratio();
        visitLength[1] = session.getVisit_length_4s_6s_ratio();
        visitLength[2] = session.getVisit_length_7s_9s_ratio();
        visitLength[3] = session.getVisit_length_10s_30s_ratio();
        visitLength[4] = session.getVisit_length_30s_60s_ratio();
        visitLength[5] = session.getVisit_length_1m_3m_ratio();
        visitLength[6] = session.getVisit_length_3m_10m_ratio();
        visitLength[7] = session.getVisit_length_10m_30m_ratio();
        visitLength[8] = session.getVisit_length_30m_ratio();

        visitStep[0] = session.getStep_length_1_3_ratio();
        visitStep[1] = session.getStep_length_4_6_ratio();
        visitStep[2] = session.getStep_length_7_9_ratio();
        visitStep[3] = session.getStep_length_10_30_ratio();
        visitStep[4] = session.getStep_length_30_60_ratio();
        visitStep[5] = session.getStep_length_60_ratio();

        map.put("taskid", taskid[0]);
        map.put("sessionCount", sessionCount[0]);
        map.put("visitLength", visitLength);
        map.put("visitStep", visitStep);

        long sessionCount1 = (long) map.get("sessionCount");
        double[] stepArrays = (double[]) map.get("visitStep");
        String[] stepname = {"1-3", "4-6", "7-9", "10-30", "30-60", "60"};
        int eachStep = 0;
        String str = "";
        for (int j = 0; j < stepArrays.length; j++) {
            eachStep = (int) (sessionCount1 * stepArrays[j]);
            str += "{value:" + eachStep + ", " + "name:\'" + stepname[j] + "\'},";
        }
        str = str.substring(0, str.length() - 1);
        log.info(str);
        return str;
    }

    @Override
    public List<Top10Category> getAllTop10CategoryList(Integer taskid) {
        return sessionMapper.getAllTop10CategoryList(taskid);
    }

}
