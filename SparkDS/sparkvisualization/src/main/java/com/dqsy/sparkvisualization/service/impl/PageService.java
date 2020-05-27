package com.dqsy.sparkvisualization.service.impl;

import com.dqsy.sparkvisualization.entity.PageSplitConvertRate;
import com.dqsy.sparkvisualization.entity.SessionAggrStat;
import com.dqsy.sparkvisualization.entity.Top10Category;
import com.dqsy.sparkvisualization.mapper.PageMapper;
import com.dqsy.sparkvisualization.mapper.SessionMapper;
import com.dqsy.sparkvisualization.service.IPageService;
import com.dqsy.sparkvisualization.service.ISessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liusinan
 * @version 1.0.0
 * @ClassName TaskService.java
 * @Description TODO
 * @createTime 2020年03月27日 15:35:00
 */
@Service
@Slf4j
public class PageService implements IPageService {

    @Autowired
    private PageMapper pageMapper;

    @Override
    public PageSplitConvertRate getPage(Integer taskid) {
        return pageMapper.getPage(taskid);
    }

    @Override
    public List<PageSplitConvertRate> getAllPageList(Integer taskid) {
        return pageMapper.getAllPageList(taskid);
    }
}
