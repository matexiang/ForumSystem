package com.lxy.forum.service.impl;

import com.lxy.forum.model.ArticleReply;
import com.lxy.forum.service.IArticleReplyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArticleReplyControllerImplTest {

    @Autowired
    private IArticleReplyService articleReplyService;

    @Test
    void create() {

        //构造一个回复的对象
        ArticleReply articleReply = new ArticleReply();
        articleReply.setArticleId(5l);
        articleReply.setPostUserId(3l);
        articleReply.setContent(" 66666");

        //调用service
        articleReplyService.create(articleReply);
        System.out.println("回复成功!!!");

    }
}