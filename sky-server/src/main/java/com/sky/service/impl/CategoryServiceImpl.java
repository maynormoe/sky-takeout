package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author Maynormoe
 */
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {


    @Autowired
    private CategoryMapper categoryMapper;


    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 分页查询分类数据
     *
     * @param categoryPageQueryDTO 分类
     * @return Page<Category>
     */
    @Override
    public PageResult<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        // 构造分页
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());

        Page<Category> categoryPage = categoryMapper.pageQuery(categoryPageQueryDTO);

        List<Category> result = categoryPage.getResult();
        long pageTotal = categoryPage.getTotal();


        return new PageResult<Category>(pageTotal, result);
    }

    /**
     * 根据类型查询分类对象
     *
     * @param categoryDTO 分类数据传输对象
     * @return List<Category>
     */
    @Override
    public List<Category> list(CategoryDTO categoryDTO) {
        List<Category> categoryList = categoryMapper.selectList(categoryDTO);
        return categoryList;
    }

    /**
     * 新增分类数据
     *
     * @param category 分类
     */
    @Override
    public void insert(Category category) {
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateUser(BaseContext.getCurrentId());
        category.setStatus(0);
        categoryMapper.insert(category);
    }

    /**
     * 根据主键修改分类状态
     *
     * @param status 状态
     * @param id     id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        categoryMapper.update(category);
    }

    /**
     * @param categoryDTO 分类传输对象
     */
    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setUpdateTime(LocalDateTime.now());
        category.setCreateTime(LocalDateTime.now());
        categoryMapper.update(category);
    }

    /**
     * 根据主键删除分类数据
     *
     * @param id id
     */
    @Override
    public void removeById(Long id) {
        // 查询分类id数据是否存在
        Category category = categoryMapper.selectById(id);
        if (Objects.isNull(category)) {
            throw new RuntimeException("id相关数据不存在");
        }
        // 查询分类相关数据
        // 查询分类相关菜品数据
        Integer dishCount = dishMapper.countByCategoryId(id);
        // 查询分类相关套餐数据
        Integer setmealCount = setmealMapper.countByCategoryId(id);
        if (dishCount > 0) {
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        if (setmealCount > 0) {
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        // 查询分类是否为启用状态
        if (category.getStatus() == 1) {
            throw new DeletionNotAllowedException("分类启用状态不能删除");
        }
        // 删除
        categoryMapper.deleteById(id);
    }
}