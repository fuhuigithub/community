package com.nowcoder.community.dao;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    User selectById(int id);

    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    User selectByName(String username);

    /**
     * 根据邮箱查询用户
     * @param email
     * @return
     */
    User selectByEmail(String email);

    /**
     * 添加用户
     * @param user
     * @return
     */
    int insertUser(User user);

    /**
     * 修改用户状态
     * @param id
     * @param status
     * @return
     */
    int updateStatus(int id, int status);

    /**
     * 修改用户头像
     * @param id
     * @param headerUrl
     * @return
     */
    int updateHeader(int id, String headerUrl);

    /**
     * 修改密码
     * @param id
     * @param password
     * @return
     */
    int updatePassword(int id, String password);

}
