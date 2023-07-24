package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


/**
 * @author Maynormoe
 */
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {


    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 从购物车中移除商品
     *
     * @param shoppingCartDTO 购物车传输对象
     */
    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        // 获取用户userId
        Long userId = BaseContext.getCurrentId();
        // 判断移除的商品类型
        if (shoppingCartDTO.getDishId() != null) {
            // 查询菜品在购物车中的份数
            ShoppingCart shoppingCart = ShoppingCart.builder()
                    .userId(userId)
                    .dishId(shoppingCartDTO.getDishId())
                    .dishFlavor(shoppingCartDTO.getDishFlavor())
                    .build();
            List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
            // 如果菜品没有查到
            if (shoppingCartList == null || shoppingCartList.size() == 0) {
                throw new RuntimeException("购物车中没有此菜品");
            }
            // 如果菜品数量为1
            ShoppingCart shoppingCartData = shoppingCartList.get(0);
            if (shoppingCartData.getNumber() == 1) {
                shoppingCartMapper.deleteById(shoppingCartData.getId());
            } else {
                // 如果菜品数量一个以上，修改份数
                shoppingCartData.setNumber(shoppingCartData.getNumber() - 1);
                shoppingCartMapper.updateNumberById(shoppingCartData);
            }
        } else {
            // 查询套餐在购物车中的份数
            ShoppingCart shoppingCart = ShoppingCart.builder()
                    .userId(userId)
                    .dishId(shoppingCartDTO.getSetmealId())
                    .build();
            List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
            // 如果套餐没有查到
            if (shoppingCartList == null || shoppingCartList.size() == 0) {
                throw new RuntimeException("购物车中没有此套餐");
            }
            // 如果套餐数量为1
            ShoppingCart shoppingCartData = shoppingCartList.get(0);
            if (shoppingCartData.getNumber() == 1) {
                shoppingCartMapper.deleteById(shoppingCartData.getId());
            } else {
                // 如果菜品数量一个以上，修改份数
                shoppingCartData.setNumber(shoppingCartData.getNumber() - 1);
                shoppingCartMapper.updateNumberById(shoppingCartData);
            }

        }
    }

    /**
     * 将商品添加到购物车
     *
     * @param shoppingCartDTO 购物车数据传输对象
     */
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        // 判断加入购物车的商品是否存在
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        // 如果存在了，将数量加一
        if (list != null && list.size() > 0) {
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(cart);
        } else {
            // 如果不存在, 需要插入一条购物车数据
            Long dishId = shoppingCartDTO.getDishId();
            Long setmealId = shoppingCartDTO.getSetmealId();

            if (dishId != null) {
                // 本次添加到购物车的是菜品
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            } else {
                // 本次添加的购物车对象是套餐
                Setmeal setmeal = setmealMapper.selectById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 查看购物车
     *
     * @return List<ShoppingCart>
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(userId).build();
        return shoppingCartMapper.list(shoppingCart);
    }

    /**
     * 清空购物车
     */
    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }
}
