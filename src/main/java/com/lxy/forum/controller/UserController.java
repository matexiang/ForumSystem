package com.lxy.forum.controller;

import com.lxy.forum.common.AppResult;
import com.lxy.forum.common.ResultCode;
import com.lxy.forum.config.AppConfig;
import com.lxy.forum.model.User;
import com.lxy.forum.service.IUserService;
import com.lxy.forum.utils.MD5Util;
import com.lxy.forum.utils.StringUtil;
import com.lxy.forum.utils.UUIDUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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
    @PostMapping("/register")
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
    @PostMapping("/login")
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
    @RequestMapping("/info")
    public AppResult<User> getUserInfo(HttpServletRequest request,
                                       @ApiParam("用户id") @RequestParam(value = "id",required = false) Long id){
        //定义返回的User对象
        User user = new User();
        //1.如果id为空,从session中获取当前登录的用户信息
        if(id == null){
            HttpSession session = request.getSession(false);
            //判断session和用户信息是否有效
            if(session == null || session.getAttribute(AppConfig.USER_SESSION) == null){
                //用户未登录,返回错误信息
                return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
            }
            //从session中获取当前登录的用户信息
            user = (User) session.getAttribute(AppConfig.USER_SESSION);

        }else {
            //2.如果id不为空,从数据库中按id查询用户信息
            user = userService.selectById(id);
        }

        if(user == null){
            return AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS);
        }

        //返回正常结果
        return AppResult.success(user);
    }

    @ApiOperation("退出登录")
    @RequestMapping("/logout")
    public AppResult logout (HttpServletRequest request){
        //获取session对象
        HttpSession session = request.getSession(false);
        //判断session是否有效
        if(session != null){
            //打印日志
            log.info("退出成功");
            //表示用户在登录状态,直接销毁session
            session.invalidate();
        }

        return AppResult.success("退出成功");
    }

    /**
     *
     * @param username
     * @param nickname
     * @param gender
     * @param email
     * @param phoneNum
     * @param remark
     * @return AppResult
     */
    @ApiOperation("修改个人信息")
    @PostMapping("/modifyInfo")
    public AppResult modifyInfo (HttpServletRequest request,
                                 @ApiParam("用户名") @RequestParam(value = "username", required = false) String username,
                                 @ApiParam("昵称") @RequestParam(value = "nickname", required = false) String nickname,
                                 @ApiParam("性别") @RequestParam(value = "gender", required = false) Byte gender,
                                 @ApiParam("邮箱") @RequestParam(value = "email", required = false) String email,
                                 @ApiParam("电话号") @RequestParam(value = "phoneNum", required = false)String phoneNum,
                                 @ApiParam("个人简介") @RequestParam(value = "remark", required = false) String remark){

        if (StringUtil.isEmpty(nickname) &&
                StringUtil.isEmpty(email) &&
                StringUtil.isEmpty(username) &&
                StringUtil.isEmpty(phoneNum) &&
                StringUtil.isEmpty(remark) &&
                gender == null) {
            return AppResult.failed("请输入要修改的内容");
        }
        HttpSession session = request.getSession(false);
        User user = (User)session.getAttribute(AppConfig.USER_SESSION);

        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setUsername(username);
        updateUser.setNickname(nickname);
        updateUser.setGender(gender);
        updateUser.setEmail(email);
        updateUser.setPhoneNum(phoneNum);
        updateUser.setRemark(remark);

        //调用service方法,修改
        userService.modifyInfo(updateUser);

        //修改完后查询
        user = userService.selectById(user.getId());
        //把查询的最新的信息放到session中
        session.setAttribute(AppConfig.USER_SESSION,user);

        return AppResult.success();

    }


}
