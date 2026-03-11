package cn.rtt.server.system.controller;

import cn.rtt.server.system.domain.LoginUser;
import cn.rtt.server.system.domain.request.role.RoleSearchRequest;
import cn.rtt.server.system.domain.response.SysPage;
import cn.rtt.server.system.domain.response.Result;
import cn.rtt.server.system.domain.entity.SysRole;
import cn.rtt.server.system.domain.entity.SysUserRole;
import cn.rtt.server.system.security.TokenService;
import cn.rtt.server.system.service.SysMenuService;
import cn.rtt.server.system.service.SysRoleService;
import cn.rtt.server.system.service.SysUserService;
import cn.rtt.server.system.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色信息
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/role")
@AllArgsConstructor
public class SysRoleController {

    private final SysRoleService roleService;

    private final TokenService tokenService;

    private final SysUserService userService;

    private final SysMenuService menuService;


    @PreAuthorize("@ss.hasPermission('system:role:list')")
    @PostMapping("/list")
    public Result<SysPage<SysRole>> page(@RequestBody RoleSearchRequest request) {
        return Result.success(roleService.pageSearch(request));
    }

    /**
     * 根据角色编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermission('system:role:query')")
    @GetMapping(value = "/{roleId}")
    public Result getInfo(@PathVariable Long roleId) {
        return Result.success(roleService.selectRoleById(roleId));
    }

    /**
     * 新增角色
     */
    @PreAuthorize("@ss.hasPermission('system:role:add')")
    @PostMapping("add")
    public Result add(@Validated @RequestBody SysRole role) {
        if (!roleService.checkRoleNameUnique(role)) {
            return Result.error("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (!roleService.checkRoleKeyUnique(role)) {
            return Result.error("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        roleService.insertRole(role);
        return Result.success();

    }

    /**
     * 修改保存角色
     */
    @PreAuthorize("@ss.hasPermission('system:role:edit')")
    @PostMapping("edit")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> edit(@Validated @RequestBody SysRole role) {
        // 不允许修改管理员和超级管理员角色
//        if (RoleEnum.isContains(role.getId())) {
//            role.setRoleName(null);
//            role.setRoleKey(null);
//        }
        roleService.checkRoleAllowed(role.getRoleId());
        roleService.checkRoleDataScope(List.of(role.getRoleId()));
        if (!roleService.checkRoleNameUnique(role)) {
            return Result.error("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (!roleService.checkRoleKeyUnique(role)) {
            return Result.error("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }

        roleService.updateRole(role);
        roleService.insertRoleMenu(role);
        // 更新缓存用户权限
        LoginUser loginUser = SecurityUtils.getLoginUser();

        loginUser.setPermissions(menuService.getPermission(loginUser.getUserId()));
        loginUser.setUser(userService.getUser(loginUser.getUser().getUsername()));
        tokenService.setLoginUser(loginUser);

        return Result.success();
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermission('system:role:edit')")
    @PutMapping("/changeStatus")
    public Result changeStatus(@RequestBody SysRole role) {
        roleService.checkRoleAllowed(role.getRoleId());
        roleService.checkRoleDataScope(List.of(role.getRoleId()));
        roleService.updateRoleStatus(role);
        return Result.success();
    }

    /**
     * 删除角色
     */
    @PreAuthorize("@ss.hasPermission('system:role:remove')")
    @GetMapping("deleteById")
    public Result remove(@RequestParam("id") Long id) {
        roleService.deleteById(id);
        return Result.success();
    }

    /**
     * 获取角色选择框列表
     */
    @PreAuthorize("@ss.hasPermission('system:role:query')")
    @GetMapping("/optionselect")
    public Result optionselect() {
        return Result.success(roleService.selectRoleAll());
    }


    /**
     * 取消授权用户
     */
    @PreAuthorize("@ss.hasPermission('system:role:edit')")
    @PostMapping("/authUser/cancel")
    public Result cancelAuthUser(@RequestBody SysUserRole userRole) {
        return Result.success(roleService.deleteAuthUser(userRole));
    }

    /**
     * 授权用户
     */
    @PreAuthorize("@ss.hasPermission('system:role:edit')")
    @PostMapping("/authUser/add")
    public Result addAuthUser(@RequestBody SysUserRole userRole) {
        roleService.checkRoleDataScope(List.of(userRole.getRoleId()));
        return Result.success(roleService.addAuthUser(userRole));
    }


}
