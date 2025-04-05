package com.sky.mapper;

import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserDishFlavorMapper {
    @Select("select * from dish_flavor where dish_id=#{id};")
    List<DishFlavor> getByDish(DishVO dishVO);
}
