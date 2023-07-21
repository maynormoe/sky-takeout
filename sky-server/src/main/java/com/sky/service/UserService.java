package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

/**
 * @author Maynormoe
 */
public interface UserService {
    /**]
     * 微信登录
      * @param userLoginDTO 用户数据传输对象
     * @return User
     */
    User wxLogin(UserLoginDTO userLoginDTO);
}
