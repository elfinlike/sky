package com.sky.controller.user;


import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.result.Result;
import com.sky.service.UserSetMealService;
import com.sky.vo.SetMealDishVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/setmeal")
@Api(tags = "套餐查询")
@Slf4j
public class UserSetMealController {

    @Autowired
    private UserSetMealService userSetMealService;
    @GetMapping("/list")
    @ApiOperation(value = "根据id查询套餐")
    public Result<List<Setmeal>> getByMeal(Long categoryId){
        List<Setmeal> setmealList=userSetMealService.getByMeal(categoryId);
        return  Result.success(setmealList);
    }

    @GetMapping("/dish/{id}")
    @ApiOperation(value = "根据套餐id查询包含的菜品")
    public Result<List<SetMealDishVO>> getDishes(@PathVariable Long id){
        List<SetMealDishVO> setMealDishVOS=userSetMealService.getDishes(id);
        return Result.success(setMealDishVOS);
    }

}
