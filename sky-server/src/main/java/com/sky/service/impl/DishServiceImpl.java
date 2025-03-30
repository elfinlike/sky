package com.sky.service.impl;

import com.sky.entity.Dish;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImpl  implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Override
    public void updateDishbyCate(Dish dish) {
        dishMapper.updatebyCate(dish);
    }

    @Override
    public Integer selByCate(Long id) {
        return dishMapper.selByCate(id);
    }
}
