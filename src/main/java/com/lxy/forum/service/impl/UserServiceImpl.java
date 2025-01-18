package com.lxy.forum.service.impl;

import com.lxy.forum.common.AppResult;
import com.lxy.forum.common.ResultCode;
import com.lxy.forum.dao.UserMapper;
import com.lxy.forum.exception.ApplicationException;
import com.lxy.forum.model.User;
import com.lxy.forum.service.IUserService;
import com.lxy.forum.utils.MD5Util;
import com.lxy.forum.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void createNormalUser(User user) {
        //非空校验, user==null 检查这个变量是否没有指向任何对象
        if(user == null || StringUtil.isEmpty(user.getUsername())
        || StringUtil.isEmpty(user.getNickname()) || StringUtil.isEmpty(user.getPassword())
        || StringUtil.isEmpty(user.getSalt())){
            //如果满足一个条件说明,注册出错,打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());

            // 抛出异常，统一抛出ApplicationException
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));

        }

        User existsUser = userMapper.selectByUserName(user.getUsername());

        //判断用户是否存在
        if(existsUser != null){
            //打印日志
            log.info(ResultCode.FAILED_USER_EXISTS.toString());
            //抛异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_EXISTS));

        }


        //新增用户,设置默认值

        user.setGender((byte)2);
        user.setArticleCount(0);
        user.setIsAdmin((byte)0);
        user.setDeleteState((byte)0);
        user.setState((byte)0);
        //当前日期
        Date date = new Date();
        user.setCreateTime(date);
        user.setUpdateTime(date);

        //写入数据
        int row = userMapper.insertSelective(user);
        if (row != 1) {

            //打印日志
            log.info(ResultCode.FAILED_CREATE.toString());

            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));

        }
        //打印日志
        log.info("新增用户成功: username = "+ user.getUsername());



    }

    @Override
    public User selectByUserName(String username) {

        if(StringUtil.isEmpty(username)){

            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //返回结果
        return userMapper.selectByUserName(username);
    }

    @Override
    public User login(String username, String password) {
        if(StringUtil.isEmpty(username) || StringUtil.isEmpty(password) ){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());

            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));

        }

        User user = selectByUserName(username);

        //查询结果作非空校验
        if(user == null){
            log.warn(ResultCode.FAILED_LOGIN.toString());

            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));

        }
        //对密码校验
        String s = MD5Util.md5Salt(password, user.getSalt());
        //用密文和数据库中存在的用户密码比较
        if(!s.equalsIgnoreCase(user.getPassword())){
            log.warn("密码错误");

            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));

        }
        //登录成攻
        return user;
    }

    @Override
    public User selectById(Long id) {
        //非空校验
        if(id == null){
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());

        //抛出异常
        throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));
        }

        User user = userMapper.selectByPrimaryKey(id);

        return user;
    }

    @Override
    public void addOneArticleCountById(Long id) {
        if(id == null || id <= 0){

            log.warn(ResultCode.FAILED_BOARD_ARTICLE_COUNT.toString());

            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_ARTICLE_COUNT));

        }
        //查询用户信息
        User user = userMapper.selectByPrimaryKey(id);
        if(user == null){
            log.warn(ResultCode.ERROR_IS_NULL.toString()+ ", user id = " + id);

            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));

        }
        //更新用户发帖数量
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setArticleCount(user.getArticleCount()+1);
        //更新数据库
        int row = userMapper.updateByPrimaryKeySelective(updateUser);

        if(row != 1){
            log.warn(ResultCode.FAILED.toString()+", 受影响行数不等于1");

            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }


    }
}
