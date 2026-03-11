package cn.rtt.server.system.service;

import cn.rtt.server.system.constant.MenuType;
import cn.rtt.server.system.constant.RoleEnum;
import cn.rtt.server.system.constant.UserConstants;
import cn.rtt.server.system.dao.SysMenuRepository;
import cn.rtt.server.system.dao.SysRoleMenuRepository;
import cn.rtt.server.system.domain.LoginUser;
import cn.rtt.server.system.domain.request.menu.MenuSearchRequest;
import cn.rtt.server.system.domain.response.Result;
import cn.rtt.server.system.domain.response.TreeMenuSelect;
import cn.rtt.server.system.domain.entity.SysMenu;
import cn.rtt.server.system.domain.entity.SysRoleMenu;
import cn.rtt.server.system.exception.SystemException;
import cn.rtt.server.system.utils.CollectionUtils;
import cn.rtt.server.system.utils.SecurityUtils;
import cn.rtt.server.system.utils.StringProUtils;
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
    public List<SysMenu> getRouteTree() {
        MenuSearchRequest request = new MenuSearchRequest();
        request.setMenuType(MenuType.BUTTON.getType());
        List<SysMenu> menus = getMenus(request);
        List<SysMenu> collect = menus.stream()
                .filter(m -> !Objects.equals(m.getMenuType(), MenuType.BUTTON.getType()))
                .collect(Collectors.toList());
        return buildMenuTree(collect);
    }

    @Override
    public List<SysMenu> getMenuTree(MenuSearchRequest request) {
        return buildMenuTree(getMenus(request));
    }

    private List<SysMenu> getMenus(MenuSearchRequest request) {
        List<SysMenu> menus;
        if (SecurityUtils.getLoginUser().getSuperAdmin()) {
            menus = menuRepository.getBaseMapper().getSuperAdminMenu(request);
        } else {
            request.setUserId(SecurityUtils.getLoginUser().getUserId());
            menus = menuRepository.getBaseMapper().getUserMenu(request);
        }
        return menus;
    }

    @Override
    public Set<String> getPermission(Long userId) {
        List<String> permissionList = menuRepository.getBaseMapper().selectPermissionByUserId(userId);
        return permissionList.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toSet());
    }

    @Override
    public List<Long> selectMenuListByRoleId(Long roleId) {
        return menuRepository.getBaseMapper().selectMenuListByRoleId(roleId, false);
    }

    @Override
    public void createMenu(SysMenu menu) {
        checkMenu(menu);

        // 外链菜单必须是 https
        //

        menuRepository.save(menu);
    }

    @Override
    public void updateMenu(SysMenu menu) {
        if (menu.getMenuId() == null) throw new IllegalArgumentException("未指定菜单");
        checkMenu(menu);
        menuRepository.updateById(menu);
    }

    private void checkMenu(SysMenu menu) {
        LambdaQueryWrapper<SysMenu> w1 = new LambdaQueryWrapper<>();
        w1.eq(SysMenu::getTitle, menu.getTitle());
        w1.eq(SysMenu::getParentId, menu.getParentId() != null ? menu.getParentId() : 0);
        if (menu.getMenuId() != null) {
            List<SysMenu> list = menuRepository.list(w1);
            for (SysMenu sysMenu : list) {
                if (!Objects.equals(sysMenu.getMenuId(), menu.getMenuId())) throw new SystemException("菜单名称已存在");
            }
        } else {
            if (menuRepository.count(w1) > 0) throw new SystemException("菜单名称已存在");
        }
        if (menu.getParentId() != null) {
            SysMenu parentMenu = menuRepository.getById(menu.getParentId());
            if (parentMenu == null) throw new SystemException("父菜单不存在");
            if (Objects.equals(menu.getParentId(), menu.getMenuId())) throw new SystemException("上级菜单不能选择自己");
        }
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
     * 构建菜单树
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

    private List<SysMenu> buildChildMenu(List<SysMenu> list, long parentId) {
        List<SysMenu> childList = list.stream().filter(menu -> menu.getParentId() == parentId).collect(Collectors.toList());
        for (SysMenu menu : childList) {
            menu.setChildren(buildChildMenu(list, menu.getMenuId()));
        }
        return childList;
    }
}
