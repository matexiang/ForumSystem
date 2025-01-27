package com.lxy.forum.controller;


import com.lxy.forum.common.AppResult;
import com.lxy.forum.common.ResultCode;
import com.lxy.forum.config.AppConfig;
import com.lxy.forum.model.Article;
import com.lxy.forum.model.ArticleReply;
import com.lxy.forum.model.User;
import com.lxy.forum.service.IArticleReplyService;
import com.lxy.forum.service.IArticleService;
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
import java.util.List;

@Slf4j
@Api(tags = "回复接口")
@RestController
@RequestMapping("/reply")
public class ArticleReplyController {

    @Autowired
    private IArticleService articleService;

    @Autowired
    private IArticleReplyService articleReplyService;

    @ApiOperation("回复帖子")
    @RequestMapping("/create")
    public AppResult create(HttpServletRequest request,
                           @ApiParam("帖子Id") @RequestParam("articleId") @NonNull Long articleId,
                            @ApiParam("帖子内容") @RequestParam("content") @NonNull  String content){

        //获取用户
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        //判断用户是否已禁言
        if(user.getState() == 1){
            //表示已禁言
            return  AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        //获取要回复的帖子信息
        Article article = articleService.selectById(articleId);
        //是否存在,或已删除
        if(article == null || article.getDeleteState() == 1){
            //表示已删除或不存在
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
        }
        //是否封贴
        if(article.getState() == 1){
            return AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED);
        }
         //构建回复对象
        ArticleReply articleReply = new ArticleReply();
        articleReply.setArticleId(articleId);//要回复的贴id
        articleReply.setPostUserId(user.getId());//回复的发送者
        articleReply.setContent(content);//回复内容

        //写入回复
        articleReplyService.create(articleReply);

        return AppResult.success();
    }

    @ApiOperation("获取回复列表")
    @GetMapping("/getReplies")
    public AppResult<List<ArticleReply>> getRepliesByArticleId(@ApiParam("帖子id") @RequestParam("articleId") @NonNull Long articleId ){
          Article article = articleService.selectById(articleId);

          if(article == null || article.getDeleteState() == 1){

              return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
          }
          //调用Service
        List<ArticleReply> articleReplies = articleReplyService.selectByArticleId(articleId);

        return AppResult.success(articleReplies);


    }

}
