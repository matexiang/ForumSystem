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
import java.util.List;

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

    @ApiOperation("查询用户的所有站内信")
    @GetMapping("/getAll")
    public AppResult<List<Message>> getAll(HttpServletRequest request){

        //获取当前登录的用户
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        //调用service
        List<Message> messages = messageService.selectByReceiveUserId(user.getId());

        return  AppResult.success(messages);


    }

    @ApiOperation("更新为已读")
    @PostMapping("/markRead")
    public AppResult markRead(HttpServletRequest request,
                              @ApiParam("站内信id") @RequestParam("id") @NonNull Long id){
        //根据id查询站内信
        Message message = messageService.selectById(id);
        if (message == null || message.getDeleteState() == 1) {

            return AppResult.failed(ResultCode.FAILED_NOT_EXISTS);

        }
        //校验站内信是不是自己的
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        if (user.getId() != message.getReceiveUserId()) {
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }
        //调用service,把状态更新成已读
        messageService.updateStateById(id,(byte)1);

        return AppResult.success();

    }

    /**
     * 回复站内信
     * @param request
     * @param repliedId
     * @param content
     * @return
     */
    @ApiOperation("回复站内信")
    @PostMapping("/reply")
    public AppResult reply (HttpServletRequest request,
                            @ApiParam("要回复的站内信id") @RequestParam("repliedId") @NonNull  Long repliedId,
                            @ApiParam("站内信内容") @RequestParam("content") @NonNull String content){

        //校验当前用户状态
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        if (user.getState() == 1) {
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        //校验要回复对的站内信状态
        Message existsMessage = messageService.selectById(repliedId);
        if(existsMessage == null || existsMessage.getDeleteState() == 1){
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
        }
        if(user.getId() == existsMessage.getPostUserId()){
            return AppResult.failed("不能给自己站内信回复");
        }
        //构造对象
        Message message = new Message();
        message.setPostUserId(user.getId());
        message.setReceiveUserId(existsMessage.getReceiveUserId());
        message.setContent(content);

        messageService.reply(repliedId,message);

        return AppResult.success();
    }



}
