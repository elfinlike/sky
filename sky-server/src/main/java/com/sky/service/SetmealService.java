package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;

import java.util.List;

public interface SetmealService {
    void addMeal(SetmealDTO setmealDTO);

     PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    Setmeal getById(Long id);

    void updateMeal(SetmealDTO setmealDTO);

    void editStatus(Integer status, Long id);

    void deleteBatch(List<Long> ids);
}
