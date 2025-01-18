package com.lxy.forum.service.impl;

import com.lxy.forum.common.AppResult;
import com.lxy.forum.common.ResultCode;
import com.lxy.forum.dao.BoardMapper;
import com.lxy.forum.exception.ApplicationException;
import com.lxy.forum.model.Board;
import com.lxy.forum.service.IBoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.transform.Result;
import java.util.List;

@Slf4j
@Service
public class BoardServiceImpl implements IBoardService {

    @Autowired
    private BoardMapper boardMapper;

    @Override
    public List<Board> selectByNum(Integer num) {

        //非空校验
        if(num <= 0){
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));

        }
        //调用Dao查询数据库中的数据
        List<Board> result = boardMapper.selectByNum(num);

        //返回结果
        return result;

    }

    @Override
    public Board selectById(Long id) {
        if(id == null || id <= 0){

            log.warn(ResultCode.FAILED_BOARD_ARTICLE_COUNT.toString());

            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_ARTICLE_COUNT));

        }
        //调用dao查询数据
        Board board = boardMapper.selectByPrimaryKey(id);
        return board;
    }

    @Override
    public void addOneArticleCountById(Long id) {

        if(id == null || id <= 0){

            log.warn(ResultCode.FAILED_BOARD_ARTICLE_COUNT.toString());

            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_ARTICLE_COUNT));

        }
        //查询对应板块
        Board board = boardMapper.selectByPrimaryKey(id);
        if(board == null){
            log.warn(ResultCode.ERROR_IS_NULL.toString()+ ", board id = " + id);

            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));

        }
        //更新帖子数量
        Board updateBoard = new Board();
        updateBoard.setId(board.getId());
        updateBoard.setArticleCount(board.getArticleCount()+1);
        //调用dao,实现更新
        int row = boardMapper.updateByPrimaryKeySelective(updateBoard);

        //受影响行数
        if(row != 1){
            log.warn(ResultCode.FAILED.toString()+", 受影响行数不等于1");

            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }




    }
}
