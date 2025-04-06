package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserSetMealDishMapper {

    @Select("select * from setmeal_dish where setmeal_id=#{id} ")
    List<SetmealDish> getIdBatch(Long id);
}
