package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Date;

@RestController
@Api(tags = "数据统计相关接口")
@Slf4j
@RequestMapping("/admin/report")
public class ReportController {

    @Autowired
    private ReportService reportService;
    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @ApiOperation(value = "营业额统计接口")
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> getTurnover(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("营业额数据统计，{},{}",begin,end);
        TurnoverReportVO turnoverReportVO=reportService.getTurnoverStatistics(begin,end);
        return Result.success(turnoverReportVO);
    }

    @ApiOperation(value = "用户统计")
    @GetMapping("/userStatistics")
    public Result<UserReportVO> getUserReport(@DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate begin,
                                              @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end){
        log.info("用户数量统计,{},{}",begin,end);
        UserReportVO userReportVO=reportService.getUserReport(begin,end);
        return Result.success(userReportVO);
    }

    @ApiOperation(value = "订单统计")
    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> getOrderReport(@DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate begin,
                                                @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end){
        log.info("订单数量统计,{},{}",begin,end);
        OrderReportVO orderReportVO=reportService.getOrderReport(begin,end);
        return Result.success(orderReportVO);
    }


    @ApiOperation(value = "菜品销量排行Top10")
    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> getSalesTop(@DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate begin,
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end){
        log.info("菜品销量排行,{},{}",begin,end);
        SalesTop10ReportVO salesTop10ReportVO=reportService.getTop10(begin,end);
        return Result.success(salesTop10ReportVO);

    }
}

