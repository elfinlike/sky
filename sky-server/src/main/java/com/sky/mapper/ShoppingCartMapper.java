package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 动态条件查询
     *
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     *
     * 根据id修改商品数量
     * @param shoppingCart
     */

    @Update("update shopping_cart set number=#{number} where id=#{id}")
    void updateById(ShoppingCart shoppingCart);

    /**
     * 插入购物车数据
     *
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart ( name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time) " +
            "values (#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{createTime})")
    void insert(ShoppingCart shoppingCart);

    @Select("select * from shopping_cart")
    List<ShoppingCart> getAll();

    @Delete("delete from shopping_cart where id=#{id}")
    void delete(ShoppingCart cart);

    @Delete("delete from shopping_cart")
    void clean();
}
