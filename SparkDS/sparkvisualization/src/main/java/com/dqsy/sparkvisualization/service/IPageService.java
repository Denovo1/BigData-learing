package com.dqsy.sparkvisualization.service;

import com.dqsy.sparkvisualization.entity.PageSplitConvertRate;

import java.util.List;

/**
 * @author liusinan
 * @version 1.0.0
 * @ClassName IPageService.java
 * @Description TODO
 * @createTime 2020年04月01日 18:07:00
 */
public interface IPageService {

    PageSplitConvertRate getPage(Integer taskid);

    List<PageSplitConvertRate> getAllPageList(Integer taskid);

}
