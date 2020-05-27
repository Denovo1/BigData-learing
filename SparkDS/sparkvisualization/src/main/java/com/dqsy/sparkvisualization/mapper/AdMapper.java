package com.dqsy.sparkvisualization.mapper;

import com.dqsy.sparkvisualization.entity.*;
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
public interface AdMapper {

    @Select("SELECT * from advertisement_province_top3 a " +
            "WHERE (select COUNT(1) from advertisement_province_top3 WHERE province=a.province AND click_count>a.click_count)<3 " +
            "AND date=curdate() " +
            "AND province=#{province} " +
            "ORDER BY province desc,click_count desc " +
            "limit 3")
    @Results({
            @Result(property = "date", column = "date"),
            @Result(property = "province", column = "province"),
            @Result(property = "adid", column = "ad_id"),
            @Result(property = "clickCount", column = "click_count")
    })
    List<AdProvinceTop3> getAdProvinceTop3(String province);

    @Select("SELECT * from advertisement_blacklist where date=curdate()")
    @Results({
            @Result(property = "userid", column = "user_id"),
            @Result(property = "date", column = "date")
    })
    List<AdBlacklist> getAdBlackList();

    @Select("select * from advertisement_real_time_click " +
            "where ad_id=#{adid} " +
            "AND date=curdate() " +
            "order by `hour`,`minute`")
    @Results({
            @Result(property = "date", column = "date"),
            @Result(property = "hour", column = "hour"),
            @Result(property = "minute", column = "minute"),
            @Result(property = "adid", column = "ad_id"),
            @Result(property = "clickCount", column = "click_count")
    })
    List<AdClickTrend> getAdClickTrend(Integer adid);

    /*@Select("select * from advertisement_real_time_click " +
            "WHERE date=curdate() " +
            "order by `hour`,`minute`")*/
    @Select("select date,`hour`,`minute`,ad_id,SUM(click_count) click_count from advertisement_real_time_click " +
            "WHERE date=curdate() " +
            "group by `hour`,`minute`;")
    @Results({
            @Result(property = "date", column = "date"),
            @Result(property = "hour", column = "hour"),
            @Result(property = "minute", column = "minute"),
            @Result(property = "adid", column = "ad_id"),
            @Result(property = "clickCount", column = "click_count")
    })
    List<AdClickTrend> getAllAdClickTrend();
}
