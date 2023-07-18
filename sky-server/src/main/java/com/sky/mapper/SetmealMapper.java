package com.sky.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author Maynormoe
 */
@Mapper
public interface SetmealMapper {

    @Select("SELECT count(id) from setmeal " +
            "where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);
}
