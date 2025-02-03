package com.lxy.forum.service;


import com.lxy.forum.model.Message;

import java.util.List;


public interface IMessageService {

    /**
     * 发送站内信
     * @param message
     */
    void create (Message message);


    /**
     * 根据用户id查询该用户未读数量
     * @param receiveUserId
     * @return
     */
    Integer selectUnreadCount( Long receiveUserId);


    /**
     * 根据接受者用户Id查询所有站内信
     * @param receiveUserId
     * @return
     */
    List<Message> selectByReceiveUserId (Long receiveUserId);

    /**
     * 更新指定站内信的状态
     * @param id
     * @param state
     */
    void updateStateById (Long id , Byte state);

    /**
     * 根据id查询站内信
     * @param id
     * @return
     */
    Message selectById (Long id);

    /**
     * 要回复对的站内信id
     * @param repliedId
     * @param message
     */

    void reply (Long repliedId, Message message);



}
