package com.sky.mapper;


import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface UserMapper {

    @Select("select * from user where openid=#{openid}")
    User getByOpenId(String openid);

    User insertByOpenId(User user);

    @Select("select count(*) from user")
    long getTotalUser();

    @Select("select count(*) from user where create_time between #{min} and #{max}")
    long getNewUser(LocalDateTime min, LocalDateTime max);
}
