package com.dqsy.sparkvisualization.service.impl;

import com.dqsy.sparkvisualization.entity.*;
import com.dqsy.sparkvisualization.mapper.AdMapper;
import com.dqsy.sparkvisualization.mapper.SessionMapper;
import com.dqsy.sparkvisualization.service.IAdService;
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
public class AdService implements IAdService {

    @Autowired
    private AdMapper adMapper;

    @Override
    public List<AdProvinceTop3> getAdProvinceTop3(String province) {
        return adMapper.getAdProvinceTop3(province);
    }

    @Override
    public List<AdBlacklist> getAdBlackList() {
        return adMapper.getAdBlackList();
    }

    @Override
    public List<AdClickTrend> getAdClickTrend(Integer adid) {
        return adMapper.getAdClickTrend(adid);
    }

    @Override
    public List<AdClickTrend> getAllAdClickTrend() {
        return adMapper.getAllAdClickTrend();
    }

}
