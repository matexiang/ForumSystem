package com.lxy.forum.service;

import com.lxy.forum.model.Board;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IBoardService {

    //查询num条记录
    List<Board> selectByNum (@Param("num") Integer num);

    //根据板块id查询板块信息
    Board selectById (Long id);

    //更新板块中帖子数量
    void addOneArticleCountById(Long id);
}
