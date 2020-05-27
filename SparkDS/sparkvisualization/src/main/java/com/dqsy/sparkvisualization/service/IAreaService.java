package com.dqsy.sparkvisualization.service;

import com.dqsy.sparkvisualization.entity.AreaTop3Product;

import java.util.List;

/**
 * @author liusinan
 * @version 1.0.0
 * @ClassName IPageService.java
 * @Description TODO
 * @createTime 2020年04月01日 18:07:00
 */
public interface IAreaService {

    AreaTop3Product getArea(Integer taskid);

    List<AreaTop3Product> getAllAreaList(Integer taskid);

    List<AreaTop3Product> getAreaList(Integer taskid);

}
