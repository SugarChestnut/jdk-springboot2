CREATE TABLE `sys_user_role`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id`      bigint(20) NOT NULL COMMENT '用户ID',
    `role_id`      bigint(20) NOT NULL COMMENT '角色ID',
    `gmt_create`   datetime DEFAULT NULL COMMENT '创建时间',
    `gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='用户和角色关联表';

INSERT INTO `sys_user_role` (`user_id`, `role_id`, `gmt_create`, `gmt_modified`)
VALUES (1, 1, NOW(), NOW()),
       (2, 2, NOW(), NOW());