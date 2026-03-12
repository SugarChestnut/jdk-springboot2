package cn.rtt.server.system.controller;

import cn.rtt.server.system.domain.entity.SysDept;
import cn.rtt.server.system.domain.request.dept.DeptSearchRequest;
import cn.rtt.server.system.domain.response.Result;
import cn.rtt.server.system.service.SysDeptService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author rtt
 * @date 2026/3/11 09:14
 */
@RestController
@RequestMapping("/system/dept")
@AllArgsConstructor
public class SysDeptController {

    private final SysDeptService deptService;

    /**
     * 获取当前登录用户菜单信息
     */
    @RequestMapping("/tree")
    public Result<List<SysDept>> treeSearch(@RequestBody DeptSearchRequest request) {
        return Result.success(deptService.treeSearch(request));
    }

    /**
     * 新增菜单
     */
    @PreAuthorize("@ss.hasPermission('system:dept:create')")
    @PostMapping("/create")
    public Result<?> create(@Validated @RequestBody SysDept dept) {
        deptService.createDept(dept);
        return Result.success();
    }

    /**
     * 修改菜单
     */
    @PreAuthorize("@ss.hasPermission('system:dept:edit')")
    @PostMapping("/update")
    public Result<?> update(@Validated @RequestBody SysDept dept) {
        deptService.updateDept(dept);
        return Result.success();
    }

    /**
     * 删除菜单
     */
    @PreAuthorize("@ss.hasPermission('system:dept:delete')")
    @GetMapping("/delete/{deptId}")
    public Result<?> delete(@PathVariable("deptId") Long deptId) {
        deptService.deleteDept(deptId);
        return Result.success();
    }
}
