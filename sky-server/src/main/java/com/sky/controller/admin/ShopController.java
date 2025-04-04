package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@Slf4j
@RequestMapping("/admin/shop")
@Api(tags = "店铺操作接口")
public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;

    public  static final String key="SHOP_STATUS";
    @GetMapping("/status")
    @ApiOperation(value = "获取营业状态")
    public Result<Integer> getShop(){
        Integer status=Integer.valueOf((String) redisTemplate.opsForValue().get(key));
        log.info("获取到的店铺的状态为：{}",status==1?"营业中":"打烊中");
        return Result.success(status);
    }

    @ApiOperation(value = "设置店铺营业状态")
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置店铺的营业状态为：{}",status==1?"营业中":"打烊中");
        redisTemplate.opsForValue().set(key,String.valueOf(status));
        return Result.success();
    }
}
