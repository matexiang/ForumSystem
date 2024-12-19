package com.lxy.forum.exception;

import com.lxy.forum.common.AppResult;
import lombok.Data;

//自定义异常
@Data
public class ApplicationException extends RuntimeException{
    //在异常中持有一个错误信息
    protected AppResult errorResult;


    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }
}
