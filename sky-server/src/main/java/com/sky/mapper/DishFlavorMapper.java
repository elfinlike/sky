package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    void insertBatch(List<DishFlavor> flavors);

    @Select("select * from dish_flavor where dish_id=#{id}")
    List<DishFlavor> getByDish(Long id);

    @Delete("delete from dish_flavor where dish_id=#{id}")
    void delete(Long id);

    void deleteBatch(List<Long> ids);
}
