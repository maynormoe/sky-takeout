package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

/**
 * @author Maynormoe
 */
public interface SetmealService {
    /**
     *  分页查询
      * @param setmealPageQueryDTO 分页数据车传输对象
     *  @return PageResult<SetmealVO>
     */
    PageResult<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     *  新增套餐
      * @param setmealDTO 套餐传输对象
     */
    void saveWithSetmealDishes(SetmealDTO setmealDTO);

    /**
     * 启用禁用套餐
      * @param status 状态
     * @param id id
     */
    void startOrStop(Integer status, Long id);

    /**'
     * 删除套餐
      * @param ids id数组
     */
    void deleteWithSetmealDish(Long[] ids);

    /**
     * 根据主键查询套餐信息
      * @param id id
     *  @return SetmealVO
     */
    SetmealVO getById(Long id);

    /**
     * 根据主键查询菜品信息
      * @param setmealDTO 菜品传输数据
     */
    void updateWithSetmealDish(SetmealDTO setmealDTO);

    /**
     * 动态查询套餐数据
      * @param setmeal 套餐
     *  @return List<SetmealVO>
     */
    List<SetmealVO> list(Setmeal setmeal);

    /**
     * 通过套餐id查询菜品
      * @param setmealId 套餐id
     *  @return List<DishItemVO>
     */
    List<DishItemVO> getDishItemById(Long setmealId);
}
