package com.sky.controller.user;

import com.sky.dto.CategoryDTO;
import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Maynormoe
 */

@RestController("CategoryUserController")
@RequestMapping("/user/category")
@Slf4j
@Api(tags = "C端分类相关接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据动态条件查询分类
     *
     * @param type 类型
     * @return Result<CategoryDTO>
     */
    @GetMapping("/list")
    @ApiOperation("根据动态条件查询分类")
    public Result<List<Category>> list(Integer type) {
        CategoryDTO categoryDTO = CategoryDTO.builder().type(type).build();
        List<Category> categoryList = categoryService.list(categoryDTO);
        return Result.success(categoryList);
    }
}
