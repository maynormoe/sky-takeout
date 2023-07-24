package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import com.sun.org.glassfish.gmbal.DescriptorFields;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author Maynormoe
 */
@RestController("DishAdminController")
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {


    @Autowired
    private DishService dishService;


    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    /**
     * 新增菜品
     *
     * @param dishDTO dish传输对象
     * @return Result<Dish>
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result<Dish> save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品, {}", dishDTO);
        dishService.saveWitWithFlavor(dishDTO);
        cleanCache("dish_*");
        return Result.success();
    }


    /**
     * 分页查询
     *
     * @param dishPageQueryDTO dish传输数据
     * @return Result<PageResult < DishVO>>
     */
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult<DishVO>> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("分页查询：{}", dishPageQueryDTO);
        PageResult<DishVO> dishPage = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(dishPage);
    }


    /**
     * 菜品起售停售
     *
     * @param status 状态
     * @return Result<String>
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        log.info("改变菜品售卖状态：{}", status);
        dishService.startOrStop(status, id);
        // 将所有的菜品缓存数据清理掉
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 菜品批量删除
     *
     * @param ids id数组
     * @return Result<String>
     */
    @DeleteMapping
    @ApiOperation("删除菜品数据")
    public Result<String> delete(@RequestParam List<Long> ids) {
        log.info("删除id为{}的菜品数据", ids.toString());
        dishService.deleteBatch(ids);

        // 将所有的菜品缓存数据清理掉
        cleanCache("dish_*");
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据id查看菜品:{}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }


    /**
     * 修改菜品
     *
     * @param dishDTO 菜品传输数据
     * @return Result<String>
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result<String> update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品：{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);
        // 将所有的菜品缓存数据清理掉
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId 分类id
     * @return Result<List < DishVO>>
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId) {
        log.info("查询分类id为{}的菜品", categoryId);
        List<Dish> dishList = dishService.list(categoryId);
        return Result.success(dishList);
    }

    private void cleanCache(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null) {
            redisTemplate.delete(keys);
        }
    }
}
