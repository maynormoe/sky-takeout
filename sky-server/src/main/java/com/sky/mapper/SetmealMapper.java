package com.sky.mapper;


import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Maynormoe
 */
@Mapper
public interface SetmealMapper {

    @Select("SELECT count(id) from setmeal " +
            "where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 分页查询
     *
     * @return Page<SetmealVO>
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 保存套餐基本信息
     *
     * @param setmeal
     */
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 更新套餐数据
     *
     * @param setmeal 套餐
     */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 根据主键查询套餐基本信息
     *
     * @param id id
     * @return Setmeal
     */
    @Select("select id, category_id, name, price, status, description, image, create_time, update_time, create_user, update_user from setmeal where id = #{id}")
    Setmeal selectById(Long id);


    /**
     * 根据id删除套餐数据
     *
     * @param id id
     */
    @Delete("delete from setmeal where id = #{id}")
    void deleteById(Long id);

    /**
     * 动态查询套餐查询
     *
     * @param setmeal 套餐
     * @return List<SetmealVO>
     */
    List<SetmealVO> list(Setmeal setmeal);

    /**
     *  根据 套餐id查询菜品
      * @param setmealId 套餐id
     *  @return List<DishItemVO>
     */
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);
}
