package com.sky.mapper;


import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Maynormoe
 */
@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     *
     * @param orders 订单
     */
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     *
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     *
     * @param orders
     */
    void update(Orders orders);

    /**
     * 分页查询订单数据
     *
     * @param ordersPageQueryDTO 订单分页数据传输对象
     * @return Page<OrderVO>
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);


    /**
     * 动态条件查询订单数据
     *
     * @param ordersBuild 订单
     * @return Orders
     */
    List<Orders> list(Orders ordersBuild);

    /**
     * 根据id查询订单数据
     *
     * @param id id
     * @return Orders
     */
    @Select("select id, number, status, user_id, address_book_id, order_time, checkout_time, pay_method, pay_status, amount, remark, phone, address, user_name, consignee, cancel_reason, rejection_reason, cancel_time, estimated_delivery_time, delivery_status, delivery_time, pack_amount, tableware_number, tableware_status from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 根据订单状态查询订单数量
     *
     * @param status 状态
     * @return Integer
     */
    Integer countStatus(Integer status);
}
