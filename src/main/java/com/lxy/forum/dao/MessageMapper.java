package com.lxy.forum.dao;

import com.lxy.forum.model.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {
    int insert(Message row);

    int insertSelective(Message row);

    Message selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Message row);

    int updateByPrimaryKey(Message row);

    /**
     * 根据用户id查询该用户未读数量
     * @param receiveUserId
     * @return
     */
    Integer selectUnreadCount( @Param("receiveUserId") Long receiveUserId);

    /**
     * 根据接受者用户Id查询所有站内信
     * @param receiveUserId
     * @return
     */
    List<Message> selectByReceiveUserId ( @Param("receiveUserId") Long receiveUserId);

}