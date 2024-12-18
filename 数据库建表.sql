drop database if exists forum_db;
CREATE database forum_db CHARACTER set utf8mb4 collate utf8mb4_general_ci;
#创建库
use forum_db;
#创建表
#创建用户表
drop table if EXISTS t_user;
create TABLE t_user(
    id bigint primary key auto_increment comment '编号，主键自增',
		username VARCHAR(20) not null unique comment '用户名,唯一',
		password VARCHAR(20) not null comment '加密后的密码',
		nickname VARCHAR(20) not null comment '昵称',
		phoneNum VARCHAR(20) comment '电话'	,
		email VARCHAR(50) comment '电子邮箱',
		gender tinyint not null default 2 comment '性别0女，1男，2保密',
		salt VARCHAR(32) not null comment '为密码加盐',
		avatarUrl VARCHAR(255) comment '用户头像路径',
		articleCount int not null default 0 comment '发帖数量',
		isAdmin tinyint not null default 0 comment '是否管理员0否，1是',
		remark VARCHAR(100) comment '备注，自我介绍',
		state tinyint not null DEFAULT 0 comment '状态0正常，1禁言',
		deleteState tinyint not null DEFAULT 0 comment '是否删除，0否，1是',
		createTime datetime not null comment '创建时间，精确到秒',
		updateTime datetime not null comment '更新时间，精确到秒'

);

#板块表
drop table if EXISTS t_board;
CREATE TABLE t_board(

     id bigint primary key auto_increment comment '编号，主键自增',
		 name VARCHAR(50) not null comment '板块名',
		 articleCount int not null default 0 comment '帖子数量',
		 sort int not null DEFAULt 0 comment '排序优先级，升序',
		 	state tinyint not null DEFAULT 0 comment '状态0正常，1禁言',
		deleteState tinyint not null DEFAULT 0 comment '是否删除，0否，1是',
		createTime datetime not null comment '创建时间，精确到秒',
		updateTime datetime not null comment '更新时间，精确到秒'
		 
);

#帖子表
drop table if EXISTS t_article;
CREATE TABLE t_article(
    
     id bigint primary key auto_increment comment '编号，主键自增',
		 boardId bigint not null comment '关联板块编号',
		 userId bigint not null comment '发帖人，关联用户编号',
		 title VARCHAR(100) not null  comment '帖子标题',
		 content text not null comment '帖子正文',
		 visitCount INT not null DEFAULT 0 comment '访问量',
		 replyCount int not null DEFAULT 0 comment '回复数',
		 likeCount int not null DEFAULT 0 comment '点赞数',
		state tinyint not null DEFAULT 0 comment '状态0正常，1禁言',
		deleteState tinyint not null DEFAULT 0 comment '是否删除，0否，1是',
		createTime datetime not null comment '创建时间，精确到秒',
		updateTime datetime not null comment '更新时间，精确到秒'

);

#帖子回复表
drop table if EXISTS t_article_reply;
CREATE TABLE t_article_reply(
    
     id bigint primary key auto_increment comment '编号，主键自增',
		articleId bigint not null comment '关联帖子编号',
		postUserId bigint not null comment '楼主用户，关联用户编号',
		replyId bigint not null comment '关联回复编号，支持楼中楼',
		replyUserId bigint not null comment '楼主下的回复用户编号，支持楼中楼',
		content VARCHAR(500) not null comment '回帖内容',
		likeCount int not null DEFAULT 0 comment '点赞数',
		state tinyint not null DEFAULT 0 comment '状态0正常，1禁言',
		deleteState tinyint not null DEFAULT 0 comment '是否删除，0否，1是',
		createTime datetime not null comment '创建时间，精确到秒',
		updateTime datetime not null comment '更新时间，精确到秒'

);

#站内信表
drop table if EXISTS t_message;
CREATE TABLE t_message(
    
     id bigint primary key auto_increment comment '编号，主键自增',
		
		postUserId bigint not null comment '发送者，关联用户编号',
		receiveUserId bigint not null comment '接收者，关联用户编号',
		content VARCHAR(255) not null comment '内容',
		state tinyint not null DEFAULT 0 comment '状态0正常，1禁言',
		deleteState tinyint not null DEFAULT 0 comment '是否删除，0否，1是',
		createTime datetime not null comment '创建时间，精确到秒',
		updateTime datetime not null comment '更新时间，精确到秒'

);





