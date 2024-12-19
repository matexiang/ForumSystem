package com.lxy.forum.controller;

import com.lxy.forum.common.AppResult;
import com.lxy.forum.exception.ApplicationException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping("exception")
    public AppResult testExcption() throws Exception{
        throw new Exception("异常");
    }

  
}
