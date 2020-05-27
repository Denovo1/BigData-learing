package com.dqsy.sparkvisualization.mapper;

import com.dqsy.sparkvisualization.entity.PageSplitConvertRate;
import com.dqsy.sparkvisualization.entity.SessionAggrStat;
import com.dqsy.sparkvisualization.entity.Top10Category;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liusinan
 * @version 1.0.0
 * @ClassName SessionMapper.java
 * @Description TODO
 * @createTime 2020年03月29日 19:01:00
 */
@Repository
public interface PageMapper {

    @Select("select * from page_flow_jump_rate where taskid=#{taskid}")
    @Results({
            @Result(property = "taskid", column = "taskid"),
            @Result(property = "convertRate", column = "jump_rate")
    })
    PageSplitConvertRate getPage(Integer taskid);

    @Select("select * from page_flow_jump_rate where taskid=#{taskid}")
    @Results({
            @Result(property = "taskid", column = "taskid"),
            @Result(property = "convertRate", column = "jump_rate")
    })
    List<PageSplitConvertRate> getAllPageList(Integer taskid);

}
