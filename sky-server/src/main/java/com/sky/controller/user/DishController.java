package com.sky.controller.user;


import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Maynormoe
 */
@RestController("DishUserController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "用户端菜品接口")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {
        // 构造redis key
        String key = "dish_" + categoryId;

        // 查询redis是否存在菜品数据
        List<DishVO> dishVOList = (List<DishVO>) redisTemplate.opsForValue().get(key);
        // 如果存在， 直接返回
        if (dishVOList != null && dishVOList.size() > 0) {
            return Result.success(dishVOList);
        }
        // 不存在，查询数据库保存在缓存中
        Dish dish = Dish.builder().categoryId(categoryId).status(StatusConstant.ENABLE).build();
        List<DishVO> dishList = dishService.listWithFlavor(dish);
        redisTemplate.opsForValue().set(key, dishList);
        return Result.success(dishList);
    }
}
