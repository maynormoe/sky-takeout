package com.sky.controller.user;


import com.sky.constant.StatusConstant;
import com.sky.entity.Setmeal;
import com.sky.mapper.SetmealMapper;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Maynormoe
 */
@RestController("SetmealUserController")
@RequestMapping("/user/setmeal")
@Slf4j
@Api(tags = "用户端套餐接口")
public class SetmealController {


    @Autowired
    private SetmealService setmealService;

    /**
     * 条件查询
     *
     * @param categoryId 分类id
     * @return Result<List <SetmealVO>>
     */
    @GetMapping("/list")
    public Result<List<SetmealVO>> list(Long categoryId) {
        Setmeal setmeal = Setmeal.builder().categoryId(categoryId).status(StatusConstant.ENABLE).build();
        List<SetmealVO> setmealVOList = setmealService.list(setmeal);
        return Result.success(setmealVOList);
    }

    /**
     * 根据套餐id查询包含的菜品列表
     *
     * @param id id
     * @return Result<List<DishItemVO>>
     */
    @GetMapping("/dish/{id}")
    @ApiOperation("根据套餐id查询包含的菜品列表")
    public Result<List<DishItemVO>> dishList(@PathVariable("id") Long id) {
        List<DishItemVO> list = setmealService.getDishItemById(id);
        return Result.success(list);
    }

}
