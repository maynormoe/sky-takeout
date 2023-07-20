package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @author Maynormoe
 */
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags = "管理端商铺相关接口")
public class ShopController {


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 设置店铺营业状态
     *
     * @param status 状态
     * @return Result<String>
     */
    @PutMapping("/{status}")
    @ApiOperation("设置店铺营业状态")
    public Result<String> setStatus(@PathVariable Integer status) {
        log.info("设置营业状态{}", status == 1 ? "营业中" : "打烊");
        redisTemplate.opsForValue().set("SHOP_STATUS", status);
        log.info("设置成功");
        return Result.success();
    }


    /**
     * 查看店铺状态
     *
     * @return Result<Integer>
     */
    @GetMapping("/status")
    @ApiOperation("查看店铺状态")
    public Result<Integer> getStatus() {
        log.info("查看店铺状态");
        Integer shopStatus = (Integer) redisTemplate.opsForValue().get("SHOP_STATUS");
        return Result.success(shopStatus);
    }
}
