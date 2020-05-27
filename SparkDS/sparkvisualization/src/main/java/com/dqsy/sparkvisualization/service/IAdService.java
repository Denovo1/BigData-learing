package com.dqsy.sparkvisualization.service;

import com.dqsy.sparkvisualization.entity.*;

import java.util.List;

/**
 * @author liusinan
 * @version 1.0.0
 * @ClassName TaskService.java
 * @Description TODO
 * @createTime 2020年03月27日 15:35:00
 */
public interface IAdService {

    List<AdProvinceTop3> getAdProvinceTop3(String province);

    List<AdBlacklist> getAdBlackList();

    List<AdClickTrend> getAdClickTrend(Integer adid);

    List<AdClickTrend> getAllAdClickTrend();


}
