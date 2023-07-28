package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.OrderDetail;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

import java.util.List;

/**
 * @author Maynormoe
 */
public interface OrderService {
    /**
     * 提交订单
     *
     * @param ordersSubmitDTO 订单提交数据传输对象
     * @return OrderSubmitVO
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 支付
     *
     * @param ordersPaymentDTO 订单支付数据传输对象
     * @return OrderPaymentVO
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 查询历史订单
     *
     * @param ordersPageQueryDTO 订单传输对象
     * @return PageResult<OrderVO>
     */
    PageResult<OrderVO> page(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单详情
     *
     * @param id 订单id
     * @return OrderVO
     */
    OrderVO getOrderDetailById(Long id);

    /**
     * 根据主键取消订单
     *
     * @param id id
     */
    void cancelOrders(Long id) throws Exception;

    /**
     * 再来一单
     *
     * @param id id
     */
    void repetition(Long id);

    /**
     * 搜索
     *
     * @param ordersPageQueryDTO 订单分页传输对象
     * @return PageResult<OrderVO>
     */
    PageResult<OrderVO> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 统计订单在各状态下的数量
     *
     * @return OrderStatisticsVO
     */
    OrderStatisticsVO statistics();

    /**
     * 详情
     *
     * @param id id
     * @return OrderDetail
     */
    OrderVO detail(Long id);

    /**
     * 接单
     *
     * @param id id
     */
    void confirm(Long id);

    /**
     * 拒单
     *
     * @param ordersRejectionDTO ordersRejectionDTO
     */
    void reject(OrdersRejectionDTO ordersRejectionDTO) throws Exception;

    /**
     * 取消订单
     *
     * @param ordersCancelDTO ordersCancelDTO
     */
    void cancel(OrdersCancelDTO ordersCancelDTO);

    /**
     * 派送
     *
     * @param id id
     */
    void delivery(Long id);

    /**
     * 完成订单
     *
     * @param id id
     */
    void complete(Long id);
}
