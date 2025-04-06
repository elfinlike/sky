package com.sky.mapper;

import com.sky.entity.Setmeal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserSetMealMapper {
    @Select("select * from setmeal where category_id=#{categoryId} and status=1")
    List<Setmeal> getByMeal(Long categoryId);
}
