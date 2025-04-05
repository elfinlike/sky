package com.sky.mapper;

import com.sky.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserDishMapper {

    @Select("select * from dish where category_id=#{categoryId}")
    List<Dish> getByCate(Long categoryId);
}
