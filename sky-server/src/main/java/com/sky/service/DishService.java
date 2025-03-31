package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    void updateDishbyCate(Dish dish);

    Integer selByCate(Long id);

    PageResult DishQuery(DishPageQueryDTO dishPageQueryDTO);

    List<Dish> getByCate(Long categoryId);

    DishVO getById(Long id);

    void updateDish(Integer status, Long id);

    void saveWithFlavor(DishDTO dishDTO);

    void updateWithFlavor(DishDTO dishDTO);

    void deleteBatch(List<Long> ids);
}
