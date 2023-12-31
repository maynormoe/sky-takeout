package com.sky.mapper;


import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author Maynormoe
 */
@Mapper
public interface ShoppingCartMapper {

    /**
     * 动态条件查询购物车
     *
     * @param shoppingCart 购物车
     * @return List<ShoppingCart>
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);


    /**
     * 根据id更新商品数量
     *
     * @param shoppingCart 购物车实体
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    /**
     * 插入购物车数据
     *
     * @param shoppingCart 购物车数据
     */
    @Insert("insert into shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, amount, create_time) " +
            "VALUES " +
            "(#{name}, #{image}, #{userId},#{dishId},#{setmealId},#{dishFlavor},#{amount},#{createTime})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 根据用户id删除购物车数据
     *
     * @param userId 用户id
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);

    @Delete("delete from shopping_cart where id = #{shoppingCartId}")
    void deleteById(Long shoppingCartId);
}
