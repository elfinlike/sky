package com.sky.mapper;

import com.sky.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {
    void updatebyCate(Dish dish);

    //根据cate的id值判断删除时该分类下是否有菜品
    @Select("select count(*) from dish where category_id=#{id}")
    Integer selByCate(Long id);
}
