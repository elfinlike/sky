package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
     * 统计指定时间区间内的营业额数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            //日期计算，计算指定日期的后一天对应的日期
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        String datelist=StringUtils.join(dateList,",");

        List<Double> turnOvers=new ArrayList<>();
        for (LocalDate localDate : dateList) {
            //查询date日期对应的营业额数据，营业额是指：状态为“已完成”的订单金额合计
            //获取一天开始时间
             LocalDateTime Min=LocalDateTime.of(localDate, LocalTime.MIN);
             //获取一天结束时间
             LocalDateTime Max=LocalDateTime.of(localDate,LocalTime.MAX);
            //select sum(amount) from orders where order_time>? and order_time<? and status=5;

            Map map=new HashMap<>();
            map.put("begin",Min);
            map.put("end",Max);
            map.put("status", Orders.COMPLETED);
            Double amount=orderMapper.sumByMap(map);
            amount=amount==null?0.0:amount;
            turnOvers.add(amount);
        }

        String turnOverList=StringUtils.join(turnOvers,",");


        TurnoverReportVO turnoverReportVO= TurnoverReportVO.builder()
                                            .turnoverList(turnOverList)
                                            .dateList(datelist)
                                            .build();
        return turnoverReportVO;
    }

    /**
     * 统计指定时间区间的用户数量情况
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserReport(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            //日期计算，计算指定日期的后一天对应的日期
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        String datelist=StringUtils.join(dateList,",");

        List<Long> UserList=new ArrayList<>();
        List<Long> newUserList=new ArrayList<>();
        for (LocalDate localDate : dateList) {
            //遍历每天的用户总量和新增用户数量
            long user=userMapper.getTotalUser();
            LocalDateTime Min=LocalDateTime.of(localDate,LocalTime.MIN);
            LocalDateTime Max=LocalDateTime.of(localDate,LocalTime.MAX);
            long newUser=userMapper.getNewUser(Min,Max);
            //将查出来的用户数量加入List中
            UserList.add(user);
            newUserList.add(newUser);
        }

        //将获得的用户数量列表转为字符串
        String UserTotal= StringUtils.join(UserList,",");
        String newUserTotal=StringUtils.join(newUserList,",");

        
        UserReportVO userReportVO= UserReportVO.builder()
                .dateList(datelist)
                .newUserList(newUserTotal)
                .totalUserList(UserTotal)
                .build();
        return userReportVO;
    }

    /**
     * 统计全部订单和当天订单的完成情况
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderReport(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            //日期计算，计算指定日期的后一天对应的日期
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        String datelist=StringUtils.join(dateList,",");

        Integer totalOrderCount=orderMapper.getTotal();
        Integer validOrderCount=orderMapper.getValid(Orders.COMPLETED);
        if (totalOrderCount == null || validOrderCount == null) {
            throw new IllegalArgumentException("订单总数或有效订单数不能为空");
        }
        // 防止除零异常
        if (totalOrderCount == 0) {
            throw new ArithmeticException("订单总数为0，无法计算完成率");
        }
        // 计算完成率，确保使用 double 类型进行除法运算
        Double orderCompletionRate = (double) validOrderCount / totalOrderCount;
        List<Integer> orderCount=new ArrayList<>();
        List<Integer> validOrderCountList=new ArrayList<>();
        for (LocalDate localDate : dateList) {
            //通过遍历，获取每一天的总单数和有效单数
            LocalDateTime Min=LocalDateTime.of(localDate,LocalTime.MIN);
            LocalDateTime Max=LocalDateTime.of(localDate,LocalTime.MAX);

            Integer totalOrder=orderMapper.getDayTotal(Min,Max);
            Integer validOrder=orderMapper.getDayValid(Min,Max,Orders.COMPLETED);
            orderCount.add(totalOrder);
            validOrderCountList.add(validOrder);
        }

        String order=StringUtils.join(orderCount,",");
        String valid=StringUtils.join(validOrderCountList,",");

        OrderReportVO orderReportVO=OrderReportVO.builder()
                .dateList(datelist)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .orderCountList(order)
                .validOrderCountList(valid)
                .build();
        return orderReportVO;
    }

    /**
     * 获取销量Top前十的菜品
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime=LocalDateTime.of(begin,LocalTime.MIN);
        LocalDateTime endTime=LocalDateTime.of(end,LocalTime.MAX);

        List<GoodsSalesDTO> goodsSalesDTOS=orderMapper.getSalesTop(beginTime,endTime);

//        List<String> nameList=new ArrayList<>();
//        List<Integer> numberList=new ArrayList<>();
//        for (GoodsSalesDTO goodsSalesDTO : goodsSalesDTOS) {
//            nameList.add(goodsSalesDTO.getName());
//            numberList.add(goodsSalesDTO.getNumber());
//        }

        //使用Stream流来进行处理
        List<String> nameList=goodsSalesDTOS.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numberList=goodsSalesDTOS.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        String name=StringUtils.join(nameList,",");
        String number=StringUtils.join(numberList,",");

        SalesTop10ReportVO salesTop10ReportVO=SalesTop10ReportVO.builder()
                .nameList(name)
                .numberList(number)
                .build();

        return salesTop10ReportVO;
    }
}
