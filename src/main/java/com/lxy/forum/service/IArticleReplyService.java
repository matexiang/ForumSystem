package com.lxy.forum.service;

import com.lxy.forum.model.ArticleReply;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IArticleReplyService {

    @Transactional
    void create (ArticleReply articleReply);

    /*
     * 根据帖子id查询所有回复
     *articleId
     *  */
    List<ArticleReply> selectByArticleId ( Long articleId);



}
