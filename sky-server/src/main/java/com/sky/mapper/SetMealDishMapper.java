package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface SetMealDishMapper {


    void addMealDish(List<SetmealDish> setmealDishes);

    @Delete("delete from setmeal_dish where setmeal_id=#{id}")
    void deleteAll(Long id);

    void deleteBatch(List<Long> ids);
}
