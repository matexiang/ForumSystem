package com.lxy.forum.service.impl;

import com.lxy.forum.common.AppResult;
import com.lxy.forum.common.ResultCode;
import com.lxy.forum.dao.MessageMapper;
import com.lxy.forum.exception.ApplicationException;
import com.lxy.forum.model.Message;
import com.lxy.forum.model.User;
import com.lxy.forum.service.IMessageService;
import com.lxy.forum.service.IUserService;
import com.lxy.forum.utils.StringUtil;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class MessageImpl implements IMessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private IUserService userService;

    @Override
    public void create(Message message) {
        //非空校验
        if(message == null || message.getPostUserId() == null ||
        message.getReceiveUserId() == null || StringUtil.isEmpty(message.getContent())){

                log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());

                //抛出异常
                throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));

        }

        //校验接受者是否存在
        User user = userService.selectById(message.getReceiveUserId());
        if (user == null || user.getDeleteState() == 1) {
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));

        }

        //设置默认值
        message.setState((byte)0);//表示未读
        message.setDeleteState((byte)0);//表示未删除

        //设置创建时间与更新时间
        Date date = new Date();

        message.setCreateTime(date);
        message.setUpdateTime(date);

        //调用dao
        int row = messageMapper.insertSelective(message);
        if (row != 1) {

            log.warn(ResultCode.FAILED_CREATE.toString());

            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));

        }

    }
}
