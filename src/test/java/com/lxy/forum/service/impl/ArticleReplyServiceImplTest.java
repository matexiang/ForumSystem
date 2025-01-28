package com.lxy.forum.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxy.forum.model.ArticleReply;
import com.lxy.forum.service.IArticleReplyService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArticleReplyServiceImplTest {

    @Autowired
    private IArticleReplyService articleReplyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void selectByArticleId() throws JsonProcessingException {
        List<ArticleReply> articleReplies = articleReplyService.selectByArticleId(1l);
        System.out.println(objectMapper.writeValueAsString(articleReplies));


    }
}