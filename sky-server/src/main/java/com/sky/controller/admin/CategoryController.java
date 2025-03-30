package com.sky.controller.admin;


import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@RestController
@RequestMapping("/admin/category")
@Api(tags = "分类相关接口")
@Slf4j//导入日志类注解
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @ApiOperation(value = "分页查询")
    @GetMapping("/page")
    public Result<PageResult> pageCategory(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分类分页查询的参数为："+categoryPageQueryDTO);
        PageResult pageResult=categoryService.PageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation(value = "修改分类")
    @PutMapping
    public Result<String> UpdateCate(@RequestBody CategoryDTO categoryDTO){
        log.info("分类修改传来的数据："+categoryDTO);
        categoryService.updateCate(categoryDTO);
        return Result.success();

    }

    @ApiOperation(value = "编辑状态")
    @PostMapping("/status/{status}")
    public Result<String> editStatus(@PathVariable Integer status,Long id){
        log.info("前端传回的status和id数据为："+status+id);
        categoryService.editStatus(status,id);
        return Result.success();
    }

    @ApiOperation(value = "新增分类")
    @PostMapping
    public Result<String> addNew(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类的数据："+categoryDTO);
        categoryService.addNew(categoryDTO);
        return Result.success();
    }

    @ApiOperation(value = "删除分类")
    @DeleteMapping
    public Result<String> deleteById(Long id){
        log.info("获取的id的值为："+id);
        categoryService.deleteById(id);
        return Result.success();
    }

    @ApiOperation(value = "根据类型查询")
    @GetMapping("/list")
    public Result<List> listByType(Integer type){
        log.info("前端获取到的type信息："+type);
        List<Category> categoryList=categoryService.listByType(type);
        return Result.success(categoryList);
    }

}
