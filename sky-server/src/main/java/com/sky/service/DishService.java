package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

/**
 * @author Maynormoe
 */
public interface DishService {

    /**
     * 新增菜品
     *
     * @param dishDTO 菜品数据传输对象
     */
    void saveWitWithFlavor(DishDTO dishDTO);

    /**
     * 分页查询
     *
     * @param dishPageQueryDTO 菜品分页传输对象
     * @return PageResult<DishVO>
     */
    PageResult<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 菜品批量删除
     *
     * @param ids id数组
     */
    void deleteBatch(List<Long> ids);

    /**
     * 菜品停售起售
     *
     * @param status 状态
     * @param id     id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据id查询对应的id盒口味数据
     *
     * @param id id
     * @return DishVO
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * 更新菜品数据
     *
     * @param dishDTO 菜品传输对象
     */
    void updateWithFlavor(DishDTO dishDTO);

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId 分类id
     * @return List<DishVO>
     */
    List<Dish> list(Long categoryId);

    /**
     * 根据分类id查询菜品
     *
     * @param dish 菜品
     * @return List<Dish>
     */
    List<DishVO> listWithFlavor(Dish dish);
}
