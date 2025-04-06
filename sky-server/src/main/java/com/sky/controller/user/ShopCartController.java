package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "购物车模块")
@Slf4j
@RequestMapping("/user/shoppingCart")
public class ShopCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;


    @ApiOperation(value = "添加购物车")
    @PostMapping("/add")
    public Result<String> add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("购物车添加信息："+shoppingCartDTO);
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    @ApiOperation(value = "查看购物车")
    @GetMapping("/list")
    public Result<List<ShoppingCart>> viewRecord(){

        List<ShoppingCart> list=shoppingCartService.getAll();
        return Result.success(list);
    }

    @ApiOperation(value = "删除购物车中的一个商品")
    @PostMapping("/sub")
    public Result<String> sub(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("购物车删除商品获得的信息"+shoppingCartDTO);
        shoppingCartService.sub(shoppingCartDTO);
        return Result.success();
    }

    @ApiOperation(value = "清空购物车")
    @DeleteMapping("/clean")
    public Result<String> clean(){
        shoppingCartService.clean();
        return Result.success();
    }

}
