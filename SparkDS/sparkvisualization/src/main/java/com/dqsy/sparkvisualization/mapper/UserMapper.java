package com.dqsy.sparkvisualization.mapper;

import com.dqsy.sparkvisualization.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {

    int insert(User record);

    //根据username查询用户
    User findByUserName(String username);

}