package com.sky.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealMapper setmealMapper;
    @Override
    public PageResult PageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
       PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());
        List<Category> list=categoryMapper.PageQuery(categoryPageQueryDTO);
        Page<Category> p= (Page<Category>) list;
        PageResult pageResult=new PageResult(p.getTotal(),p.getResult());
        return pageResult;
    }

    @Override
    public void updateCate(CategoryDTO categoryDTO) {
        Category category=new Category();
        BeanUtils.copyProperties(categoryDTO,category);
//        category.setUpdateTime(LocalDateTime.now());
//        category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.updateCate(category);
    }

    @Override
    public void editStatus(Integer status, Long id) {
        Category category=Category.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        Dish dish=Dish.builder()
                .categoryId(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        dishService.updateDishbyCate(dish);
        categoryMapper.updateCate(category);
    }

    @Override
    public void addNew(CategoryDTO categoryDTO) {
        Category category=new Category();
        //将对象categoryDTO中的数据拷贝到category对象当中
        BeanUtils.copyProperties(categoryDTO,category);
        //设置创建时间和修改时间，创建人和修改人
//        category.setCreateTime(LocalDateTime.now());
//        category.setUpdateTime(LocalDateTime.now());
//        category.setUpdateUser(BaseContext.getCurrentId());
//        category.setCreateUser(BaseContext.getCurrentId());
        //分类状态默认值为Disable 禁用状态
        category.setStatus(StatusConstant.DISABLE);
        categoryMapper.addNew(category);
    }

    @Override
    public void deleteById(Long id) {
        //检查该分类是否关联了菜品
        Integer count=dishService.selByCate(id);
        if(count>0){
            //当前分类关联了菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
        Integer mealist=setmealMapper.selByCate(id);
        if(mealist>0){
            //当前分类包含了套餐，无法删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        categoryMapper.deleteById(id);

    }

    @Override
    public List<Category> listByType(Integer type) {
       return categoryMapper.listByType(type);
    }
}
