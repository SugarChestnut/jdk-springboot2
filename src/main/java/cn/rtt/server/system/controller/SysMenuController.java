package cn.rtt.server.system.controller;


import cn.rtt.server.system.constant.UserConstants;
import cn.rtt.server.system.domain.response.Result;
import cn.rtt.server.system.domain.response.TreeMenuSelect;
import cn.rtt.server.system.domain.entity.SysMenu;
import cn.rtt.server.system.service.SysMenuService;
import cn.rtt.server.system.utils.SecurityUtils;
import cn.rtt.server.system.utils.StringProUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.Strings;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单信息
 */
@RestController
@RequestMapping("/system/menu")
@AllArgsConstructor
public class SysMenuController {

    private final SysMenuService menuService;

    /**
     * 获取当前登录用户菜单路由信息
     */
    @GetMapping("tree")
    public Result<List<SysMenu>> tree() {
        return Result.success(menuService.getTree());
    }

    /**
     * 获取菜单列表
     */
    @PreAuthorize("@ss.hasPermission('system:menu:list')")
    @GetMapping("/list")
    public Result list(SysMenu menu) {
        List<SysMenu> menus = menuService.selectMenuList(menu, SecurityUtils.getUserId());
        return Result.success(menus);
    }

    /**
     * 获取菜单列表
     */
    @PreAuthorize("@ss.hasPermission('system:menu:list')")
    @GetMapping("/listAll")
    public Result listAll(SysMenu menu) {
        List<SysMenu> menus = menuService.selectMenuList(menu, null);
        return Result.success(menus);
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermission('system:menu:query')")
    @GetMapping(value = "/{menuId}")
    public Result getInfo(@PathVariable Long menuId) {
        return Result.success(menuService.selectMenuById(menuId));
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeselect")
    public Result<List<TreeMenuSelect>> treeselect(SysMenu menu) {
        List<SysMenu> menus = menuService.selectMenuList(menu, SecurityUtils.getUserId());
        return Result.success(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 加载对应角色菜单列表树
     */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public Result roleMenuTreeSelect(@PathVariable("roleId") Long roleId) {
        List<SysMenu> menus = menuService.selectMenuList(SecurityUtils.getUserId());
        Map<String, Object> ajax = new HashMap<>();
        ajax.put("checkedKeys", menuService.selectMenuListByRoleId(roleId));
        ajax.put("menus", menuService.buildMenuTreeSelect(menus));
        return Result.success(ajax);
    }

    /**
     * 新增菜单
     */
    @PreAuthorize("@ss.hasPermission('system:menu:create')")
    @PostMapping("create")
    public Result<?> create(@Validated @RequestBody SysMenu menu) {
        menuService.createMenu(menu);
        return Result.success();
    }

    /**
     * 修改菜单
     */
    @PreAuthorize("@ss.hasPermission('system:menu:edit')")
    @PostMapping("edit")
    public Result edit(@Validated @RequestBody SysMenu menu) {
        if (!menuService.checkMenuNameUnique(menu)) {
            return Result.error("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        } else if (UserConstants.YES_FRAME == menu.getIsFrame() && !StringProUtils.isHttp(menu.getPath())) {
            return Result.error("修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        } else if (menu.getMenuId().equals(menu.getParentId())) {
            return Result.error("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        menuService.updateMenu(menu);
        return Result.success();
    }

    /**
     * 删除菜单
     */
    @PreAuthorize("@ss.hasPermission('system:menu:delete')")
    @GetMapping("/delete/{menuId}")
    public Result<?> delete(@PathVariable("menuId") Long menuId) {
        menuService.deleteMenu(menuId);
        return Result.success();
    }


}
