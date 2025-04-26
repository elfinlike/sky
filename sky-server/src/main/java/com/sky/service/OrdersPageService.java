package com.sky.service;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;

public interface OrdersPageService {
    PageResult PageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO getStatistics();

    void setConfirm(Long id);

    void setDelivery(Long id);

    void setComplete(Long id);

    void setRejection(OrdersRejectionDTO ordersRejectionDTO);

    OrderVO detail(Long id);
}
