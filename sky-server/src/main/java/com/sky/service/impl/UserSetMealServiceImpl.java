package com.sky.service.impl;

import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.DishMapper;
import com.sky.mapper.UserSetMealDishMapper;
import com.sky.mapper.UserSetMealMapper;
import com.sky.service.UserSetMealService;
import com.sky.vo.DishVO;
import com.sky.vo.SetMealDishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static ch.qos.logback.classic.spi.ThrowableProxyVO.build;

@Service
public class UserSetMealServiceImpl implements UserSetMealService {

    @Autowired
    private UserSetMealMapper userSetMealMapper;

    @Autowired
    private UserSetMealDishMapper userSetMealDishMapper;

    @Autowired
    private DishMapper dishMapper;
    @Override
    public List<Setmeal> getByMeal(Long categoryId) {
        List<Setmeal> setmeals=userSetMealMapper.getByMeal(categoryId);
        return setmeals;
    }

    @Override
    public List<SetMealDishVO> getDishes(Long id) {
        //先通过setmeal_dish表获取套餐中的dish的id
        List<SetmealDish> dishes=userSetMealDishMapper.getIdBatch(id);
        //根据获取出的dish_id来查找name，description，image

        List<SetMealDishVO> setMealDishVOS=new ArrayList<>();
        for (SetmealDish setmealDishdish:
             dishes) {
            DishVO dishVO =dishMapper.getById(setmealDishdish.getDishId());
            SetMealDishVO setMealDishVO=new SetMealDishVO();
            setMealDishVO.setName(setmealDishdish.getName());
            setMealDishVO.setDescription(dishVO.getDescription());
            setMealDishVO.setCopies(setmealDishdish.getCopies());
            setMealDishVO.setImage(dishVO.getImage());
            setMealDishVOS.add(setMealDishVO);

        }
        return setMealDishVOS;
    }
}
