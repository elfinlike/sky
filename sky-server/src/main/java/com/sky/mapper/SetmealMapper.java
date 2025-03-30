package com.sky.mapper;

import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {
    @Select("select COUNT(*) from setmeal where category_id=#{id}")
    Integer selByCate(Long id);
}
