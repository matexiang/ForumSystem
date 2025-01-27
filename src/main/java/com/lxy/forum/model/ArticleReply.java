package com.lxy.forum.model;

import lombok.Data;

import java.util.Date;
@Data
public class ArticleReply {

    //编号
    private Long id;

    //帖子id,关联article
    private Long articleId;

    //回复用户的编号
    private Long postUserId;

    //忽略,需求中楼中楼功能
    private Long replyId;

    //忽略,需求中楼中楼功能
    private Long replyUserId;

    //回复的`正文
    private String content;

    //忽略,需求中点赞功能
    private Integer likeCount;

    //状态 0正常, 1禁用
    private Byte state;

    //状态 0正常, 1删除
    private Byte deleteState;

    //创建时间
    private Date createTime;

    //更新时间
    private Date updateTime;

    //关联对象--回复发布者
    private User user;


}