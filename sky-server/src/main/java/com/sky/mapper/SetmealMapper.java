package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public interface SetmealMapper {
    @Select("select COUNT(*) from setmeal where category_id=#{id}")
    Integer selByCate(Long id);

    @AutoFill(OperationType.INSERT)
    void addMeal(Setmeal setmeal);

    List<Setmeal> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    @Select("select * from setmeal where id=#{id}")
    Setmeal getById(Long id);

    @AutoFill(OperationType.UPDATE)
    void updateMeal(Setmeal setmeal);

    void deleteBatch(List<Long> ids);

    @Select("select count(*) from setmeal where status=#{status}")
    Integer getByStatus(Integer status);
}
