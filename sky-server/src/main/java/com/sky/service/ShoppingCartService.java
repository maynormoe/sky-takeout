package com.sky.service;


import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

/**
 * @author Maynormoe
 */
public interface ShoppingCartService {

    /**
     * 从购物车中移除商品
     *
     * @param shoppingCartDTO 购物车传输对象
     */
    void sub(ShoppingCartDTO shoppingCartDTO);

    /**
     * 将商品添加到购物车
     *
     * @param shoppingCartDTO 购物车数据传输对象
     */
    void add(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看购物车
     *
     * @return List<ShoppingCart>
     */
    List<ShoppingCart> showShoppingCart();

    /**
     * 清空购物车
     */
    void clean();
}
