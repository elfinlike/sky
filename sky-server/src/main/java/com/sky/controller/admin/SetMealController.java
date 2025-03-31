package com.sky.controller.admin;


import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Api(tags = "套餐接口")
@RequestMapping("/admin/setmeal")
public class SetMealController {

    @Autowired
    private SetmealService setmealService;
    @ApiOperation(value = "新增套餐")
    @PostMapping
    public Result<String> addMeal(@RequestBody SetmealDTO setmealDTO){
        log.info("前端传回的套餐详情为："+setmealDTO);
        setmealService.addMeal(setmealDTO);
        return Result.success();
    }

    @ApiOperation(value = "修改套餐")
    @PutMapping
    public Result<String> updateMeal(@RequestBody SetmealDTO setmealDTO){
        setmealService.updateMeal(setmealDTO);
        return Result.success();
    }

    @ApiOperation(value = "分页查询")
    @GetMapping("/page")
    public Result<PageResult> setMealQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("分页数据为："+setmealPageQueryDTO);
        PageResult pageResult=setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation(value = "根据id查询套餐")
    @GetMapping("/{id}")
    public Result<Setmeal> getById(@PathVariable Long id){
        return Result.success(setmealService.getById(id));
    }

    @ApiOperation(value = "套餐的停售、起售")
    @PostMapping("/status/{status}")
    public Result<String> editStatus(@PathVariable Integer status,Long id){
        setmealService.editStatus(status,id);
        return Result.success();
    }

    @ApiOperation(value = "批量删除套餐")
    @DeleteMapping
    public Result<String> deleteBatch(@RequestParam List<Long> ids){
        setmealService.deleteBatch(ids);
        return Result.success();
    }
}
