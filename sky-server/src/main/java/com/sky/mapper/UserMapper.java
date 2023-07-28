package com.sky.mapper;


import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author Maynormoe
 */
@Mapper
public interface UserMapper {
    /**
     * 通过openId获取用户
     *
     * @param openId openId
     * @return User
     */
    @Select("select id, openid, name, phone, sex, id_number, avatar, create_time from user where openid = #{openId}")
    User getByOpenid(String openId);

    /**
     * 添加用户
     *
     * @param user 用户
     */
    void insert(User user);

    /**
     * 根据id查询用户
     *
     * @param userId 用户id
     * @return
     */
    @Select("select id, openid, name, phone, sex, id_number, avatar, create_time from user where id = #{userId}")
    User getById(Long userId);
}
