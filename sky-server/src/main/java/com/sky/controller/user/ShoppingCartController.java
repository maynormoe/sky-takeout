package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Maynormoe
 */
@RestController
@Slf4j
@RequestMapping("/user/shoppingCart")
@Api(tags = "用户端购物车接口")
public class ShoppingCartController {


    @Autowired
    private ShoppingCartService shoppingCartService;


    /**
     * @param shoppingCartDTO 购物车
     * @return Result<String>
     */
    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public Result<String> add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加购物车，商品信息为{}", shoppingCartDTO);
        shoppingCartService.add(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 查看购物车
     *
     * @return Result<List < ShoppingCart>>
     */
    @GetMapping("/list")
    @ApiOperation("查看购物车")
    public Result<List<ShoppingCart>> list() {
        log.info("查看购物车");
        List<ShoppingCart> shoppingCartList = shoppingCartService.showShoppingCart();
        return Result.success(shoppingCartList);
    }

    /**
     *  清空购物车
      * @return Result<String>
     */
    @DeleteMapping("/clean")
    @ApiOperation("清空购物车")
    public Result<String> clean() {
        shoppingCartService.clean();
        return Result.success();
    }

    /**
     *  从购物车里移除商品
      * @return Result<String>
     */
    @PostMapping("/sub")
    @ApiOperation("从购物车里移除商品")
    public Result<String> sub(ShoppingCartDTO shoppingCartDTO) {
        log.info("从购物车里移除商品");
        shoppingCartService.sub(shoppingCartDTO);
        return Result.success();
    }
}
