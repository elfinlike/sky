package com.sky.controller.admin;



import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrdersPageService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController("AdminOrderController")
@Api(tags = "管理端订单查询")
@Slf4j
@RequestMapping("/admin/order")
public class OrderController {

    @Autowired
    private OrdersPageService ordersPageService;

    @ApiOperation(value = "历史订单查询")
    @GetMapping("/conditionSearch")
     public Result<PageResult> PageQuery(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("需要进行搜索的条件为："+ordersPageQueryDTO.toString());
        PageResult pageResult=ordersPageService.PageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/statistics")
    @ApiOperation(value = "统计各个状态的订单数量")
    public Result<OrderStatisticsVO> getStatistics(){
        OrderStatisticsVO orderStatisticsVO=ordersPageService.getStatistics();
        return Result.success(orderStatisticsVO);
    }

    @PutMapping("/confirm")
    @ApiOperation(value = "接单")
    public Result<String> Confirm(@RequestBody Map<Object,Integer> map){
        log.info("需要节点的id是："+map.get("id"));
        Long id= Long.valueOf(map.get("id"));
        ordersPageService.setConfirm(id);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    @ApiOperation(value = "派送订单")
    public Result<String> Delivery(@PathVariable Long id){
        log.info("需要派送的订单id为"+id);
        ordersPageService.setDelivery(id);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    @ApiOperation(value = "完成订单")
    public Result<String> Complete(@PathVariable Long id){
        log.info("完成的订单为："+id);
        ordersPageService.setComplete(id);
        return Result.success();
    }

    @PutMapping("/rejection")
    @ApiOperation(value = "拒绝订单")
    public Result<String> Rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        log.info("拒绝的订单id和原因"+ordersRejectionDTO.toString());
        ordersPageService.setRejection(ordersRejectionDTO);
        return Result.success();
    }

    @GetMapping("/details/{id}")
    @ApiOperation(value = "查询订单详情")
    public Result<OrderVO> details(@PathVariable Long id){
        log.info("需要查询详情的id"+id);
        OrderVO orderVO=ordersPageService.detail(id);
        return Result.success(orderVO);
    }
}
