package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "工作台接口")
@Slf4j
@RequestMapping("/admin/workspace")
public class workSpaceController {

    @Autowired
    private WorkSpaceService workSpaceService;
    @GetMapping("/businessData")
    @ApiOperation(value = "查看今日运营数据")
    public Result<BusinessDataVO> getBusinessData(){
        BusinessDataVO businessDataVO=workSpaceService.getBusinessData();
        return Result.success(businessDataVO);
    }

    @GetMapping("/overviewSetmeals")
    @ApiOperation(value = "查询套餐总览")
    public Result<SetmealOverViewVO> getOverView(){
        SetmealOverViewVO setmealOverViewVO=workSpaceService.getMealOverView();
        return Result.success(setmealOverViewVO);
    }

    @GetMapping("/overviewDishes")
    @ApiOperation(value = "查询菜品总览")
    public Result<DishOverViewVO> getDishOverView(){
        DishOverViewVO dishOverViewVO=workSpaceService.getDishOverView();
        return Result.success(dishOverViewVO);
    }

    @GetMapping("/overviewOrders")
    @ApiOperation(value = "查询订单管理数据")
    public Result<OrderOverViewVO> getOrderOverView(){
        OrderOverViewVO orderOverViewVO=workSpaceService.getOrderOverView();
        return Result.success(orderOverViewVO);
    }
}
