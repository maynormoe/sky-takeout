package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;


/**
 * @author Maynormoe
 */
@Service
public class UserServiceImpl implements UserService {

    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";


    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    /**
     * ]
     * 微信登录
     *
     * @param userLoginDTO 用户数据传输对象
     * @return User
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        String openId = getOpenId(userLoginDTO.getCode());
        // 判断openId是否为空
        if (openId == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        // 判断当前用户是否为新用户
        User user = userMapper.getByOpenid(openId);

        // 判断用户是否为新用户
        if (Objects.isNull(user)) {
            user = User.builder().openid(openId).createTime(LocalDateTime.now()).build();
            // 如果是新用户，自动完成注册
            userMapper.insert(user);
        }


        // 返回这个用户对象

        return user;
    }

    /**
     * 获取openId
     *
     * @param code code
     * @return String
     */
    private String getOpenId(String code) {
        // 调用微信接口服务。 获取当前用户openId
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("appid", weChatProperties.getAppid());
        hashMap.put("secret", weChatProperties.getSecret());
        hashMap.put("js_code", code);
        hashMap.put("grant_type", "authorization_code");

        String json = HttpClientUtil.doGet(WX_LOGIN, hashMap);

        JSONObject jsonObject = JSON.parseObject(json);
        String openId = jsonObject.getString("openid");
        return openId;
    }
}
