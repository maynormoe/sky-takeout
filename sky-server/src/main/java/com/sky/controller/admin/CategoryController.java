package com.sky.controller.admin;


import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Maynormoe
 */
@RestController("CategoryAdminController")
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类相关接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询分类数据
     *
     * @param categoryPageQueryDTO 分类分页传输对象
     * @return Result<PageResult < Category>>
     */
    @GetMapping("/page")
    @ApiOperation("分页查询分类数据")
    public Result<PageResult<Category>> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分页查询:{}", categoryPageQueryDTO);
        PageResult<Category> categoryPage = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(categoryPage);
    }

    /**
     * 根据类型查询分类数据
     *
     * @param categoryDTO 分类分页传输对象
     * @return Result<List < Category>>
     */
    @GetMapping("list")
    @ApiOperation("根据类型查询分类数据")
    public Result<List<Category>> list(CategoryDTO categoryDTO) {
        log.info("查询type为{}数据", categoryDTO.getType());
        List<Category> categoryList = categoryService.list(categoryDTO);
        return Result.success(categoryList);
    }


    /**
     * 新增分类数据
     *
     * @param category 分类
     * @return Result<Category>
     */
    @PostMapping
    @ApiOperation("新增分类数据")
    public Result<Category> save(@RequestBody Category category) {
        log.info("新增分类数据: {}", category);
        categoryService.insert(category);
        log.info("新增成功");
        return Result.success();
    }

    /**
     * 根据主键修改分类状态
     *
     * @param status 状态
     * @param id     id
     * @return Result<Category>
     */
    @PostMapping("/status/{status}")
    @ApiOperation("修改分类状态")
    public Result<Category> startOrStop(@PathVariable Integer status, Long id) {
        log.info("修改id{}的分类状态", id);
        categoryService.startOrStop(status, id);
        log.info("修改成功");
        return Result.success();
    }


    /**
     * 修改分类数据
     * @param categoryDTO 分类数据传输对象
     * @return Result<Category>
     */
    @PutMapping
    @ApiOperation("修改分类数据")
    public Result<Category> update(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改分类数据");
        categoryService.update(categoryDTO);
        return Result.success();
    }

    /**
     *  删除分类数据
      * @param id id
     *  @return  Result<Category>
     */
    @DeleteMapping
    @ApiOperation("删除分类数据")
    public Result<Category> delete(Long id) {
        log.info("删除id为{}的数据",id);
        categoryService.removeById(id);
        return Result.success();
    }
}
