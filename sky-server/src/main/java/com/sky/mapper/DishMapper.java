package com.sky.mapper;


import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Maynormoe
 */
@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品菜品数据
     *
     * @param categoryId id
     * @return Integer
     */
    @Select("SELECT count(id) from dish " +
            "where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO dish传输对象
     * @return Page<DishVO>
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);


    /**
     * 根据主键查询菜品
     *
     * @param id id
     * @return Dish
     */
    @Select("select id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user from dish where id = #{id}")
    Dish getById(Long id);

    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    /**
     * 修改菜品数据
     *
     * @param dish 菜品数据
     */
    void update(Dish dish);


    /**
     * 动态查询菜品数据
     *
     * @param dish 菜品
     * @return List<Dish>
     */
    List<Dish> list(Dish dish);

    /**
     * 通过套餐id查询菜品数据
     *
     * @param setmealId id
     * @return List<Dish>
     */
    List<Dish> getBySetmealId(Long setmealId);
}
