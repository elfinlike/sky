package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface OrdersPageMapper {


    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 返回待接单数量
     * @return
     */
    @Select("select count(*) from orders where status=2")
    int getToBeConfirm();

    /**
     * 返回待派送数量
     * @return
     */
    @Select("select count(*) from orders where status=3")
    int getConfirmed();

    /**
     * 返回派送中的数量
     * @return
     */
    @Select("select count(*) from orders where status=4")
    int getDeliveryInProgress();

    @Update("update orders set status=#{status} where id=#{id}")
    void setStatus(Long id, int status);

    /**
     * 拒单
     * @param ordersRejectionDTO 传入的需要进行修改的数据实体
     */

    @Update("update orders set status=#{status},rejection_reason=#{rejectionReason},cancel_time=#{cancelTime} where id=#{id}")
    void setRejection(Orders ordersRejectionDTO);

    /**
     * 修改订单为派送中状态
     * @param id id名
     * @param status 状态
     * @param now 配送状态/
     */
    @Update("update orders set status=#{status},delivery_status=#{now} where id=#{id}")
    void setDelivery(Long id, int status, Integer now);

    /**
     * 修改为订单完成状态
     */
    @Update("update orders set status=#{status},delivery_time=#{deliveryTime} where id=#{id}")
    void setComplete(Orders orders);
}
