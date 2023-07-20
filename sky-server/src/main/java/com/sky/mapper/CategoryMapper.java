package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Maynormoe
 */

@Mapper
public interface CategoryMapper {
    /**
     * '
     *
     * @param categoryPageQueryDTO 分类数据传输对象
     * @return Page<Category>
     */
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据类型查询分类数据
     *
     * @return List<Category>
     */
    List<Category> selectList(CategoryDTO categoryDTO);

    @Insert("INSERT INTO category " +
            "(id, type, name, sort, status, create_time, update_time, create_user, update_user) " +
            "VALUES (null, #{type},#{name},#{sort},#{status},#{ createTime},#{updateTime},#{createUser},#{ updateUser})")
    @AutoFill(OperationType.INSERT)
    void insert(Category category);

    /**
     * 根据主键更新分类数据
     *
     * @param category 分类
     */
    @AutoFill(OperationType.UPDATE)
    void update(Category category);

    /**
     * 根据id查询分类数据
     *
     * @param id id
     * @return Category
     */
    @Select("SELECT id, type, name, sort, status, create_time, update_time, create_user, update_user from category " +
            "where id = #{id} " +
            "order by sort asc, update_time desc")
    Category selectById(Long id);

    /**
     * 根据主键删除分类数据
     *
     * @param id id
     */
    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);
}
