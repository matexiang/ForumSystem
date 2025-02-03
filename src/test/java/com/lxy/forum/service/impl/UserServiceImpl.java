package com.lxy.forum.service.impl;

import com.lxy.forum.common.AppResult;
import com.lxy.forum.common.ResultCode;
import com.lxy.forum.dao.UserMapper;
import com.lxy.forum.exception.ApplicationException;
import com.lxy.forum.model.User;
import com.lxy.forum.service.IUserService;
import com.lxy.forum.utils.MD5Util;
import com.lxy.forum.utils.StringUtil;
import com.lxy.forum.utils.UUIDUtil;
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
    public void subOneArticleCountById(Long id) {

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
        updateUser.setArticleCount(user.getArticleCount()-1);

        //判断减一后是否小于0
        if(updateUser.getArticleCount() < 0){
            updateUser.setArticleCount(0);
        }

        //更新数据库
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if(row != 1){
            log.warn(ResultCode.FAILED.toString()+ " , 受影响的行数不等于 1");

            throw  new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }


    }


    @Override
    public void modifyInfo(User user) {

        //非空校验
        if(user == null || user.getId() <= 0 || user.getId() == null){

            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());

            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));

        }
        //校验用户是否存在
        User existsUser = userMapper.selectByPrimaryKey(user.getId());
        if (existsUser == null) {

            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());

            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));

        }
        //3.定义一个唯一性的校验
        boolean checkAttr = false;//false表示没有通过校验

        //4. 定义一个专门用来更新的对象,防止用户传入的User的对象设置了其他属性
        //当使用动态SQL进行更新的时候,覆盖了没有经过校验的字段
        User updateUser = new User();
        //5.设置用户id
        updateUser.setId(user.getId());
        //6. 对每一个参数进行校验并赋值
        if(!StringUtil.isEmpty(user.getUsername())
        || !existsUser.getUsername().equals(user.getUsername())){
            //需要用户更新用户名时,进行唯一的校验
            User checkUser = userMapper.selectByUserName(user.getUsername());
            if(checkUser != null){
                //用户已存在
                log.warn(ResultCode.FAILED_USER_EXISTS.toString());

                //抛出异常
                throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_EXISTS));

            }
            //数据库中没有找到响应用户,表示可以修改用户名
            updateUser.setUsername(user.getUsername());
            //更新标志位
            checkAttr = true;

        }
        //7.校验昵称
        if(!StringUtil.isEmpty(user.getNickname())
        && !existsUser.getNickname().equals(user.getNickname())){
            //设置昵称
            updateUser.setNickname(user.getNickname());
            //更新标志位
            checkAttr = true;

        }
        //8.校验性别
        if(user.getGender() != null && existsUser.getGender() != user.getGender()){
            //设置性别
            updateUser.setGender(user.getGender());
            if (updateUser.getGender() > 2 || updateUser.getGender() < 0) {
                updateUser.setGender((byte)2);
            }
            //更新标志位
            checkAttr = true;

        }
        //9.校验邮箱
        if(!StringUtil.isEmpty(user.getEmail())
        && !user.getEmail().equals(existsUser.getEmail())){
            //设置邮箱
            updateUser.setEmail(user.getEmail());
            //更新标志位
            checkAttr = true;

        }
        //9.校验电话号码
        if(!StringUtil.isEmpty(user.getPhoneNum())
                && !user.getPhoneNum().equals(existsUser.getPhoneNum())){
            //设置电话号码
            updateUser.setPhoneNum(user.getPhoneNum());
            //更新标志位
            checkAttr = true;

        }
        //10.校验个人简介
        if(!StringUtil.isEmpty(user.getRemark())
                && !user.getRemark().equals(existsUser.getRemark())){
            //设置电话号码
            updateUser.setRemark(user.getRemark());
            //更新标志位
            checkAttr = true;

        }
        //11,根据标志位来决定是否可以执行更新
        if (checkAttr == false) {

            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());

            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //12,调用DAO
        int row = userMapper.updateByPrimaryKeySelective(updateUser);

        if (row != 1) {
            log.warn(ResultCode.FAILED.toString()+", 受影响行数不等于1");

            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }


    }

    @Override
    public void modifyPassword(Long id, String newPassword, String oldPassword) {
        if(id == null || id <= 0 || StringUtil.isEmpty(newPassword )
        || StringUtil.isEmpty(oldPassword)){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());

            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));

        }
        //查询要修改的用户信息
        User user = userMapper.selectByPrimaryKey(id);
        //校验用户是否存在
        if (user == null || user.getDeleteState() == 1) {

            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());

            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }
        //校验老密码是否正确
        //对老密码进行加密,获取密文
        String oldEncryptPassword = MD5Util.md5Salt(oldPassword,user.getSalt());
        //与用户当前密码进行比较
        if(!oldEncryptPassword.equalsIgnoreCase(user.getPassword())){
            log.warn(ResultCode.FAILED.toString()+"原密码错误!");

            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }

        //生成一个新的盐
        String salt = UUIDUtil.UUID32();

        //生成新密码的密文
        String encryptPassword = MD5Util.md5Salt(newPassword,salt);

        //构造要更新的对象
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setSalt(salt);
        updateUser.setPassword(encryptPassword);
        Date date = new Date();
        updateUser.setUpdateTime(date);

        //调用dao
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if (row != 1) {
            log.warn(ResultCode.FAILED.toString());

            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }


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
