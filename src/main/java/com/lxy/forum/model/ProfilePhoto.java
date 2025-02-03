package com.lxy.forum.model;

import lombok.Data;

@Data
public class ProfilePhoto {
    private Integer id;
    private String username;
    private String avatar;//头像路径
}
