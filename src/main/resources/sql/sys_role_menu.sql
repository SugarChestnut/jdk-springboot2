CREATE TABLE `sys_role_menu`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `role_id`      bigint(20) NOT NULL COMMENT '角色ID',
    `menu_id`      bigint(20) NOT NULL COMMENT '菜单ID',
    `gmt_create`   datetime DEFAULT NULL COMMENT '创建时间',
    `gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='角色和菜单关联表';