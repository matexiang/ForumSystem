package com.lxy.forum.config;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.lxy.forum.dao")
public class MybatisConfig {
}
