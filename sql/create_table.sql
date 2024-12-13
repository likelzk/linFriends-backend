create database lin_friend;

create table if not exists lin_friend.user
(
    id           bigint auto_increment
        primary key,
    username     varchar(256)                       null comment '用户昵称
',
    userAccount  varchar(256)                       null comment '账号',
    avatarUrl    varchar(1024)                      null comment '用户头像',
    gender       tinyint                            null comment '性别 1-女 0-男',
    userPassword varchar(512)                       not null comment '密码
',
    phone        varchar(128)                       null comment '电话',
    email        varchar(512)                       null comment '邮箱',
    userStatus   int      default 0                 not null comment '状态 0-正常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间
',
    tags         varchar(1024)                      null comment '标签 json 列表',
    updateTime   datetime default CURRENT_TIMESTAMP null comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    userRole     int      default 0                 not null comment '用户角色 0-普通，1-管理员'
)
    comment '用户';

