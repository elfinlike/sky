package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //判断当前加入购物车中的商品是否已经存在了
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> list=shoppingCartMapper.list(shoppingCart);
        //如果已经存在了，只需要将商品数量加1
        if(list!=null&&list.size()>0){
            ShoppingCart cart=list.get(0);//获取第一条数据也是唯一一条数据
            cart.setNumber(cart.getNumber()+1);
            shoppingCartMapper.updateById(cart);
        }else {
            //如果不存在，插入一条购物车数据

            //判断本次添加到购物车的是菜品还是套餐
            Long dishId=shoppingCartDTO.getDishId();
            Long setmealId=shoppingCartDTO.getSetmealId();
            if(dishId!=null){//本次添加到购物车的是菜品
                DishVO dishVO=dishMapper.getById(dishId);
                shoppingCart.setName(dishVO.getName());
                shoppingCart.setImage(dishVO.getImage());
                shoppingCart.setAmount(dishVO.getPrice());
            }else{//本次添加到购物车的是套餐
                Setmeal setmeal=setmealMapper.getById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }


    }

    @Override
    public List<ShoppingCart> getAll() {
        return shoppingCartMapper.getAll();
    }

    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //判断该商品在shoppingCart中的数量；
        //因为查询出来必定只有一条数据，因此，直接获取列表第一个实体就可以获取到数据
        List<ShoppingCart> list=shoppingCartMapper.list(shoppingCart);
        ShoppingCart cart=list.get(0);

        if(cart.getNumber()>1){//数量大于1的减一
            cart.setNumber(cart.getNumber()-1);
            shoppingCartMapper.updateById(cart);
        }else{//对于数量等于1的删除
            shoppingCartMapper.delete(cart);
        }
    }

    @Override
    public void clean() {
        shoppingCartMapper.clean();
    }
}
