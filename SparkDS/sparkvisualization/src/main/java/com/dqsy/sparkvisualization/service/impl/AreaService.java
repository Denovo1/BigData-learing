package com.dqsy.sparkvisualization.service.impl;

import com.dqsy.sparkvisualization.entity.AreaTop3Product;
import com.dqsy.sparkvisualization.mapper.AreaMapper;
import com.dqsy.sparkvisualization.service.IAreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liusinan
 * @version 1.0.0
 * @ClassName TaskService.java
 * @Description TODO
 * @createTime 2020年03月27日 15:35:00
 */
@Service
@Slf4j
public class AreaService implements IAreaService {

    @Autowired
    private AreaMapper areaMapper;

    @Override
    public AreaTop3Product getArea(Integer taskid) {
        return areaMapper.getArea(taskid);
    }

    @Override
    public List<AreaTop3Product> getAllAreaList(Integer taskid) {
        return areaMapper.getAllAreaList(taskid);
    }

    @Override
    public List<AreaTop3Product> getAreaList(Integer taskid) {
        return areaMapper.getAreaList(taskid);
    }
}
