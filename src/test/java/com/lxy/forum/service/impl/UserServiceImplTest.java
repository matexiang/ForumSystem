package com.lxy.forum.service.impl;

import com.lxy.forum.dao.UserMapper;
import com.lxy.forum.model.User;
import com.lxy.forum.service.IUserService;
import com.lxy.forum.utils.MD5Util;
import com.lxy.forum.utils.UUIDUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplTest {

    @Resource
    private IUserService userService;

    @Transactional
    @Test
    void createNormalUser() {

        User user = new User();

        user.setUsername("1");
        user.setNickname("1");
        String password = "1";
        String salt = UUIDUtil.UUID32();
        String cip = MD5Util.md5Salt(password,salt);
        user.setPassword(cip);

        user.setSalt(salt);

        userService.createNormalUser(user);


    }

    @Test
    void selectByUserName() {

        User user = userService.selectByUserName("李向阳");
        System.out.println(user);

    }

    @Test
    void login() {
        User login = userService.login("测", "1");
        System.out.println(login);

    }

    @Test
    void selectById() {
        User user = userService.selectById(1l);
        System.out.println(user);

    }
}