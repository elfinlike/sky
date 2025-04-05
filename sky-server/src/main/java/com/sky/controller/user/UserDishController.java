package com.sky.controller.user;


import com.sky.dto.DishDTO;
import com.sky.result.Result;
import com.sky.service.UserDishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @ApiOperation(value = "根据菜品id查询菜品")
    @GetMapping("/list")
    public Result<List<DishVO>> dishList(Long categoryId){
        log.info("前端传回的categoryId为："+categoryId);
        List<DishVO> dishVOS=userDishService.getByCate(categoryId);
        return Result.success(dishVOS);
    }
}
