package cn.rtt.server.system.service;

import cn.rtt.server.system.constant.RoleEnum;
import cn.rtt.server.system.constant.UserConstants;
import cn.rtt.server.system.dao.SysMenuRepository;
import cn.rtt.server.system.dao.SysRoleMenuRepository;
import cn.rtt.server.system.domain.LoginUser;
import cn.rtt.server.system.domain.response.TreeMenuSelect;
import cn.rtt.server.system.domain.entity.SysMenu;
import cn.rtt.server.system.domain.entity.SysRoleMenu;
import cn.rtt.server.system.exception.SystemException;
import cn.rtt.server.system.utils.CollectionUtils;
import cn.rtt.server.system.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单权限表 服务实现类
 */
@Service
@Slf4j
@AllArgsConstructor
public class SysMenuServiceImpl implements SysMenuService {

    private final SysMenuRepository menuRepository;

    private final SysRoleMenuRepository roleMenuRepository;

    @Override
    public Set<String> getPermission(Long userId) {
        List<String> permissionList = menuRepository.getBaseMapper().selectPermissionByUserId(userId);
        return permissionList.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toSet());
    }

    @Override
    public List<SysMenu> getTree() {
        List<SysMenu> menus;
        if (SecurityUtils.getLoginUser().getSuperAdmin()) {
            menus = menuRepository.getBaseMapper().getSuperAdminRoute();
        } else {
            menus = menuRepository.getBaseMapper().getRouteByUserId(SecurityUtils.getLoginUser().getUserId());
        }
        return buildMenuTree(menus);
    }

    @Override
    public Set<String> selectMenuPermsByRoleId(Long roleId) {
        return null;
    }

    @Override
    public Set<String> selectMenuPermsByUserId(Long userId) {
        return null;
    }

    @Override
    public List<SysMenu> selectMenuList(SysMenu menu, Long userId) {
        List<SysMenu> menuList;
        // 管理员显示所有菜单信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (RoleEnum.isAdmin(loginUser.getUser().getRoles())) {
            menuList = menuRepository.getBaseMapper().selectMenuList(menu);
        } else {
            menu.getParams().put("userId", userId);
            menuList = menuRepository.getBaseMapper().selectMenuListByUserId(menu);
        }
        if (menu.getNotNextAllNodeId() != null && CollectionUtils.isNotEmpty(menuList)) {
            menuList = menuList.stream().filter(m -> m.getMenuId().intValue() != menu.getNotNextAllNodeId()).collect(Collectors.toList());
        }
        return menuList;
    }

    @Override
    public List<SysMenu> selectMenuList(Long userId) {
        return selectMenuList(new SysMenu(), userId);
    }

    @Override
    public SysMenu selectMenuById(Long menuId) {
        return menuRepository.getById(menuId);
    }

    @Override
    public List<TreeMenuSelect> buildMenuTreeSelect(List<SysMenu> menus) {
        List<SysMenu> menuTrees = buildMenuTree(menus);
        return menuTrees.stream().map(TreeMenuSelect::new).collect(Collectors.toList());
    }

    @Override
    public List<Long> selectMenuListByRoleId(Long roleId) {
        return menuRepository.getBaseMapper().selectMenuListByRoleId(roleId, false);
    }

    @Override
    public boolean checkMenuNameUnique(SysMenu menu) {
        long menuId = menu.getMenuId() == null ? -1L : menu.getMenuId();
        SysMenu info = menuRepository.getBaseMapper().checkMenuNameUnique(menu.getMenuName(), menu.getParentId());
        if (info != null && info.getMenuId() != menuId) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public boolean hasChildByMenuId(Long menuId) {
        return false;
    }

    @Override
    public boolean checkMenuExistRole(Long menuId) {
        return roleMenuRepository.getBaseMapper().checkMenuExistRole(menuId) > 0;
    }

    @Override
    public void createMenu(SysMenu menu) {
        if (StringUtils.isEmpty(menu.getMenuName())) throw new SystemException("菜单名称不能为空");
        LambdaQueryWrapper<SysMenu> w1 = new LambdaQueryWrapper<>();
        w1.eq(SysMenu::getMenuName, menu.getMenuName());
        w1.eq(SysMenu::getParentId, menu.getParentId());
        if (menuRepository.count(w1) > 0) throw new SystemException("菜单名称已存在");
        // 外链菜单必须是 https
        //

        menuRepository.save(menu);
    }

    @Override
    public void updateMenu(SysMenu sysMenu) {
        menuRepository.updateById(sysMenu);
    }

    @Override
    public void deleteMenu(long sysMenuId) {
        LambdaQueryWrapper<SysMenu> w1 = new LambdaQueryWrapper<>();
        w1.eq(SysMenu::getParentId, sysMenuId);
        if (menuRepository.count(w1) > 0) throw new SystemException("存在子菜单,不允许删除");

        LambdaQueryWrapper<SysRoleMenu> w2 = new LambdaQueryWrapper<>();
        w2.eq(SysRoleMenu::getMenuId, sysMenuId);
        if (roleMenuRepository.count(w2) > 0) throw new SystemException("菜单已分配,不允许删除");
        menuRepository.removeById(sysMenuId);
    }

    /**
     * 构建前端所需要树结构
     *
     * @param menus 菜单列表
     * @return 树结构列表
     */
    private List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> topMenu = menus.stream()
                .filter(menu -> menu.getParentId() == null || menu.getParentId() == 0)
                .collect(Collectors.toList());

        for (SysMenu menu : topMenu) {
            menu.setChildren(buildChildMenu(menus, menu.getMenuId()));
        }
        return topMenu;
    }

    /**
     * 根据父节点的ID获取所有子节点
     *
     * @param list     分类表
     * @param parentId 传入的父节点ID
     * @return String
     */
    private List<SysMenu> buildChildMenu(List<SysMenu> list, long parentId) {
        List<SysMenu> childList = list.stream().filter(menu -> menu.getParentId() == parentId).collect(Collectors.toList());
        for (SysMenu menu : childList) {
            menu.setChildren(buildChildMenu(list, menu.getMenuId()));
        }
        return childList;
    }
}
