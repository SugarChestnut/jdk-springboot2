package cn.rtt.server.system.service;


import cn.rtt.server.system.domain.entity.SysRoleMenu;

import java.util.List;

/**
 * <p>
 * 角色和菜单关联表 服务类
 * </p>
 *
 * @author author
 * @since 2024-07-18
 */
public interface SysRoleMenuService {

    List<SysRoleMenu> getByRoleId(Long roleId);

    void removeByRoleId(Long roleId);
}
