package com.lxy.forum.service;

import com.lxy.forum.model.User;
import org.springframework.stereotype.Service;

public interface IUserService {

    //创建一个用户
    void createNormalUser(User user);

    //根据用户名查询用户信息
    User selectByUserName(String username);

    //处理用户登录
    User login (String username, String password);

    //根据id查询用户信息
    User selectById(Long id);

    //更新当前用户发帖数 +1
    void addOneArticleCountById(Long id);


    /**
     * 用户发帖数-1
     * @param id
     */
    void subOneArticleCountById (Long id);

    /**
     * 修改个人信息
     * @param user
     */
    void modifyInfo(User user);

    /**
     * 修改密码
     * @param id
     * @param newPassword
     * @param oldPassword
     */
    void modifyPassword(Long id,String newPassword, String oldPassword);


}



