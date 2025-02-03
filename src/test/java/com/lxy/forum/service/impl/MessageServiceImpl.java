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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class MessageServiceImpl implements IMessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private IUserService userService;

    @Override
    public Integer selectUnreadCount(Long receiveUserId) {

        //非空校验
        if(receiveUserId == null || receiveUserId <= 0){

            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());

            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        //直接调用dao
        Integer count = messageMapper.selectUnreadCount(receiveUserId);
        //正常查询是不可能出现null,如果为null跑出异常
        if (count == null) {

            log.warn(ResultCode.ERROR_SERVICES.toString());

            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
        return count;
    }

    @Override
    public List<Message> selectByReceiveUserId(Long receiveUserId) {

        //非空校验
        if(receiveUserId == null || receiveUserId <= 0){

            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());

            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        List<Message> messages = messageMapper.selectByReceiveUserId(receiveUserId);

        return messages;
    }

    @Override
    public Message selectById(Long id) {

        if(id == null || id <= 0 ){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());

            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));

        }

        Message message = messageMapper.selectByPrimaryKey(id);

        return message;
    }



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
    @Override
    public void updateStateById(Long id, Byte state) {
        //非空校验
        // state : 0 未读 1 已读 2 已回复
        if(id == null || id <= 0 || state < 0 || state > 2 ){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));

        }
        //构造更新的对象
        Message updateMessage = new Message();
        updateMessage.setId(id);
        updateMessage.setState(state);
        Date date = new Date();
        updateMessage.setUpdateTime(date);
        //调用dao
        int row = messageMapper.updateByPrimaryKeySelective(updateMessage);
        if (row != 1) {

            log.warn(ResultCode.ERROR_SERVICES.toString());

            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));

        }


    }

    @Override
    public void reply(Long repliedId, Message message) {

        if(repliedId == null || repliedId <= 0  ){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));

        }

        //校验repliedId站内信状态
        Message existsMessage = messageMapper.selectByPrimaryKey(repliedId);
        if (existsMessage == null || existsMessage.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));


        }
        updateStateById(repliedId,(byte)2);
        create(message);


    }


}
