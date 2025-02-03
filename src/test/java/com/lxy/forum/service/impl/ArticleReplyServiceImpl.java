package com.lxy.forum.service.impl;

import com.lxy.forum.common.AppResult;
import com.lxy.forum.common.ResultCode;
import com.lxy.forum.dao.ArticleReplyMapper;
import com.lxy.forum.exception.ApplicationException;
import com.lxy.forum.model.ArticleReply;
import com.lxy.forum.service.IArticleReplyService;
import com.lxy.forum.service.IArticleService;
import com.lxy.forum.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ArticleReplyServiceImpl implements IArticleReplyService {

    @Autowired
    private ArticleReplyMapper articleReplyMapper;
    @Autowired
    private IArticleService articleService;

    @Override
    public void create(ArticleReply articleReply) {
        //非空校验
        if(articleReply == null || articleReply.getArticleId() == null
        || articleReply.getPostUserId() == null
        || StringUtil.isEmpty(articleReply.getContent())){

            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());

            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //设置默认值
        articleReply.setReplyId(null);
        articleReply.setReplyUserId(null);
        articleReply.setLikeCount(0);
        articleReply.setState((byte)0);
        articleReply.setDeleteState((byte)0);
        Date date = new Date();
        articleReply.setCreateTime(date);
        articleReply.setUpdateTime(date);
        //写入数据库
        int row = articleReplyMapper.insertSelective(articleReply);
        if(row != 1){
            //打印日志
            log.warn(ResultCode.ERROR_SERVICES.toString());

            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }

        //更新帖子表中回复数
        articleService.addOneReplyCountById(articleReply.getArticleId());

        //打印日志
        log.info("回复成功, article id = " + articleReply.getArticleId() + "user id = " +
                articleReply.getPostUserId());


    }

    @Override
    public List<ArticleReply> selectByArticleId(Long articleId) {

        if(articleId == null || articleId <= 0){
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());

            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        //调用dao
        List<ArticleReply> result = articleReplyMapper.selectByArticleId(articleId);


        return result;


    }
}
