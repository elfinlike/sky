package com.sky.controller.user;



import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@Api(tags = "条件查询")
@RequestMapping("/user/category/list")
public class UserCategoryController {

    @Autowired
    private CategoryService categoryService;
    @ApiOperation(value = "分类接口")
    @GetMapping
    public Result<List<Category>> getByType(Integer type){
        List<Category> categories=categoryService.getByType(type);
        return Result.success(categories);

    }
}
