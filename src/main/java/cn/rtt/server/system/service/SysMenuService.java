package cn.rtt.server.system.service;


import cn.rtt.server.system.domain.request.menu.MenuSearchRequest;
import cn.rtt.server.system.domain.response.TreeMenuSelect;
import cn.rtt.server.system.domain.entity.SysMenu;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 菜单权限表 服务类
 * </p>
 *
 * @author author
 * @since 2024-07-18
 */
public interface SysMenuService {
    /**
     * 获取路由树
     */
    List<SysMenu> getRouteTree();
    /**
     * 获取菜单树
     */
    List<SysMenu> getMenuTree(MenuSearchRequest request);
    /**
     *获取用户权限标识
     */
    Set<String> getPermission(Long userId);
    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    List<Long> selectMenuListByRoleId(Long roleId);

    void createMenu(SysMenu sysMenu);

    void updateMenu(SysMenu sysMenu);

    void deleteMenu(long sysMenuId);
}
