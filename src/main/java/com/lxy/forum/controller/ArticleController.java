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

        //查询所有
        List<Article> articles = articleService.selectAll();
        if(articles == null){
            //非空判断,如果为空,创建一个空集合
            articles = new ArrayList<>();
        }


        return AppResult.success(articles);
    }
}
