package com.lxy.forum.controller;

import com.lxy.forum.common.AppResult;
import com.lxy.forum.exception.ApplicationException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("测试类的相关接口")
@RestController
@RequestMapping("/test")
public class TestController {

    @ApiOperation("测试接口2")
    @RequestMapping("exception")
    public AppResult testExcption() throws Exception{
        throw new Exception("异常");
    }

    @RequestMapping("appexception")
    public AppResult testApplicationException() {
        throw new ApplicationException("ApplicationException异常");
    }
}
