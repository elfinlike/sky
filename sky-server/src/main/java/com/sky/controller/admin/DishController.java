package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@Api(tags = "菜品管理")
@RequestMapping("/admin/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation(value = "分页查询菜品")
    @GetMapping("/page")
    public Result<PageResult> DishQuery( DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询传回的参数为："+dishPageQueryDTO);
        PageResult pageResult=dishService.DishQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }


    @ApiOperation(value = "按照分类id查询菜品")
    @GetMapping("/list")
    public Result<List<Dish>> getByCate(Long categoryId){
        List<Dish> list=dishService.getByCate(categoryId);
        return Result.success(list);
    }

    @ApiOperation(value = "按照id查询菜品")
    @GetMapping("/{id}")
    public Result<DishVO> getByID(@PathVariable Long id){
        DishVO list=dishService.getById(id);
        return Result.success(list);
    }

    @ApiOperation(value = "修改菜品状态")
    @PostMapping("/status/{status}")
    public Result<String> editStatus(@PathVariable Integer status,Long id){
        log.info("前端传回的status和id"+status+id);
        dishService.updateDish(status,id);
        //清理所有缓存数据
        Set keys=redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        return Result.success();
    }

    @ApiOperation(value = "新增菜品")
    @PostMapping
    public Result<String> save(@RequestBody DishDTO dishDTO){
        log.info("具体的参数："+dishDTO);
        dishService.saveWithFlavor(dishDTO);

        //清理缓存数据
        String key="dish_"+dishDTO.getCategoryId();
        redisTemplate.delete(key);
        return Result.success();
    }

    @ApiOperation(value = "修改菜品信息")
    @PutMapping
    public Result<String> updateDish(@RequestBody DishDTO dishDTO){
        log.info("具体的参数为："+dishDTO);
        dishService.updateWithFlavor(dishDTO);

        //获取key
        Set keys=redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        return Result.success();
    }

    @ApiOperation(value = "批量删除菜品")
    @DeleteMapping
    public Result<String> deleteBatch(@RequestParam List<Long> ids){
        log.info("需要批量删除的菜品id为："+ids);
        dishService.deleteBatch(ids);
        //批量删除后，删除所有key
        Set keys=redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        return Result.success();
    }
}
