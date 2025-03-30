package com.sky.mapper;


import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    /*
    获取分页记录
     */
    public List<Category> PageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    //对cate记录进行更新
    void updateCate(Category category);

    //插入新分类
    @Insert("insert into category(type, name, sort, status, create_time, update_time, create_user, update_user)" +
            " VALUES" +
            " (#{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void addNew(Category category);

    //根据id删除分类
    @Delete("delete from category where id=#{id}")
    void deleteById(Long id);


    List<Category> listByType(Integer type);
}
