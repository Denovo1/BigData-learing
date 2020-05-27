package com.dqsy.sparkvisualization.mapper;

import com.dqsy.sparkvisualization.entity.SessionAggrStat;
import com.dqsy.sparkvisualization.entity.Task;
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
public interface SessionMapper {

    @Select("select * from session_browse_target where task_id=#{taskid}")
    @Results({
            @Result(property = "taskid", column = "task_id"),
            @Result(property = "session_count", column = "session_count"),
            @Result(property = "visit_length_1s_3s_ratio", column = "1s_3s"),
            @Result(property = "visit_length_4s_6s_ratio", column = "4s_6s"),
            @Result(property = "visit_length_7s_9s_ratio", column = "7s_9s"),
            @Result(property = "visit_length_10s_30s_ratio", column = "10s_30s"),
            @Result(property = "visit_length_30s_60s_ratio", column = "30s_60s"),
            @Result(property = "visit_length_1m_3m_ratio", column = "1m_3m"),
            @Result(property = "visit_length_3m_10m_ratio", column = "3m_10m"),
            @Result(property = "visit_length_10m_30m_ratio", column = "10m_30m"),
            @Result(property = "visit_length_30m_ratio", column = "30m"),
            @Result(property = "step_length_1_3_ratio", column = "1_3"),
            @Result(property = "step_length_4_6_ratio", column = "4_6"),
            @Result(property = "step_length_7_9_ratio", column = "7_9"),
            @Result(property = "step_length_10_30_ratio", column = "10_30"),
            @Result(property = "step_length_30_60_ratio", column = "30_60"),
            @Result(property = "step_length_60_ratio", column = "60")
    })
    SessionAggrStat getAllSessionAggrStat(Integer taskid);

    @Select("select * from session_browse_target where task_id=#{taskid}")
    @Results({
            @Result(property = "taskid", column = "task_id"),
            @Result(property = "session_count", column = "session_count"),
            @Result(property = "visit_length_1s_3s_ratio", column = "1s_3s"),
            @Result(property = "visit_length_4s_6s_ratio", column = "4s_6s"),
            @Result(property = "visit_length_7s_9s_ratio", column = "7s_9s"),
            @Result(property = "visit_length_10s_30s_ratio", column = "10s_30s"),
            @Result(property = "visit_length_30s_60s_ratio", column = "30s_60s"),
            @Result(property = "visit_length_1m_3m_ratio", column = "1m_3m"),
            @Result(property = "visit_length_3m_10m_ratio", column = "3m_10m"),
            @Result(property = "visit_length_10m_30m_ratio", column = "10m_30m"),
            @Result(property = "visit_length_30m_ratio", column = "30m"),
            @Result(property = "step_length_1_3_ratio", column = "1_3"),
            @Result(property = "step_length_4_6_ratio", column = "4_6"),
            @Result(property = "step_length_7_9_ratio", column = "7_9"),
            @Result(property = "step_length_10_30_ratio", column = "10_30"),
            @Result(property = "step_length_30_60_ratio", column = "30_60"),
            @Result(property = "step_length_60_ratio", column = "60")
    })
    List<SessionAggrStat> getAllSessionAggrStatList(Integer taskid);

    @Select("select * from top10_cg_behavior_count where task_id=#{taskid}")
    @Results({
            @Result(property = "taskid", column = "task_id"),
            @Result(property = "categoryid", column = "cg_id"),
            @Result(property = "clickCount", column = "click_count"),
            @Result(property = "orderCount", column = "order_count"),
            @Result(property = "payCount", column = "pay_count")
    })
    List<Top10Category> getAllTop10CategoryList(Integer taskid);
}
