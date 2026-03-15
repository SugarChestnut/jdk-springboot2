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
     * 获取部门树
     */
    @RequestMapping("/search")
    public Result<List<SysDept>> search(@RequestBody DeptSearchRequest request) {
        return Result.success(deptService.search(request));
    }

    /**
     * 获取部门用户筛选，不构建树
     */
    @RequestMapping("/select")
    public Result<List<SysDept>> select(@RequestBody DeptSearchRequest request) {
        return Result.success(deptService.searchForSelect(request));
    }

    /**
     * 新增部门
     */
    @PreAuthorize("@ss.hasPermission('system:dept:create')")
    @PostMapping("/create")
    public Result<?> create(@Validated @RequestBody SysDept dept) {
        deptService.createDept(dept);
        return Result.success();
    }

    /**
     * 修改部门
     */
    @PreAuthorize("@ss.hasPermission('system:dept:edit')")
    @PostMapping("/update")
    public Result<?> update(@Validated @RequestBody SysDept dept) {
        deptService.updateDept(dept);
        return Result.success();
    }

    /**
     * 删除部门
     */
    @PreAuthorize("@ss.hasPermission('system:dept:delete')")
    @GetMapping("/delete/{deptId}")
    public Result<?> delete(@PathVariable("deptId") Long deptId) {
        deptService.deleteDept(deptId);
        return Result.success();
    }
}
