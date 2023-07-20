package com.sky.service.impl;

import com.sky.mapper.SetmealDishMapper;
import com.sky.service.SetmealDishService;
import com.sky.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Maynormoe
 */

@Service
public class SetmealDishServiceImpl implements SetmealDishService {

    @Autowired
    private SetmealDishMapper setmealDishMapper;

}
