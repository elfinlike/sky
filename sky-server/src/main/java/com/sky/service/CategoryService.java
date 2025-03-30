package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CategoryService {
    PageResult PageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    void updateCate(CategoryDTO categoryDTO);

    void editStatus(Integer status, Long id);

    void addNew(CategoryDTO categoryDTO);

    void deleteById(Long id);

    List<Category> listByType(Integer type);
}
