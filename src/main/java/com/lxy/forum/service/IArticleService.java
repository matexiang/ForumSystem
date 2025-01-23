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

    /**
     * 根据帖子id查询记录
     * @param id
     * @return
     */
    Article selectById(Long id);

    /**
     * 编辑帖子
     * @param id
     * @param title
     * @param content
     */
    public void modify (Long id,String title, String content);


    /**
     * 点赞帖子
     * @param id 帖子id
     */
    void thumbsUpById(Long id);


    /**
     * 根据Id删除帖子
     * @param id
     */
    @Transactional //事务
    void deleteById (Long id);


    /**
     * 文章回复数量+1
     * @param id
     */
    @Transactional //事务
    void addOneReplyCountById (Long id);








}
