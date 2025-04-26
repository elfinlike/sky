package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.xiaoymin.knife4j.core.util.CollectionUtils;
import com.sky.constant.MessageConstant;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.OrdersPageMapper;
import com.sky.result.PageResult;
import com.sky.service.OrdersPageService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import com.sky.vo.OrdersListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrdersPageServiceImpl implements OrdersPageService {

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private OrdersPageMapper ordersPageMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private WeChatPayUtil weChatPayUtil;
    public PageResult PageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        Page<Orders> page = ordersPageMapper.pageQuery(ordersPageQueryDTO);

        // 部分订单状态，需要额外返回订单菜品信息，将Orders转化为OrderVO
        List<OrderVO> orderVOList = getOrderVOList(page);

        return new PageResult(page.getTotal(), orderVOList);
    }
    private List<OrderVO> getOrderVOList(Page<Orders> page) {
        // 需要返回订单菜品信息，自定义OrderVO响应结果
        List<OrderVO> orderVOList = new ArrayList<>();

        List<Orders> ordersList = page.getResult();
        if (!CollectionUtils.isEmpty(ordersList)) {
            for (Orders orders : ordersList) {
                // 将共同字段复制到OrderVO
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                String orderDishes = getOrderDishesStr(orders);

                // 将订单菜品信息封装到orderVO中，并添加到orderVOList
                orderVO.setOrderDishes(orderDishes);
                orderVOList.add(orderVO);
            }
        }
        return orderVOList;
    }

    /**
     * 根据订单id获取菜品信息字符串
     *
     * @param orders
     * @return
     */
    private String getOrderDishesStr(Orders orders) {
        // 查询订单菜品详情信息（订单中的菜品和数量）
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

        // 将每一条订单菜品信息拼接为字符串（格式：宫保鸡丁*3；）
        List<String> orderDishList = orderDetailList.stream().map(x -> {
            String orderDish = x.getName() + "*" + x.getNumber() + ";";
            return orderDish;
        }).collect(Collectors.toList());

        // 将该订单对应的所有菜品信息拼接在一起
        return String.join("", orderDishList);
    }


    /**
     * 返回各个状态的订单数量 待派送数量、派送中数量、待接单数量
     * @return
     */
    @Override
    public OrderStatisticsVO getStatistics() {
        OrderStatisticsVO orderStatisticsVO=new OrderStatisticsVO();
        int toBeConfirmed=ordersPageMapper.getToBeConfirm();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        int confirmed=ordersPageMapper.getConfirmed();
        orderStatisticsVO.setConfirmed(confirmed);
        int deliveryInProgress=ordersPageMapper.getDeliveryInProgress();
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        return orderStatisticsVO;
    }

    /**
     * 修改为已接单
     * @param id
     */
    @Override
    public void setConfirm(Long id) {
        //先检查是否有对应id的订单
        Orders orders=orderMapper.getById(id);
        if(orders==null||!orders.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        int status=Orders.CONFIRMED;
        ordersPageMapper.setStatus(id,status);
    }

    /**
     * 修改为配送中
     * @param id
     */
    @Override
    public void setDelivery(Long id) {
        Orders orders=orderMapper.getById(id);
        if(orders==null||!orders.getStatus().equals(Orders.CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        int status=Orders.DELIVERY_IN_PROGRESS;
        ordersPageMapper.setDelivery(id,status,Orders.DELIVERY_IN_PROGRESS);
    }

    /**
     * 完成订单
     * @param id
     */
    @Override
    public void setComplete(Long id) {
        Orders orders=Orders.builder()
                .id(id)
                .status(Orders.COMPLETED)
                .deliveryTime(LocalDateTime.now())
                .build();

        ordersPageMapper.setComplete(orders);
    }

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    @Override
    public void setRejection(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orders=orderMapper.getById(ordersRejectionDTO.getId());
        //只有存在的订单，并且状态为待接单状态才能被拒单
        if(orders==null||!orders.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        if(orders.getPayStatus()==Orders.PAID){
            //用户已经付款了，需要对支付金额进行退款
            String refund = null;
            try {
                refund = weChatPayUtil.refund(
                        orders.getNumber(),
                        orders.getNumber(),
                        new BigDecimal(0.01),
                        new BigDecimal(0.01));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            log.info("申请退款：{}", refund);
        }
        Orders orders1=new Orders();
        BeanUtils.copyProperties(ordersRejectionDTO,orders1);
        orders1.setStatus(Orders.CANCELLED);
        orders1.setCancelTime(LocalDateTime.now());

        ordersPageMapper.setRejection(orders1);
    }

    /**
     * 订单详情
     * @param id
     * @return
     */
    @Override
    public OrderVO detail(Long id) {
        Orders orders=orderMapper.getById(id);
        List<OrderDetail> orderDetails=orderDetailMapper.getByOrderId(id);
        OrderVO orderVO=new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(orderDetails);
        return orderVO;
    }
}
