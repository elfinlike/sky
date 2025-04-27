package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.UserService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private DishMapper dishMapper;
    @Override
    public BusinessDataVO getBusinessData() {
        //新增用户数量；
        LocalDateTime begin=LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime end=LocalDateTime.of(LocalDate.now(),LocalTime.MAX);

        //获取当天创建的用户数量
        Integer newUser=(int) userMapper.getNewUser(begin,end);
        newUser=newUser==null?0:newUser;
        //获取当天营业额
        Map map=new HashMap<>();
        map.put("begin",begin);
        map.put("end",end);
        map.put("status", Orders.COMPLETED);
        Double turnOver=orderMapper.sumByMap(map);
        turnOver=turnOver==null?0.0:turnOver;
        //获取有效订单数
        Integer validCount= orderMapper.getValid(Orders.COMPLETED);
        validCount=validCount==null?0:validCount;
        //订单完成率
        Integer totalOrderCount=orderMapper.getTotal();
        if (totalOrderCount == null ) {
            throw new IllegalArgumentException("订单总数不能为空");
        }
        // 防止除零异常
        if (totalOrderCount == 0) {
            throw new ArithmeticException("订单总数为0，无法计算完成率");
        }
        // 计算完成率，确保使用 double 类型进行除法运算
        Double orderCompletionRate = (double) validCount / totalOrderCount;
        DecimalFormat df = new DecimalFormat("#0.00");

        //获取平均客单价
        Double sumAmount=orderMapper.getSumAmount(Orders.COMPLETED);

        if (validCount == 0) {
            throw new ArithmeticException("实际完成订单总数为0，无法计算完成率");
        }
        Double avg= Double.valueOf(df.format((double)sumAmount / validCount));

        BusinessDataVO businessDataVO=BusinessDataVO.builder()
                .newUsers(newUser)
                .turnover(turnOver)
                .validOrderCount(validCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(avg)
                .build();
        return businessDataVO;
    }

    @Override
    public DishOverViewVO getDishOverView() {
        //获取停售套餐数量
        Integer discontinued=dishMapper.getByStatus(StatusConstant.DISABLE);
        discontinued=discontinued==null?0:discontinued;
        //获取正在销售套餐数量
        Integer sold= dishMapper.getByStatus(StatusConstant.ENABLE);
        sold=sold==null?0:sold;

        DishOverViewVO dishOverViewVO=DishOverViewVO.builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
        return dishOverViewVO;
    }

    @Override
    public SetmealOverViewVO getMealOverView() {
        Integer discontinued=setmealMapper.getByStatus(StatusConstant.DISABLE);
        discontinued=discontinued==null?0:discontinued;
        //获取正在销售套餐数量
        Integer sold= setmealMapper.getByStatus(StatusConstant.ENABLE);
        sold=sold==null?0:sold;

        SetmealOverViewVO setmealOverViewVO=SetmealOverViewVO.builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
        return setmealOverViewVO;
    }

    @Override
    public OrderOverViewVO getOrderOverView() {
        //获取待接单数量
        Integer waitingOrder=orderMapper.getByStatus(Orders.TO_BE_CONFIRMED);
        //获取待派送的数量
        Integer deliveredOrder=orderMapper.getByStatus(Orders.CONFIRMED);
        //已经完成的数量
        Integer completedOrder=orderMapper.getByStatus(Orders.COMPLETED);
        //已经取消的数量
        Integer cancelledOrder=orderMapper.getByStatus(Orders.CANCELLED);
        //获取全部订单
        Integer all=orderMapper.getTotal();

        OrderOverViewVO orderOverViewVO=OrderOverViewVO.builder()
                .allOrders(all)
                .waitingOrders(waitingOrder)
                .deliveredOrders(deliveredOrder)
                .completedOrders(completedOrder)
                .cancelledOrders(cancelledOrder)
                .build();

        return orderOverViewVO;
    }
}
