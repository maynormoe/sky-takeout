package com.sky.mapper;


import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Maynormoe
 */
@Mapper
public interface OrderDetailMapper {
    /**
     * 批量插入订单明细
     *
     * @param orderDetailList 订单明细列表
     */
    void insertBatch(List<OrderDetail> orderDetailList);

    /**
     * 根据订单id返回订单明细
     *
     * @param ordersId 订单id
     * @return List<OrderDetail>
     */
    @Select("select id, name, image, order_id, dish_id, setmeal_id, dish_flavor, number, amount from order_detail where order_id = #{ordersId}")
    List<OrderDetail> getByOrderId(Long ordersId);
}
