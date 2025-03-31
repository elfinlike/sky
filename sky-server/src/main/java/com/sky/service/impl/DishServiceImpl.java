package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



@Service
@Slf4j
public class DishServiceImpl  implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Override
    public void updateDishbyCate(Dish dish) {
        dishMapper.updatebyCate(dish);
    }

    @Override
    public Integer selByCate(Long id) {
        return dishMapper.selByCate(id);
    }

    @Override
    public PageResult DishQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<Dish> p=(Page<Dish>) dishMapper.PageQuery(dishPageQueryDTO);
        PageResult pageResult=new PageResult(p.getTotal(),p.getResult());
        return pageResult;
    }

    @Override
    public List<Dish> getByCate(Long categoryId) {
        return dishMapper.getByCate(categoryId);
    }

    @Override
    public DishVO getById(Long id) {
        DishVO dishVOS=dishMapper.getById(id);
        //根据id查询口味
        List<DishFlavor> dishFlavors=dishFlavorMapper.getByDish(id);
        //将查询到的dishFlavor集合插到dishVos中
        dishVOS.setFlavors(dishFlavors);
        return dishVOS;
    }

    @Override
    public void updateDish(Integer status, Long id) {
        Dish dish=Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.updateDish(dish);
    }

    //新增菜品和对应的口味
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        //向菜品表插入数据
        dishMapper.insert(dish);
        //向口味表插入n条数据

        //获取insert语句生成的主键值
        Long dishId= dish.getId();
        List<DishFlavor> flavors=dishDTO.getFlavors();
        if (flavors!=null&&flavors.size()>0){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        //首先先根据id更新dish
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.updateDish(dish);
        log.info("id值为："+dishDTO.getId());

        //判断flavor是否为空
        List<DishFlavor> flavors=dishDTO.getFlavors();
        if (flavors!=null&&flavors.size()>0){
            //先将原口味进行删除，然后再进行插入
            dishFlavorMapper.delete(dishDTO.getId());
            //删除之后进行插入
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.insertBatch(flavors);
        }
        if (flavors==null||flavors.size()==0){
            dishFlavorMapper.delete(dishDTO.getId());
        }
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        //先删除该id的口味，再删除菜品
        dishFlavorMapper.deleteBatch(ids);
        //然后再批量删除菜品
        dishMapper.deleteBatch(ids);
    }
}
