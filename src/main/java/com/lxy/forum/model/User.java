package com.lxy.forum.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
@ApiModel("用户信息")
@Data
public class User {
    private Long id;

    @ApiModelProperty("用户")
    private String username;

    private String password;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("电话号码")
    private String phoneNum;
    @ApiModelProperty("邮箱")
    private String email;

    private Byte gender;

    private String salt;

    @ApiModelProperty("头像地址")
    private String avatarUrl;

    private Integer articleCount;

    private Byte isAdmin;

    private String remark;

    private Byte state;

    private Byte deleteState;

    private Date createTime;

    private Date updateTime;


}