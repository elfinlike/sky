package com.sky.controller.user;


import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.UserDishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(tags = "用户查询菜品")
@Slf4j
@RequestMapping("/user/dish")
public class UserDishController {

    @Autowired
    private UserDishService userDishService;

    @Autowired
    private RedisTemplate redisTemplate;
    @ApiOperation(value = "根据菜品id查询菜品")
    @GetMapping("/list")
    public Result<List<DishVO>> dishList(Long categoryId){
        log.info("前端传回的categoryId为："+categoryId);
        //构造redis中的key dish_分类id
        String key="dish_"+categoryId;
        //查询redis是否存在菜品数据
        List<DishVO> list=(List<DishVO>) redisTemplate.opsForValue().get(key);
        //如果存在，直接返回，无需查询数据库
        if(list!=null&&list.size()>0){
            return Result.success(list);
        }
        //如果不存在，查询数据库，将查询到的数据放入数据库中

        Dish dish=new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);
        list=userDishService.getByCate(dish);
        redisTemplate.opsForValue().set(key,list);
        return Result.success(list);
    }
}
