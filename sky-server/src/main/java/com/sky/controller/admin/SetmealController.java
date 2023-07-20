package com.sky.controller.admin;


import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author Maynormoe
 */
@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController {


    @Autowired
    private SetmealService setmealService;


    /**
     * 套餐数据分页查询
     *
     * @param setmealPageQueryDTO 套餐分页数据传输对象
     * @return Result<PageResult < SetmealVO>>
     */
    @GetMapping("/page")
    @ApiOperation("套餐数据分页查询")
    public Result<PageResult<SetmealVO>> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("分页查询: {}", setmealPageQueryDTO);
        PageResult<SetmealVO> pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 新增套餐
     *
     * @return Result<String>
     */
    @PostMapping
    @ApiOperation("新增套餐")
    public Result<String> save(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐: {}", setmealDTO);
        setmealService.saveWithSetmealDishes(setmealDTO);
        return Result.success();
    }


    /**
     * 新增套餐
     *
     * @param status 状态
     * @param id     id
     * @return Result<String>
     */
    @PostMapping("/status/{status}")
    @ApiOperation("修改套餐状态")
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        log.info("{}id为{}的套餐", status == 1 ? "启用" : "禁用", id);
        setmealService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 删除套餐
     *
     * @param ids id数组
     * @return Result<String>
     */
    @DeleteMapping
    @ApiOperation("删除套餐")
    public Result<String> delete(Long[] ids) {
        log.info("删除id为{}的套餐数据", Arrays.toString(ids));
        setmealService.deleteWithSetmealDish(ids);
        return Result.success();
    }


    /**
     * 根据主键查询套餐
     *
     * @return Result<SetmealVO>
     */
    @GetMapping("/{id}")
    @ApiOperation("根据主键查询套餐")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    /**
     * 修改套餐
     *
     * @param setmealDTO 套餐数据传输对象
     * @return Result<String>
     */
    @PutMapping
    @ApiOperation("修改套餐")
    public Result<String> update(@RequestBody SetmealDTO setmealDTO) {
        setmealService.updateWithSetmealDish(setmealDTO);
        return Result.success();
    }

}
