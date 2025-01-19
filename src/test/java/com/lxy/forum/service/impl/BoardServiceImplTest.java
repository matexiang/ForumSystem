package com.lxy.forum.service.impl;

import com.lxy.forum.model.Board;
import com.lxy.forum.service.IBoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BoardServiceImplTest {

    @Resource
    private IBoardService bordService;


    @Test
    void selectByNum() {
        List<Board> boards = bordService.selectByNum(4);
        System.out.println(boards);
    }

    @Test
    @Transactional//执行网测试方法回滚数据库操作
    void addOneArticleCountById() {
        bordService.addOneArticleCountById(1L);
        System.out.println("更新成功");
    }
}