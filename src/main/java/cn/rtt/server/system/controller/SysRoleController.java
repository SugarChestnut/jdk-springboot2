package cn.rtt.server.system.controller;

import cn.rtt.server.system.domain.request.role.RoleSearchRequest;
import cn.rtt.server.system.domain.response.SysPage;
import cn.rtt.server.system.domain.response.Result;
import cn.rtt.server.system.domain.entity.SysRole;
import cn.rtt.server.system.domain.entity.SysUserRole;
import cn.rtt.server.system.service.SysRoleService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

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

    @PreAuthorize("@ss.hasPermission('system:role:list')")
    @RequestMapping("/list")
    public Result<SysPage<SysRole>> page(@RequestBody RoleSearchRequest request) {
        return Result.success(roleService.pageSearch(request));
    }

    @PreAuthorize("@ss.hasPermission('system:role:edit')")
    @RequestMapping("/menu/{roleId}")
    public Result<Set<Long>> menu(@PathVariable("roleId") Long roleId) {
        return Result.success(roleService.menu(roleId));
    }

    @PreAuthorize("@ss.hasPermission('system:role:edit')")
    @RequestMapping("/dept/{roleId}")
    public Result<Set<Long>> dept(@PathVariable("roleId") Long roleId) {
        return Result.success(roleService.dept(roleId));
    }

    /**
     * 新增角色
     */
    @PreAuthorize("@ss.hasPermission('system:role:add')")
    @PostMapping("create")
    public Result<?> create(@Validated @RequestBody SysRole role) {
        roleService.createRole(role);
        return Result.success();

    }

    /**
     * 修改保存角色
     */
    @PreAuthorize("@ss.hasPermission('system:role:edit')")
    @PostMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(@Validated @RequestBody SysRole role) {
        roleService.updateRole(role);
        return Result.success();
    }

    /**
     * 删除角色
     */
    @PreAuthorize("@ss.hasPermission('system:role:remove')")
    @GetMapping("/delete/{roleId}")
    public Result<?> remove(@PathVariable("roleId") Long roleId) {
        roleService.deleteById(roleId);
        return Result.success();
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermission('system:role:edit')")
    @PutMapping("/changeStatus")
    public Result<?> changeStatus(@RequestBody SysRole role) {
        roleService.updateRoleStatus(role);
        return Result.success();
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
        return Result.success(roleService.addAuthUser(userRole));
    }


}
