package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrderQueryDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrdersListVO;
import com.sky.websocket.WebSocketServer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatPayUtil weChatPayUtil;

    @Autowired
    private WebSocketServer webSocketServer;
    /**
     * 用户下单
     * @param submitDTO
     * @return
     */
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO submitDTO) {

        //处理业务异常
        //地址簿为空
        AddressBook addressBook=addressBookMapper.getById(submitDTO.getAddressBookId());
        if(addressBook==null){
            //抛出业务异常
            throw  new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //购物车为空
        Long UserId= BaseContext.getCurrentId();
        ShoppingCart shoppingCart=new ShoppingCart();
        shoppingCart.setUserId(UserId);
        List<ShoppingCart> shoppingCartList=shoppingCartMapper.list(shoppingCart);
        if(shoppingCartList==null||shoppingCartList.size()==0){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //向订单表中插入一条数据
        Orders orders=new Orders();
        BeanUtils.copyProperties(submitDTO,orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);//设置为待付款
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(UserId);
        orders.setAddress(addressBook.getProvinceName()+addressBook.getCityName()
                +addressBook.getDistrictName()+addressBook.getDetail());
        orderMapper.insert(orders);
        //向订单明细表中插入n条数据orderMapper.insert(orders);
        for(ShoppingCart cart:shoppingCartList){
            OrderDetail orderDetail=new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailMapper.insert(orderDetail);
        }

        //清空当前用户购物车
        shoppingCartMapper.clean(UserId);
        //封装vo返回结果
        OrderSubmitVO orderSubmitVO=OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();
        return orderSubmitVO;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getByOpenId(String.valueOf(userId));

//        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
        JSONObject jsonObject=new JSONObject();

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));
        paySuccess(ordersPaymentDTO.getOrderNumber());
        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

        //通过websocket向客户端浏览器推送消息 type orderID content
        Map map=new HashMap<>();
        map.put("type",1);//type 1 表示来单提醒 2 表示客户催单
        map.put("orderID",ordersDB.getId());
        map.put("content","订单号："+outTradeNo);
        //将map转为json

        String json=JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);
    }

    /**
     * 订单查询
     * @param orderQueryDTO
     * @return
     */
    @Override
    public PageResult PageQuery(OrderQueryDTO orderQueryDTO) {
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        BeanUtils.copyProperties(orderQueryDTO, ordersPageQueryDTO);
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());

        // 启用分页
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        // 执行查询，返回分页结果
        List<Orders> ordersList = orderMapper.PageQuery(ordersPageQueryDTO);

        // 检查 ordersList 是否为 Page 类型
        if (!(ordersList instanceof Page)) {
            throw new RuntimeException("分页插件未正确生效");
        }

        Page<Orders> page = (Page<Orders>) ordersList;

        // 转换为 OrdersListVO
        List<OrdersListVO> ordersListVOS = new ArrayList<>();
        for (Orders order : page.getResult()) {
            OrdersListVO ordersListVO = new OrdersListVO();
            BeanUtils.copyProperties(order, ordersListVO);
            ordersListVOS.add(ordersListVO);
        }

        // 查询订单详情
        for (OrdersListVO list : ordersListVOS) {
            List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(list.getId());
            list.setOrderDetailList(orderDetails);
        }

        // 创建 PageResult
        PageResult pageResult = new PageResult(page.getTotal(),ordersListVOS);
        return pageResult;
    }
    @Override
    public void cancel(Long id) throws Exception {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(id);

        // 校验订单是否存在
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        if (ordersDB.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());

        // 订单处于待接单状态下取消，需要进行退款
        if (ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            //调用微信支付退款接口
            weChatPayUtil.refund(
                    ordersDB.getNumber(), //商户订单号
                    ordersDB.getNumber(), //商户退款单号
                    new BigDecimal(0.01),//退款金额，单位 元
                    new BigDecimal(0.01));//原订单金额

            //支付状态修改为 退款
            orders.setPayStatus(Orders.REFUND);
        }

        // 更新订单状态、取消原因、取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    @Override
    public void repetition(Long id) {
    //查询出订单详情
        List<OrderDetail> orderDetails=orderDetailMapper.getByOrderId(id);
        List<ShoppingCart> shoppingCartList=new ArrayList<>();
        for (OrderDetail orderDetail:
             orderDetails) {
            ShoppingCart shoppingCart=new ShoppingCart();
            BeanUtils.copyProperties(orderDetail,shoppingCart);
            shoppingCart.setId(null);
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartList.add(shoppingCart);
        }

        shoppingCartMapper.clean(BaseContext.getCurrentId());
        for (ShoppingCart shop:
             shoppingCartList) {
            shoppingCartMapper.insert(shop);

        }
    }

    /**
     * 订单详情
     * @param id
     * @return
     */
    @Override
    public OrdersListVO list(Long id) {
        Orders orders=orderMapper.getById(id);
        List<OrderDetail> orderDetails=orderDetailMapper.getByOrderId(id);
        OrdersListVO ordersListVO=new OrdersListVO();
        BeanUtils.copyProperties(orders,ordersListVO);
        ordersListVO.setOrderDetailList(orderDetails);
        return ordersListVO;
    }
}
