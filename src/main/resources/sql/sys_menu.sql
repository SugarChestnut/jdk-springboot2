CREATE TABLE `sys_menu`
(
    `menu_id`      bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `menu_name`    varchar(255) NOT NULL COMMENT '菜单名称',
    `parent_id`    bigint(20)   DEFAULT '0' COMMENT '父菜单ID',
    `order_num`    int(11)      DEFAULT '0' COMMENT '显示顺序',
    `path`         varchar(200) DEFAULT '' COMMENT '路由地址',
    `component`    varchar(255) DEFAULT NULL COMMENT '组件路径',
    `query`        varchar(255) DEFAULT NULL COMMENT '路由参数',
    `route_name`   varchar(255) DEFAULT '' COMMENT '路由名称',
    `is_frame`     int(11)      DEFAULT '0' COMMENT '是否为外链（0否 1是）',
    `is_cache`     int(11)      DEFAULT '0' COMMENT '是否缓存（0不缓存 1缓存）',
    `menu_type`    char(1)      DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
    `visible`      char(1)      DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
    `status`       char(1)      DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
    `permission`   varchar(100) DEFAULT NULL COMMENT '权限标识',
    `icon`         varchar(100) DEFAULT '#' COMMENT '菜单图标',
    `gmt_create`   datetime     DEFAULT NULL COMMENT '创建时间',
    `gmt_modified` datetime     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB COMMENT ='菜单表';

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`,
                        `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`,
                        `status`, `permission`, `icon`, `gmt_create`, `gmt_modified`)
VALUES (1, '系统管理', 0, 99, '/system', '', '', 'system', 0, 0, 'M', '0',
        0, '', 'iconfont icon-xitongshezhi', now(), now()),
       (2, '用户管理', 1, 1, '/system/user', 'system/user/index.vue', '', 'user', 0, 0, 'C', '0',
        0, 'system:user:list', 'iconfont icon-gerenzhongxin', now(), now()),
       (3, '角色管理', 1, 2, '/system/role', 'system/role/index.vue', '', 'role', 0, 0, 'C', '0',
        0, 'system:role:list', 'iconfont icon-skin', now(), now()),
       (4, '菜单管理', 1, 3, '/system/menu', 'system/menu/index.vue', '', 'menu', 0, 0, 'C', '0',
        0, 'system:menu:list', 'iconfont icon-bolangneng', now(), now());