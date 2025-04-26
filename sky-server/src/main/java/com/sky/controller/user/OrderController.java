package com.sky.controller.user;

import com.alibaba.fastjson.JSON;
import com.sky.constant.MessageConstant;
import com.sky.dto.OrderQueryDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.OrderMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrdersListVO;
import com.sky.websocket.WebSocketServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController("UserOrderController")
@Slf4j
@Api(tags = "用户订单相关接口")
@RequestMapping("/user/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private WebSocketServer webSocketServer;
    /**
     *用户下单
     * @param submitDTO
     * @return
     */
    @PostMapping("/submit")
    @ApiOperation(value = "用户下单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO submitDTO){
        log.info("用户下单"+submitDTO);
       OrderSubmitVO submitVO= orderService.submitOrder(submitDTO);
        return Result.success(submitVO);
    }


    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    @GetMapping("/historyOrders")
    @ApiOperation(value = "历史订单查询")
    public Result<PageResult> pageQuery(OrderQueryDTO orderQueryDTO){
        log.info("前端传回的查询数据为："+orderQueryDTO);
        PageResult pageResult=orderService.PageQuery(orderQueryDTO);
        return Result.success(pageResult);
    }

    @PutMapping("/cancel/{id}")
    @ApiOperation(value = "取消订单")
    public Result<String> cancel(@PathVariable Long id) throws Exception {
        log.info("需要取消的订单id："+id);
        orderService.cancel(id);
        return Result.success();
    }

    @PostMapping("/repetition/{id}")
    @ApiOperation(value = "再来一单")
    //应该将查询的数据放到购物车里去
    public Result<String> repetition(@PathVariable Long id){
        log.info("需要再来一单的id号："+id);
        orderService.repetition(id);
        return Result.success();
    }

    @GetMapping("/orderDetail/{id}")
    @ApiOperation(value = "查看订单详情")
    public Result<OrdersListVO> orderDetail(@PathVariable Long id){
        OrdersListVO ordersListVO=orderService.list(id);
        return Result.success(ordersListVO);
    }





    @GetMapping("/reminder/{id}")
    @ApiOperation(value = "催单")
    public Result<String> reminder(@PathVariable Long id){
        Orders orders=orderMapper.getById(id);
        if (orders==null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Map map=new HashMap<>();
        map.put("type",2);
        map.put("orderId",id);
        map.put("content","订单号"+orders.getNumber());

        String json= JSON.toJSONString(map);
        log.info("推送消息为:{}",json);
        //推送到所有的管理端
        webSocketServer.sendToAllClient(json);
        return Result.success();
    }
}
