package com.sky.service.impl;

import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.UserDishFlavorMapper;
import com.sky.mapper.UserDishMapper;
import com.sky.service.UserDishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDishServiceImpl implements UserDishService {

    @Autowired
    private UserDishMapper userDishMapper;

    @Autowired
    private UserDishFlavorMapper userDishFlavorMapper;
    @Override
    public List<DishVO> getByCate(Long categoryId) {
        //先找出cate
        List<Dish> dishes=userDishMapper.getByCate(categoryId);
        if(dishes==null){
            return null;
        }
        List<DishVO> dishVOS=new ArrayList<>();
        if (dishes!=null){
            for (Dish dish:
                 dishes) {
               DishVO dishVO=new DishVO();
               BeanUtils.copyProperties(dish,dishVO);
               dishVOS.add(dishVO);
            }

            for (DishVO dish:
                 dishVOS) {
                List<DishFlavor> dishFlavors=userDishFlavorMapper.getByDish(dish);
                dish.setFlavors(dishFlavors);
            }
        }
        return dishVOS;
    }
}
