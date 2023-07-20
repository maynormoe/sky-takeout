package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Maynormoe
 */
@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 分页查询
     *
     * @param setmealPageQueryDTO 套餐数据传输对象
     * @return PageResult<SetmealVO>
     */
    @Override
    public PageResult<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        // 分页器
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());

        Page<SetmealVO> setmealPage = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult<SetmealVO>(setmealPage.getTotal(), setmealPage.getResult());
    }

    /**
     * 新增套餐
     *
     * @param setmealDTO 套餐传输对象
     */
    @Override
    @Transactional
    public void saveWithSetmealDishes(SetmealDTO setmealDTO) {
        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();

        if (setmealDishList == null || setmealDishList.size() == 0) {
            throw new RuntimeException("套餐下菜品不能为空");
        }
        // 保存套餐基本信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insert(setmeal);
        Long setmealId = setmeal.getId();
        // 保存套餐菜品信息
        setmealDishList = setmealDishList.stream().map(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
            return setmealDish;
        }).collect(Collectors.toList());

        setmealDishMapper.insertBatch(setmealDishList);

    }

    /**
     * 启用禁用套餐
     *
     * @param status 状态
     * @param id     id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder().id(id).status(status).build();
        setmealMapper.update(setmeal);
    }

    /**
     * '
     * 删除套餐
     *
     * @param ids id数组
     */
    @Override
    public void deleteWithSetmealDish(Long[] ids) {
        // 查询套餐是否启用 启用套餐不可删除
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.selectById(id);
            if (setmeal.getStatus() == 1) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        // 删除套餐基本信息
        for (Long id : ids) {
            setmealMapper.deleteById(id);
            // 删除套餐关联菜品信息
            setmealDishMapper.deleteBySetmealId(id);
        }
    }

    /**
     * 根据主键查询套餐信息
     *
     * @param id id
     * @return SetmealVO
     */
    @Override
    public SetmealVO getById(Long id) {
        // 查询套餐基本信息
        Setmeal setmeal = setmealMapper.selectById(id);

        // 查询套餐关联菜品信息
        List<SetmealDish> setmealDishList = setmealDishMapper.selectBySetmealId(id);

        // 封装要返回的数据
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishList);
        return setmealVO;
    }

    /**
     * 根据主键查询菜品信息
     *
     * @param setmealDTO 菜品传输数据
     */
    @Transactional
    @Override
    public void updateWithSetmealDish(SetmealDTO setmealDTO) {
        // 更新套餐基本信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);
        // 更新套餐菜品信息
        // 删除原来套餐菜品信息
        setmealDishMapper.deleteBySetmealId(setmealDTO.getId());
        // 添加新的套餐菜品信息
        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();
        if (setmealDishList != null && setmealDishList.size() != 0) {
            setmealDishList.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealDTO.getId());
            });
            setmealDishMapper.insertBatch(setmealDishList);
        }

    }
}
