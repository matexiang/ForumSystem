package com.lxy.forum.controller;


import com.lxy.forum.common.AppResult;
import com.lxy.forum.common.ResultCode;
import com.lxy.forum.config.AppConfig;
import com.lxy.forum.model.Article;
import com.lxy.forum.model.Board;
import com.lxy.forum.model.User;
import com.lxy.forum.service.IArticleService;
import com.lxy.forum.service.IBoardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Api(tags = "帖子接口")
@RestController
@RequestMapping("/article")
public class ArticleController {


    @Autowired
    private IArticleService articleService;

    @Autowired
    private IBoardService boardService;

    //发布新帖子
    @ApiOperation("发布新贴")
    @RequestMapping("/create")
    public AppResult create(HttpServletRequest request,
                            @ApiParam("板块Id") @RequestParam("boardId") @NonNull Long boardId,
                            @ApiParam("文章标题") @RequestParam("title") @NonNull String title,
                            @ApiParam("文章内容") @RequestParam("content") @NonNull String content){

        //校验用户是否禁言
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        if(user.getState() == 1){
            //用户禁言
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        //板块校验
        Board board = boardService.selectById(boardId.longValue());
        if(board == null || board.getDeleteState() == 1 || board.getState() == 1){

            //打印日志
            log.warn(ResultCode.FAILED_BOARD_BANNED.toString());

            return AppResult.failed(ResultCode.FAILED_CREATE);
        }

        //封装文章对象
        Article article = new Article();
        article.setTitle(title);
        article.setContent(content);
        article.setBoardId(boardId);
        article.setUserId(user.getId());
        //调用service
        articleService.create(article);

        //响应成功
        return AppResult.success();
    }

    @ApiOperation("获取帖子列表")
    @RequestMapping("/getAllByBoardId")
    public AppResult<List<Article>> getAllByBoard(@ApiParam("板块Id") @RequestParam(value = "boardId", required = false) Long boardId){


        List<Article> articles;

        if(boardId == null){
            //查询所有
            articles = articleService.selectAll();
        }else {
            articles = articleService.selectAllByBoard(boardId);
        }

        if(articles == null){
            //非空判断,如果为空,创建一个空集合
            articles = new ArrayList<>();
        }


        return AppResult.success(articles);
    }

    @ApiOperation("根据帖子Id获取详情")
    @RequestMapping("/details")
    public AppResult<Article> getDetails(HttpServletRequest request,
                                         @ApiParam("帖子Id") @RequestParam("id") @NonNull Long id){

        //从Session获取当前登录的用户
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);


        //调用Service.获取帖子详情
        Article article =articleService.selectDetailById(id);
        //判断结果为空
        if(article == null){
            //返回错误信息
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
        }
        //判断当前用户是否为作者
        if(user.getId() == article.getUserId()){
            article.setOwn(true);
        }

        return AppResult.success(article);
    }
    @ApiOperation("修改帖子")
    @RequestMapping("/modify")
    public AppResult modify (HttpServletRequest request,
                            @ApiParam("帖子Id") @RequestParam("id") @NonNull Long id,
                             @ApiParam("帖子标题") @RequestParam("title") @NonNull String title,
                             @ApiParam("帖子正文") @RequestParam("content") @NonNull String content){

        //获取当前登录用户
        HttpSession session = request.getSession(false);

        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        //校验用户状态
        if(user.getState() == 1){
            //返回错误描述
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        //查询帖子详情
        Article article = articleService.selectById(id);
        if(article == null){
            //返回错误描述
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);

        }
        if(user.getId() != article.getUserId()){
            //返回错误描述
            return  AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }
        //判断帖子状态
        if(article.getState() == 1 || article.getDeleteState() == 1){

            return AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED);

        }
        //调Service
        articleService.modify(id,title,content);

        log.info("帖子更新成功,Article id = " + id + "User id =" + user.getId() + ".");

        return AppResult.success();
    }

    @ApiOperation("点赞")
    @RequestMapping("/thumbsUp")
    public  AppResult thumbsUp (HttpServletRequest request,
            @ApiParam("帖子Id") @RequestParam("id") @NonNull Long id){
        //校验用户状态
        HttpSession session = request.getSession(false);

        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        //判断用户是否被禁言
        if(user.getState() == 1){
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);

        }
        //直接调用Service
        articleService.thumbsUpById(id);

        return AppResult.success();

    }

    @ApiOperation("删除帖子")
    @RequestMapping("/delete")
    public AppResult deleteById (HttpServletRequest request,
                                 @ApiParam("帖子id") @RequestParam("id") @NonNull Long id){
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        //表示用户禁言
        if(user.getState() == 1){
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);

        }
        //查询帖子详情
        Article article = articleService.selectById(id);
        //检验帖子状态
        if(article ==  null || article.getDeleteState() == 1){
            //帖子已删除
            return  AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
        }
        //校验当前登录用户是不是作者
        if(user.getId() != article.getUserId()){
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }
        //调用service
        articleService.deleteById(id);

        return AppResult.success();
    }

    @ApiOperation("获取用户的帖子列表")
    @GetMapping("/getAllByUserId")
    public AppResult<List<Article>> getAllByUserId ( HttpServletRequest request,@ApiParam("用户id") @RequestParam(value = "userId",required = false) Long userId){

        if(userId == null){
            HttpSession session = request.getSession(false);

            User user =(User) session.getAttribute(AppConfig.USER_SESSION);
            userId = user.getId();
        }
        //调用Service
        List<Article> articles = articleService.selectByUserId(userId);

        return AppResult.success(articles);

    }


}
