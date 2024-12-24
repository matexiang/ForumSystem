package com.lxy.forum.controller;

import com.lxy.forum.common.AppResult;
import com.lxy.forum.common.ResultCode;
import com.lxy.forum.config.AppConfig;
import com.lxy.forum.model.User;
import com.lxy.forum.service.IUserService;
import com.lxy.forum.utils.MD5Util;
import com.lxy.forum.utils.UUIDUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.http.HttpRequest;

@Api(tags = "用户接口")
@Slf4j
@RequestMapping("/user")
@RestController
public class UserController {

    @Autowired
    private IUserService userService;
    //用户注册
    @ApiOperation("用户注册")
    @RequestMapping("/register")
    public AppResult register (@ApiParam("用户名") @RequestParam("username") @NonNull String username,
                               @ApiParam("昵称") @RequestParam("nickname") @NonNull String nickname,
                               @ApiParam("密码") @RequestParam("password") @NonNull String password,
                               @ApiParam("确认密码") @RequestParam("passwordRepeat") @NonNull String passwordRepeat){

        //校验重复密码是否相同
        if(!password.equals(passwordRepeat)){
            log.warn(ResultCode.FAILED_TWO_PWD_NOT_S.toString());

            //返回错误信息
            return AppResult.failed(ResultCode.FAILED_TWO_PWD_NOT_S);
        }

        //准备数据
        User user = new User();
        user.setUsername(username);
        user.setNickname(nickname);
        //处理密码
        //1.生成盐
        String salt = UUIDUtil.UUID32();

        String finallypassword = MD5Util.md5Salt(password, salt);

        user.setPassword(finallypassword);
        user.setSalt(salt);

        userService.createNormalUser(user);

        return AppResult.success();
    }

    //用户登录

    @ApiOperation("用户登录")
    @RequestMapping("/login")
    public AppResult login (HttpServletRequest request,
                            @ApiParam("用户名") @RequestParam("username") @NonNull String username,
                            @ApiParam("密码") @RequestParam("password") @NonNull String password){

        //1.调用Service中的登录方法,返回User对象
        User user = userService.login(username, password);
        if(user == null){
            log.warn(ResultCode.FAILED_LOGIN.toString());
            return AppResult.failed(ResultCode.FAILED_LOGIN);
        }
        //2. 如果登录成功把User对象设置到Session作用中
        HttpSession session = request.getSession(true);
        session.setAttribute(AppConfig.USER_SESSION,user);
        return AppResult.success();
    }

    @ApiOperation("获取用户信息")
    @RequestMapping("info")
    public AppResult<User> getUserInfo(HttpServletRequest request,Long id){


        return null;
    }
}
