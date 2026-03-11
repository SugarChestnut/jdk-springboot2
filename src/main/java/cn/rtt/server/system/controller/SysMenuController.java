package cn.rtt.server.system.controller;


import cn.rtt.server.system.domain.request.menu.MenuSearchRequest;
import cn.rtt.server.system.domain.response.Result;
import cn.rtt.server.system.domain.entity.SysMenu;
import cn.rtt.server.system.service.SysMenuService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单信息
 */
@RestController
@RequestMapping("/system/menu")
@AllArgsConstructor
public class SysMenuController {

    private final SysMenuService menuService;

    /**
     * 获取当前登录用户路由信息
     */
    @RequestMapping("/route")
    public Result<List<SysMenu>> route() {
        return Result.success(menuService.getRouteTree());
    }

    /**
     * 获取当前登录用户菜单信息
     */
    @PreAuthorize("@ss.hasPermission('system:menu:list')")
    @RequestMapping("/tree")
    public Result<List<SysMenu>> tree(@RequestBody MenuSearchRequest request) {
        return Result.success(menuService.getMenuTree(request));
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
    public Result<?> edit(@Validated @RequestBody SysMenu menu) {
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
