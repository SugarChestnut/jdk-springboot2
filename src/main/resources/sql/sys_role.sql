CREATE TABLE `sys_role`
(
    `role_id`      bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name`    varchar(255) NOT NULL COMMENT '角色名称',
    `role_key`     varchar(255) NOT NULL COMMENT '角色权限字符串',
    `status`       tinyint      NOT NULL DEFAULT 0 COMMENT '角色状态（0正常 1停用）',
    `gmt_create`   datetime              DEFAULT NULL COMMENT '创建时间',
    `gmt_modified` datetime              DEFAULT NULL COMMENT '更新时间',
    `remark`       varchar(500)          DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='角色信息表';

INSERT INTO `sys_role` (`role_id`, `role_name`, `role_key`, `status`, `gmt_create`, `gmt_modified`)
VALUES (1, '超级管理员', 'super_admin', 0, NOW(), NOW()),
       (2, '管理员', 'admin', 0, NOW(), NOW());