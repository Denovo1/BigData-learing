package com.dqsy.sparkvisualization.mapper;

import com.dqsy.sparkvisualization.entity.AreaTop3Product;
import com.dqsy.sparkvisualization.entity.PageSplitConvertRate;
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
public interface AreaMapper {

    @Select("select * from area_top3_product where task_id=#{taskid}")
    @Results({
            @Result(property = "taskid", column = "task_id"),
            @Result(property = "area", column = "area"),
            @Result(property = "areaLevel", column = "area_level"),
            @Result(property = "productid", column = "pd_id"),
            @Result(property = "cityInfos", column = "city_names"),
            @Result(property = "clickCount", column = "click_count"),
            @Result(property = "productName", column = "pd_name"),
            @Result(property = "productStatus", column = "seller_type")
    })
    AreaTop3Product getArea(Integer taskid);

    @Select("select area,product_id from area_top3_product where task_id=#{taskid}")
    @Results({
            @Result(property = "taskid", column = "task_id"),
            @Result(property = "area", column = "area"),
            @Result(property = "areaLevel", column = "area_level"),
            @Result(property = "productid", column = "pd_id"),
            @Result(property = "cityInfos", column = "city_names"),
            @Result(property = "clickCount", column = "click_count"),
            @Result(property = "productName", column = "pd_name"),
            @Result(property = "productStatus", column = "seller_type")
    })
    List<AreaTop3Product> getAllAreaList(Integer taskid);

    @Select("select * from area_top3_product where task_id=#{taskid}")
    @Results({
            @Result(property = "taskid", column = "task_id"),
            @Result(property = "area", column = "area"),
            @Result(property = "areaLevel", column = "area_level"),
            @Result(property = "productid", column = "pd_id"),
            @Result(property = "cityInfos", column = "city_names"),
            @Result(property = "clickCount", column = "click_count"),
            @Result(property = "productName", column = "pd_name"),
            @Result(property = "productStatus", column = "seller_type")
    })
    List<AreaTop3Product> getAreaList(Integer taskid);

}
