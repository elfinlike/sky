package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.PageBean;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetMealDishMapper setMealDishMapper;
    //由于涉及多表操作，因此需要添加事务管理
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addMeal(SetmealDTO setmealDTO) {
        //首先要添加套餐到套餐表中
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //添加套餐
        setmealMapper.addMeal(setmeal);

        //获取套餐的id
        Long id=setmeal.getId();

        //判断前端传回来的list是否为空
        List<SetmealDish> setmealDishes=setmealDTO.getSetmealDishes();
        if (setmealDishes!=null&&setmealDishes.size()>0){//表示传回的列表不为空
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(id);
            });
            log.info("setMealDish关联表需要存储的数据为："+setmealDishes);
            //然后将列表数据传入数据库中
            setMealDishMapper.addMealDish(setmealDishes);
        }
    }

    //分页查询
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        List<Setmeal> list=setmealMapper.pageQuery(setmealPageQueryDTO);
        Page<Setmeal> p=(Page<Setmeal>) list;
        PageResult pageResult=new PageResult(p.getTotal(),p.getResult());
        return pageResult;
    }

    @Override
    public Setmeal getById(Long id) {
        return setmealMapper.getById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateMeal(SetmealDTO setmealDTO) {
        //多表操作，使用事务保持数据库的一致性
        //首先要更新套餐到
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //添加套餐
        setmealMapper.updateMeal(setmeal);



        //清空与之相关的数据库
        setMealDishMapper.deleteAll(setmealDTO.getId());
        //判断前端传回来的list是否为空
        List<SetmealDish> setmealDishes=setmealDTO.getSetmealDishes();
        if (setmealDishes!=null&&setmealDishes.size()>0){//表示传回的列表不为空
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealDTO.getId());
            });
            log.info("setMealDish关联表需要存储的数据为："+setmealDishes);
            //然后将列表数据传入数据库中
            setMealDishMapper.addMealDish(setmealDishes);
        }

    }

    @Override
    public void editStatus(Integer status, Long id) {
        Setmeal setmeal=Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.updateMeal(setmeal);
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        //从逻辑上讲，先删除套餐中的关联表的菜品，再删除套餐
        setMealDishMapper.deleteBatch(ids);
        setmealMapper.deleteBatch(ids);
    }
}
