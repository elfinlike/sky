package com.sky.service;

import com.sky.entity.Setmeal;
import com.sky.vo.SetMealDishVO;

import java.util.List;

public interface UserSetMealService {
    List<Setmeal> getByMeal(Long categoryId);

    List<SetMealDishVO> getDishes(Long id);
}
