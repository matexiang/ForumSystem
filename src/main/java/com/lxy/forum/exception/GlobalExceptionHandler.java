package com.lxy.forum.exception;

import com.lxy.forum.common.AppResult;
import com.lxy.forum.common.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(ApplicationException.class)
    public AppResult applicationException (ApplicationException e){

        e.printStackTrace();
        //打印日志
        log.error(e.getMessage());
        if(e.getErrorResult() != null){
            return e.getErrorResult();

        }
        //判空
        if(e.getMessage() == null || e.getMessage().equals("")){
            return AppResult.failed(ResultCode.ERROR_IS_NULL);
        }
        return AppResult.failed(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public AppResult exceptionHandler (Exception e){

        log.error(e.getMessage());
        if(e.getMessage() == null || e.getMessage().equals("")){
            return AppResult.failed(ResultCode.ERROR_IS_NULL);
        }
        //返回异常信息
        return AppResult.failed(e.getMessage());
    }
}
