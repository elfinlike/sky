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
    public List<DishVO> getByCate(Dish dish) {
        List<Dish> dishList = userDishMapper.getByCate(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = userDishFlavorMapper.getByDish(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
