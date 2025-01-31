package com.lxy.forum.service;


import com.lxy.forum.model.Message;


public interface IMessageService {

    /**
     * 发送站内信
     * @param message
     */
    void create (Message message);
}
