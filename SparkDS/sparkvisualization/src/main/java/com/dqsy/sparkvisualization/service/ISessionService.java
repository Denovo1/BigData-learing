package com.dqsy.sparkvisualization.service;

import com.dqsy.sparkvisualization.entity.SessionAggrStat;
import com.dqsy.sparkvisualization.entity.Task;
import com.dqsy.sparkvisualization.entity.Top10Category;

import java.util.List;
import java.util.Map;

/**
 * @author liusinan
 * @version 1.0.0
 * @ClassName TaskService.java
 * @Description TODO
 * @createTime 2020年03月27日 15:35:00
 */
public interface ISessionService {

    SessionAggrStat getAllSessionAggrStat(Integer taskid);

    List<SessionAggrStat> getAllSessionAggrStatList(Integer taskid);

    String getstr(SessionAggrStat session);

    List<Top10Category> getAllTop10CategoryList(Integer taskid);

}
