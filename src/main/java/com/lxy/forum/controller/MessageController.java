package com.lxy.forum.controller;

import com.lxy.forum.common.AppResult;
import com.lxy.forum.common.ResultCode;
import com.lxy.forum.config.AppConfig;
import com.lxy.forum.model.Message;
import com.lxy.forum.model.User;
import com.lxy.forum.service.IMessageService;
import com.lxy.forum.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Api(tags = "站内信接口")
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private IMessageService messageService;
    @Autowired
    private IUserService userService;

    @ApiOperation("发送站内信")
    @PostMapping("/send")
    public AppResult send(HttpServletRequest request,
                          @ApiParam("接收者Id") @RequestParam("receiveUserId") @NonNull Long receiveUserId,
                          @ApiParam("内容") @RequestParam("content") @NonNull  String content){

        //获取当前用户登录信息
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        //当前用户登录用户状态,如果禁言就不能发信息
        if (user.getState() == 1) {
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        //不能给自己发送站内信
        if (user.getId() == receiveUserId) {
            return AppResult.failed("不能给自己发送站内信");
        }
        //校验接受者是否存在
        User receiveUser = userService.selectById(receiveUserId);
        if (receiveUser == null || receiveUser.getDeleteState() == 1) {
            return AppResult.failed("接受者状态异常");
        }
        //封装对象
        Message message = new Message();
        message.setPostUserId(user.getId());
        message.setReceiveUserId(receiveUserId);
        message.setContent(content);
        //调用service
        messageService.create(message);

        return AppResult.success("发送成功! ");
    }

    @ApiOperation("/获取未读数")
    @GetMapping("/getUnreadCount")
    public AppResult<Integer> getUnreadCount(HttpServletRequest request){

        //1.获取当前登录用户
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        //调用service
        Integer count = messageService.selectUnreadCount(user.getId());

        return AppResult.success(count);

    }

}
