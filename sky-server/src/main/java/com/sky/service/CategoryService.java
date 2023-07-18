package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

/**
 * @author Maynormoe
 */
public interface CategoryService {

    /**
     * 分页查询分类数据
     *
     * @param categoryPageQueryDTO 分类
     * @return Page<Category>
     */
    PageResult<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据类型查询分类对象
     * @param categoryDTO 分类数据传输对象
     * @return List<Category>
     */
    List<Category> list(CategoryDTO categoryDTO);

    /**
     * 新增分类数据
     * @param category 分类
     */
    void insert(Category category);


    /**
     * 根据主键修改分类状态
     * @param status 状态
     * @param id id
     */
    void startOrStop(Integer status, Long id);

    /**
     *  
      * @param categoryDTO 分类传输对象
     */
    void update(CategoryDTO categoryDTO);

    /**
     *
     * 根据主键删除分类数据
      * @param id id
     */
    void removeById(Long id);
}
