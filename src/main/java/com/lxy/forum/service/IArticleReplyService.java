package com.lxy.forum.service;

import com.lxy.forum.model.ArticleReply;
import org.springframework.transaction.annotation.Transactional;

public interface IArticleReplyService {

    @Transactional
    void create (ArticleReply articleReply);


}
