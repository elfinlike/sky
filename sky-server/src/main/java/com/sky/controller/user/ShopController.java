package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("userShopController")
@Slf4j
@Api(tags = "用户查询店铺状态")
@RequestMapping("/user/shop")
public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;

    public static final String key="SHOP_STATUS";
    @GetMapping("/status")
    @ApiOperation(value = "获取营业状态")
    public Result<Integer> getShop(){
        Integer status=Integer.valueOf((String)redisTemplate.opsForValue().get(key));
        log.info("获取到的店铺的状态为：{}",status==1?"营业中":"打烊中");
        return Result.success(status);
    }
}
