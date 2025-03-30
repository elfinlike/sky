package com.sky.service;

import com.sky.entity.Dish;

public interface DishService {
    void updateDishbyCate(Dish dish);

    Integer selByCate(Long id);
}
