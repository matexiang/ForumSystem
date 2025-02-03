package com.lxy.forum.controller;

import com.lxy.forum.common.AppResult;
import com.lxy.forum.common.ResultCode;
import com.lxy.forum.exception.ApplicationException;
import com.lxy.forum.model.Board;
import com.lxy.forum.service.IBoardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Api(tags = "板块接口")
@RestController
@RequestMapping("/board")
public class BoardController {

    //从配置文件中读取值,如果没有配置,默认值为9
    @Value("${forum.index.board-num:9}")
    private Integer indexBoardNum;

    @Autowired
    private IBoardService boardService;

    @ApiOperation("获取首页板块列表")
    @RequestMapping("/toplist")
    public AppResult<List<Board>> toplist(){
        log.info("首页板块个数为:"+indexBoardNum);
        //调用Service查询结果
        List<Board> boards = boardService.selectByNum(indexBoardNum);
        //判断是否为空
        if(boards == null){
            boards = new ArrayList<>();
        }
        return AppResult.success(boards);
    }

    @ApiOperation("获取板块信息")
    @RequestMapping("getById")
    public AppResult<Board> getById(@ApiParam("板块Id") @RequestParam("id") @NonNull Long id){
        //调用Service
        Board board = boardService.selectById(id);

        //对查询的结果进行检验
        if(board == null || board.getDeleteState() == 1){
            //打印日志
            log.warn(ResultCode.FAILED_BOARD_NOT_EXISTS.toString());
            //抛异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXISTS));

        }

        //返回结果
        return AppResult.success(board);
    }


}
