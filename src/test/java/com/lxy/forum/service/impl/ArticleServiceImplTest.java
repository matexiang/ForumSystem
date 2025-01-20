package com.lxy.forum.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxy.forum.model.Article;
import com.lxy.forum.service.IArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ArticleServiceImplTest {
    @Autowired
    private IArticleService articleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create() {

        Article article = new Article();

        article.setUserId(1L);
        article.setBoardId(1L);
        article.setTitle("2025新年快乐");
        article.setContent("新年快乐吖");
        articleService.create(article);
        System.out.println("发帖成功!");

    }

    @Test
    void selectAll() throws JsonProcessingException {

        //调用service
        List<Article> articles = articleService.selectAll();
        //转换成json字符串
        System.out.println(objectMapper.writeValueAsString(articles));
    }

    @Test
    void selectDetailById() throws JsonProcessingException {

        Article article = articleService.selectDetailById(1l);
        System.out.println(objectMapper.writeValueAsString(article));
    }

    @Test
    void modify() {

        articleService.modify(1l,"牛马","测试内容修改");
        System.out.println("更新成功!!!");

    }

    @Test
    void selectById() throws JsonProcessingException {
        Article article = articleService.selectById(1l);
        System.out.println(objectMapper.writeValueAsString(article));
    }

    @Test
    void thumbsUpById() {
        articleService.thumbsUpById(2l);
        System.out.println("点赞成功!!!");

    }
}