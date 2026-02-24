CREATE TABLE `sys_user`
(
    `user_id`      bigint(20)   NOT NULL AUTO_INCREMENT,
    `username`     varchar(255) NOT NULL COMMENT '用户名',
    `password`     varchar(255) NOT NULL COMMENT '密码',
    `gender`       tinyint      NOT NULL DEFAULT 0 COMMENT '性别0女1男',
    `avatar_url`   varchar(1000)         DEFAULT NULL COMMENT '头像地址',
    `mobile`       varchar(255) NOT NULL COMMENT '手机',
    `gmt_create`   datetime     NOT NULL COMMENT '创建时间',
    `gmt_modified` datetime     NOT NULL COMMENT '修改时间',
    `status`       int(11)      NOT NULL DEFAULT '0' COMMENT '0正常1封禁',
    `login_ip`     varchar(255) COMMENT '登录IP',
    `login_time`   datetime COMMENT '登录时间',
    PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='用户表';

-- 2. 插入管理员用户
INSERT INTO `sys_user` (`user_id`, `username`, `password`, `gender`, `mobile`, `gmt_create`, `gmt_modified`,
                        `status`)
VALUES (2, 'admin', '$2a$10$FdEkA3c2FMbSLFwvu/qwnevHnI9WIn68FXTYu0NrzffxiE33K1d1m', 1, '13800138000', NOW(), NOW(), 0),
       (1, 'superadmin', '$2a$10$FdEkA3c2FMbSLFwvu/qwnevHnI9WIn68FXTYu0NrzffxiE33K1d1m', 1, '13800138000', NOW(), NOW(),
        0);