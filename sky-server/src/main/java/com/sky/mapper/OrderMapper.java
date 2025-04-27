package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 插入数据
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    List<Orders> PageQuery(OrdersPageQueryDTO ordersPageQueryDTO);


    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);

    /**
     * 根据订单状态和订单时间查询订单
     * @param status
     * @param orderTime
     * @return
     */
    @Select("select * from orders where status=#{status} and order_time<#{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);


    @SuppressWarnings("MybatisXMapperMethodInspection")
    Double sumByMap(Map map);

    /**
     * 获取所有的订单数量
     * @return
     */
    @Select("select count(*) from orders")
    Integer getTotal();

    /**
     * 获取所有的完成的订单数量
     * @param completed
     * @return
     */
    @Select("select count(*) from orders where status=#{completed}")
    Integer getValid(Integer completed);

    /**
     * 获取当天的所有订单数量
     * @param min
     * @param max
     * @return
     */
    @Select("select count(*) from orders where order_time between #{min} and #{max}")
    Integer getDayTotal(LocalDateTime min, LocalDateTime max);

    /**
     * 获取当天实际完成的订单数量
     * @param min
     * @param max
     * @param completed
     * @return
     */
    @Select("select count(*) from orders where order_time between #{min} and #{max} and status=#{completed}")
    Integer getDayValid(LocalDateTime min, LocalDateTime max, Integer completed);

    /**
     * 统计指定时间区间内的销量Top前十
     * @param begin
     * @param end
     * @return
     */
    List<GoodsSalesDTO> getSalesTop(LocalDateTime begin, LocalDateTime end);

    @Select("select sum(amount) from orders where status=#{completed}")
    Double getSumAmount(Integer completed);

    @Select("select count(*) from orders where status=#{status}")
    Integer getByStatus(Integer status);
}
