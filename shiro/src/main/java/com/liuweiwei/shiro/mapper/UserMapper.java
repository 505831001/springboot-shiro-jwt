package com.liuweiwei.shiro.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * Created with IntelliJ IDEA
 *
 * @author liuweiwei 505831001@qq.com
 * @since 2018-04-06
 */
@Mapper
public interface UserMapper {
    /**
     * 获得密码
     *
     * @param username 用户名
     */
    String getPassword(String username);

    /**
     * 获得角色权限
     *
     * @param username 用户名
     * @return user/admin
     */
    String getRole(String username);
}
