package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {
    void updatebyCate(Dish dish);

    //根据cate的id值判断删除时该分类下是否有菜品
    @Select("select count(*) from dish where category_id=#{id}")
    Integer selByCate(Long id);


    List<Dish> PageQuery(DishPageQueryDTO dishPageQueryDTO);

    @Select("select * from dish where category_id=#{categoryId}")
    List<Dish> getByCate(Long categoryId);

    @Select("select * from dish where id=#{id} ")
    DishVO getById(Long id);


    @AutoFill(OperationType.UPDATE)
    void updateDish(Dish dish);

    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    void deleteBatch(List<Long> ids);

    @Select("select count(*) from dish where status=#{status}")
    Integer getByStatus(Integer status);
}
