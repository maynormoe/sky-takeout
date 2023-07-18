package com.sky.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
}
