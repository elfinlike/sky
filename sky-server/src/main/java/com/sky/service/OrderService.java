package com.sky.service;

import com.sky.dto.OrderQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrdersListVO;

public interface OrderService {
    OrderSubmitVO submitOrder(OrdersSubmitDTO submitDTO);/**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);


    PageResult PageQuery(OrderQueryDTO orderQueryDTO);

    void cancel(Long id) throws Exception;

    void repetition(Long id);

    OrdersListVO list(Long id);
}
