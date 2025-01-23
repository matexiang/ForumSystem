package com.lxy.forum.service.impl;

import com.lxy.forum.common.AppResult;
import com.lxy.forum.common.ResultCode;
import com.lxy.forum.dao.ArticleMapper;
import com.lxy.forum.exception.ApplicationException;
import com.lxy.forum.model.Article;
import com.lxy.forum.model.Board;
import com.lxy.forum.model.User;
import com.lxy.forum.service.IArticleService;
import com.lxy.forum.service.IBoardService;
import com.lxy.forum.service.IUserService;
import com.lxy.forum.utils.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ArticleServiceImpl implements IArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    //用户和板块的操作
    @Autowired
    private IBoardService boardService;

    @Autowired
    private IUserService userService;

    @Override
    public void create(Article article) {

        //非空校验
        if(article == null || article.getUserId() == null
                ||article.getBoardId() == null
                ||StringUtil.isEmpty(article.getTitle())
        || StringUtil.isEmpty(article.getContent())){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));

        }
        //设置默认值
        article.setVisitCount(0); //访问数
        article.setReplyCount(0);
        article.setLikeCount(0);
        article.setDeleteState((byte)0);
        article.setState((byte)0);
        Date date = new Date();
        article.setCreateTime(date);
        article.setUpdateTime(date);

        //写入数据库
        int articleRow = articleMapper.insertSelective(article);

        if (articleRow <= 0){
            log.warn(ResultCode.FAILED_CREATE.toString());

            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));

        }


        //获取用户信息
        User user = userService.selectById(article.getUserId());
        //没有找到指定的用户信息
        if(user == null){
            log.warn(ResultCode.FAILED_CREATE.toString() + ", 发帖失败, user id =" + article.getUserId());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));


        }
        //更新用户发帖数
        userService.addOneArticleCountById(user.getId());

        //获取板块信息
        Board board = boardService.selectById(article.getBoardId());

        //是否在数据库中有对应的板块
        if(board == null){
            log.warn(ResultCode.FAILED_CREATE.toString() + ", 发帖失败, board id =" + article.getBoardId());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));

        }

        //更新板块中的帖子数量
        boardService.addOneArticleCountById(board.getId());

        log.info(ResultCode.SUCCESS.toString()+", user id ="+article.getUserId()+",board id ="
                + article.getBoardId()+ "," +article.getId()+" :发帖成功!");



    }

    @Override
    public List<Article> selectAll() {
        //调用dao
        List<Article> articles = articleMapper.selectAll();

        //返回结果
        return articles;
    }

    @Override
    public List<Article> selectAllByBoard(Long boardId) {
        //非空校验
        if(boardId == null || boardId <= 0){
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());

            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));

        }
        //校验板块是否存在
        Board board = boardService.selectById(boardId);
        if(board == null){
            log.warn(ResultCode.FAILED_BOARD_NOT_EXISTS.toString());
            throw  new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXISTS));
        }
        //调用DAO,查询
        List<Article> articles = articleMapper.selectAllByBoard(boardId);

        return articles;
    }

    @Override
    public Article selectDetailById(Long id) {

        //非空校验
        if(id == null || id <= 0){
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());

            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));

        }
        //调用DAO
        Article article = articleMapper.selectDetailById(id);
        //判断结果是否为空
        if(article == null){
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());

            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));
        }
        //更新帖子访问次数
        Article updateArticle = new Article();
        updateArticle.setId(article.getId());
        updateArticle.setVisitCount(article.getVisitCount()+1);
        //保存数据库
        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);
        if(row != 1){
            log.warn(ResultCode.ERROR_SERVICES.toString());

            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
        //更新返回对象的访问次数
        article.setVisitCount(article.getVisitCount()+1);

        return article;
    }

    @Override
    public Article selectById(Long id) {

        //非空校验
        if(id == null || id <= 0 ){

            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());

            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));

        }
        //调用DAO
        Article article = articleMapper.selectByPrimaryKey(id);

        return article;
    }


    @Override
    public void thumbsUpById(Long id) {

        //非空校验
        if(id == null || id <= 0 ){

            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());

            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));

        }
        //获取帖子详情
        Article article = articleMapper.selectByPrimaryKey(id);
        //帖子不存在
        if (article == null || article.getDeleteState() == 1){
            //打印日志
            log.warn(ResultCode. FAILED_ARTICLE_NOT_EXISTS.toString());

            throw new ApplicationException(AppResult.failed( ResultCode. FAILED_ARTICLE_NOT_EXISTS ));

        }
        //帖子状态异常
        if ( article.getState() == 1 ){
            //打印日志
            log.warn(ResultCode. FAILED_ARTICLE_BANNED.toString());

            throw new ApplicationException(AppResult.failed( ResultCode. FAILED_ARTICLE_BANNED ));

        }
        //构造要更新的对象
        Article updateArticle = new Article();
        updateArticle.setId(article.getId());
        updateArticle.setLikeCount(article.getLikeCount()+1);
        updateArticle.setUpdateTime(new Date());

        //调用DAO
        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);

        if(row != 1){
            log.warn(ResultCode.ERROR_SERVICES.toString());

            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }


    }

    @Override
    public void deleteById(Long id) {
        //非空校验
        if(id == null || id <= 0 ){

            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());

            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));

        }

        //根据帖子Id查询帖子信息
        Article article = articleMapper.selectByPrimaryKey(id);
        if(article == null || article.getDeleteState() == 1){
            log.warn(ResultCode.FAILED_BOARD_NOT_EXISTS.toString() + " ,article id = " + id);

            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXISTS));

        }
        Article updateArticle = new Article();
        updateArticle.setId(article.getId());
        updateArticle.setDeleteState((byte)1);
        //调用DAO
        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);
        if(row != 1){

            log.warn(ResultCode.ERROR_SERVICES.toString());

            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));

        }
        //更新板块中帖子的数量
        boardService.subOneArticleCountById(article.getBoardId());
        //更新用户发帖数
        userService.subOneArticleCountById(article.getUserId());

        log.info("删除帖子成功, article Id = "+article.getId() + ", user id =" + article.getUserId());



    }

    @Override
    public void addOneReplyCountById(Long id) {

        //非空校验
        if(id == null || id <= 0 ){

            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());

            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));

        }

        //获取帖子记录
        Article article = articleMapper.selectByPrimaryKey(id);
        //检验帖子状态
        if(article == null || article.getDeleteState() == 1){

            //打印日志
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());

            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));

        }
        if(article.getState() == 1){
            //打印日志
            log.warn(ResultCode.FAILED_BOARD_BANNED.toString());

            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_BANNED));
        }
        //更新操作
        Article updateArticle = new Article();
        updateArticle.setId(article.getId());
        //回复数 = 原回复数 + 1
        updateArticle.setReplyCount(article.getReplyCount()+1);
        updateArticle.setUpdateTime(new Date());

        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);
        if(row != 1){
            //打印日志
            log.warn(ResultCode.ERROR_SERVICES.toString());

            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }


    }

    @Override
    public void modify(Long id, String title, String content) {

        //非空校验
        if(id == null || id <= 0 ||StringUtil.isEmpty(title)
        || StringUtil.isEmpty(content)){

            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());

            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));

        }
        //要更新的帖子对象
        Article updateArticle = new Article();
        updateArticle.setId(id);
        updateArticle.setTitle(title);
        updateArticle.setContent(content);
        updateArticle.setUpdateTime(new Date());

        //调用DAO
        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);
        if(row != 1){
            log.warn(ResultCode.ERROR_SERVICES.toString());

            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));

        }




    }
}
