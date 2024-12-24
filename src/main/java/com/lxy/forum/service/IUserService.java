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

    User selectById(Long id);
}
