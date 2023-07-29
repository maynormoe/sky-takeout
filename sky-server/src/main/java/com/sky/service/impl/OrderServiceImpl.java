package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.*;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Maynormoe
 */

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {


    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatPayUtil weChatPayUtil;

    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 提交订单
     *
     * @param ordersSubmitDTO 订单提交数据传输对象
     * @return OrderSubmitVO
     */
    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        // 处理各种异常（地址薄为空，购物车数据为空）
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        // 查询当前用户购物车数据
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);

        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        // 向订单表插入一条数据
        Orders orders = new Orders();
        // 向订单明细表插入数据
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(orders.getPhone());
        orders.setAddress(orders.getAddress());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(BaseContext.getCurrentId());
        orderMapper.insert(orders);
        ArrayList<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);
        // 清空当前用户的购物车数据
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
        // 封装vo
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        String orderNumber = ordersPaymentDTO.getOrderNumber();

        //调用微信支付接口，生成预支付交易单
        /*JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal("0.01"), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );*/
        JSONObject jsonObject = new JSONObject();

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        paySuccess(orderNumber);

        Orders orders = orderMapper.getByNumber(orderNumber);

        // 通过websocket向客户端推送消息
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", 1);
        map.put("orderId", orders.getId());
        map.put("content", "订单号：" + orderNumber);

        String jsonString = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(jsonString);
        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    @Override
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

    }

    /**
     * 查询历史订单
     *
     * @param ordersPageQueryDTO 订单传输对象
     * @return PageResult<OrderVO>
     */
    @Override
    public PageResult<OrderVO> page(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        // 获取用户id
        Long userId = BaseContext.getCurrentId();
        ordersPageQueryDTO.setUserId(userId);
        Page<Orders> orderPage = orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> orderVOList = new ArrayList<>();

        // 查询订单明细， 封装到orderVo进行响应
        if (orderPage != null && orderPage.getTotal() > 0) {
            for (Orders orders : orderPage) {
                Long ordersId = orders.getId();

                // 查询订单明细
                List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(ordersId);
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetailList);
                orderVOList.add(orderVO);
            }
        }
        return new PageResult<>(orderPage.getTotal(), orderVOList);
    }

    /**
     * 根据id查询订单详情
     *
     * @param id 订单id
     * @return OrderVO
     */
    @Override
    public OrderVO getOrderDetailById(Long id) {

        Long userId = BaseContext.getCurrentId();
        // 调用mapper查询订单
        Orders orders = orderMapper.getById(id);
        if (!Objects.equals(orders.getUserId(), userId)) {
            return new OrderVO();
        }
        // 查询订单详情数据，封装返回
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        // 查询订单明细数据
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());
        orderVO.setOrderDetailList(orderDetailList);

        return orderVO;
    }

    /**
     * 根据主键取消订单
     *
     * @param id id
     */
    @Override
    public void cancelOrders(Long id) throws Exception {
        // 根据用户id查询订单
        Orders ordersDB = orderMapper.getById(id);

        // 效验订单是否存在
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        if (ordersDB.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = Orders.builder().id(ordersDB.getId()).build();
        // 订单处于接单状态下取消。需要进行退款
        if (Objects.equals(ordersDB.getStatus(), Orders.CONFIRMED)) {
            // 调用微信支付接口
            weChatPayUtil.refund(
                    ordersDB.getNumber(), ordersDB.getNumber(),
                    new BigDecimal("0.01"), new BigDecimal("0.01")
            );

            // 将支付状态改为退款
            orders.setPayStatus(Orders.REFUND);
        }

        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 再来一单
     *
     * @param id id
     */
    @Override
    public void repetition(Long id) {
        // 获取用户id
        Long userId = BaseContext.getCurrentId();
        // 根据订单id查询当前订单
        Orders orders = orderMapper.getById(id);
        // 判断订单是否存在
        if (orders != null) {
            // 如果订单存在 如果不是用户的订单
            if (!Objects.equals(orders.getUserId(), userId)) {
                throw new OrderBusinessException("不是该用户的订单");
            } else {
                // 通过订单id 获取订单详请
                List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
                // 将订单信息加入购物车中
                ShoppingCart shoppingCart = new ShoppingCart();
                orderDetailList.forEach(orderDetail -> {
                    BeanUtils.copyProperties(orderDetail, shoppingCart);
                    shoppingCart.setUserId(userId);
                    shoppingCartMapper.insert(shoppingCart);
                });
            }
        } else {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
    }

    /**
     * 搜索
     *
     * @param ordersPageQueryDTO 订单分页传输对象
     * @return PageResult<OrderVO>
     */
    @Override
    public PageResult<OrderVO> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        // 分页构造
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        // 动态查询订单数据
        Page<Orders> orders = orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> orderVOList = new ArrayList<>();

        orderVOList = orders.getResult().stream().map(order -> {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);
            // 根据订单id查询订单明细
            List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(order.getId());
            String orderDishes = orderDetailList.stream().map(OrderDetail::getName).collect(Collectors.toList()).toString();
            orderVO.setOrderDishes(orderDishes);
            return orderVO;
        }).collect(Collectors.toList());
        return new PageResult<>(orders.getTotal(), orderVOList);
    }

    /**
     * 统计订单在各状态下的数量
     *
     * @return OrderStatisticsVO
     */
    @Override
    public OrderStatisticsVO statistics() {
        // 根据状态查询订单数量
        Integer confirmed = orderMapper.countStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);
        Integer toBeConfirmed = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
        // 封装Vo
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        return orderStatisticsVO;
    }

    /**
     * 详情
     *
     * @param id id
     * @return OrderDetail
     */
    @Override
    public OrderVO detail(Long id) {
        // 根据订单id查询订单基本信息
        Orders orders = orderMapper.getById(id);

        // 根据订单id查询订单详情信息
        List<OrderDetail> detailList = orderDetailMapper.getByOrderId(id);

        // 封装订单包含菜品信息
        String orderDishes = detailList.stream().map(OrderDetail::getName).collect(Collectors.toList()).toString();

        // 封装vO
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDishes(orderDishes);
        orderVO.setOrderDetailList(detailList);
        return orderVO;
    }

    /**
     * 接单
     *
     * @param id id
     */
    @Override
    public void confirm(Long id) {
        //  根据id查询订单数据
        Orders orders = orderMapper.getById(id);
        // 检查订单是否存在
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        // 订单存在 修改订单状态
        Orders ordersBuilder = Orders.builder().id(id).status(Orders.CONFIRMED).build();
        orderMapper.update(ordersBuilder);
    }

    /**
     * 拒单
     *
     * @param ordersRejectionDTO ordersRejectionDTO
     */
    @Override
    public void reject(OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        // 根据订单id查询订单
        Long orderId = ordersRejectionDTO.getId();
        Orders orders = orderMapper.getById(orderId);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 订单只有存在且状态为2（待接单）才可以拒单
        if (!Objects.equals(orders.getStatus(), Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        // 订单原因不存在
        if (ordersRejectionDTO.getRejectionReason() == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 判断支付状态
        Integer payStatus = orders.getPayStatus();
        if (Objects.equals(payStatus, Orders.PAID)) {
            //用户已支付，需要退款
//            String refund = weChatPayUtil.refund(
//                    orders.getNumber(),
//                    orders.getNumber(),
//                    new BigDecimal(0.01),
//                    new BigDecimal(0.01));
            log.info("申请退款");
        }
        // 订单存在, 更改订单状态 拒单需要退款
        Orders ordersBuilder = Orders.builder()
                .id(orderId)
                .status(Orders.CANCELLED)
                .cancelReason(ordersRejectionDTO.getRejectionReason())
                .cancelTime(LocalDateTime.now())
                .build();
        orderMapper.update(ordersBuilder);
    }

    /**
     * 取消订单
     *
     * @param ordersCancelDTO ordersCancelDTO
     */
    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(ordersCancelDTO.getId());

        //支付状态
        Integer payStatus = ordersDB.getPayStatus();
        if (payStatus == 1) {
            //用户已支付，需要退款
//            String refund = weChatPayUtil.refund(
//                    ordersDB.getNumber(),
//                    ordersDB.getNumber(),
//                    new BigDecimal(0.01),
//                    new BigDecimal(0.01));
            log.info("申请退款");
        }

        // 管理端取消订单需要退款，根据订单id更新订单状态、取消原因、取消时间
        Orders orders = new Orders();
        orders.setId(ordersCancelDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 派送
     *
     * @param id id
     */
    @Override
    public void delivery(Long id) {
        // 根据id查询订单
        Orders orders = orderMapper.getById(id);

        // 订单存在 而且状态已接单
        if (orders != null && !Objects.equals(orders.getStatus(), Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        // 修改订单状态
        Orders ordersBuilder = Orders.builder().id(id).status(Orders.DELIVERY_IN_PROGRESS).build();
        orderMapper.update(ordersBuilder);
    }

    /**
     * 完成订单
     *
     * @param id id
     */
    @Override
    public void complete(Long id) {
        // 根据id查询订单信息
        Orders orders = orderMapper.getById(id);
        // 如果订单为空 或者状态为接单之前
        if (orders == null || !Objects.equals(orders.getStatus(), Orders.DELIVERY_IN_PROGRESS) || Objects.equals(orders.getPayStatus(), Orders.UN_PAID)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        // 修改订单状态
        Orders ordersBuild = Orders.builder()
                .id(id)
                .status(Orders.COMPLETED)
                .deliveryTime(LocalDateTime.now())
                .build();
        orderMapper.update(ordersBuild);
    }

    /**
     * 客户催单
     *
     * @param id id
     */
    @Override
    public void reminder(Long id) {
        // 根据id查询订单
        Orders orders = orderMapper.getById(id);
        if (Objects.isNull(orders)) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("type", 2);
        map.put("orderId", id);
        map.put("content", "客户催单" + orders.getNumber());

        String jsonString = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(jsonString);

    }
}
