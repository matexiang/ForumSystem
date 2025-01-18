package com.lxy.forum.service;

import com.lxy.forum.model.Article;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IArticleService {

    //发布的帖子
    @Transactional //给这个方法添加上事务,被事务管理起来
    void create (Article article);


    /**
     * 查询所有帖子列表
     * @return
     */
    List<Article> selectAll();


    List<Article> selectAllByBoard(Long boardId);

    /**
     * 根据帖子id查询详情
     * @param id
     * @return
     */
    Article selectDetailById( Long id);




}
